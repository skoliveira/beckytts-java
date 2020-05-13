/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.skoliveira.beckytts;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.audio.QueuedTrack;
import com.github.skoliveira.beckytts.settings.Settings;
import com.github.skoliveira.beckytts.tts.gTTS;
import com.github.skoliveira.beckytts.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Listener extends ListenerAdapter
{
    private final Bot bot;
    protected static long requests;

    public Listener(Bot bot)
    {
        this.bot = bot;
        Listener.requests = 0;
    }

    @Override
    public void onReady(ReadyEvent event) 
    {
        bot.getSettingsManager().init();
        if(event.getJDA().getGuilds().isEmpty())
        {
            Logger log = LoggerFactory.getLogger("BeckyTTS");
            log.warn("This bot is not on any guilds! Use the following link to add the bot to your guilds!");
            log.warn(event.getJDA().getInviteUrl(BeckyTTS.RECOMMENDED_PERMS));
        }
        if(bot.getConfig().useUpdateAlerts())
        {
            bot.getThreadpool().scheduleWithFixedDelay(() -> 
            {
                User owner = bot.getJDA().getUserById(bot.getConfig().getOwnerId());
                if(owner!=null)
                {
                    String currentVersion = OtherUtil.getCurrentVersion();
                    String latestVersion = OtherUtil.getLatestVersion();
                    if(latestVersion!=null && !currentVersion.equalsIgnoreCase(latestVersion))
                    {
                        String msg = String.format(OtherUtil.NEW_VERSION_AVAILABLE, currentVersion, latestVersion);
                        owner.openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
                    }
                }
            }, 0, 24, TimeUnit.HOURS);
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        Member me = event.getGuild().getSelfMember();
        List<Member> listJoined = event.getChannelJoined().getMembers();
        List<Member> listLeft = event.getChannelLeft().getMembers();
        if((listLeft.size()==1 && listLeft.contains(me)) ||
                (listJoined.size()==1 && listJoined.contains(me))) {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            handler.stopAndClear();
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        Member me = event.getGuild().getSelfMember();
        List<Member> list = event.getChannelLeft().getMembers();
        if(list.size()==1 && list.contains(me)) {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            handler.stopAndClear();
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Settings settings = bot.getSettingsManager().getSettings(event.getGuild());
        if(!settings.getAutoTtsMode())
            return;

        TextChannel tchannel = settings.getTextChannel(event.getGuild());    	
        if(tchannel!=null && !event.getChannel().equals(tchannel))
            return;

        VoiceChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();
        if(current==null)
            current = settings.getVoiceChannel(event.getGuild());
        GuildVoiceState userState = event.getMember().getVoiceState();
        if(!userState.inVoiceChannel() || userState.isDeafened() || (current!=null && !userState.getChannel().equals(current)))
            return;

        VoiceChannel afkChannel = event.getGuild().getAfkChannel();
        if(afkChannel!=null && afkChannel.equals(userState.getChannel()))
            return;

        if(!settings.containsAutoTtsUser(event.getMember()))
            return;

        String message = event.getMessage().getContentStripped();

        // remove links
        String regexUrl = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)";
        message = message.replaceAll(regexUrl, "");

        // remove emojis
        message = message.replaceAll(":\\S+:", "");

        // remove extra white spaces
        message = message.replaceAll("(?: |\\t)(?: |\\t)+", " ");

        if(message.isBlank())
            return;

        if(!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
        {
            try 
            {
                event.getGuild().getAudioManager().setSelfDeafened(true);
                event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
            }
            catch(PermissionException ex) 
            {
                return;
            }
        }

        gTTS tts = new gTTS();
        String[] urls = tts.getTtsUrls(message);
        for(String url : urls) {
            bot.getPlayerManager().loadItemOrdered(event.getGuild(), url, new EventTtsHandler(event));
            Listener.requests++;
        }
    }

    private class EventTtsHandler implements AudioLoadResultHandler
    {
        private final GuildMessageReceivedEvent event;

        private EventTtsHandler(GuildMessageReceivedEvent event)
        {
            this.event = event;
        }

        private void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
                return;

            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            handler.addTrack(new QueuedTrack(track, event.getAuthor()));
        }

        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
        {
            int[] count = {0};
            playlist.getTracks().stream().forEach((track) -> {
                if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
                {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }

        @Override
        public void trackLoaded(AudioTrack track)
        {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }
            else
            {
                loadPlaylist(playlist, null);
            }
        }

        @Override
        public void noMatches()
        {
            event.getChannel().sendMessage(bot.getConfig().getWarning()+" No results found for `"+event.getMessage().getContentStripped()+"`.").queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON) {
                if(throwable.getMessage().contains("URL"))
                    event.getChannel().sendMessage(bot.getConfig().getError()+" Error request: "+Listener.requests+". TTS service unavailable.").queue();
                else
                    event.getChannel().sendMessage(bot.getConfig().getError()+" Error loading: "+throwable.getMessage()).queue();
            } else {
                event.getChannel().sendMessage(bot.getConfig().getError()+" Error loading track.").queue();
            }
        }
    }

}
package com.github.skoliveira.beckytts.commands.ttsrole;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.audio.QueuedTrack;
import com.github.skoliveira.beckytts.commands.TTSRoleCommand;
import com.github.skoliveira.beckytts.settings.Settings;
import com.github.skoliveira.beckytts.tts.MessageHearing;
import com.github.skoliveira.beckytts.tts.gTTS;
import com.github.skoliveira.beckytts.utils.FormatUtil;
import com.github.skoliveira.beckytts.utils.LanguageUtil;
import com.github.skoliveira.beckytts.utils.MessageUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class TtsCmd extends TTSRoleCommand
{

    public TtsCmd(Bot bot)
    {
        super(bot);
        this.name = "tts";
        this.arguments = "<text to speech>";
        this.help = "plays the text to speech";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        Settings settings = bot.getSettingsManager().getSettings(event.getGuild());
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            if(settings.containsAutoTtsUser(event.getMember()))
            {
                settings.removeAutoTtsUser(event.getMember());
                event.replySuccess(event.getMember().getAsMention() + " is no longer an autotts user");
            }
            else 
            {
                event.reply("Click on the flag to choose your language:", m -> {
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("US")).queue(); // 🇺🇸
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("GB")).queue(); // 🇬🇧
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("AU")).queue(); // 🇦🇺
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("BR")).queue(); // 🇧🇷
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("PT")).queue(); // 🇵🇹
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("ES")).queue(); // 🇪🇸
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("FR")).queue(); // 🇫🇷
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("DE")).queue(); // 🇩🇪
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("IT")).queue(); // 🇮🇹
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("RU")).queue(); // 🇷🇺
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("JP")).queue(); // 🇯🇵
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("CN")).queue(); // 🇨🇳
                    m.addReaction(LanguageUtil.getFlagFromAlpha2("KR")).queue(); // 🇰🇷
                    bot.getWaiter().waitForEvent(MessageReactionAddEvent.class,
                            (e -> e.getUser().equals(event.getAuthor()) &&
                                    LanguageUtil.isISOCountryFlag(e.getReactionEmote().getEmoji())),
                            e -> {
                                String flag = e.getReactionEmote().getEmoji();
                                String local = LanguageUtil.getLanguageFromFlag(flag) + '-' + LanguageUtil.getAlpha2FromFlag(flag);
                                settings.addAutoTtsUser(event.getMember(), local);
                                event.replySuccess(event.getMember().getAsMention() + " is now an autotts user");
                                m.delete().queueAfter(5, TimeUnit.SECONDS);
                            },
                            1, TimeUnit.MINUTES,
                            () -> {
                                event.replyError("Sorry, you took too long.");
                                m.delete().queue();
                            });
                });
            }
            return;
        }

        String message = new MessageHearing(event.getMessage()).getContentHearing(event.getArgs());

        if(message.isBlank())
            return;

        if(settings.isSlangInterpreterEnabled()) {
            StringBuilder sb = new StringBuilder(message.length());
            String[] array = message.split(" |\\t");
            for(String e : array) {
                String word = e.replaceAll("(\\p{L}+\\+|\\p{L}+).*", "$1");
                if(settings.containsSlang(word)) {                
                    sb.append(e.replaceAll("\\p{L}+\\+|\\p{L}+", settings.getSlangValue(word)));
                }
                else {
                    sb.append(e);
                }
                sb.append(' ');
            }
            message = sb.toString().trim();
        }

        // build onomatopoeias
        message = MessageUtil.onomatopoeia(message);

        gTTS tts = new gTTS();
        String[] urls;
        try {
            urls = tts.getTtsUrls(message);
            for(String url : urls) {
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), url, new ResultHandler(event));
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class ResultHandler implements AudioLoadResultHandler
    {
        private final CommandEvent event;

        private ResultHandler(CommandEvent event)
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
            event.reply(FormatUtil.filter(event.getClient().getWarning()+" No results found for `"+event.getArgs()+"`."));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON)
                event.reply(event.getClient().getError()+" Error loading: "+throwable.getMessage());
            else
                event.reply(event.getClient().getError()+" Error loading track.");
        }
    }
}
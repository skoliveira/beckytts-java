package com.github.skoliveira.beckytts.commands.ttsrole;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.audio.QueuedTrack;
import com.github.skoliveira.beckytts.commands.TTSRoleCommand;
import com.github.skoliveira.beckytts.settings.Settings;
import com.github.skoliveira.beckytts.tts.GoogleTTS;
import com.github.skoliveira.beckytts.utils.FormatUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TtsCmd extends TTSRoleCommand
{   
    
    public TtsCmd(Bot bot)
    {
        super(bot);
        this.name = "tts";
        this.arguments = "<text to speach>";
        this.help = "plays the text to speach";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            Settings settings = bot.getSettingsManager().getSettings(event.getGuild());
            if(settings.getAutoTtsMode()) {
	            if(settings.containsAutoTtsUser(event.getMember()))
	            {
	            	settings.removeAutoTtsUser(event.getMember());
	            	event.replySuccess(event.getMember().getAsMention() + " is no longer an autotts user");
	            }
	            else 
	            {
	            	settings.addAutoTtsUser(event.getMember());
	            	event.replySuccess(event.getMember().getAsMention() + " is now an autotts user");
	            }
	            return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" TTS Command:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <text to speach>` - plays the text to speach");
            for(Command cmd: children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">") 
                ? event.getArgs().substring(1,event.getArgs().length()-1) 
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
        String url = "";        
        GoogleTTS gtts = new GoogleTTS();
		try {
		    gtts.init(args.replaceAll("\\s\\s+", " ").trim(), "pt", false, false);
		    url = gtts.exec();   
		} catch (Exception e) {
		    e.printStackTrace();
		}
        bot.getPlayerManager().loadItemOrdered(event.getGuild(), url, new ResultHandler(event));
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

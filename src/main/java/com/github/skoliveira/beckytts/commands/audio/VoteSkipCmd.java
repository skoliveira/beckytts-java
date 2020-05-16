package com.github.skoliveira.beckytts.commands.audio;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.commands.AudioCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;

public class VoteSkipCmd extends AudioCommand 
{
    public VoteSkipCmd(Bot bot)
    {
        super(bot);
        this.name = "voteskip";
        this.help = "votes to skip the currently speech";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        Long userId = handler.getRequester();
        Member u = event.getGuild().getMemberById(userId);
        int listeners = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
        String msg;
        if(handler.getVotes().contains(event.getAuthor().getId()))
            msg = event.getClient().getWarning()+" You already voted to skip this speech `[";
        else
        {
            msg = event.getClient().getSuccess()+" You voted to skip the speech `[";
            handler.getVotes().add(event.getAuthor().getId());
        }
        int skippers = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
        int required = (int)Math.ceil(listeners * .55);
        msg+= skippers+" votes, "+required+"/"+listeners+" needed]`";
        if(skippers>=required)
        {
            msg+="\n"+event.getClient().getSuccess()+" Skipped **Text To Speech**"
                    +(userId==0 ? "" : " (requested by "+(u==null ? "someone" : "**"+u.getNickname()+"**")+")");
            handler.stopAndClearUser(userId);
        }
        event.reply(msg);
    }

}

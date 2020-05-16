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
package com.github.skoliveira.beckytts.commands.audio;

import com.github.skoliveira.beckytts.BeckyTTS;
import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.commands.AudioCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends AudioCommand 
{
    public SkipCmd(Bot bot)
    {
        super(bot);
        this.name = "skip";
        this.help = "votes to skip the text to speech";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
        this.category = null;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        Long userId = handler.getRequester();
        Member u = event.getGuild().getMemberById(userId);
        if(event.getAuthor().getIdLong()==userId)
        {
            event.replySuccess("Skipped **Text To Speech**"+(userId==0 ? "" : 
                " (requested by "+(u==null ? "someone" : "**"+u.getNickname()+"**")+")"));
            handler.stopAndClearUser(userId);
            event.getMessage().addReaction(BeckyTTS.THUMBSUP_EMOJI).queue();
        }
        else
        {
            int listeners = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
            String msg;
            if(handler.getVotes().contains(event.getAuthor().getId()))
                msg = event.getClient().getWarning()+" You already voted to skip this text to speech `[";
            else
            {
                msg = event.getClient().getSuccess()+" You voted to skip the text to speech `[";
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
    
}

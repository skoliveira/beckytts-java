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
import com.github.skoliveira.beckytts.commands.TTSRoleCommand;
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
        this.help = "skip the currently speech";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        Long requesterId = handler.getRequester();
        Member m = event.getGuild().getMemberById(requesterId);
        if(event.getAuthor().getIdLong()==requesterId || TTSRoleCommand.checkTTSPermission(event)) {
            event.replySuccess("Skipped **Text To Speech**"+(requesterId==0 ? "" : 
                " (requested by "+(m==null ? "someone" : "**"+m.getEffectiveName()+"**")+")"));
            handler.stopAndClearUser(requesterId);
            event.getMessage().addReaction(BeckyTTS.THUMBSUP_EMOJI).queue();
            return;
        }

        VoteSkipCmd skip = new VoteSkipCmd(bot);
        skip.doCommand(event);     
    }
}

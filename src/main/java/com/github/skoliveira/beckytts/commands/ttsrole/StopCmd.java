/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.github.skoliveira.beckytts.commands.ttsrole;

import com.github.skoliveira.beckytts.BeckyTTS;
import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.commands.TTSRoleCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class StopCmd extends TTSRoleCommand 
{
    public StopCmd(Bot bot)
    {
        super(bot);
        this.name = "stop";
        this.help = "stops and leaving the voice channel";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.getMessage().addReaction(BeckyTTS.THUMBSUP_EMOJI).queue();
    }
}

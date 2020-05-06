/*
 * Copyright 2016 John Grosh (jagrosh).
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

import com.github.skoliveira.beckytts.commands.admin.*;
import com.github.skoliveira.beckytts.commands.dj.*;
import com.github.skoliveira.beckytts.commands.general.*;
import com.github.skoliveira.beckytts.commands.music.*;
import com.github.skoliveira.beckytts.commands.owner.*;
import com.github.skoliveira.beckytts.entities.Prompt;
import com.github.skoliveira.beckytts.gui.GUI;
import com.github.skoliveira.beckytts.settings.SettingsManager;
import com.github.skoliveira.beckytts.utils.OtherUtil;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.*;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import java.awt.Color;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class BeckyTTS 
{
    public final static String PLAY_EMOJI  = "\u25B6"; // ▶
    public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
    public final static String STOP_EMOJI  = "\u23F9"; // ⏹
    public final static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                                Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // startup log
        Logger log = LoggerFactory.getLogger("Startup");
        
        // create prompt to handle startup
        Prompt prompt = new Prompt("BeckyTTS", "Switching to nogui mode. You can manually start in nogui mode by including the -Dnogui=true flag.", 
                "true".equalsIgnoreCase(System.getProperty("nogui", "false")));
        
        // check deprecated nogui mode (new way of setting it is -Dnogui=true)
        for(String arg: args)
            if("-nogui".equalsIgnoreCase(arg))
            {
                prompt.alert(Prompt.Level.WARNING, "GUI", "The -nogui flag has been deprecated. "
                        + "Please use the -Dnogui=true flag before the name of the jar. Example: java -jar -Dnogui=true BeckyTTS.jar");
                break;
            }
        
        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);
        
        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();
        if(!config.isValid())
            return;
        
        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        
        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                                "a music bot that is [easy to host yourself!](https://github.com/skoliveira/BeckyTTS) (v. "+version+")",
                                new String[]{"High-quality music playback", "FairQueue™ Technology", "Easy to host yourself"},
                                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶
        
        // set up the command client
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCommand(),
                        new SettingsCmd(bot),
                        
                        new PlayCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SCSearchCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),

                        new ForceRemoveCmd(bot),
                        new ForceskipCmd(bot),
                        new MoveTrackCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new PrefixCmd(bot),
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        
                        new DebugCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot)
                );
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean noactivity = false;
        if(config.getStatus()!=OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if(config.getActivity()==null)
            cb.useDefaultGame();
        else if(config.getActivity().getName().equalsIgnoreCase("none"))
        {
            cb.setActivity(null);
            noactivity = true;
        }
        else
            cb.setActivity(config.getActivity());
        
        if(!prompt.isNoGUI())
        {
            try 
            {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } 
            catch(Exception e) 
            {
                log.error("Could not start GUI. If you are "
                        + "running on a server or in a location where you cannot display a "
                        + "window, please run in nogui mode using the -Dnogui=true flag.");
            }
        }
        
        log.info("Loaded config from " + config.getConfigLocation());
        
        // attempt to log in and start
        try
        {
            JDA jda = JDABuilder.createDefault(config.getToken())
                    .setAudioSendFactory(new NativeAudioSendFactory())
                    .setActivity(noactivity ? null : Activity.playing("loading..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE || config.getStatus()==OnlineStatus.OFFLINE
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(cb.build(), waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        }
        catch (LoginException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "BeckyTTS", ex + "\nPlease make sure you are "
                    + "editing the correct config.txt file, and that you have used the "
                    + "correct token (not the 'secret'!)\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
        catch(IllegalArgumentException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "BeckyTTS", "Some aspect of the configuration is "
                    + "invalid: " + ex + "\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
    }
}

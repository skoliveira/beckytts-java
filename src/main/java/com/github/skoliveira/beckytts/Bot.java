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
package com.github.skoliveira.beckytts;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.skoliveira.beckytts.audio.AudioHandler;
import com.github.skoliveira.beckytts.audio.PlayerManager;
import com.github.skoliveira.beckytts.gui.GUI;
import com.github.skoliveira.beckytts.settings.SettingsManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import okhttp3.OkHttpClient;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Bot
{
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;
    private final BotConfig config;
    private final SettingsManager settings;
    private final PlayerManager players;

    private boolean shuttingDown = false;
    private JDA jda;
    private GUI gui;

    public Bot(EventWaiter waiter, BotConfig config, SettingsManager settings)
    {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
        this.threadpool = Executors.newSingleThreadScheduledExecutor();
        this.players = new PlayerManager(this);
        this.players.init();
    }

    public BotConfig getConfig()
    {
        return config;
    }

    public SettingsManager getSettingsManager()
    {
        return settings;
    }

    public EventWaiter getWaiter()
    {
        return waiter;
    }

    public ScheduledExecutorService getThreadpool()
    {
        return threadpool;
    }

    public PlayerManager getPlayerManager()
    {
        return players;
    }

    public JDA getJDA()
    {
        return jda;
    }

    public void closeAudioConnection(long guildId)
    {
        Guild guild = jda.getGuildById(guildId);
        if(guild!=null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    public void resetActivity()
    {
        Activity activity = config.getActivity()==null || config.getActivity().getName().equalsIgnoreCase("none") ? null : config.getActivity();
        if(!Objects.equals(jda.getPresence().getActivity(), activity))
            jda.getPresence().setActivity(activity);
    }

    public void shutdown()
    {
        if(shuttingDown)
            return;
        shuttingDown = true;
        threadpool.shutdownNow();
        if(jda.getStatus()!=JDA.Status.SHUTTING_DOWN)
        {
            jda.getGuilds().stream().forEach(g -> 
            {
                g.getAudioManager().closeAudioConnection();
                AudioHandler ah = (AudioHandler)g.getAudioManager().getSendingHandler();
                if(ah!=null)
                {
                    ah.stopAndClear();
                    ah.getPlayer().destroy();
                }
            });
            OkHttpClient client = jda.getHttpClient();
            client.connectionPool().evictAll();
            try {
                client.dispatcher().executorService().awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(gui!=null)
            gui.dispose();
        System.exit(0);
    }

    public void setJDA(JDA jda)
    {
        this.jda = jda;
    }

    public void setGUI(GUI gui)
    {
        this.gui = gui;
    }
}

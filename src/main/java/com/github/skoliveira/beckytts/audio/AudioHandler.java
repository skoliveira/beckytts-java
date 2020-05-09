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
package com.github.skoliveira.beckytts.audio;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import com.github.skoliveira.beckytts.BeckyTTS;
import com.github.skoliveira.beckytts.queue.FairQueue;
import com.github.skoliveira.beckytts.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AudioHandler extends AudioEventAdapter implements AudioSendHandler 
{
    private final FairQueue<QueuedTrack> queue = new FairQueue<>();
    private final Set<String> votes = new HashSet<>();

    private final PlayerManager manager;
    private final AudioPlayer audioPlayer;
    private final long guildId;

    private AudioFrame lastFrame;

    protected AudioHandler(PlayerManager manager, Guild guild, AudioPlayer player)
    {
        this.manager = manager;
        this.audioPlayer = player;
        this.guildId = guild.getIdLong();
    }

    public int addTrackToFront(QueuedTrack qtrack)
    {
        if(audioPlayer.getPlayingTrack()==null)
        {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        }
        else
        {
            queue.addAt(0, qtrack);
            return 0;
        }
    }

    public int addTrack(QueuedTrack qtrack)
    {
        if(audioPlayer.getPlayingTrack()==null)
        {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        }
        else
            return queue.add(qtrack);
    }

    public FairQueue<QueuedTrack> getQueue()
    {
        return queue;
    }

    public void stopAndClear()
    {
        queue.clear();
        audioPlayer.stopTrack();
        //current = null;
    }

    public boolean isMusicPlaying(JDA jda)
    {
        return guild(jda).getSelfMember().getVoiceState().inVoiceChannel() && audioPlayer.getPlayingTrack()!=null;
    }

    public Set<String> getVotes()
    {
        return votes;
    }

    public AudioPlayer getPlayer()
    {
        return audioPlayer;
    }

    public long getRequester()
    {
        if(audioPlayer.getPlayingTrack()==null || audioPlayer.getPlayingTrack().getUserData(Long.class)==null)
            return 0;
        return audioPlayer.getPlayingTrack().getUserData(Long.class);
    }

    // Audio Events
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) 
    {
        if(queue.isEmpty())
        {
            if(!manager.getBot().getConfig().getStay())
                manager.getBot().closeAudioConnection(guildId);
            // unpause, in the case when the player was paused and the track has been skipped.
            // this is to prevent the player being paused next time it's being used.
            player.setPaused(false);
        }
        else
        {
            QueuedTrack qt = queue.pull();
            player.playTrack(qt.getTrack());
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) 
    {
        votes.clear();
    }

    public String getTopicFormat(JDA jda)
    {
        if(isMusicPlaying(jda))
        {
            long userid = getRequester();
            AudioTrack track = audioPlayer.getPlayingTrack();
            String title = track.getInfo().title;
            if(title==null || title.equals("Unknown Title"))
                title = track.getInfo().uri;
            return "**"+title+"** ["+(userid==0 ? "autoplay" : "<@"+userid+">")+"]"
            + "\n" + (audioPlayer.isPaused() ? BeckyTTS.PAUSE_EMOJI : BeckyTTS.PLAY_EMOJI) + " "
            + "[" + FormatUtil.formatTime(track.getDuration()) + "] "
            + FormatUtil.volumeIcon(audioPlayer.getVolume());
        }
        else return "No music playing " + BeckyTTS.STOP_EMOJI + " " + FormatUtil.volumeIcon(audioPlayer.getVolume());
    }

    // Audio Send Handler methods
    @Override
    public boolean canProvide() 
    {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() 
    {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() 
    {
        return true;
    }

    // Private methods
    private Guild guild(JDA jda)
    {
        return jda.getGuildById(guildId);
    }
}

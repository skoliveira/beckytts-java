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
package com.github.skoliveira.beckytts.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.skoliveira.beckytts.tts.Slang;
import com.jagrosh.jdautilities.command.GuildSettingsProvider;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Settings implements GuildSettingsProvider
{
    private final SettingsManager manager;
    protected long textId;
    protected long voiceId;
    protected long roleId;
    private int volume;
    private boolean autoTtsMode;
    private String prefix;
    private final Set<Long> ttsUsers;
    private final Set<String> blacklist;
    private final Slang slangs;
    private boolean slangMode;

    public Settings(SettingsManager manager, String textId, String voiceId, String roleId, int volume, boolean autoTtsMode, String prefix)
    {
        this.manager = manager;
        try
        {
            this.textId = Long.parseLong(textId);
        }
        catch(NumberFormatException e)
        {
            this.textId = 0;
        }
        try
        {
            this.voiceId = Long.parseLong(voiceId);
        }
        catch(NumberFormatException e)
        {
            this.voiceId = 0;
        }
        try
        {
            this.roleId = Long.parseLong(roleId);
        }
        catch(NumberFormatException e)
        {
            this.roleId = 0;
        }
        this.volume = volume;
        this.autoTtsMode = autoTtsMode;
        this.prefix = prefix;
        this.ttsUsers = new HashSet<>();
        this.blacklist = new HashSet<>();
        this.slangs = new Slang();
        this.slangMode = true;
    }

    public Settings(SettingsManager manager, long textId, long voiceId, long roleId, int volume, boolean autoTtsMode, String prefix)
    {
        this.manager = manager;
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
        this.volume = volume;
        this.autoTtsMode = autoTtsMode;
        this.prefix = prefix;
        this.ttsUsers = new HashSet<>();
        this.blacklist = new HashSet<>();
        this.slangs = new Slang();
        this.slangMode = true;
    }

    // Getters
    public TextChannel getTextChannel(Guild guild)
    {
        return guild == null ? null : guild.getTextChannelById(textId);
    }

    public VoiceChannel getVoiceChannel(Guild guild)
    {
        return guild == null ? null : guild.getVoiceChannelById(voiceId);
    }

    public Role getRole(Guild guild)
    {
        return guild == null ? null : guild.getRoleById(roleId);
    }

    public int getVolume()
    {
        return volume;
    }

    public boolean getAutoTtsMode()
    {
        return autoTtsMode;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public boolean getSlangMode()
    {
        return slangMode;
    }
    
    public boolean containsSlang(String key) {
        return slangs.containsKey(key);
    }
    
    public String getSlangValue(String key) {
        return slangs.get(key);
    }
    
    @Override
    public Collection<String> getPrefixes()
    {
        return prefix == null ? Collections.emptySet() : Collections.singleton(prefix);
    }

    // Setters
    public void setTextChannel(TextChannel tc)
    {
        this.textId = tc == null ? 0 : tc.getIdLong();
        this.manager.writeSettings();
    }

    public void setVoiceChannel(VoiceChannel vc)
    {
        this.voiceId = vc == null ? 0 : vc.getIdLong();
        this.manager.writeSettings();
    }

    public void setRole(Role role)
    {
        this.roleId = role == null ? 0 : role.getIdLong();
        this.manager.writeSettings();
    }

    public void setVolume(int volume)
    {
        this.volume = volume;
        this.manager.writeSettings();
    }

    public void setAutoTtsMode(boolean mode)
    {
        this.autoTtsMode = mode;
        this.manager.writeSettings();
    }

    public void setPrefix(String prefix)
    {
        if(this.prefix!=null)
            this.removeFromBlacklist(this.prefix);
        this.prefix = prefix;
        if(prefix!=null)
            this.addInBlacklist(prefix);
        this.manager.writeSettings();
    }
    
    public void setSlangMode(boolean mode)
    {
        this.autoTtsMode = mode;
    }

    public boolean addAutoTtsUser(Member member) {
        Long userid = member == null ? null : member.getIdLong();
        return ttsUsers.add(userid);
    }

    public boolean removeAutoTtsUser(Member member) {
        Long userid = member == null ? null : member.getIdLong();
        return ttsUsers.remove(userid);
    }

    public boolean containsAutoTtsUser(Member member) {
        Long userid = member == null ? null : member.getIdLong();
        return ttsUsers.contains(userid);
    }

    public boolean addInBlacklist(String word) {
        return blacklist.add(word);
    }

    public boolean removeFromBlacklist(String word) {
        return blacklist.remove(word);
    }

    public String[] getBlacklist() {
        return blacklist.toArray(new String[blacklist.size()]);
    }
    
}
package com.github.skoliveira.beckytts.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.github.skoliveira.beckytts.tts.Slang;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class SettingsImpl implements Settings {
    private final SettingsManager manager;
    private final Set<String> blacklist;
    private final Map<Long,String> userslanguage;
    private Slang slangs;
    private boolean slanginterpreter;
    private long textId;
    private long voiceId;
    private long roleId;
    private int volume;
    private String prefix;

    public SettingsImpl(SettingsManager manager, Set<String> blacklist, Map<Long,String> users) {
        this.manager = manager;
        this.blacklist = blacklist;
        this.userslanguage = users;
    }

    // Getters
    @Override
    public long getTextChannelId() {
        return this.textId;
    }

    @Override
    public TextChannel getTextChannel(Guild guild)
    {
        return guild == null ? null : guild.getTextChannelById(textId);
    }

    @Override
    public long getVoiceChannelId() {
        return this.voiceId;
    }

    @Override
    public VoiceChannel getVoiceChannel(Guild guild)
    {
        return guild == null ? null : guild.getVoiceChannelById(voiceId);
    }

    @Override
    public long getRoleId() {
        return this.roleId;
    }

    @Override
    public Role getRole(Guild guild)
    {
        return guild == null ? null : guild.getRoleById(roleId);
    }

    @Override
    public int getVolume()
    {
        return volume;
    }

    @Override
    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public boolean isSlangInterpreterEnabled()
    {
        return slanginterpreter;
    }

    @Override
    public boolean containsSlang(String key) {
        return slangs.containsKey(key);
    }

    @Override
    public String getSlangValue(String key) {
        return slangs.get(key);
    }

    @Override
    public boolean containsAutoTtsUser(Member member) {
        if(member == null)
            return false;
        return this.userslanguage.containsKey(member.getIdLong());
    }

    @Override
    public String[] getBlacklist() {
        return blacklist.toArray(new String[blacklist.size()]);
    }


    // Setters
    @Override
    public Collection<String> getPrefixes()
    {
        return prefix == null ? Collections.emptySet() : Collections.singleton(prefix);
    }

    @Override
    public void setTextChannel(long tcid) {
        this.textId = tcid;
        this.manager.writeSettings();
    }

    @Override
    public void setTextChannel(TextChannel tc) {
        this.textId = tc == null ? 0 : tc.getIdLong();
        this.manager.writeSettings();
    }

    @Override
    public void setVoiceChannel(long vcid) {
        this.voiceId = vcid;
        this.manager.writeSettings();
    }

    @Override
    public void setVoiceChannel(VoiceChannel vc) {
        this.voiceId = vc == null ? 0 : vc.getIdLong();
        this.manager.writeSettings();
    }

    @Override
    public void setRole(long roleid) {
        this.roleId = roleid;
        this.manager.writeSettings();
    }

    @Override
    public void setRole(Role role) {
        this.roleId = role == null ? 0 : role.getIdLong();
        this.manager.writeSettings();
    }

    @Override
    public void setVolume(int volume) {
        this.volume = volume;
        this.manager.writeSettings();
    }

    @Override
    public void setPrefix(String prefix) {
        if(this.prefix!=null)
            this.removeFromBlacklist(this.prefix);
        this.prefix = prefix;
        if(prefix!=null)
            this.addInBlacklist(prefix);
        this.manager.writeSettings();
    }

    @Override
    public void enableSlangInterpreter() {
        this.slanginterpreter = true;
    }

    @Override
    public void disableSlangInterpreter() {
        this.slanginterpreter = false;
    }

    @Override
    public void addAutoTtsUser(Member member, String lang) {
        if(member == null)
            return;
        this.userslanguage.put(member.getIdLong(), lang);
    }

    @Override
    public void removeAutoTtsUser(Member member) {
        if(member == null)
            return;
        this.userslanguage.remove(member.getIdLong());
    }

    @Override
    public boolean addInBlacklist(String word) {
        return blacklist.add(prefix);
    }

    @Override
    public boolean removeFromBlacklist(String word) {
        return blacklist.remove(prefix);
    }

}
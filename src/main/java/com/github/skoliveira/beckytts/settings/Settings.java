package com.github.skoliveira.beckytts.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public interface Settings extends GuildSettingsProvider {

    // Getters
    public long getTextChannelId();
    public TextChannel getTextChannel(Guild guild);
    public long getVoiceChannelId();
    public VoiceChannel getVoiceChannel(Guild guild);
    public long getRoleId();
    public Role getRole(Guild guild);
    public int getVolume();
    public String getPrefix();
    public boolean isSlangInterpreterEnabled();
    public boolean containsSlang(String key);
    public String getSlangValue(String key);
    public boolean containsAutoTtsUser(Member member);
    public String[] getBlacklist();

    // Setters
    public void setTextChannel(long tcid);
    public void setTextChannel(TextChannel tc);
    public void setVoiceChannel(long vcid);
    public void setVoiceChannel(VoiceChannel vc);
    public void setRole(long roleid);
    public void setRole(Role role);
    public void setVolume(int volume);
    public void setPrefix(String prefix);
    public void enableSlangInterpreter();
    public void disableSlangInterpreter();
    public void addAutoTtsUser(Member member, String lang);
    public void removeAutoTtsUser(Member member);
    public boolean addInBlacklist(String word);
    public boolean removeFromBlacklist(String word);

}
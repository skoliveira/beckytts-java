package com.github.skoliveira.beckytts.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingsBuilder {
    private final SettingsManager manager;
    private long textid;
    private long voiceid;
    private long roleid;
    private int volume;
    private String prefix;
    private boolean slanginterpreter;
    private final Map<Long,String> userslanguage;
    private final Set<String> blacklist;
    
    private SettingsBuilder(SettingsManager manager) {
        this.manager = manager;
        this.blacklist = new HashSet<>();
        this.userslanguage = new HashMap<>();
    }
    
    public static SettingsBuilder createDefault(SettingsManager manager) {
        return new SettingsBuilder(manager).applyDefault();
    }
    
    private SettingsBuilder applyDefault() {
        this.setTextChannel(0);
        this.setVoiceChannel(0);
        this.setRole(0);
        this.setVolume(100);
        this.setPrefix(null);
        this.setSlangInterpreter(false);
        return this;
    }
    
    public Settings build() {
        Settings settings = new SettingsImpl(manager, blacklist, userslanguage);
        settings.setTextChannel(textid);
        settings.setVoiceChannel(voiceid);
        settings.setRole(roleid);
        settings.setVolume(volume);
        settings.setPrefix(prefix);
        if(slanginterpreter)
            settings.enableSlangInterpreter();
        else
            settings.disableSlangInterpreter();
        return settings;
    }

    public SettingsBuilder setTextChannel(long textchannelid) {
        this.textid = textchannelid;
        return this;
    }
    
    public SettingsBuilder setVoiceChannel(long voicechannelid) {
        this.voiceid = voicechannelid;
        return this;
    }
    
    public SettingsBuilder setRole(long roleid) {
        this.roleid = roleid;
        return this;
    }

    public SettingsBuilder setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public SettingsBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public SettingsBuilder setSlangInterpreter(boolean value) {
        this.slanginterpreter = value;
        return this;
    }

    public SettingsBuilder addInBlacklist(String prefix) {
        this.blacklist.add(prefix);
        return this;
    }

    
}

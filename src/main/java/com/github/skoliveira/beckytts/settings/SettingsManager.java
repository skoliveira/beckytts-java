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
package com.github.skoliveira.beckytts.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.github.skoliveira.beckytts.utils.OtherUtil;
import com.jagrosh.jdautilities.command.GuildSettingsManager;

import net.dv8tion.jda.api.entities.Guild;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SettingsManager implements GuildSettingsManager<Settings>
{
    private final HashMap<Long,Settings> settings;

    public SettingsManager()
    {
        this.settings = new HashMap<>();
    }

    /**
     * Gets non-null settings for a Guild
     * 
     * @param guild the guild to get settings for
     * @return the existing settings, or new settings for that guild
     */
    @Override
    public Settings getSettings(Guild guild)
    {
        Long guildId = guild.getIdLong();
        return settings.computeIfAbsent(guildId, id -> createDefaultSettings(guild));
    }

    private Settings createDefaultSettings(Guild guild)
    {
        Settings settings = SettingsBuilder.createDefault(this)
                .setRole(guild.getIdLong()) // @everyone id
                .build();
        return settings;
    }

    @Override
    public void init() {
        try {
            JSONObject loadedSettings = new JSONObject(new String(Files.readAllBytes(OtherUtil.getPath("serversettings.json"))));
            loadedSettings.keySet().stream().forEach((id) -> {
                JSONObject o = loadedSettings.getJSONObject(id);
                SettingsBuilder sb = SettingsBuilder.createDefault(this);
                if(o.has("text_channel_id"))
                    sb.setTextChannel(o.getLong("text_channel_id"));
                if(o.has("voice_channel_id"))
                    sb.setVoiceChannel(o.getLong("voice_channel_id"));
                if(o.has("role_id"))
                    sb.setRole(o.getLong("role_id"));
                else
                    sb.setRole(Long.parseUnsignedLong(id));
                if(o.has("volume"))
                    sb.setVolume(o.getInt("volume"));
                if(o.has("prefix"))
                    sb.setPrefix(o.getString("prefix"));
                if(o.has("slang_interpreter"))
                    sb.setSlangInterpreter(o.getBoolean("slang_interpreter"));
                if(o.has("blacklist")) {
                    o.getJSONArray("blacklist").forEach((word) -> {
                        if(word!=null) {
                            sb.addInBlacklist(word.toString());
                        }
                    });
                }
                Settings s = sb.build();
                settings.put(Long.parseLong(id), s);
            });
        } catch(IOException | JSONException e) {
            LoggerFactory.getLogger("Settings").warn("Failed to load server settings (this is normal if no settings have been set yet): "+e);
        }
    }

    public void writeSettings()
    {
        JSONObject obj = new JSONObject();
        settings.keySet().stream().forEach(key -> {
            JSONObject o = new JSONObject();
            Settings s = settings.get(key);
            if(s.getTextChannelId() != 0)
                o.put("text_channel_id", s.getTextChannelId());
            if(s.getVoiceChannelId() != 0)
                o.put("voice_channel_id", s.getVoiceChannelId());
            if(s.getRoleId() != key)
                o.put("role_id", s.getRoleId());
            if(s.getVolume() != 100)
                o.put("volume", s.getVolume());
            if(s.getPrefix() != null)
                o.put("prefix", s.getPrefix());
            if(s.isSlangInterpreterEnabled())
                o.put("slang_interpreter", true);
            if(s.getBlacklist().length > 0)
                o.put("blacklist", s.getBlacklist());
            else
                o.remove("blacklist");
            obj.put(Long.toString(key), o);
        });
        try {
            Files.write(OtherUtil.getPath("serversettings.json"), obj.toString(4).getBytes());
        } catch(IOException ex){
            LoggerFactory.getLogger("Settings").warn("Failed to write to file: "+ex);
        }
    }

    @Override
    public void shutdown() {
        this.settings.clear();
    }

}
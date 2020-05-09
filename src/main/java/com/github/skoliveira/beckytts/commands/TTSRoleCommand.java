package com.github.skoliveira.beckytts.commands;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.settings.Settings;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

public abstract class TTSRoleCommand extends AudioCommand
{
    public TTSRoleCommand(Bot bot)
    {
        super(bot);
        this.category = new Category("TTS", event -> checkTTSPermission(event));
    }
    
    public static boolean checkTTSPermission(CommandEvent event)
    {
        if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
            return true;
        if(event.getGuild()==null)
            return true;
        if(event.getMember().hasPermission(Permission.MANAGE_SERVER))
            return true;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        Role ttsrole = settings.getRole(event.getGuild());
        return ttsrole!=null && (event.getMember().getRoles().contains(ttsrole) || ttsrole.getIdLong()==event.getGuild().getIdLong());
    }
}

package com.github.skoliveira.beckytts.commands.admin;

import java.util.List;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.commands.AdminCommand;
import com.github.skoliveira.beckytts.settings.Settings;
import com.github.skoliveira.beckytts.utils.FormatUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.api.entities.Role;

public class SetroleCmd extends AdminCommand
{
    public SetroleCmd(Bot bot)
    {
        this.name = "setrole";
        this.help = "sets the TTS role for certain tts commands";
        this.arguments = "<rolename|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a role name or NONE");
            return;
        }
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.setRole(null);
            event.reply(event.getClient().getSuccess()+" TTS role cleared; Only Admins can use the tts commands.");
        }
        else
        {
            List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
            if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" No Roles found matching \""+event.getArgs()+"\"");
            else if (list.size()>1)
                event.reply(event.getClient().getWarning()+FormatUtil.listOfRoles(list, event.getArgs()));
            else
            {
                s.setRole(list.get(0));
                event.reply(event.getClient().getSuccess()+" TTS commands can now be used by users with the **"+list.get(0).getName()+"** role.");
            }
        }
    }
    
}

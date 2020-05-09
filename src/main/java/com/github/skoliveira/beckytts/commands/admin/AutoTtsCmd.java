package com.github.skoliveira.beckytts.commands.admin;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.commands.AdminCommand;
import com.github.skoliveira.beckytts.commands.TTSRoleCommand;
import com.github.skoliveira.beckytts.settings.Settings;
import com.jagrosh.jdautilities.command.CommandEvent;

@SuppressWarnings("unused")
public class AutoTtsCmd extends AdminCommand
{
    public AutoTtsCmd(Bot bot)
    {
        this.name = "autotts";
        this.help = "mode that enable/disable tts for all your chatting";
        this.arguments = "[on|off]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) 
    {
        boolean value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
            value = !settings.getAutoTtsMode();
        }
        else if(event.getArgs().equalsIgnoreCase("true")
                || event.getArgs().equalsIgnoreCase("on")
                || event.getArgs().equalsIgnoreCase("enable"))
        {
            value = true;
        }
        else if(event.getArgs().equalsIgnoreCase("false")
                || event.getArgs().equalsIgnoreCase("off")
                || event.getArgs().equalsIgnoreCase("disable"))
        {
            value = false;
        }
        else
        {
            event.replyError("Valid options are `on` or `off` (or leave empty to toggle)");
            return;
        }
        settings.setAutoTtsMode(value);
        event.replySuccess("AutoTTS mode is now `"+(value ? "ENABLE" : "DISABLE")+"` on *"+event.getGuild().getName()+"*");
    }
}

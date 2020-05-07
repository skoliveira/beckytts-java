package com.github.skoliveira.beckytts.commands.dj;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.commands.DJCommand;
import com.github.skoliveira.beckytts.settings.Settings;
import com.jagrosh.jdautilities.command.CommandEvent;

public class AutoTtsCmd extends DJCommand
{
    public AutoTtsCmd(Bot bot)
    {
        super(bot);
        this.name = "autotts";
        this.help = "switch autotts mode";
        this.arguments = "[on|off]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }
    
    // override musiccommand's execute because we don't actually care where this is used
    @Override
    protected void execute(CommandEvent event) 
    {
        boolean value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
            value = !settings.getAutoTtsMode();
        }
        else if(event.getArgs().equalsIgnoreCase("true") || event.getArgs().equalsIgnoreCase("on"))
        {
            value = true;
        }
        else if(event.getArgs().equalsIgnoreCase("false") || event.getArgs().equalsIgnoreCase("off"))
        {
            value = false;
        }
        else
        {
            event.replyError("Valid options are `on` or `off` (or leave empty to toggle)");
            return;
        }
        settings.setAutoTtsMode(value);
        event.replySuccess("AutoTTS mode is now `"+(value ? "ON" : "OFF")+"`");
    }

    @Override
    public void doCommand(CommandEvent event) { /* Intentionally Empty */ }
}

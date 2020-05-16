package com.github.skoliveira.beckytts.commands.admin;

import com.github.skoliveira.beckytts.Bot;
import com.github.skoliveira.beckytts.commands.AdminCommand;
import com.github.skoliveira.beckytts.commands.TTSRoleCommand;
import com.github.skoliveira.beckytts.settings.Settings;
import com.jagrosh.jdautilities.command.Command;
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
        this.children = new Command[]{new BlacklistCmd(bot)};
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

    public class BlacklistCmd extends AdminCommand {
        private Bot bot;

        public BlacklistCmd(Bot bot) {
            this.bot = bot;
            this.name = "blacklist";
            this.help = "show the blacklist";
            this.arguments = "<subcommand>";
            this.guildOnly = true;
            this.children = new Command[]{new ListCmd(bot),new AddCmd(bot),new DelCmd(bot)};
        }

        @Override
        protected void execute(CommandEvent event) {
            if(!event.getArgs().isEmpty()) {
                event.reply("`"+this.getName()+" list` show the blacklist\n"
                        + "`"+this.getName()+" add` add word to the blacklist\n"
                        + "`"+this.getName()+" del` remove word from the blacklist");
                return;
            }
            ListCmd listcmd = new ListCmd(bot);
            listcmd.execute(event);
        }

        public class ListCmd extends AdminCommand {

            public ListCmd(Bot bot) {
                this.name = "list";
                this.help = "show the blacklist";
                this.guildOnly = true;
            }

            @Override
            protected void execute(CommandEvent event) {
                String[] list = bot.getSettingsManager().getSettings(event.getGuild()).getBlacklist();
                String msg = "```" + "Blacklist:";
                for(String word : list)
                    msg += "\n   " + word;
                msg += "```";
                event.reply(msg);
            }
        }

        public class AddCmd extends AdminCommand {

            public AddCmd(Bot bot) {
                this.name = "add";
                this.help = "add word to the blacklist";
                this.arguments = "<word>";
                this.guildOnly = true;
            }

            @Override
            protected void execute(CommandEvent event) {
                if(event.getArgs().isEmpty())
                    return;

                if(bot.getSettingsManager().getSettings(event.getGuild()).addWord(event.getArgs())) {
                    event.replySuccess("`"+event.getArgs()+"` added to the blacklist");
                    bot.getSettingsManager().writeSettings();
                    return;
                }
                
                event.replyError("Couldn't add `"+event.getArgs()+"` to the blacklist"
                        + "\nItem already exists in the list");
            }
        }

        public class DelCmd extends AdminCommand {

            public DelCmd(Bot bot) {
                this.name = "del";
                this.help = "remove word from the blacklist";
                this.arguments = "<word>";
                this.aliases = new String[]{"remove","delete"};
                this.guildOnly = true;
            }

            @Override
            protected void execute(CommandEvent event) {
                if(event.getArgs().isEmpty())
                    return;

                if(bot.getSettingsManager().getSettings(event.getGuild()).removeWord(event.getArgs())) {
                    event.replySuccess("`"+event.getArgs()+"` removed from the blacklist");
                    bot.getSettingsManager().writeSettings();
                    return;
                }

                event.replyError("Couldn't remove `"+event.getArgs()+"` from the blacklist"
                        + "\nItem not found in the list"+"```");
            }

        }

    }

}
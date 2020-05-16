package com.github.skoliveira.beckytts.commands.general;

import com.github.skoliveira.beckytts.Bot;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PingCmd extends Command {

    public PingCmd(Bot bot)
    {
        this.name = "ping";
        this.help = "checks the bot's latency";
        this.guildOnly = false;
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event) {
        long webping = event.getJDA().getGatewayPing();
        event.getChannel().sendMessageFormat("Websocket: %d ms", webping).queue();
        for(int i=0; i<4; i++) {
            event.getJDA().getRestPing().queue(
                    (t) -> event.getChannel().sendMessageFormat("Ping: %d ms", t).queue());
        }       
    }

}
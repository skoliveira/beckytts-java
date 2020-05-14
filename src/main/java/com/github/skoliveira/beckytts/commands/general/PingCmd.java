package com.github.skoliveira.beckytts.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PingCmd extends Command {

    public PingCmd()
    {
        this.name = "ping";
        this.help = "checks the bot's latency";
        this.guildOnly = false;
        this.aliases = new String[]{"pong"};
    }

    @Override
    protected void execute(CommandEvent event) {
        long webping = event.getJDA().getGatewayPing();
        event.getJDA().getRestPing().queue(
                (time) -> event.getChannel().sendMessageFormat("Ping: %d ms | Websocket: %d ms", time, webping).queue());
    }

}
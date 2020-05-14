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
        event.getChannel().sendMessageFormat("Websocket: %d ms", webping).queue();
        for(int i=0; i<4; i++) {
            event.getJDA().getRestPing().queue(
                    (t) -> event.getChannel().sendMessageFormat("Ping: %d ms", t).queue());
        }       
    }

}
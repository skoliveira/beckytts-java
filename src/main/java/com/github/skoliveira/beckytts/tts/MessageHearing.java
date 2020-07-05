package com.github.skoliveira.beckytts.tts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.skoliveira.beckytts.utils.MessageUtil;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

public class MessageHearing {
    private final Object mutex = new Object();  
    private final Message message;

    private String contentResolved = null;
    private String contentHearing = null;

    public MessageHearing(Message message) {
        this.message = message;
    }

    private String getContentResolved(String content) {
        if(contentResolved != null)
            return contentResolved;
        synchronized (mutex) {
            if(contentResolved != null)
                return contentResolved;
            String tmp = content;
            for(TextChannel channel : message.getMentionedChannels()) {
                tmp = tmp.replaceAll(channel.getAsMention(), '#'+channel.getName());
            }
            for(Emote emote : message.getEmotes()) {
                tmp = tmp.replaceAll(emote.getAsMention(), emote.getName());
            }
            for(Role role : message.getMentionedRoles()) {
                tmp = tmp.replaceAll(role.getAsMention(), '@'+role.getName());
            }
            for(Member member : message.getMentionedMembers()) {
                tmp = tmp.replaceAll("<@!?"+Pattern.quote(member.getId())+'>', 
                        Matcher.quoteReplacement(member.getEffectiveName()));
            }
            return contentResolved = tmp;
        }
    }

    public String getContentHearing() {
        if(contentHearing != null)
            return contentHearing;
        synchronized (mutex) {
            if(contentHearing != null)
                return contentHearing;
            String tmp = getContentHearing(message.getContentRaw());

            tmp = MessageUtil.removeLinks(tmp);

            return contentHearing = tmp;
        }
    }

    public String getContentHearing(String content) {
        if(contentHearing != null)
            return contentHearing;

        synchronized (mutex) {
            if(contentHearing != null)
                return contentHearing;

            // all IMentionable entities will be resolved and markdown sanitize
            String tmp = MarkdownSanitizer.sanitize(getContentResolved(content));

            return contentHearing = tmp;
        }
    }
}
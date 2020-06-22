package com.github.skoliveira.beckytts.utils;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;

public class MessageUtil {
    
    private static String resolve(String contentRaw, Message m) {
        String message = contentRaw;
        for(IMentionable i : m.getMentions(Message.MentionType.CHANNEL)) {
            message = message.replaceAll(i.getAsMention(),
                    "#" + m.getGuild().getTextChannelById(i.getIdLong()).getName());
        }
        for(IMentionable i : m.getMentions(Message.MentionType.EMOTE)) {
            message = message.replaceAll("<:(\\S+):"+i.getId()+">", "$1");
        }
        for(IMentionable i : m.getMentions(Message.MentionType.ROLE)) {
            message = message.replaceAll(i.getAsMention(),
                    "@" + m.getGuild().getRoleById(i.getIdLong()).getName());
        }
        for(IMentionable i : m.getMentions(Message.MentionType.USER)) {
            message = message.replaceAll("<@!?"+i.getId()+">",
                    m.getGuild().getMemberById(i.getIdLong()).getEffectiveName());
        }
        return message;
    }
    
    public static String process(Message m) {
        return process(m.getContentRaw(), m);
    }
    
    public static String process(String contentRaw, Message m) {
        String message = resolve(contentRaw, m);
        
        // remove links
        String regexUrl = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)";
        message = message.replaceAll(regexUrl, "");
      
        // remove extra white spaces or tabs
        message = message.replaceAll("[ |\\t][ |\\t]+", " ");
        message = message.replaceAll("\\n[ |\\t]", "\n");
        
        return message;
    }
    
    public static String onomatopoeia(String fun) {
        if(fun.length() < 3)
            return fun;

        StringBuilder sb = new StringBuilder();
        sb.append(fun.substring(0,2));
        for(int i=2; i<fun.length(); i++) {
            if(isOno(fun.substring(i-2,i+1))) {
                if(sb.charAt(sb.length()-2) != ' ' ) {
                    sb.insert(sb.length()-1, ' ');
                }
                sb.append(' ').append(fun.charAt(i));
            }
            else {
                sb.append(fun.charAt(i));
            }
        }
        return sb.toString();
    }
    private static char[] vowels = {'a','ã','e','i','o','u'};
    private static boolean isOno(String s) {
        if(s.length() < 3)
            return false;

        for(char v : vowels) {
            if(v == s.charAt(s.length()-3) && v == s.charAt(s.length()-2) && v == s.charAt(s.length()-1)) {
                return true;
            }
        }
        return false;
    }
}
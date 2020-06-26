package com.github.skoliveira.beckytts.utils;

import java.util.regex.Pattern;

public class MessageUtil {
    // PATTERN table
    // [[url][descriptiom]]
    private static final Pattern ORGMODE_LINK_DESCRIPTION = Pattern.compile("\\[\\[\\S+\\]\\[(.+)\\]\\]");

    // [[url]]
    private static final Pattern ORGMODE_LINK = Pattern.compile("\\[\\[\\S+\\]\\]");

    // ![description](image)
    private static final Pattern MARKDOWN_IMAGE = Pattern.compile("!\\[.+\\]\\(\\S+\\)");

    // [description](url)
    private static final Pattern MARKDOWN_LINK = Pattern.compile("\\[(.+)\\]\\(\\S+\\)");

    // [url description]
    private static final Pattern WIKIA_LINK_DESCRIPTION = Pattern.compile("\\[(https?:)?\\/\\/\\S+\\s(.+)\\]");

    // [url]
    private static final Pattern WIKIA_LINK = Pattern.compile("\\[(https?:)?\\/\\/\\S+\\]");

    // [https://...]
    private static final Pattern LINK_SQUARE_BRACKETED = Pattern.compile("\\[https?:\\/\\/\\S+\\]");

    // (https://...)
    private static final Pattern LINK_BRACKETED = Pattern.compile("\\(https?:\\/\\/\\S+\\)");

    // https://...
    private static final Pattern LINK = Pattern.compile("https?:\\/\\/\\S+");

    // url
    private static final Pattern GENERIC_LINK = Pattern.compile(
            "[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,4}\\b[-a-zA-Z0-9@:%_+.~#=\\/?&;,!]*");


    public static String removeLinks(String content) {
        String tmp = content;

        // DO NOT CHANGE THE ORDER

        // sanitize orgmode links
        tmp = tmp.replaceAll(ORGMODE_LINK_DESCRIPTION.pattern(), "$1");
        tmp = tmp.replaceAll(ORGMODE_LINK.pattern(), "");

        // sanitize markdown links
        tmp = tmp.replaceAll(MARKDOWN_IMAGE.pattern(), "");
        tmp = tmp.replaceAll(MARKDOWN_LINK.pattern(), "$1");

        // sanitize external wikia links
        tmp = tmp.replaceAll(WIKIA_LINK_DESCRIPTION.pattern(), "$2");
        tmp = tmp.replaceAll(WIKIA_LINK.pattern(), "");

        // remove generic links
        tmp = tmp.replaceAll(LINK_SQUARE_BRACKETED.pattern(), "");
        tmp = tmp.replaceAll(LINK_BRACKETED.pattern(), "");
        tmp = tmp.replaceAll(LINK.pattern(), "");
        tmp = tmp.replaceAll("\\["+GENERIC_LINK.pattern()+"\\]", "");
        tmp = tmp.replaceAll("\\("+GENERIC_LINK.pattern()+"\\)", "");
        tmp = tmp.replaceAll(GENERIC_LINK.pattern(), "");

        // remove extra white spaces or tabs
        tmp = tmp.replaceAll("[ \\t][ \\t]+", " ");
        tmp = tmp.replaceAll("\\n[ \\t]", "\n");

        return tmp;
    }


    public static String onomatopoeia(String content) {
        if(content.length() < 3)
            return content;

        StringBuilder sb = new StringBuilder();
        sb.append(content.substring(0,2));
        for(int i=2; i<content.length(); i++) {
            if(isOno(content.substring(i-2,i+1))) {
                if(sb.charAt(sb.length()-2) != ' ') {
                    sb.insert(sb.length()-1, ' ');
                }
                sb.append(' ').append(content.charAt(i));
            }
            else {
                sb.append(content.charAt(i));
            }
        }
        return sb.toString();
    }
    private static char[] vowels = {'a','ã','e','é','i','o','u'};
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
package com.github.skoliveira.beckytts.utils;

public class MessageUtil {
    
    public static String onomatopoeia(String content) {
        if(content.length() < 3)
            return content;

        StringBuilder sb = new StringBuilder();
        sb.append(content.substring(0,2));
        for(int i=2; i<content.length(); i++) {
            if(isOno(content.substring(i-2,i+1))) {
                if(sb.charAt(sb.length()-2) != ' ' ) {
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
package com.github.skoliveira.beckytts.tts;

import java.util.ArrayList;
import java.util.List;

public class gTTS {
    private static final String GOOGLE_TTS_URL =
            "https://translate.google.com/translate_tts";
    private static final int MAX_SIZE = 200;
    private static final char[] END_MARKS = {'.', '!', '?', '\n'};
    private static final char[] PAUSE_MARKS = {':', ';', ','};
    private static final char[] SPACE_MARKS = {' ', '\t'};

    public String[] getTtsUrls(String text) {
        return getTtsUrls(text, "pt");
    }

    public String[] getTtsUrls(String text, String lang) {
        if(text==null | text.isBlank())
            return null;

        List<String> list = splitText(text);
        for(int index=0; index<list.size(); index++) {
            String element = GOOGLE_TTS_URL + "?"
                    + "ie=" + "UTF-8"
                    + "&tl=" + lang
                    + "&q=" + format(list.get(index))
                    + "&client=" + "tw-ob";
            list.set(index, element);
        }
        String[] array = list.toArray(new String[list.size()]);
        return array;
    }

    private List<String> splitText(String text) {
        List<String> list = new ArrayList<String>();
        String sentence;
        for(int i=0; i<text.length(); i+=sentence.length()) {
            if(i+MAX_SIZE < text.length()) {
                sentence = getSentence(text.substring(i, i+MAX_SIZE), END_MARKS);
                if(sentence.length() == MAX_SIZE) {
                    sentence = getSentence(text.substring(i, i+MAX_SIZE), PAUSE_MARKS);
                    if(sentence.length() == MAX_SIZE) {
                        sentence = getSentence(text.substring(i, i+MAX_SIZE), SPACE_MARKS);
                    }
                }
            }
            else {
                sentence = text.substring(i);
            }
            list.add(sentence);
        }
        return list;
    }

    private String getSentence(String str, final char[] marks) {
        for(int i=str.length()-1; i>0; i--) {
            if(isMark(str.charAt(i), marks))
                return str.substring(0, i);
        }
        return str;
    }

    private boolean isMark(char c, final char[] marks) {
        for(char m : marks) {
            if(m == c)
                return true;
        }
        return false;
    }

    private String format(String str) {
        String s = str;
        s = s.replaceAll("%",   "%25");
        s = s.replaceAll("\\s", "%20");
        s = s.replaceAll("\"",  "%22");
        s = s.replaceAll("#",   "%23");
        s = s.replaceAll("&",   "%26");
        s = s.replaceAll("\\+", "%2B");
        s = s.replaceAll("<",   "%3C");
        s = s.replaceAll(">",   "%3E");
        s = s.replaceAll("\\\\","%5C");
        s = s.replaceAll("\\^", "%5E");
        s = s.replaceAll("`",   "%60");
        s = s.replaceAll("\\{", "%7B");
        s = s.replaceAll("\\|", "%7C");
        s = s.replaceAll("\\}", "%7D");
        return s;
    }

}
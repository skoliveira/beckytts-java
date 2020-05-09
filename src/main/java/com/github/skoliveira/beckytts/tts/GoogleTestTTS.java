package com.github.skoliveira.beckytts.tts;

import java.io.IOException;

public class GoogleTestTTS {

	public static void main(String[] args) throws IOException {
	    /*
		String text = "Isso é um teste";
		String url = "";
		GoogleTTS gtts = new GoogleTTS();
		try {
		    gtts.init(text.replaceAll("\\s\\s+", " ").trim(), "pt", false, false);
		    url = gtts.exec();
		    System.out.println(url);
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
		*/
	    String test = "docs.google.com/document/u/0/";
	    String regexUrl = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)";
	    System.out.print(test.replaceAll(regexUrl, "true")); 
	}
}

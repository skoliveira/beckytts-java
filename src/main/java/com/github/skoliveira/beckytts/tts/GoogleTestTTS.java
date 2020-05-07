package com.github.skoliveira.beckytts.tts;

import java.io.IOException;

public class GoogleTestTTS {

	public static void main(String[] args) throws IOException {
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
	}
}

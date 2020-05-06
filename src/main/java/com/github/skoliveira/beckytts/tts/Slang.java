package com.github.skoliveira.beckytts.tts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Slang {
	private Map<String,String> slangs;
	
	public Slang() {
		this.slangs = new HashMap<>();
	}

	public Map<String, String> getSlangs() {
		return slangs;
	}
	
	public Set<String> getKeySet() {
		return slangs.keySet();
	}
	
}
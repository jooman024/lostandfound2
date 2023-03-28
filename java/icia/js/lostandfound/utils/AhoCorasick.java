package icia.js.lostandfound.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;

public class AhoCorasick {
	public static List<String> searchKeywords(String text, List<String> keywords) {
		TrieBuilder builder = Trie.builder();
	    for (String keyword : keywords) {
	        builder = builder.addKeyword(keyword);
	    }
	    Trie trie = builder.build();

	    List<String> matches = new ArrayList<String>();
	    Collection<Emit> emits = trie.parseText(text);
	    for (Emit emit : emits) {
	        matches.add(emit.getKeyword());
	    }
	    return matches;
	}

}

package model;

import java.util.HashSet;
import java.util.Set;

import model.words.Word;

/**
 * This class stores all words and contains methods which operations on stored data
 * 
 * @author Filip K.
 */
public class Dictionary {

	private Set<Word> words;
		
	public Dictionary() {
		words = new HashSet<>();
	}

	public int getNumberOfWords() {
		return words.size();
	}
	
	/**
	 * Add new word to dictionary
	 * @param word
	 * 			you want to add
	 */
	public void addNewWord(Word word) {
		words.add(word);
	}
	
	/**
	 * Checks that the given word exists in the dictionary
	 * @param word
	 * 			you want to check
	 * @return
	 * 		true if word exists, false otherwise
	 */
	public boolean isWordExist(Word word) {
		return words.contains(word);
	}
	
}

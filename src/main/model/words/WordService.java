package model.words;

/**
 * Service class that makes operations on words
 * @author Filip
 *
 */
public class WordService {

	private Dictionary dictionary;
	private WordRepository wordRepository;
	
	public WordService(Dictionary dictionary, WordRepository wordRepository) {
		this.dictionary = dictionary;
		this.wordRepository = wordRepository;
	}

	public boolean saveReportedWord(String word, long player_id) {
		return wordRepository.saveReportedWord(word, player_id);
	}
	
	public void addWord(Word word) {
		dictionary.addNewWord(word);
	}
	
	public boolean validateWord(Word word, String letter) {
		return dictionary.isWordExist(word)
				&& word.getValue().toUpperCase().startsWith(letter);
	}
	
	public int getNumberOfWords() {
		return dictionary.getNumberOfWords();
	}
}

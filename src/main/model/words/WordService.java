package model.words;

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
	
	public boolean wordValidate(Word word) {
		return dictionary.isWordExist(word);
	}
	
	public int getNumberOfWords() {
		return dictionary.getNumberOfWords();
	}
}

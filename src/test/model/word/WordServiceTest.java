package model.word;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.words.Dictionary;
import model.words.Name;
import model.words.WordRepository;
import model.words.WordService;

public class WordServiceTest {

	private static final String WORD_NAME = "Ala";

	@Mock
	private WordRepository wordRepository;

	private Dictionary dictionary;
	private WordService wordService;

	private Name name;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		dictionary = new Dictionary();
		wordService = new WordService(dictionary, wordRepository);

		name = new Name(WORD_NAME);
	}

	@Test
	public void addWord() {
		wordService.addWord(name);
		assertTrue(dictionary.isWordExist(name));
	}

	@Test
	public void wordNotExist() {
		assertFalse(dictionary.isWordExist(name));
	}

	@Test
	public void saveReportedWord() {
		when(wordRepository.saveReportedWord(WORD_NAME, 1)).thenReturn(true);
		assertTrue(wordService.saveReportedWord(WORD_NAME, 1));
	}
}

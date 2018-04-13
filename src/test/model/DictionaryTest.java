package model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

import model.words.City;
import model.words.Country;
import model.words.Word;

public class DictionaryTest {

	private static final String POLAND = "Poland";
	private static final String POLAND_LC = "poland";
	
	private Dictionary dictionary;
	
	private Word country;
	private Word city;
	
	@Before
	public void before() {
		dictionary = new Dictionary();
		country = new Country(POLAND);
		city = new City("Warsaw");
	}
	
	@Test
	public void addNewWord() {
		dictionary.addNewWord(country);
		dictionary.addNewWord(city);
		assertEquals(2, dictionary.getNumberOfWords());
	}
	
	@Test
	public void wordExist() {
		dictionary.addNewWord(country);
		assertTrue(dictionary.isWordExist(new Country(POLAND)));
	}
	
	@Test
	public void wordExistIgnoreCase() {
		dictionary.addNewWord(country);
		assertTrue(dictionary.isWordExist(new Country(POLAND_LC)));
	}
	
	@Test
	public void wordNotExist() {
		dictionary.addNewWord(country);
		dictionary.isWordExist(new Country("Wars"));
	}
	
}

package utils;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import model.DataType;
import model.Dictionary;
import model.words.City;
import model.words.Country;

public class DataReaderTest {

	private static final String FILE_NAME_COUNTRY = "src/test/resources/test_country.txt";
	private static final String FILE_NAME_CITY = "src/test/resources/test_city.txt";
	
	private Dictionary dictionary;
	private DataReader dataReader;
	
	private Country poland;
	private Country usa;
	private City warsaw;
	private City losAngeles;
	
	@Before
	public void beforeMethod() {
		dictionary = new Dictionary();
		dataReader = new DataReader(dictionary);
		
		poland = new Country("Polska");
		usa = new Country("USA");
		warsaw = new City("Warszawa");
		losAngeles = new City("Los Angeles");
	}
	
	@Test
	public void shouldAddCountryToDictionary() {
		dataReader.readDataFromFile(FILE_NAME_COUNTRY, DataType.COUNTRY);
		assertThat(dictionary.getNumberOfWords(), is(2));
		assertTrue(dictionary.isWordExist(poland));
		assertTrue(dictionary.isWordExist(usa));
	}
	
	@Test
	public void shouldAddCityToDictionary() {
		dataReader.readDataFromFile(FILE_NAME_CITY, DataType.CITY);
		assertThat(dictionary.getNumberOfWords(), is(2));
		assertTrue(dictionary.isWordExist(warsaw));
		assertTrue(dictionary.isWordExist(losAngeles));
	}
}

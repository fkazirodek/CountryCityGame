package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import model.DataType;
import model.Dictionary;
import model.words.Animal;
import model.words.City;
import model.words.Country;
import model.words.Name;
import model.words.Word;

/**
 * This class contains methods that allow you to read data from files and write them in the
 * dictionary which provided in the class constructor
 * 
 * @author Filip K.
 *
 */
public class DataReader {

	private Dictionary dictionary;

	/**
	 * Constructor DataReader, you must provide Dictionary in which you want to store the loaded data
	 * @param dictionary
	 * 				there your data will be store
	 */
	public DataReader(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * This method reads the data from the file passed as an argument and writes the
	 * data as words in the dictionary which was passed in the constructor. Data
	 * pattern in the file: each line in the file should represent one value
	 * (object) e.g. name of Country or City
	 * 
	 * @param pathname
	 *            must not be null
	 * @param dataType
	 *            the type of data stored in the file
	 */
	public void readDataFromFile(String pathname, DataType dataType) {
		List<String> words = null;

		try {
			words = Files.readAllLines(new File(pathname).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		words.stream()
				.map(String::trim)
				.map(word -> createWord(word, dataType))
				.forEach(dictionary::addNewWord);

	}

	/**
	 * This private method creates a new object based on the data type and the word 
	 * passed as method parameters. The data type affects the type of object that 
	 * will be created and returned
	 * @param name
	 * 			from which the object will be create
	 * @param dataType
	 * 			type of object
	 * @return
	 * 			new Word object
	 */
	private Word createWord(String name, DataType dataType) {
		switch (dataType) {
		case COUNTRY:
			return new Country(name);
		case CITY:
			return new City(name);
		case ANIMAL:
			return new Animal(name);
		case NAME:
			return new Name(name);
		default:
			return null;
		}
	}
}

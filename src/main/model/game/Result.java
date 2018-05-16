package model.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores player's results
 * @author Filip
 *
 */
public class Result {

	private String player;
	private Map<String, String> values;
	
	public Result() {
		values = new HashMap<>();
	}
	
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	
	public String addValue(String key, String value) {
		return values.put(key, value);
	}
	
	public String getValue(String key) {
		return values.get(key);
	}
	
	public Map<String, String> getResults() {
		return values;
	}
}

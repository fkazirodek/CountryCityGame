package model.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Game {

	private static AtomicLong atomicLong = new AtomicLong();
	private static Map<Integer, Character> letters = new HashMap<>(); 
	
	private long id;
	private Character letter;
	private Map<String, Result> results;
	
	public Game() {
		id = atomicLong.getAndIncrement();
		results = new HashMap<>();
		if(letters.isEmpty())
			initLetters();
		letter = getRandomLetter();
	}
	
	public long getId() {
		return id;
	}
	
	public Character getLetter() {
		return letter;
	}

	public void setLetter(Character letter) {
		this.letter = letter;
	}

	public String getPlayer(int index) {
		Set<String> keySet = results.keySet();
		String[] players = keySet.toArray(new String[keySet.size()]);
		return players[index];
	}
	
	public Optional<String> getPlayer(String login) {
		return results
				.keySet()
				.stream()
				.filter(p -> p.equals(login))
				.findFirst();
	}
	
	public Result getResult(String player) {
		return results.get(player);
	}

	public Result addPlayer(String player) {
		return results.put(player, null);
	}
	
	public Result addResult(String player, Result result) {
		return results.put(player, result);
	}
	
	public int getPlayerSize() {
		return results.size();
	}
	
	public void forEachPlayer(Consumer<String> action) {
		results.keySet().forEach(action);
	}
	
	public Set<Entry<String,Result>> getEntrySet() {
		return results.entrySet();
	}
	
	public boolean isFinished() {
		return results
					.entrySet()
					.stream()
					.allMatch(e -> e.getValue() != null);
	}
	
	private static Character getRandomLetter() {
		Random random = new Random();
		int randomNum = random.nextInt(25);
		return letters.get(randomNum);
	}
	
	private static void initLetters() {
		letters.put(0, 'A');
		letters.put(1, 'B');
		letters.put(2, 'C');
		letters.put(3, 'D');
		letters.put(4, 'E');
		letters.put(5, 'F');
		letters.put(6, 'G');
		letters.put(7, 'H');
		letters.put(8, 'I');
		letters.put(9, 'J');
		letters.put(10, 'K');
		letters.put(11, 'L');
		letters.put(12, 'M');
		letters.put(13, 'N');
		letters.put(14, 'O');
		letters.put(15, 'Ó');
		letters.put(16, 'P');
		letters.put(17, 'R');
		letters.put(18, 'S');
		letters.put(19, 'T');
		letters.put(20, 'U');
		letters.put(21, 'W');
		letters.put(22, 'X');
		letters.put(23, 'Y');
		letters.put(24, 'Z');
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (id != other.id)
			return false;
		return true;
	}

}

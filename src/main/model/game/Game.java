package model.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * This class stores players and their game results. 
 * Class is also responsible for generating a random letter
 * @author Filip
 *
 */
public class Game {

	private static AtomicLong atomicLong = new AtomicLong();
	private static Map<Integer, Character> letters = new HashMap<>(); 
	
	private long id;
	private Character letter;
	private Map<String, Result> results;
	private AtomicInteger countResults;
	
	public Game() {
		id = atomicLong.getAndIncrement();
		results = new ConcurrentHashMap<>();
		countResults = new AtomicInteger();
		if(letters.isEmpty())
			initLetters();
		letter = getRandomLetter();
	}
	
	public long getId() {
		return id;
	}
	
	public synchronized Character getLetter() {
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
	
	public boolean containsPlayer(String login) {
		return results.containsKey(login);
	}
	
	public Result getResult(String player) {
		return results.get(player);
	}

	public Result addPlayer(String player) {
		return results.put(player, new Result());
	}
	
	public Result addResult(String player, Result result) {
		countResults.incrementAndGet();
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
	
	/**
	 * The game is over if all players' answers are present
	 * @return true if all players finished the game, false otherwise
	 */
	public boolean isFinished() {
		return countResults.get() == results.size();
	}
	
	public String getWinner() {
		String player_1 = getPlayer(0);
		String player_2 = getPlayer(1);
		Integer result_1 = Integer.valueOf(getResult(player_1).getValue("points"));
		Integer result_2 = Integer.valueOf(getResult(player_2).getValue("points"));
		return result_1 == result_2 ? "draw" : (result_1 > result_2 ? player_1 : player_2);
	}
	
	private Character getRandomLetter() {
		Random random = new Random();
		int randomNum = random.nextInt(25);
		return letters.get(randomNum);
	}
	
	private void initLetters() {
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
		letters.put(15, 'P');
		letters.put(16, 'R');
		letters.put(17, 'S');
		letters.put(18, 'T');
		letters.put(19, 'U');
		letters.put(20, 'W');
		letters.put(21, 'Y');
		letters.put(22, 'Z');
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

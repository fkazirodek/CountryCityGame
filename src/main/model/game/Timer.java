package model.game;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * This Timer class is responsible for the countdown of time
 * @author 
 *
 */
public class Timer {

	private AtomicInteger counter = new AtomicInteger(30);
	private Timeline timeline;
	
	/**
	 * Starts counting down the time from the initial value (default = 30) to 0. When it reaches zero the timer is stopped
	 * @param consumer passes the expression for which the timing is to be applied
	 */
	public void startTimer(Consumer<String> consumer) {
		timeline = new Timeline(
				new KeyFrame(Duration.seconds(1), 
							 e -> {
								 if (counter.get() == 0) {
									 timeline.stop();
									 counter.set(30);
								 }
								 consumer.accept(String.valueOf(counter.getAndDecrement()));
							 })
		);
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	/**
	 * Get current timer value
	 * @return int current value
	 */
	public int getCounterValue() {
		return counter.get();
	}
	
	/**
	 * Set initial timer value
	 * @param newValue
	 */
	public void setNewCounerValue(int newValue) {
		counter.set(newValue);
	}
}

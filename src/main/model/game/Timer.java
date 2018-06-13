package model.game;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Timer {

	private AtomicInteger counter = new AtomicInteger(30);
	private Timeline timeline;
	
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
	
	public int getCounterValue() {
		return counter.get();
	}
	
	public void setNewCounerValue(int newValue) {
		counter.set(newValue);
	}
}

package ch.pschatzmann.news.processing;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.news.Main;

/**
 * TimerTask and Timer to schedule a repeating action
 * 
 * @author pschatzmann
 *
 */
public class FixedRateTimer implements Serializable {
	private static Logger log = LoggerFactory.getLogger(FixedRateTimer.class);

	public static void schedule(Runnable f, long rate) {
		schedule(f, 0, rate);
	}

	public static void schedule(Runnable f, long delay, long rate) {
		// setup TimerTask with runnable
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				f.run();
				log.info("end");
			}
		};

		// running timer task as daemon thread
		Timer timer = new Timer(true);		
		timer.scheduleAtFixedRate(timerTask, delay, rate);
		log.info("scheduled");

	}

}

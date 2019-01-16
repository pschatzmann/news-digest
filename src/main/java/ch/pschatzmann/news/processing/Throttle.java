package ch.pschatzmann.news.processing;

import java.io.Serializable;

/**
 * Limits the number of calls using the defined number of milliseconds. The New York Times
 * only allows 1 call per second!
 * 
 * @author pschatzmann
 *
 */

public class Throttle implements Serializable {
	private long ms;
	private Long nextTime = null;
	
	public Throttle(int ms){		
		this.ms = ms;
	}
	
	public void throttle() {
		if (nextTime!=null) {
			while (System.currentTimeMillis()<nextTime) {
				try {
					Thread.sleep(ms / 10);
				} catch (InterruptedException e) {
				}
			}
		}
		nextTime = System.currentTimeMillis()+ms;		
	}
}

package ch.pschatzmann.news;

import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.news.processing.FixedRateTimer;
import ch.pschatzmann.news.processing.RestPagedQuery;

/**
 * Paged query for the New York times Search API
 * 
 * @author pschatzmann
 *
 */
public class NYTPagedQuery extends RestPagedQuery {
	private static Logger log = LoggerFactory.getLogger(FixedRateTimer.class);

	public NYTPagedQuery(WebTarget web) {
		super(web, "response", "docs", "", "page");
	}

	/**
	 * Calculate the number of pages. On each page there are 10 entries.
	 */
	protected void setupPages(JsonObject responseJson) {
		if (this.getPage() == 1) {
			JsonObject meta = responseJson.getJsonObject("meta");
			Integer hits = meta.getInt("hits");
			long pages = (long) Math.ceil(hits.doubleValue() / 10.0);
			this.setPages(pages);
			log.info("Setting pages to: {}", pages);
		}
	}
	
	public int getThrottleTimeMS() {
		return 1000;
	}


}

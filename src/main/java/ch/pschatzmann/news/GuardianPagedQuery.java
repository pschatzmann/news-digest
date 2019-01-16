package ch.pschatzmann.news;

import javax.ws.rs.client.WebTarget;

import ch.pschatzmann.news.processing.RestPagedQuery;

/**
 * Paged query for the Guardian Search API
 * @author pschatzmann
 *
 */
public class GuardianPagedQuery extends RestPagedQuery {
	public GuardianPagedQuery(WebTarget web){
		super(web,"response","results","pages","page");
	}
	
	public int getThrottleTimeMS() {
		return 500;
	}

}

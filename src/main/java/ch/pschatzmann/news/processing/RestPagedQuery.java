package ch.pschatzmann.news.processing;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebTarget which is returning as stream of JsonObject objects.
 * This class supports paging: Iterator and Iterable are used for the processing of pages; 
 * The returned stream contains all pages!
 * 
 * @author pschatzmann
 *
 */
public class RestPagedQuery implements Iterator<JsonArray>, Iterable<JsonArray>, Serializable {
	private static Logger log = LoggerFactory.getLogger(RestPagedQuery.class);
	private WebTarget web;
	private long page = 1;
	private long pages = 1;
	private String responseTagName="response";
	private String resultTagName="results";
	private String pagesTagName="pages";
	private String pageTagName="page";
	private Throttle throttle = new Throttle(getThrottleTimeMS());
	/**
	 * Constructor which supports paging
	 * @param web
	 * @param responseTagName
	 * @param resultTagName
	 * @param pagesTagName
	 * @param pageTagName
	 */
	public RestPagedQuery(WebTarget web, String responseTagName, String resultTagName, String pagesTagName, String pageTagName) {
		this.web = web;
		this.responseTagName = responseTagName;
		this.resultTagName = resultTagName;
		this.pagesTagName = pagesTagName;
		this.pageTagName = pageTagName;
	}

	/**
	 * Constructor w/o paging
	 * @param web
	 * @param responseTagName
	 * @param resultTagName
	 */
	public RestPagedQuery(WebTarget web, String responseTagName, String resultTagName) {
		this.web = web;
		this.responseTagName = responseTagName;
		this.resultTagName = resultTagName;
		this.pagesTagName = "";
		this.pageTagName = "";
	}
	
	@Override
	public boolean hasNext() {
		return page  <= pages;
	}

	@Override
	public JsonArray next() {
		JsonArray array = Json.createArrayBuilder().build();
		try {
			log.info("Processing page {}", page);
			JsonObject responseJson = restCall().getJsonObject(responseTagName);
			if (responseJson != null) {
				array = responseJson.getJsonArray(resultTagName);
				setupPages(responseJson);
			} 
			page++;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return array;
	}

	protected long getPage() {
		return page;
	}

	protected void setPage(long page) {
		this.page = page;
	}

	protected long getPages() {
		return pages;
	}

	protected void setPages(long pages) {
		this.pages = pages;
	}

	protected void setupPages(JsonObject responseJson) {
		if (!pagesTagName.isEmpty()) {
			pages = responseJson.getInt(pagesTagName);
		}
	}
	
	/**
	 * Execute the rest call. If we support paging we incect the page number
	 * @return
	 */
	protected JsonObject restCall() {
		throttle.throttle();
		JsonObject response=null;
		if (pageTagName.isEmpty()) {
			response = web.request().get(JsonObject.class);
		} else {
			response = web.queryParam(pageTagName, "" + page).request().get(JsonObject.class);
		}
		return response;
	}

	@Override
	public Iterator<JsonArray> iterator() {
		return this;
	}

	/**
	 * Returns the result as stream
	 * 
	 * @return
	 */
	public Stream<JsonObject> stream() {
		return StreamSupport.stream(this.spliterator(), false)
			.flatMap(array -> array.getValuesAs(JsonObject.class).stream());
	}
	
	/**
	 * Provides the waiting time in ms between calls. Per default there is no waiting time.
	 * @return
	 */
	protected int getThrottleTimeMS() {
		return 0;
	}


}

package ch.pschatzmann.news;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import ch.pschatzmann.news.processing.FixedRateTimer;

/**
 * Reads single RSS and saves the data to the SolrStore
 * 
 * @author pschatzmann
 *
 */
public class RSSSingleData implements IData {
	private URL urlFeed;
	private IDocumentStore store;
	private String publisher;
	
	
	public RSSSingleData(IDocumentStore store, String publisher, String urlFeed) {
		try {
			this.urlFeed = new URL(urlFeed);
			this.store = store;
			this.publisher = publisher;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void save() {
		getFeed().getEntries().forEach(entry -> store.add(getDocument(entry)));						
	}

	protected SyndFeed getFeed() {
		try {
			SyndFeedInput input = new SyndFeedInput();
			return input.build(new XmlReader(urlFeed));
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	protected Document getDocument(SyndEntry entry) {
		String author = entry.getAuthor();
		List<String> categories = entry.getCategories().stream().map(sc -> sc.getName()).collect(Collectors.toList());
		StringBuffer content = new StringBuffer();
		entry.getContents().stream().forEach(c -> content.append(c.getValue()));
		Date date = entry.getPublishedDate();
		String link = entry.getLink();				
		Document headline = new Document();
		
		headline.setPublisher_t(publisher);
		headline.setAuthor_t(author);
		headline.setLink_t(link);
		headline.setPublishDate_t(date);
		headline.setKeys(categories);
		headline.setContent_t(entry.getTitle());
		return headline;
	}
	
	/**
	 * Schedules to download and save the data at the indicated intervals
	 */
	@Override
	public void scheduleSave(long scheduleMs) {
		FixedRateTimer.schedule(() -> this.save(), scheduleMs);		
	}
}

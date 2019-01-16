package ch.pschatzmann.news.test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import ch.pschatzmann.news.Document;
import ch.pschatzmann.news.IData;
import ch.pschatzmann.news.IDocumentStore;
import ch.pschatzmann.news.RSSSingleData;

/**
 * Tests for the RSSSingleData
 * 
 * @author pschatzmann
 *
 */
public class TestRSS implements IDocumentStore {
	private int count;
	
	@Test
	public void testRss() {
		IData data = new RSSSingleData(this,"nytimes"," http://rss.nytimes.com/services/xml/rss/nyt/Technology.xml");
		data.save();
		Assert.assertTrue(count > 0);
	}

	@Override
	public void add(Document headline) {
		System.out.println("add "+headline.getId());
		Assert.assertNotNull(headline.getContent_t());
		Assert.assertNotNull(headline.getAuthor_t());
		Assert.assertNotNull(headline.getId());
		Assert.assertNotNull(headline.getLink_t());
		Assert.assertNotNull(headline.getPublishDate_t());
		Assert.assertNotNull(headline.getKeys());
		Assert.assertNotNull(headline.getPublishDate_t());

		Assert.assertEquals("nytimes", headline.getPublisher_t());
		count++;
	}

	@Override
	public boolean contains(Map<String, String> criteria) {
		return false;
	}

	@Override
	public boolean contains(String id) {
		return false;
	}

	@Override
	public Stream<Document> stream(String q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<Document> stream(Map<String, String> criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Document> list(Map search, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}

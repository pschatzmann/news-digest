package ch.pschatzmann.news.test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import ch.pschatzmann.news.Document;
import ch.pschatzmann.news.IData;
import ch.pschatzmann.news.IDocumentStore;
import ch.pschatzmann.news.HistoryDataNYT;
import ch.pschatzmann.news.RSSSingleData;
import ch.pschatzmann.news.SolrDocumentStore;

/**
 * Tests for NYTHistoryData
 * 
 * @author pschatzmann
 *
 */
public class TestNYT implements IDocumentStore {
	private int count;
	
	@Ignore
	@Test
	public void testNYT() {
		HistoryDataNYT data = new HistoryDataNYT(this);
		data.save(2018,01);
		System.out.println("Count = "+count);
		Assert.assertTrue(count > 0);
	}
	


	@Override
	public void add(Document headline) {
		//System.out.println("add "+headline.getId());
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
		return Stream.empty();
	}

	@Override
	public Stream<Document> stream(Map<String, String> criteria) {
		return Stream.empty();
	}

	@Override
	public List<Document> list(Map search, int i) {
		return Collections.emptyList();
	}
}

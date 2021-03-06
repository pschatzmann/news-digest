package ch.pschatzmann.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.CloudSolrStream;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.news.processing.PagedDocuments;
import ch.pschatzmann.news.processing.SolrPagedQuery;
import ch.pschatzmann.news.processing.SolrTupleStream;

/**
 * Access to Solr Repository to store and search for Headlines
 * 
 * @author pschatzmann
 *
 */
public class SolrDocumentStore implements IDocumentStore {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(SolrDocumentStore.class);
	private transient SolrClient client;
	private Set<String> ids = new HashSet();
	private String coreName = Utils.property("solrCoreName", "mycore");
	private int pageSize = 10000;

	/**
	 * Simple Constructor
	 */
	public SolrDocumentStore() {
	}

	/**
	 * Constructor which overrides the default parameters
	 * 
	 * @param coreName
	 * @param urlString
	 */
	public SolrDocumentStore(String coreName, String urlString) {
		this.coreName = coreName;
	}

	/**
	 * Add a collection of new documents
	 * 
	 * @param headlines
	 */
	public void add(Collection<Document> headlines) {
		headlines.forEach(hl -> add(hl));
	}

	/**
	 * Add a new document
	 * 
	 * @param doc
	 */
	public synchronized void add(Document doc) {
		try {
			log.info("Add {} ({})", doc.getId(), doc.getPublishDate_t());
			if (!ids.contains(doc.getId())) {
				final UpdateResponse response = getSolrClient().addBean(coreName, doc, 10000);
				getSolrClient().commit(coreName);
				ids.add(doc.getId());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * List all exiting documents which match the indicated query parameter
	 * 
	 * @param q
	 * @return
	 */
	public List<Document> list(String q, int rowCount) {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", q);
		return list(queryParamMap, rowCount);
	}

	/**
	 * List all existing documents which match the indicated search parameter
	 * 
	 * @param search
	 * @param rowCount
	 * @return
	 */
	public List<Document> list(Map<String, String> search, int rowCount) {
		List<Document> result = new ArrayList();
		try {
			if (!search.containsKey("df")) {
				search.put("df", "content_t");
			}
			if (!search.containsKey("rows")) {
				search.put("rows", "" + rowCount);
			}
			if (!search.containsKey("sort")) {
				search.put("sort", "id asc");
			}

			MapSolrParams queryParams = new MapSolrParams(search);
			QueryResponse response = getSolrClient().query(coreName, queryParams);
			result = response.getBeans(Document.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Solr Streaming API
	 * @param q
	 * @return
	 * @throws IOException
	 */
	public Stream<Tuple> tuples(String q) throws IOException {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", q);
		return tuples(queryParamMap);
	}

	/**
	 * Solr Streaming API
	 * @param search
	 * @return
	 * @throws IOException
	 */
	public Stream<Tuple> tuples(Map<String, String> search) throws IOException {
		search.put("qt", "/export");
		if (search.get("fl")==null) {
			search.put("fl", "publishDate_t, content_t, id");
		}
		if (!search.containsKey("sort")) {
			search.put("sort", "id asc");
		}
		CloudSolrStream cstream = new CloudSolrStream(this.getSolrURL(), this.getCoreName(),new MapSolrParams(search));
		return new SolrTupleStream(cstream).stream();
	}
	

	/**
	 * Returns the query result as stream which is iterating over all solr pages
	 * 
	 * @param search
	 * @return
	 */
	public Stream<Document> stream(Map<String, String> search) {
		if (!search.containsKey("df")) {
			search.put("df", "content_t");
		}
		if (!search.containsKey("sort")) {
			search.put("sort", "id asc");
		}
		Stream<QueryResponse> targetStream = new SolrPagedQuery(this, search, coreName, pageSize).stream();
		return targetStream.flatMap(qr -> qr.getBeans(Document.class).stream());
	}

	/**
	 * Returns the query result as stream which is iterating over all solr pages
	 * 
	 * @param q
	 * @return
	 */
	public Stream<Document> stream(String q) {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", q);
		return stream(queryParamMap);
	}

	/**
	 * Access to paged document which provides the document numbers and individual
	 * pages
	 * 
	 * @param search
	 * @return
	 */
	public List<PagedDocuments> pagedDocuments(Map<String, String> search) {
		if (!search.containsKey("df")) {
			search.put("df", "content_t");
		}
		if (!search.containsKey("sort")) {
			search.put("sort", "id asc");
		}
		List<PagedDocuments> result = new SolrPagedQuery(this, search, coreName, pageSize).pages();
		log.info("Numbe of pages: {}", result.size());
		return result;
	}

	/**
	 * Access to paged document which provides the document numbers and individual
	 * pages
	 * 
	 * @param q
	 * @return
	 */
	public List<PagedDocuments> pagedDocuments(String q) {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", q);
		return pagedDocuments(queryParamMap);
	}

	/**
	 * Determine the number of records
	 * 
	 * @param search
	 * @return
	 */
	public long count(Map<String, String> search) {
		if (!search.containsKey("df")) {
			search.put("df", "content_t");
		}
		search.put("rows", "0");
		long result = new SolrPagedQuery(this, search, coreName, pageSize).count();
		return result;
	}

	/**
	 * Determine the number of records
	 * 
	 * @param q
	 * @return
	 */
	public long count(String q) {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", q);
		return count(queryParamMap);
	}

	/**
	 * Checks if the query contains any data
	 * 
	 * @param queryParamMap
	 * @return
	 */
	public boolean contains(Map<String, String> queryParamMap) {
		try {
			queryParamMap.put("count", "1");
			MapSolrParams queryParams = new MapSolrParams(queryParamMap);
			QueryResponse response = getSolrClient().query(coreName, queryParams);
			return response.getResults().getNumFound() > 0;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Checks if the query contains any data
	 */
	@Override
	public boolean contains(String q) {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", q);
		return contains(queryParamMap);
	}

	/**
	 * Determines the Internal page size used by the stream interface. The default
	 * is 10000 records
	 * 
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Defines the Internal page size used by the stream interface
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns a SolrClient
	 * 
	 * @return
	 */
	public SolrClient getSolrClient() {
		if (client == null) {
			String urlString = getSolrURL();
			this.client = new HttpSolrClient.Builder(urlString).build();
		}
		return client;
	}

	/**
	 * Determines the collection name
	 * 
	 * @return
	 */
	public String getCoreName() {
		return coreName;
	}

	/**
	 * Determines the URL to Solr
	 * 
	 * @return
	 */
	public String getSolrURL() {
		return Utils.property("solrURL", "http://nuc.local:8983/solr");
	}

}

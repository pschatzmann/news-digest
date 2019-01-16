package ch.pschatzmann.news;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Store which supports the storing and searching of documents
 * 
 * @author pschatzmann
 *
 */
public interface IDocumentStore extends Serializable{

	void add(Document headline);

	boolean contains(Map<String, String> criteria);

	boolean contains(String id);

	public Stream<Document> stream(String q);

	public Stream<Document> stream(Map<String, String> criteria);

	public List<Document> list(Map<String,String> search, int i);

}

package ch.pschatzmann.news.utils;

import java.io.Serializable;

import ch.pschatzmann.news.Document;

/**
 * Generates a reproducible key from the document content
 * 
 * @author pschatzmann
 *
 */
public interface IKeyGenerator extends Serializable {
	public String getKey(Document doc);

}

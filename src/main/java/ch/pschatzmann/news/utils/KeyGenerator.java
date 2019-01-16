package ch.pschatzmann.news.utils;

import ch.pschatzmann.news.Document;

/**
 * Generates the document id from the publish date + hash
 * @author pschatzmann
 *
 */
public class KeyGenerator implements IKeyGenerator {

	public String getKey(Document doc) {
		String date = doc.getPublishDate_t();
		date = date.replaceAll("-", "");
		date = date.replaceAll(" ", "");
		date = date.replaceAll(":", "");
		date = date.replaceAll(":", "");
		StringBuffer sb = new StringBuffer(date);
		int hash = doc.getContent_t().hashCode();
		if (hash>=0) {
			sb.append("+");
		}
		sb.append(hash);
		return sb.toString();
	}
	
}

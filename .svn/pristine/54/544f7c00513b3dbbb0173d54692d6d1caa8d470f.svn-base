package ch.pschatzmann.news.utils;

import java.io.Serializable;

import org.deeplearning4j.text.corpora.sentiwordnet.SWN3;

import ch.pschatzmann.news.Utils;

/**
 * Classify into positive, neutral and negative. If the classification fails we return n/a
 * 
 * @author pschatzmann
 *
 */
public class AnalyseSentiment implements Serializable {
	private SWN3 svn3;

	public String process(String txt) {
		try {
			if (Utils.isEmpty(txt)) {
				return "neutral";
			}
			if (svn3 == null) {
				svn3 = new SWN3();
			}
			return svn3.classify(txt);
		} catch (Exception e) {
			return "n/a";
		}
	}
}

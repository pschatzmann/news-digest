package ch.pschatzmann.news.utils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 * Determine Named Entities with the help of OpenNLP
 * 
 * @author pschatzmann
 *
 */
public class AnalyseNamedEntity implements Serializable{
	private TokenizerME tokenizer;
	private NameFinderME nameFinder;

	public AnalyseNamedEntity()  {
	}

	public synchronized Collection<String> process(String sentence) {
		if (tokenizer==null) setup();
		String tokens[] = tokenizer.tokenize(sentence);
		Span spans[] = nameFinder.find(tokens);
		List<String> list = new ArrayList();
		for (Span s : spans)
			list.add(tokens[s.getStart()]);
		
		return list;
	}

	protected void setup() {
		try {
			InputStream inputStreamTokenizer = this.getClass().getResourceAsStream("/en-token.bin");
			InputStream inputStreamNameFinder = this.getClass().getResourceAsStream("/en-ner-organization.bin");
			tokenizer = new TokenizerME(new TokenizerModel(inputStreamTokenizer));
			nameFinder = new NameFinderME(new TokenNameFinderModel(inputStreamNameFinder));
			inputStreamTokenizer.close();
			inputStreamNameFinder.close();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}

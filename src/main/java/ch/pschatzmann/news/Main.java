package ch.pschatzmann.news;

import java.io.IOException;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.news.processing.FixedRateTimer;

/**
 * Loads the History data from the NWT and the RSS entries defined in the feeds.csv file 
 * @author pschatzmann
 *
 */
public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);
	private static String solrCore = Utils.property("solrCore","mycore");
	private static String solrUrl = Utils.property("solrUrl","http://nuc.local:8983/solr");
	
	
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
		log.info("Document import has been started");
		SolrDocumentStore store = new SolrDocumentStore(solrCore, solrUrl);

		try {
		// save RSS every hour		
			new RSSData(store).scheduleSave( 1000L * 60L * 60L * 1L);
		} catch(Exception ex) {
			log.error(ex.getMessage());
		}

		try {
		// save Guaridan every day
			new HistoryDataGuardian(store).scheduleSave( 1000L * 60L * 60L * 24L);
		} catch(Exception ex) {
			log.error(ex.getMessage());
		}

		try {
		// save NYT history every day
			new HistoryDataNYT(store).scheduleSave(1000L * 60L * 60L * 24L);	
		} catch(Exception ex) {
			log.error(ex.getMessage());
		}

		log.info("The application is running...");
		
		Utils.waitForEver();
	}


}

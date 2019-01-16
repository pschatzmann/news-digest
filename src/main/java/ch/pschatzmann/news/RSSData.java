package ch.pschatzmann.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.news.processing.FixedRateTimer;

/**
 * Loads a list of RSS sources from a CSV url resource
 * 
 * @author pschatzmann
 *
 */
public class RSSData implements IData {
	private static Logger log = LoggerFactory.getLogger(RSSData.class);
	private List<RSSSingleData> list;
	private IDocumentStore store;
	
	public RSSData(IDocumentStore store, String fileName)  {
		this.store = store;
		InputStream is=null;
		try {
			is = Utils.getInputStream(fileName);
			if (is==null) {
				String file = Utils.property("rssSourceFile","/feeds.csv");
				log.info("Using rssSourceFile: {}",file);
				is = Utils.getInputStream(file);
			}
			if (is!=null) {
				list = Utils.readLines(is).stream()
				.parallel()
				.map(line -> line.split(","))
				.filter(sa -> sa.length==2)
				.map(sa -> new RSSSingleData(store,sa[0],sa[1])).collect(Collectors.toList());	
			} else {
				log.warn("Could not find any input for RSSData");
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (is!=null) is.close();
			} catch(Exception ex) {}
		}
	}
	
	public RSSData(IDocumentStore store)  {
		this(store, null);		
	}

	
	@Override
	public void save() {
		if (list!=null) {
			list.forEach(r -> r.save());
		}
	}
	
	/**
	 * Schedules to download and save the data at the indicated intervals
	 */
	@Override
	public void scheduleSave(long scheduleMs) {
		FixedRateTimer.schedule(() -> this.save(), scheduleMs);		
	}


}

Recently I have spent some time to investigate the options to access the history of news articles via an API. I was mainly interested in APIs which can be accessed free of charge.

Here is the list of the most useful providers:
<ul>
 	<li><strong>Guardian</strong>
- Easy API
- Acceptable Rate Limits
- Access to over 1,900,000 pieces of content
- Free for non-commercial usage</li>
 	<li><strong>New York Times</strong>
- Provides API to search and separate API to download monthly data
- Rate Limits are quickly reached in the search API
- Provides data since 1851
- Free for non-commercial usage</li>
 	<li><strong>RSS</strong>
- Many Free Sources
- Very limited History</li>
</ul>
As a conclusion, I was ending up with an architecture which
<ul>
 	<li>replicates the data sources into a Local Search engine (<a href="http://lucene.apache.org/solr/">Solr</a>)</li>
 	<li>provides some Utility classes to simplify different scenarios</li>
</ul>
In <a href="https://nbviewer.jupyter.org/gist/pschatzmann/056a8305a8823f7d0b5c057d07d8d76f">this Gist</a> I provide a quick overview of the possibilities to access news headlines using functionality which is available in the JVM. The examples are implemented in Scala using <a href="https://jupyter.org/">Jupyter</a> with the <a href="http://beakerx.com/">BeakerX kernel</a>.

We also have a <a href="https://hub.docker.com/r/pschatzmann/news-digest/" target="_blank" rel="noopener">Docker Image.</a>


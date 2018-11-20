import org.apache.commons.io.IOUtils
import java.nio.charset.*
 
def flowFile = session.get()
if(!flowFile) return
 
flowFile = session.write(flowFile, {inputStream, outputStream ->
 
   def feedXml = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
   def record = ""
		
    try {
         def rss = new XmlSlurper().parseText(feedXml)
    	 def channelLastBuildDate = rss.channel.lastBuildDate
    	 def channelTitle = rss.channel.title
    	 def channelLink = rss.channel.link
    	 rss.channel.item.each(){it ->
    	     def itemTitle = it.title
    	     def itemDescription = it.description
    	     def itemLink = it.link
    	     def itemPubDate = it.pubDate
    	     // remove HTML from description
    	     itemDescription = itemDescription.toString().replaceAll("\\<.*?>|&#.*;", "").trim()
    	     record = record + channelLastBuildDate + "\t" + 
    				channelTitle + "\t" + 
    				channelLink + "\t" + 
    				itemTitle + "\t" + 
    				itemDescription + "\t" + 
    				itemLink + "\t" + 
    				itemPubDate + "\n"
    	}
    	outputStream.write(record.getBytes(StandardCharsets.UTF_8))
    }
    catch(e) {
    	def channel = channelLink == null ? "UNK" : channelLink
    	log.error("Error during processing of RSS feed channel: ${channel}", e)
    }
} as StreamCallback)
 
session.transfer(flowFile, REL_SUCCESS)
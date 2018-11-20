import org.apache.commons.io.IOUtils
import java.nio.charset.*
 
def flowFile = session.get()
if(!flowFile) return

filename = customfilename.evaluateAttributeExpressions(flowFile).value
f = new File("/Users/gkeys/DEV/demos/custom logging/output/${filename}")
 
flowFile = session.write(flowFile, {inputStream, outputStream ->
 
    try {
			f.append(IOUtils.toString(inputStream, StandardCharsets.UTF_8)+'\n')
    	
    }
    catch(e) {
    	log.error("Error during processing custom logging: ${filename}", e)
    }
} as StreamCallback)
 
session.transfer(flowFile, REL_SUCCESS)
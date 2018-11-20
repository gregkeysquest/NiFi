import org.apache.commons.io.IOUtils
import java.nio.charset.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern
 
def flowFile = session.get()
if(!flowFile) return
 
def fail = false
 
flowFile = session.write(flowFile, {inputStream, outputStream ->
		
		
    try {

			def recordIn = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
			def cells = recordIn.split(',')
			
			// validate compCode
			Pattern compCode = Pattern.compile('[0-9]{4}|[0-9]{5}')
			Matcher matcher = compCode.matcher(cells[2])
			if(!matcher.matches()) fail = true
			
			// transform hireDate (for demo, assume all are validated)
			SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yyyy")
			SimpleDateFormat dtnorm = new SimpleDateFormat("yyyy-MM-dd")
			//Date date = dt.parse(cells[3])
			def hireDateNorm = dtnorm.format(dt.parse(cells[3])) 
			
			def recordOut = cells[0]+','+
											cells[1]+','+
											cells[2]+','+
											hireDateNorm+','+
											cells[4]+','+
											cells[5]+','+
											cells[6]+','+
											cells[7]+','+
											cells[8]

		            outputStream.write(recordOut.getBytes(StandardCharsets.UTF_8))
		            recordOut = ''
		
    }
    catch(e) {
    	log.error("Error during processing of validate.groovy", e)
    	session.transfer(inputStream, REL_FAILURE)
    }
    


} as StreamCallback)
 
if(fail){
	session.transfer(flowFile, REL_FAILURE)
	fail = false
} else {
	session.transfer(flowFile, REL_SUCCESS)
}
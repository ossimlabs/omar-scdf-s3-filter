package io.ossim.omar.scdf.s3filter

import groovy.json.JsonException
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.SendTo
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

/**
 * Created by cdowin on 6/8/2017
 */
@SpringBootApplication
@EnableBinding(Processor.class)
@Slf4j
class OmarScdfS3FilterApplication
{
	/**
	 * The main entry point of the SCDF S3 Filter application.
	 * @param args
	 */
	static final void main(String[] args)
	{
	       SpringApplication.run OmarScdfS3FilterApplication, args
	}

	/**
	 * Receives a message from a SCDF SQS Notifier.  Checks for a json message
     * with a bucket name and file name key. Those values are extracted and
     * wrapped into a simple json to pass along.
	 *
	 * @param message The message object from the SQS Notifier (in JSON)
	 * @return a JSON message of the file in S3 and bucket location
	 */
	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	final String filter(final Message<?> message)
	{
        log.debug("Message received: ${message}")

        try
        {
            final def parsedJson = new JsonSlurper().parseText(message.payload)
            final String bucketName = parsedJson.Records.s3.bucket.name[0]
            final String fileName = parsedJson.Records.s3.object.key[0]

            final JsonBuilder parsedJsonS3Data = new JsonBuilder()
            parsedJsonS3Data.file(
                bucket: bucketName,
                filename: fileName,
            )

            log.debug("Parsed data:\n" + parsedJsonS3Data.toString())
    		return parsedJsonS3Data.toString()
        }
        catch (JsonException jsonEx)
        {
            log.warn("Message received is not in proper JSON format, skipping\n   Message body: ${message}")
    		return null
        }
	}
}

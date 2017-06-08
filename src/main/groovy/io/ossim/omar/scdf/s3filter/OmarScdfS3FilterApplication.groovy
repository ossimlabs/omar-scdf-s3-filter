package io.ossim.omar.scdf.s3filter

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.Resource
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by cdowin on 6/8/2017
 */

@SpringBootApplication
@EnableBinding(Processor.class)
final class OmarScdfS3FilterApplication {

	/**
	 * The application logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass())

	/**
	 * ResouceLoader used to access the s3 bucket objects
	 */
	@Autowired
	private ResourceLoader resourceLoader

	/**
	 * Provides a URI for the s3
	 */
	@Autowired
	private ResourcePatternResolver resourcePatternResolver

    OmarScdfS3FilterApplication() {

    }

	/**
	 * The main entry point of the SCDF Aggregator application.
	 * @param args
	 */
	static final void main(String[] args) {
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
	@StreamListener(Processor.INPUT) @SendTo(Processor.OUTPUT)
	final String filter(final Message<?> message){

		if(logger.isDebugEnabled()){
			logger.debug("Message received: ${message}")
		}

        final def jsonParser = new JsonSlurper()

        try {
            final def parsedJson = jsonParser.parseText(message)
            final String bucketName = parsedJson.Records.s3.bucket.name[0]
            final String fileName = parsedJson.Records.s3.object.key[0]

            final JsonBuilder parsedJsonS3Data = new JsonBuilder()
            parsedJsonS3Data.file(
                    bucket: bucketName,
                    filename: fileName,
            )

            if (logger.isDebugEnabled()) {
                logger.debug("Parsed data:\n" + parsedJsonS3Data.toString())
            }

        } catch (final groovy.json.JsonException jsonEx) {
            logger.warn("Message received is not in proper JSON format, skipping\n   Message body: ${message}")
        }

		return parsedJsonS3Data.toString()
	}
}

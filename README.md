# omar-scdf-s3-filter
The OMAR S3 Filter is a Spring Cloud Data Flow (SCDF) Processor.
This means it:
1. Receives a message on a Spring Cloud input stream using Kafka.
2. Performs an operation on the data.
3. Sends the result on a Spring Cloud output stream using Kafka to a listening SCDF Processor or SCDF Sink.

## Purpose
The OMAR SCDF S3 Filter receives a JSON message from the OMAR SCDF SQS Listener and checks that the message is a proper AWS Records message containing the information needed for the OMAR SCDF Aggregator. This includes the AWS S3 bucket name and the name of an object contained in the S3 bucket.

## JSON Input Example (from the SQS Listener)
```json
{
   "Records":[
      {
         "eventVersion":"2.0",
         "eventSource":"aws:s3",
         "awsRegion":"us-east-1",
         "eventTime":"2017-05-17T15:19:17.054Z",
         "eventName":"ObjectCreated:Put",
         "userIdentity":{
            "principalId":"AWS:AIDAJEZ522MASFRCX33EA"
         },
         "requestParameters":{
            "sourceIPAddress":"72.239.134.33"
         },
         "responseElements":{
            "x-amz-request-id":"B96EEA576F92C7AE",
            "x-amz-id-2":"ziF5E2fAivtJAC7zuieVgHIJeAtNf0zgsx/qiW0C0OUibZQw0OgZs75EEmxYnENGwnlYBAfljyA="
         },
         "s3":{
            "s3SchemaVersion":"1.0",
            "configurationId":"NzlkMjZhOGMtNWUzOS00ZmQzLTkzYzYtMTJiNDY2N2Y2ZjUw",
            "bucket":{
               "name":"omar-dropbox",
               "ownerIdentity":{
                  "principalId":"A15X0AZ24P2BXT"
               },
               "arn":"arn:aws:s3:::omar-dropbox"
            },
            "object":{
               "key":"12345/SCDFTestImages.zip",
               "size":1753,
               "eTag":"e10e1145f9c0c39387b09b584eb5e523",
               "sequencer":"00591C69F4E5D745D6"
            }
         }
      }
   ]
}
```

## JSON Output Example (to the OMAR SCDF Aggregator)
```json
{
   "files":[
      {
         "bucket":"omar-dropbox",
         "filename":"12345/SCDFTestImages.zip"
      },
      {
         "bucket":"omar-dropbox",
         "filename":"12345/SCDFTestImages.zip_56734.email"
      }
   ]
}
```

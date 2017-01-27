package lambda


import com.amazonaws.HttpMethod
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest

import scala.collection.JavaConverters._
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.impl.client.DefaultHttpClient
import java.io.ByteArrayInputStream
import java.util.Base64
import java.util.Date

import com.typesafe.config.{Config, ConfigFactory}



/**
  * The handler in the Aws Lambda console is lambda.S3CopyToBucketLambda::handleRequest
  */
class S3CopyToBucketLambda extends RequestHandler[S3Event, String] with ConfigBase {

  /**
    * The handler is invoked when an event is triggered from a bucket (configured in the aws lambda console ) and copy to
    * the destBucket the file who was affected in the srcBucket
    *
    * @param s3Event Its the input variable
    * @param context The context constain the lambdalogger to see in cloudWatch
    * @return the output value of the handler
    */
  override def handleRequest(s3Event: S3Event, context: Context): String = {
    val s3Client: AmazonS3Client = new AmazonS3Client(new BasicAWSCredentials(
      config.getString("aws.user"), config.getString("aws.pass")))

    context.getLogger.log("s3 event " + s3Event.toJson)
    val destBucket: String = config.getString("aws.destBucket")

    context.getLogger.log(context.getLogGroupName)
    println(context.getLogGroupName)
    context.getLogger.log(context.getLogStreamName)
    println(context.getLogStreamName)

    var result = ""
    //For each s3Record, make a copy of then in destBucket
    s3Event.getRecords.asScala.foreach(e => {
      try {
        context.getLogger().log(e.getS3().getBucket().getName() + " " + e.getS3().getObject().getKey())
        println(e.getS3().getBucket().getName() + " " + e.getS3().getObject().getKey())

        s3Client.copyObject(e.getS3().getBucket().getName(), e.getS3().getObject().getKey(),
          destBucket, e.getS3().getObject().getKey() + "lambda")

        val milliSeconds = new java.util.Date().getTime() + 1000 * 60
        val presignedUrl = new GeneratePresignedUrlRequest(destBucket, e.getS3().getObject().getKey() + "_lambda", HttpMethod.GET)
          .withExpiration(new Date(milliSeconds))
        result = s3Client.generatePresignedUrl(presignedUrl).toURI().toString()

      } catch {
        case ee: Exception => context.getLogger().log(ee.getMessage())
      }
    })
    result
  }

}


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
  * Created by glucero on 1/26/17.
  */
trait ConfigBase {
  val config : Config = ConfigFactory.load("application.conf")

}

class S3CopyToBucketLambda extends RequestHandler[S3Event, String] with ConfigBase{

  case class S3Attachment(name: String, content: Array[Byte], contextType: String)

  override def handleRequest(s3Event: S3Event, context: Context): String = {
    val s3Client: AmazonS3Client = new AmazonS3Client(new BasicAWSCredentials(
      config.getString("aws.user"),config.getString("aws.pass")))
    context.getLogger.log("evento " + s3Event.toJson)
    val destBucket: String = config.getString("aws.destBucket")

    context.getLogger.log(context.getLogGroupName)
    println(context.getLogGroupName)
    context.getLogger.log(context.getLogStreamName)
    println(context.getLogStreamName)

    var result =""
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


  def sendMail(msg: String) = {
    val body: S3Attachment = textHtml(msg, "scalaLambda")
    val sb: StringBuilder = new StringBuilder
    val enc: Base64.Encoder = Base64.getEncoder
    sb.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:sendMail xmlns:ns2=\"http://integration.esb.gps.cl/\">")
    sb.append("<SendMailRequest><body>")
    sb.append("<name>" + body.name + "</name>")
    sb.append("<content>" + enc.encodeToString(body.content) + "</content>")
    sb.append("<contentType>" + body.contextType + "</contentType>")
    sb.append("</body>")
    sb.append("<from>" + "noreply@reddsystem.com" + "</from>")
    sb.append("<to>" + "glucero@reddsystem.com" + "</to>")
    sb.append("<subject>" + "Esto viene de AWS Lambda ScalaVersion" + "</subject>")
    sb.append("</SendMailRequest>")
    sb.append("<token>faketoken</token>")
    sb.append("</ns2:sendMail></soap:Body></soap:Envelope>")
    val payload1: String = sb.toString
    val client: HttpClient = new DefaultHttpClient
    val post: HttpPost = new HttpPost("http://132.255.70.97:8181/cxf/ws/mail/send")
    post.setHeader("Content-Type", "text/xml")
    post.setHeader("Accept", "*/*")
    post.setHeader("SOAPAction", "")
    val request: BasicHttpEntity = new BasicHttpEntity
    request.setContent(new ByteArrayInputStream(payload1.getBytes))
    post.setEntity(request)
    client.execute(post)
  }

  def textHtml(text: String, name: String) = S3Attachment(name, text.getBytes, "text/html")

}


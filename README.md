# scalaAwsLambda
Scala aws lambda examples

## Environment 

* mac Os Sierra
* jdk 1.8.0_112
* scala 2.12

The project have java and scala packages and have two simple examples to play with lambdas

The config file **src/main/resources/application.conf** contains the credentials,source and destination bucket
    
    aws {
        user = "user"
        pass = "pass"
        srcBucket = "src"
        destBucket = "dest"
    }

Java package is under **src/main/java/lambda** with one class **RandomHelloWorld**, that have the method `handleJava`

This method is invoked by aws when the lambda function runs. 

    public String handleJava(int number, Context context) 

Return a `String` and receive two parameters,an int value, and the second is the `Context` (from which the log can be accessed)

Using the int parameters, access to a predefined list of String(with random countries names) nad return the corresponding name of the indexOf, and in other case return ***"Hello world from unknown"***


Scala package is under **src/main/scala/lambda** with one class **S3CopyToBucketLambda** which implements **RequestHandler**

This method is invoked when an operation in the source bucket (you decide which operation in the aws console) is executed.
Basically makes a copy from the source bucket to the destination bucket, adding the suffix *lambda* to the key result.

    override def handleRequest(s3Event: S3Event, context: Context): String 
    
And return a presigned url to download the object , with an expiration timeout, in the example is 
  
    val milliSeconds = new java.util.Date().getTime() + 1000 * 60
  
The result is a URI string


## sbt Dependencies

    val dependencies = Seq(
           "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",          
           "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
           "com.amazonaws" % "aws-lambda-java-log4j" % "1.0.0",
           "com.typesafe" % "config" % "1.3.1",
           "org.scalatest" %% "scalatest" % "2.2.6" % Test,
           "junit" % "junit" % "4.10" % Test) 
  
In my learning path i discovered that if you add in the dependencies the 

*"com.amazonaws" % "aws-java-sdk" % "1.11.80"*
 
Then in the aws console, when you run the lambda , the result is `classNotFoundException`
  
## Create the lambda function
  
*In the aws console, choose  *Lambda ->  Create a Lambda Function*

![first](https://github.com/gastonlucero/scalaAwsLambda/blob/master/images/first.png)
  

*Then select *Blank Function* 

![second](https://github.com/gastonlucero/scalaAwsLambda/blob/master/images/second.png)
  

*Configure the s3 trigger (here you choose the event type and the source bucket)*

![third](https://github.com/gastonlucero/scalaAwsLambda/blob/master/images/third.png)


*Select the runtime, Java 8 in this case*

![fourth](https://github.com/gastonlucero/scalaAwsLambda/blob/master/images/fourth.png)
  

*The important configuration is here, the Handler is *package.class::handlerMethodName*, and select the role*

![fifth](https://github.com/gastonlucero/scalaAwsLambda/blob/master/images/fifth.png)


And thats all, ready to test

In test package , the **TestLambda** class , have a method to put an object in a source bucket.
The file you upload to the source bucket , with the key **test** must exists in the destination bucket name with the key **testlambda**

Another way to test is with the  predefined test cases available in the console

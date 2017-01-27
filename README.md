# scalaAwsLambda
Scala aws lambda examples

The project have java and scala packages    

Java package is under **src/main/java/lambda** with one class **RandomHelloWorld**, that have the method `handleJava`

This method is invoked by aws when the lambda function runs. 

   `public String handleJava(int number, Context context)` 

Return a `String` and receive two parameters,an int value, and the second is the `Context` (from which the log can be accessed)

Using the int parameters, access to a predefined list of String(with random countries names) nad return the corresponding name of the indexOf, and in other case return ***"Hello world from unknown"*** 

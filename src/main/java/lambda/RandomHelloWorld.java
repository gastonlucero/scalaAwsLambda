package lambda;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * The handler in the Aws Lambda console is lambda.RandomHelloWorld::handleJava
 */
public class RandomHelloWorld {

    private String[] countries = {"Chile", "Brasil", "Mexico", "Argentina", "España"};

    public String handleJava(int number, Context context) {
        String result = "unknown";
        if (number < 4) {
            result = countries[number];
            context.getLogger().log("\nHello world from " + result);
        } else {
            context.getLogger().log("\nHello world from " + result);
        }
        context.getLogger().log("\nLogGroupName = " + context.getLogGroupName());
        context.getLogger().log("\nLogStreamName = " + context.getLogStreamName());
        context.getLogger().log("\nAwsRequestId = " + context.getAwsRequestId());
        context.getLogger().log("\nFunctionName = " + context.getFunctionName());
        context.getLogger().log("\nFunctionArn = " + context.getInvokedFunctionArn().toString());

        return result;
    }
}

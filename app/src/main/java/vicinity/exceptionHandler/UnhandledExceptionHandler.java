package vicinity.exceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import vicinity.vicinity.Tabs;
/**
 * This class must handle the uncaught exceptions to avoid sudden crash
 *
 */
public class UnhandledExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {

    private final Activity activityContext;
    private final String LINE_SEPARATOR = "\n";

    /**
     * Public constructor
     * @param activityContext takes an activity
     */
    public UnhandledExceptionHandler(Activity activityContext){
        this.activityContext=activityContext;
    }

    /**
     * Implemented abstract method from UncaughtExceptionHandler
     * @param thread Thread
     * @param exception Throwable exception
     */
    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        Intent intent = new Intent(activityContext, Tabs.class);
        intent.putExtra("error", errorReport.toString());
        activityContext.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }


}

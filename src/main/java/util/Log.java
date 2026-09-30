package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

/**
 * Step logger - writes to the console/log file (logback) and to the Extent report node
 * of the test running on the current thread.
 */
public class Log {

    private static final Logger log = LoggerFactory.getLogger("TestStep");

    public static void info(String message) {
        log.info(message);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.info(message);
        }
    }

    public static void warn(String message) {
        log.warn(message);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.warning(message);
        }
    }

    /** Multi-line content (e.g. request/response) - shown as a code block in the report. */
    public static void code(String title, String content) {
        log.info("{}\n{}", title, content);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.info(title);
            test.info(MarkupHelper.createCodeBlock(content));
        }
    }
}

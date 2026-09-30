package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {

    public static String strEnv, strRunMode, strApplication, retryCount, runOnDocker, dockerUrl, strBrowser;
    private static boolean headless;

    static {
        strEnv = System.getProperty("env");
        strRunMode = System.getProperty("runMode");
        strApplication = System.getProperty("application");
        retryCount = System.getProperty("retryCount");
        runOnDocker = System.getProperty("runOnDocker");
        dockerUrl = System.getProperty("dockerUrl");
        strBrowser = System.getProperty("browser", "");   //chrome (default) | firefox | edge | brave
        headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        // Local defaults - used only when not passed via -D from maven/CI
        if (strEnv == null) strEnv = "staging";
        if (strRunMode == null) strRunMode = "local";           //localLab for parallel execution
        if (strApplication == null) strApplication = "DemoProject";
        if (retryCount == null) retryCount = "0";
        if (runOnDocker == null) runOnDocker = "false";
        if (dockerUrl == null) dockerUrl = "http://localhost:4444/";

        validateConfig();
    }

    private final Properties properties;

    public ConfigFileReader() {
        BufferedReader reader;
        String strPropertyPath = null;
        try {
            if (strApplication.equalsIgnoreCase("DemoProject")) {
                if (strEnv.equalsIgnoreCase("staging")) {
                    strPropertyPath = "src/main/resources/config/staging.properties";
                } else if (strEnv.equalsIgnoreCase("dev")) {
                    strPropertyPath = "src/main/resources/config/dev.properties";
                } else if (strEnv.equalsIgnoreCase("prod")) {
                    strPropertyPath = "src/main/resources/config/prod.properties";
                } else if (strEnv.equalsIgnoreCase("qa")) {
                    strPropertyPath = "src/main/resources/config/qa.properties";
                } else if (strEnv.equalsIgnoreCase("sigmaqa")) {
                    strPropertyPath = "src/main/resources/config/sigmaqa.properties";
                }
            }

            if (strPropertyPath == null) {
                throw new RuntimeException("No config file mapped for application=" + strApplication + ", env=" + strEnv);
            }
            reader = new BufferedReader(new FileReader(strPropertyPath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + strPropertyPath);
        }
    }

    /** Headless when -Dheadless=true or runMode=localLab (CI / parallel lab runs). */
    public static boolean isHeadless() {
        return headless || strRunMode.equalsIgnoreCase("localLab");
    }

    private static void validateConfig() {
        if (strEnv == null || strRunMode == null || strApplication == null) {
            System.err.println("----------");
            System.err.println("ERROR: Missing critical configuration. Please uncomment in ConfigFileReader.");
            System.err.println("----------");
            System.exit(1); // Terminate script execution
        }
    }

    public String getBaseUrlWeb() {
        String strBaseUrlWeb = properties.getProperty("BaseUrlWeb");
        if (strBaseUrlWeb != null)
            return strBaseUrlWeb;
        else
            throw new RuntimeException("Base url web is not defined.");
    }

    public int getImplicitWait() {
        String wait = properties.getProperty("implicitWait");
        if (wait != null)
            return Integer.parseInt(wait);
        else
            return 0;
    }

    public int getExplicitWait() {
        String wait = properties.getProperty("explicitWait");
        if (wait != null)
            return Integer.parseInt(wait);
        else
            return 10;
    }

    public int getPageLoadTimeout() {
        String pageLoad = properties.getProperty("pageLoadTimeout");
        if (pageLoad != null)
            return Integer.parseInt(pageLoad);
        else
            return 60;
    }

    public String getUsername() {
        String username = properties.getProperty("username");
        if (username != null)
            return username;
        else
            throw new RuntimeException("username not specified in the Configuration properties file.");
    }

    public String getPassword() {
        String password = properties.getProperty("password");
        if (password != null)
            return password;
        else
            throw new RuntimeException("password not specified in the Configuration properties file.");
    }

    public String getInventoryUrl() {
        String inventoryUrl = properties.getProperty("inventoryUrl");
        if (inventoryUrl != null)
            return inventoryUrl;
        else
            throw new RuntimeException("inventoryUrl not specified in the Configuration properties file.");
    }

    // ------------------------------------------------------------------
    // API (Restful-Booker)
    // ------------------------------------------------------------------

    public String getApiBaseUrl() {
        String apiBaseUrl = properties.getProperty("apiBaseUrl");
        if (apiBaseUrl != null)
            return apiBaseUrl;
        else
            throw new RuntimeException("apiBaseUrl not specified in the Configuration properties file.");
    }

    public String getApiUsername() {
        String apiUsername = properties.getProperty("apiUsername");
        if (apiUsername != null)
            return apiUsername;
        else
            throw new RuntimeException("apiUsername not specified in the Configuration properties file.");
    }

    public String getApiPassword() {
        String apiPassword = properties.getProperty("apiPassword");
        if (apiPassword != null)
            return apiPassword;
        else
            throw new RuntimeException("apiPassword not specified in the Configuration properties file.");
    }

    /** Retries for 5xx / connection errors - the public Restful-Booker server fails intermittently. */
    public int getApiRetryCount() {
        String retry = properties.getProperty("apiRetryCount");
        return retry != null ? Integer.parseInt(retry) : 2;
    }

    /** Whole-test retries for API tests (on top of per-request retries). */
    public int getApiTestRetryCount() {
        String retry = properties.getProperty("apiTestRetryCount");
        return retry != null ? Integer.parseInt(retry) : 1;
    }

    public long getApiResponseTimeLimitMs() {
        String limit = properties.getProperty("apiResponseTimeLimitMs");
        return limit != null ? Long.parseLong(limit) : 10000L;
    }
}

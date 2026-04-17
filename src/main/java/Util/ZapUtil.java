package Util;
import org.openqa.selenium.Proxy;
import org.zaproxy.clientapi.core.*;

public class ZapUtil {

    private static ClientApi clientApi;
    public static Proxy proxy;

    private static final String zapAddress = "127.0.0.1";
    private static final int zapPort = 8081;
    private static final String apiKey = "nnjvrqpea49ol592eighoso6ko";

    static {

        clientApi = new ClientApi(zapAddress, zapPort, apiKey);

        proxy = new Proxy()
                .setSslProxy(zapAddress + ":" + zapPort)
                .setHttpProxy(zapAddress + ":" + zapPort);

        // Custom Header (for backend visibility)
        try {
            clientApi.replacer.addRule(
                    "ZAP_TEST_HEADER",     // description
                    "true",                // enabled
                    "REQ_HEADER",          // match type
                    "X-ZAP-TEST",          // match string
                    "",                    // regex
                    "SecurityTestInProgress", // replacement
                    "",                    // initiators
                    ""                     // url
            );
        } catch (ClientApiException e) {
            System.out.println("Error adding header rule: " + e.getMessage());
        }
    }

    //  FIXED Passive Scan Wait
    public static void waitTillPassiveScanCompleted() {
        try {
            ApiResponse apiResponse = clientApi.pscan.recordsToScan();
            String records = ((ApiResponseElement) apiResponse).getValue();

            while (!records.equals("0")) {
                System.out.println("Passive Scan in progress... Remaining: " + records);

                Thread.sleep(2000); // IMPORTANT FIX

                apiResponse = clientApi.pscan.recordsToScan();
                records = ((ApiResponseElement) apiResponse).getValue();
            }

            System.out.println("Passive Scan Completed");

        } catch (Exception e) {
            System.out.println("Error in Passive Scan: " + e.getMessage());
        }
    }

    //FIXED Active Scan
    public static void startActiveScan(String targetUrl) {
        try {
            System.out.println("Starting Active Scan...");

            // Increase scan strength (important)
            clientApi.ascan.setOptionThreadPerHost(5);

            ApiResponse response = clientApi.ascan.scan(targetUrl, "True", "False", null, null, null);
            String scanId = ((ApiResponseElement) response).getValue();

            int progress;

            do {
                Thread.sleep(5000);

                progress = Integer.parseInt(
                        ((ApiResponseElement) clientApi.ascan.status(scanId)).getValue()
                );

                System.out.println("Active Scan Progress: " + progress + "%");

            } while (progress < 100);

            System.out.println("Active Scan Completed");

        } catch (Exception e) {
            System.out.println("Error in Active Scan: " + e.getMessage());
        }
    }

    // FIXED Report Generation
    public static void generateZapReport(String siteToTest) {

        try {
            clientApi.reports.generate(
                    "Middle Office Security Report",   // title
                    "traditional-html",               // template
                    null,
                    "ZAP Automation Report",          // description
                    null,
                    siteToTest,
                    null,
                    null,
                    null,
                    "ZapReport",                      // file name
                    null,
                    System.getProperty("user.dir"),  // save path
                    null
            );

            System.out.println(" Report Generated Successfully");

        } catch (ClientApiException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}
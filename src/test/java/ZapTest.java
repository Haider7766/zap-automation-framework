import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import static Util.ZapUtil.*;
public class ZapTest {
    WebDriver driver;

    private final String urlToTest = "https://middleofficeqa.croeminc.com:9083";
    @BeforeMethod
    public void setUp() {

        ChromeOptions chromeOptions = new ChromeOptions();

        //  ZAP Proxy attach
        chromeOptions.setProxy(proxy);

        // SSL ignore (important for ZAP)
        chromeOptions.setAcceptInsecureCerts(true);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(chromeOptions);
    }
    @Test
    public void demoTest() {

        // Step 1: Open application
        driver.get(urlToTest);

        // Step 2: Wait for passive scan
        waitTillPassiveScanCompleted();

        //  Step 3: Start Active Scan (MAIN IMPACT)
        startActiveScan(urlToTest);
    }

    @AfterMethod
    public void tearDown(Method method) {

        // Step 4: Generate Report
        generateZapReport(urlToTest);

        driver.quit();
    }
}
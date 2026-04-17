import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.lang.reflect.Method;

import static Util.ZapUtil.*;

public class ZapTest {

    WebDriver driver;

    private final String urlToTest =
            "https://middleofficeqa.croeminc.com:9083/catalogs/global/payment-validator-type";

    @BeforeMethod
    public void setUp() {

        ChromeOptions chromeOptions = new ChromeOptions();

        //ZAP Proxy attach
        chromeOptions.setProxy(proxy);

        //  SSL ignore (important for ZAP)
        chromeOptions.setAcceptInsecureCerts(true);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(chromeOptions);
    }

    // =========================
    // LOGIN METHOD (SSO FLOW)
    // =========================
    public void login() throws InterruptedException {

        // Step 1: Open login page
        driver.get("https://middleofficeqa.croeminc.com:9083/auth/login");

        Thread.sleep(3000);

        // Step 2: Click "Proceed to Login"
        driver.findElement(By.xpath("//button[contains(text(),'Proceed')]")).click();

        Thread.sleep(5000); // wait for redirect

        // Step 3: Enter credentials (update if needed)
        driver.findElement(By.id("username")).sendKeys("your_username");
        driver.findElement(By.id("password")).sendKeys("your_password");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // Step 4: Wait for dashboard
        Thread.sleep(8000);

        System.out.println(" Login Done: " + driver.getCurrentUrl());
    }

    @Test
    public void demoTest() throws InterruptedException {

        // Step 1: Login first
        login();

        // Step 2: Open target page after login
        driver.get(urlToTest);

        // Step 3: Wait for passive scan
        waitTillPassiveScanCompleted();

        //  Step 4: Active Scan
        startActiveScan(urlToTest);
    }

    @AfterMethod
    public void tearDown(Method method) {

        // Step 5: Generate Report
        generateZapReport(urlToTest);

        driver.quit();
    }
}
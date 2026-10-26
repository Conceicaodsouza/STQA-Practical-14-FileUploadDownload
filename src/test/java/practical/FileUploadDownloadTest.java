package practical;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

import static org.junit.Assert.*;

public class FileUploadDownloadTest {

    private WebDriver driver;
    private Path downloadDirectory;

    @Before
    public void setUp() throws Exception {

        downloadDirectory = Paths.get(
                System.getProperty("user.dir"),
                "target", "downloads"
        ).toAbsolutePath();

        Files.createDirectories(downloadDirectory);

        // Remove any previous downloaded file.
        Files.deleteIfExists(
                downloadDirectory.resolve("sample-download.txt"));

        String pageUrl =
        "http://127.0.0.1:8000/file-upload-download.html";

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        options.setExperimentalOption("prefs", Map.of(
                "download.default_directory",
                downloadDirectory.toString(),
                "download.prompt_for_download", false,
                "download.directory_upgrade", true,
                "safebrowsing.enabled", true
        ));

        driver = new ChromeDriver(options);

        // Explicitly allow downloads in headless Chrome.
        ((org.openqa.selenium.chromium.HasCdp) driver)
                .executeCdpCommand(
                        "Browser.setDownloadBehavior",
                        Map.of(
                                "behavior", "allow",
                                "downloadPath",
                                downloadDirectory.toString(),
                                "eventsEnabled", true
                        )
                );

        driver.get(pageUrl);
    }

    @Test
    public void testFileUpload() throws Exception {

        System.out.println("Starting file upload test...");

       //STUDENT TASK 1
        
        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10));

        WebElement status = wait.until(
                d -> d.findElement(By.id("uploadStatus")));

        assertTrue("Upload success message missing.",
                status.getText().contains(
                        "File uploaded successfully"));

        assertTrue("Uploaded filename missing.",
                status.getText().contains(
                        "sample-upload.txt"));

        System.out.println(
                "PASS: File upload validation successful.");
    }

    @Test
    public void testFileDownload() throws Exception {

        System.out.println("Starting file download test...");

       //STUDENT TASK 2
        
        Path downloadedFile = downloadDirectory.resolve(
                "sample-download.txt");

        long deadline = System.currentTimeMillis() + 20000;

        while (System.currentTimeMillis() < deadline) {

            boolean fileExists = Files.exists(downloadedFile);

            boolean downloadInProgress = Files.exists(
                    downloadDirectory.resolve(
                            "sample-download.txt.crdownload"));

            if (fileExists && !downloadInProgress
                    && Files.size(downloadedFile) > 0) {
                break;
            }

            Thread.sleep(500);
        }

        assertTrue("Downloaded file does not exist at: "
                        + downloadedFile,
                Files.exists(downloadedFile));

        assertTrue("Downloaded file is empty.",
                Files.size(downloadedFile) > 0);

        String content = Files.readString(downloadedFile);

        assertTrue("Downloaded file content is incorrect.",
                content.contains("STQA Practical 14"));

        System.out.println(
                "PASS: Downloaded file exists and content is verified.");
    }

    @Test
    public void testRobotClass() throws Exception {

        System.out.println("Starting Robot Class test...");

        // Codespaces has no graphical desktop.
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println(
                    "INFO: Robot keyboard simulation cannot run "
                    + "in headless Codespaces.");
            System.out.println(
                    "INFO: Robot demonstration requires a "
                    + "desktop environment.");
            return;
        }

       //Student task 3
           
        System.out.println(
                "PASS: Robot keyboard simulation executed.");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

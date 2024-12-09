package com.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

public class ChromeBrowser {

    private static AppiumDriverLocalService service;

    public static void main(String[] args) throws MalformedURLException {
        UiAutomator2Options options = getUiAutomator2Options();
        AppiumDriver driver = null;

        try {
            URL serverUrl = startServer();

            // Chrome-specific options
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("androidPackage", "com.android.chrome");

            // Merging ChromeOptions into UiAutomator2Options
            options.merge(chromeOptions);

            driver = new AndroidDriver(serverUrl, options);

            driver.get("http://www.google.com");
            Thread.sleep(4000);

            WebElement searchBox = driver.findElement(By.name("q"));
            if (searchBox.isDisplayed()) {
                System.out.println("Google page loaded successfully - Search box is visible.");
            } else {
                System.out.println("Google page did not load as expected - Search box is not visible.");
            }

            Thread.sleep(2000);

            searchBox.sendKeys("Apple vs Android", Keys.RETURN);
            Thread.sleep(4000);

            // JavaScript scroll down
            JavascriptExecutor js = driver;
            js.executeScript("window.scrollBy(0, 500)", "");

            Thread.sleep(2000);

            // JavaScript scroll down
            JavascriptExecutor js1 = driver;
            js1.executeScript("window.scrollBy(0, 500)", "");

            Thread.sleep(2000);

        } catch (InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                stopServer();
            }
        }
    }

    private static UiAutomator2Options getUiAutomator2Options() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setAutomationName("UiAutomator2");
        options.setUdid("emulator-5554");
        options.setPlatformVersion("15");
        options.setChromedriverExecutable(System.getProperty("user.dir") + "/src/test/resources/driver/chromedriver");

        // Setting browser name
        options.setCapability("browserName", "chrome");
        return options;
    }

    public static URL startServer() {
        int port = findFreePort();
        DesiredCapabilities capabilities = new DesiredCapabilities();

        service = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(port)
                .withCapabilities(capabilities)
                .build();

        service.start();
        System.out.println("Appium server started on port: " + port);
        return service.getUrl();
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available ports found", e);
        }
    }

    public static void stopServer() {
        if (service != null) {
            service.stop();
            System.out.println("Appium server stopped.");
        }
    }
}

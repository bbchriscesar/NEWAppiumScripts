package com.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

public class AndroidSettingsAutomation {

    private static AppiumDriverLocalService service;

    public static void main(String[] args) {
        UiAutomator2Options options = getUiAutomator2Options();
        AndroidDriver driver = null;

        try {

            closeSettingsApp(); 

            // Start the Appium server
            URL serverUrl = startServer();

            // Initialize the AndroidDriver
            driver = new AndroidDriver(serverUrl, options);

            // Scroll to "Accessibility"
            scrollToText(driver, "Accessibility");

            // Scroll to the end of the page
            scrollToEnd(driver);

            // Scroll back to the top
            scrollToTop(driver);

            // Click on "Apps"
            clickElementByXpath(driver, "//android.widget.TextView[@resource-id=\"android:id/title\" and @text=\"Apps\"]");

            navigateBack(driver);

            closeSettingsApp();

        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        } finally {
            // Quit driver and stop the server
            if (driver != null) {
                driver.quit();
            }
            stopServer();
        }
    }

    private static UiAutomator2Options getUiAutomator2Options() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setAppPackage("com.android.settings");
        options.setAppActivity(".Settings");
        options.setAutomationName("UiAutomator2");
        options.setUdid("emulator-5554");
        options.setPlatformVersion("15");
        options.setNoReset(true);
        return options;
    }

    private static void scrollToText(AndroidDriver driver, String text) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))"));
        System.out.println("Scrolled to text: " + text);
    }

    private static void scrollToEnd(AndroidDriver driver) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollToEnd(10)"));
        System.out.println("Scrolled to the end of the page.");
    }

    private static void scrollToTop(AndroidDriver driver) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollToBeginning(10)"));
        System.out.println("Scrolled back to the top of the page.");
    }

    private static void clickElementByXpath(AndroidDriver driver, String xpath) {
        driver.findElement(AppiumBy.xpath(xpath)).click();
        System.out.println("Clicked element with XPath: " + xpath);
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

    private static void closeSettingsApp() {
        try {
            String command = "adb shell am force-stop com.android.settings";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Closed Settings app if it was open.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to close Settings app: " + e.getMessage());
        }
    }

    public static void navigateBack(AndroidDriver driver) {
        try {
            driver.navigate().back();
            Thread.sleep(1000);
            System.out.println("Navigated backward using driver navigation.");
        } catch (Exception e) {
            System.out.println("Error occurred while navigating backward: " + e.getMessage());
        }
    }
    
}
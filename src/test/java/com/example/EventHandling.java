package com.example;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

public class EventHandling {
    private static AppiumDriverLocalService service;

    public static void main(String[] args) {
        UiAutomator2Options options = getUiAutomator2Options();
        AndroidDriver driver = null; // Changed AppiumDriver to AndroidDriver

        try {
            closeSettingsApp();
            URL serverUrl = startServer();

            driver = new AndroidDriver(serverUrl, options);
            Thread.sleep(4000);

            driver.findElement(By.xpath("//android.widget.TextView[@content-desc=\"Views\"]")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Drag and Drop\")")).click();
            Thread.sleep(2000);

            // Use key event handling to go back
            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            Thread.sleep(2000);

            // Use key event handling to go back
            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            System.out.println("Exception occurred: " + e.getMessage());

        } finally {
            if (driver != null) {
                closeSettingsApp();
                driver.quit();
                stopServer();
            }
        }
    }

    private static UiAutomator2Options getUiAutomator2Options() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setAppPackage("io.appium.android.apis");
        options.setAppActivity("io.appium.android.apis.ApiDemos");
        options.setAutomationName("UiAutomator2");
        options.setUdid("emulator-5554");
        options.setPlatformVersion("15");
        options.setNoReset(true);
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

    private static void closeSettingsApp() {
        try {
            String command = "adb shell am force-stop io.appium.android.apis";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Closed Settings app if it was open.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to close Settings app: " + e.getMessage());
        }
    }
}

package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class eCommerceApp {
    private static AppiumDriverLocalService service;

    public static void main(String[] args) {

        UiAutomator2Options options = getUiAutomator2Options();
        AppiumDriver driver = null;

        try {
            closeECommerceApp();
            URL serverUrl = startServer();

            driver = new AndroidDriver(serverUrl, options);
            Thread.sleep(4000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/spinnerCountry")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().textContains(\"France\"))"));

            Thread.sleep(2000);

            driver.findElement(AppiumBy.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\" and @text=\"France\"]")).click();

            Thread.sleep(2000);

        } catch (InterruptedException e) {
            System.out.println("Exception occurred: " + e.getMessage());

        } finally {
            if (driver != null) {
                closeECommerceApp();
                driver.quit();
                stopServer();
            }
        }
    }

    private static UiAutomator2Options getUiAutomator2Options() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setApp("/Users/cesarchr/Downloads/General-Store.apk");
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

    private static void closeECommerceApp() {
        try {
            String command = "adb shell am force-stop com.androidsample.generalstore";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Closed e-Commerce app if it was open.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to close Settings app: " + e.getMessage());
        }
    }
}

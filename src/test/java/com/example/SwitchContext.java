package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class SwitchContext {
    private static AppiumDriverLocalService service;

    public static void main(String[] args) throws IOException {

        UiAutomator2Options options = getUiAutomator2Options();
        AndroidDriver driver = null; // Change AppiumDriver to AndroidDriver

        try {
            closeECommerceApp("com.androidsample.generalstore");
            closeECommerceApp("com.android.chrome");
            URL serverUrl = startServer();

            driver = new AndroidDriver(serverUrl, options);
            Thread.sleep(4000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/spinnerCountry")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().textContains(\"Brazil\"))"));

            Thread.sleep(2000);

            driver.findElement(AppiumBy.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\" and @text=\"Brazil\"]")).click();

            Thread.sleep(2000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/nameField")).sendKeys("James Bond");
            Thread.sleep(2000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/radioMale")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/btnLetsShop")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.xpath("(//android.widget.TextView[@resource-id=\"com.androidsample.generalstore:id/productAddCart\"])[2]")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/appbar_btn_cart")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.className("android.widget.CheckBox")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.id("com.androidsample.generalstore:id/btnProceed")).click();
            Thread.sleep(4000);

            driver.context("WEBVIEW_com.androidsample.generalstore");

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

            driver.context("NATIVE_APP");
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            System.out.println("Exception occurred: " + e.getMessage());

        } finally {
            if (driver != null) {
                closeECommerceApp("com.androidsample.generalstore");
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
        options.setChromedriverExecutable(System.getProperty("user.dir") + "/src/test/resources/driver/129v/chromedriver");
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

    private static void closeECommerceApp(String appName) {
        try {
            String command = "adb shell am force-stop " + appName;
            @SuppressWarnings("deprecation")
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Closed app if it was open.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to close app: " + e.getMessage());
        }
    }

    /* private static void closeECommerceApp() {
        try {
            String command = "adb shell am force-stop com.androidsample.generalstore";
            @SuppressWarnings("deprecation")
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Closed Settings app if it was open.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to close Settings app: " + e.getMessage());
        }
    } */
}
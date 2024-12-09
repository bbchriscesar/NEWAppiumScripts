package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class DragDrop {
    private static AppiumDriverLocalService service;

    public static void main(String[] args) {

        UiAutomator2Options options = getUiAutomator2Options();
        AppiumDriver driver = null;

        try {
            closeSettingsApp();
            URL serverUrl = startServer();

            driver = new AndroidDriver(serverUrl, options);
            Thread.sleep(4000);

            driver.findElement(By.xpath("//android.widget.TextView[@content-desc=\"Views\"]")).click();
            Thread.sleep(2000);

            driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Drag and Drop\")")).click();

            Thread.sleep(2000);

            // Perform drag-and-drop using the reusable method
            WebElement dragElement = driver.findElement(By.id("io.appium.android.apis:id/drag_dot_1"));
            WebElement dropElement = driver.findElement(By.id("io.appium.android.apis:id/drag_dot_2"));
            dragAndDrop(driver, dragElement, dropElement);

            Thread.sleep(2000);

            driver.navigate().back();
            Thread.sleep(2000);

            driver.navigate().back();
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
            System.out.println("Closed API Demos app if it was open.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to close Settings app: " + e.getMessage());
        }
    }

    // Reusable method for drag-and-drop
    private static void dragAndDrop(AppiumDriver driver, WebElement dragElement, WebElement dropElement) {
        int startX = dragElement.getLocation().getX() + (dragElement.getSize().width / 2);
        int startY = dragElement.getLocation().getY() + (dragElement.getSize().height / 2);
        int endX = dropElement.getLocation().getX() + (dropElement.getSize().width / 2);
        int endY = dropElement.getLocation().getY() + (dropElement.getSize().height / 2);

        // Define the drag-and-drop action using W3C Actions API
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragAndDrop = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Perform the drag-and-drop gesture
        driver.perform(Arrays.asList(dragAndDrop));
        System.out.println("Drag-and-drop gesture performed.");
    }
}

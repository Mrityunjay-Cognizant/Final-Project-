package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        WebDriver dr = new ChromeDriver();
        dr.get("https://onecognizant.cognizant.com/Welcome");
        WebElement element = dr.findElement(By.xpath("//p[@class=\"activityTxt\" and text() =\"Ask HR\"]"));
//        JavascriptExecutor js = (JavascriptExecutor) dr;
//        js.executeScript("arguments[0].click();", element);
        Actions actions = new Actions(dr);
        actions.moveToElement(element).perform(); // This scrolls the element into view
        element.click();

    }
}
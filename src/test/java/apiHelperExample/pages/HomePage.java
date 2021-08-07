package apiHelperExample.pages;


import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import java.io.ByteArrayInputStream;

import static java.lang.String.format;


/**
 * Sample page
 */
public class HomePage extends Page {

    @FindBy(css = ".header-search-input")
    @CacheLookup
    public WebElement searchField;

    public HomePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Step("Открыта страница {baseUrl}")
    public void open(String baseUrl) {
        driver.get(baseUrl);
        Allure.addAttachment("Главная страница GitHub", new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
    }

    @Step("Ищем репозитории по ключевому слову {value}")
    public RepositoriesPage searchForRepositories(String value) {
        waitForElement(searchField).sendKeys(value);
        waitForElement(searchField).sendKeys(Keys.ENTER);
        Allure.addAttachment(format("Найденные репозитории по ключевому слову ", value), new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
        return new RepositoriesPage(driver);
    }
}

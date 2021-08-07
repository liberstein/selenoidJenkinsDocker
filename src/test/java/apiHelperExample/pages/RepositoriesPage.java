package apiHelperExample.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoriesPage extends Page {
    @FindBy(css = ".repo-list-item .f4.text-normal")
    public List<WebElement> repoNames;

    public RepositoriesPage(WebDriver webDriver) {
        super(webDriver);
    }

    public List<WebElement> getRepoNames() {
        return repoNames;
    }

    @Step("Получили значения найденных репозиториев со страницы")
    public LinkedList<String> getRepoListNamesFromPage() {
        return getRepoNames().stream()
                .map(item -> waitForElement(item).getText().trim().toLowerCase())
                .collect(Collectors.toCollection(LinkedList::new));
    }
}

package apiHelperExample;

import apiHelperExample.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class SampleJUnitTest extends JUnitTestBase {

    private HomePage homepage;

    @Test
    @Epic("GitHub")
    @Feature("Поиск репозиториев")
    @Story("Список найденных репозиториев содержит ключевое слово")
    @Description("Тест проверяет, что список найденных репозиториев содержит ключевое слово")
    public void testRepositoriesListSearch() {
        String searchQuery = "test2code";
        homepage = new HomePage(driver);
        homepage.open(baseUrl);
        List<String> actualRepositoriesList = homepage.searchForRepositories(searchQuery)
                .getRepoListNamesFromPage();
        assertTrue(actualRepositoriesList.stream().allMatch(item -> item.contains(searchQuery)),
                String.format("List Elements: [%s] does not contains text [%s] ", actualRepositoriesList, searchQuery));
    }

    @Test
    @Epic("GitHub")
    @Feature("Поиск репозиториев")
    @Story("Количество найденных репозиториев равно ожидаемому значению")
    @Description("Тест проверяет, что количество найденных репозиториев равно ожидаемому результату")
    public void testRepositoriesListSearch1() {
        String searchQuery = "healenium";
        homepage = new HomePage(driver);
        homepage.open(baseUrl);
        List<String> actualRepositoriesList = homepage.searchForRepositories(searchQuery)
                .getRepoListNamesFromPage();
        assertThat(actualRepositoriesList.size(), equalTo(10));
    }
}

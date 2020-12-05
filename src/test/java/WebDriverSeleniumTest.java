import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WebDriverSeleniumTest {

    private WebDriver driver;
    private final String RESULTS_CLASSNAME = "goods-item";


    @BeforeMethod(alwaysRun = true)
    public void browserSetUp() {
        driver = new ChromeDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void browserTearDown() {
        driver.quit();
        driver = null;
    }

    @Test (description = "test changing cart content while adding an item to cart")
    public void cartTextChanging() {
        waitLoading();
        String URL = "https://imarket.by/product/electrolux-ew6s5r06w/";
        driver.get(URL);
        waitAjaxCompleted();

        String buttonXpath = "//*[contains(@class, 'btn-red-new to-basket')]";
        WebElement addToCartBtn = driver.findElement(By.xpath(buttonXpath));
        addToCartBtn.click();

        String cartContentXpath = "//*[@id=\"popupBasket\"]/div/div/div[2]/div[2]/div[2]/a";
        WebElement cartContent = driver.findElement(By.xpath(cartContentXpath));
        String notExpectedCartContent = "Ваша корзина пуста!";
        String expectedCartContent = "Стиральная машина Electrolux EW6S5R06W";
        String actualCartContent = cartContent.getText();

        Assert.assertNotEquals(notExpectedCartContent, actualCartContent);
        Assert.assertEquals(actualCartContent, expectedCartContent);
    }

    @Test (description = "test incrementing purchase counter while adding an item to cart")
    public void cartPurchaseCounterChanging() {
        waitLoading();
        String URL = "https://imarket.by/product/electrolux-ew6s5r06w/";
        driver.get(URL);
        waitAjaxCompleted();

        String buttonXpath = "//*[contains(@class, 'btn-red-new to-basket')]";
        WebElement addToCartBtn = driver.findElement(By.xpath(buttonXpath));
        addToCartBtn.click();

        String counterXpath = "/html/body/div[3]/div/div[1]/div[6]/div[2]/p";
        WebElement counterValue = driver.findElement(By.xpath(counterXpath));
        String expectedCounterValue = "1";
        String actualCartContent = counterValue.getText();

        Assert.assertEquals(expectedCounterValue, actualCartContent);
    }

    @Test (description = "test the number of search results for the correct query")
    public void commonSearchTermResultsAreNotEmpty() {
        waitLoading();
        String URL = "https://imarket.by/";
        driver.get(URL);
        waitAjaxCompleted();

        String inputId = "search_input";
        WebElement searchInput = waitForWebElementLocatedBy(driver, By.id(inputId));
        String query = "велосипед";
        searchInput.sendKeys(query);
        String buttonXpath = "//*[@id='main-search-submit']";
        WebElement searchBtn = driver.findElement(By.xpath(buttonXpath));
        searchBtn.click();

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Timeout for waiting search result list was exceeded!");

        List<WebElement> searchResults = wait.until(new Function<WebDriver, List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                return driver.findElements(By.className(RESULTS_CLASSNAME));
            }
        });

        System.out.println("Search results number: " + searchResults.size());
        Assert.assertTrue(searchResults.size() > 0, "Search results mustn't be empty!");
    }

    private void waitLoading() {
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    }

    private boolean waitAjaxCompleted() {
        return new WebDriverWait(driver, 10).until(CustomConditions.jQueryAjaxCompleted());
    }

    private static WebElement waitForWebElementLocatedBy(WebDriver driver, By by) {
        return new WebDriverWait(driver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }
}

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Consumer;

public final class SeleniumTests {
    @Before
    public void setup() {
        System.out.println("Starting setup...");
        WebDriverManager.chromedriver().setup();
        System.out.println("Setup finished");
    }

    @Test
    public void firstTest_clickWhoWeServeCheckSubmenu() {
        usingDriver(driver -> {
            driver.findElement(whoWeServeBtnQuery).click();
            waitUntilAppear(driver, whoWeServeLinksQuery);
            var actualItems = driver
                    .findElements(whoWeServeLinksQuery)
                    .stream()
                    .map(WebElement::getText)
                    .toArray(String[]::new);
            // Not matches spec in production:
            Assert.assertArrayEquals(whoWeServeExpectedItems, actualItems);
        });
    }

    @Test
    public void secondTest_typeJavaInSearchCheckSuggestionsArea() {
        usingDriver(driver -> {
            var searchInput = driver.findElement(searchInputQuery);
            searchInput.click();
            searchInput.sendKeys(searchQueryText);
            waitUntilAppear(driver, suggestionsAreaQuery);
            var suggestionsArea = driver.findElement(suggestionsAreaQuery);
            var searchInputLocation = searchInput.getLocation();
            var searchInputSize = searchInput.getSize();
            var suggestionsAreaLocation = suggestionsArea.getLocation();
            var suggestionsAreaSize = suggestionsArea.getSize();
            Assert.assertEquals(searchInputLocation.x, suggestionsAreaLocation.x);
            Assert.assertEquals(searchInputLocation.y + searchInputSize.height, suggestionsAreaLocation.y);
            Assert.assertTrue(suggestionsAreaSize.width >= searchInputSize.width);
        });
    }

    @Test
    public void thirdTest_typeJavaInSearchCheckSearchResults() {
        usingDriver(driver -> {
            var searchInput = driver.findElement(searchInputQuery);
            searchInput.click();
            searchInput.sendKeys(searchQueryText);
            driver.findElement(searchBtnQuery).click();
            waitUntilAppear(driver, productItemsQuery);
            var products = driver.findElements(productItemsQuery);
            Assert.assertEquals(products.size(), 10);
            products.forEach(product -> {
                var title = product.findElement(productTitleQuery).getText();
                var containsAddToCartBtn = product.findElement(productAddToCartQuery).isDisplayed();
                Assert.assertTrue(title.contains(searchQueryText));
                // Not matches spec in production:
                Assert.assertTrue(containsAddToCartBtn);
            });
        });
    }

    @Test
    public void forthTest_clickSubjectsEducationCheckSubmenu() {
        usingDriver(driver -> {
            hover(driver, subjectsBtnQuery);
            waitUntilAppear(driver, educationBtnQuery);
            hover(driver, educationBtnQuery);
            waitUntilAppear(driver, educationItemsQuery);
            var actualItems = driver
                    .findElements(educationItemsQuery)
                    .stream()
                    .map(WebElement::getText)
                    .toArray(String[]::new);
            Assert.assertArrayEquals(educationExpectedItems, actualItems);
        });
    }


    private static void usingDriver(Consumer<ChromeDriver> usage) {
        var driver = new ChromeDriver();
        try {
            driver.get(wileyUrl);
            driver.findElement(confirmLocationBtnQuery).click();
            usage.accept(driver);
        } finally {
            driver.close();
        }
    }

    private static void hover(ChromeDriver driver, By query) {
        new Actions(driver).moveToElement(driver.findElement(query)).build().perform();
    }

    private static final String[] whoWeServeExpectedItems = new String[]{
            "Students",
            "Instructors",
            "Book Authors",
            "Professionals",
            "Researchers",
            "Institutions",
            "Librarians",
            "Corporations",
            "Societies",
            "Journal Editors",
            "Government"
    };

    private static final String[] educationExpectedItems = new String[]{
            "Information & Library Science",
            "Education & Public Policy",
            "K-12 General",
            "Higher Education General",
            "Vocational Technology",
            "Conflict Resolution & Mediation (School settings)",
            "Curriculum Tools- General",
            "Special Educational Needs",
            "Theory of Education",
            "Education Special Topics",
            "Educational Research & Statistics",
            "Literacy & Reading",
            "Classroom Management"
    };

    private static final By educationItemsQuery = By.xpath("//li[contains(@class, 'dropdown-submenu')]" +
            "/a[contains(text(), 'Education')]/..//li[contains(@class, 'dropdown-item')]");

    private static final String searchQueryText = "Java";

    private static final By productTitleQuery = By.className("product-title");

    private static final By productAddToCartQuery = By.className("add-to-cart-button");

    private static final By productItemsQuery = By.className("product-item");

    private static final By searchBtnQuery = By.xpath("//form[@action='/en-us/search']//button[@type='submit']");

    private static final By suggestionsAreaQuery = By.id("ui-id-2");

    private static final String wileyUrl = "https://www.wiley.com/en-us";

    private static final By searchInputQuery = By.id("js-site-search-input");

    private static final By confirmLocationBtnQuery = By.className("changeLocationConfirmBtn");

    private static final By whoWeServeLinksQuery = By.xpath("//*[@id='Level1NavNode1']/ul/li/a");

    private static final By subjectsBtnQuery =
            By.xpath("//li[contains(@class, 'dropdown-submenu')]/a[contains(text(), 'SUBJECTS')]");

    private static final By educationBtnQuery =
            By.xpath("//li[contains(@class, 'dropdown-submenu')]/a[contains(text(), 'Education')]");

    private static final By whoWeServeBtnQuery =
            By.xpath("//li[contains(@class, 'dropdown-submenu')]/a[contains(text(), 'WHO WE SERVE')]");

    private void waitUntilAppear(ChromeDriver driver, By by) {
        var wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
}

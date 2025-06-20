import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class PointsTests {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testLogout() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement signBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign")));
        signBtn.click();

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

        username.clear();
        username.sendKeys("oldUser");
        password.clear();
        password.sendKeys("oldUser");

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button._logoutButton_oi5hg_17")
        ));
        logoutBtn.click();

        Thread.sleep(1500);

        Assertions.assertTrue(driver.getPageSource().contains("Register"));
    }

    @Test
    void testPaintWithoutNothing() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        Thread.sleep(1500);

        // Ожидаем сообщение "R is not selected"
        Assertions.assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"), "R is not selected"
        )));
    }

    @Test
    void testNoYValue() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement greenButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input._button_1a43n_45[value='1']")
        ));
        greenButton.click();

        WebElement buttonR = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[label[text()='R:']]//input[@class='_button_1a43n_45' and @value='1.5']")
        ));
        buttonR.click();

        Thread.sleep(1500);
        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        boolean messageAppeared = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"),
                "Y value must be number in {-3...3}"
        ));

        Assertions.assertTrue(messageAppeared, "Ожидаемое сообщение не появилось на странице");
    }

    @Test
    void testInvalidYValue() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement yInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[label[text()='Y:']]/input")
        ));
        yInput.clear();
        yInput.sendKeys("asdadad");

        WebElement xButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input._button_1a43n_45[value='1']")
        ));
        xButton.click();

        WebElement buttonR = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[label[text()='R:']]//input[@class='_button_1a43n_45' and @value='1.5']")
        ));
        buttonR.click();

        Thread.sleep(1500);
        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        boolean messageAppeared = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"),
                "Y value must be number in {-3...3}"
        ));

        Assertions.assertTrue(messageAppeared, "Ожидаемое сообщение не появилось на странице");
    }

    @Test
    void testNoXValue() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement yInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[label[text()='Y:']]/input")
        ));
        yInput.clear();
        yInput.sendKeys("2");

        Thread.sleep(1500);

        WebElement buttonR = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[label[text()='R:']]//input[@class='_button_1a43n_45' and @value='1.5']")
        ));
        buttonR.click();

        Thread.sleep(1500);

        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        boolean messageAppeared = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"),
                "X is not selected"
        ));

        Assertions.assertTrue(messageAppeared, "Ожидаемое сообщение не появилось на странице");
    }

    @Test
    void testNoRValue() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        WebElement xButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input._button_1a43n_45[value='1']")
        ));
        xButton.click();

        Thread.sleep(1500);

        WebElement yInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[label[text()='Y:']]/input")
        ));
        yInput.clear();
        yInput.sendKeys("2");

        Thread.sleep(1500);

        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        boolean messageAppeared = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"),
                "R is not selected"
        ));

        Assertions.assertTrue(messageAppeared, "Ожидаемое сообщение не появилось на странице");
    }

    @Test
    void testHit() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        WebElement xButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input._button_1a43n_45[value='-1']")
        ));
        xButton.click();

        WebElement yInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[label[text()='Y:']]/input")
        ));
        yInput.clear();
        yInput.sendKeys("1");

        WebElement buttonR = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[label[text()='R:']]//input[@class='_button_1a43n_45' and @value='2']")
        ));
        buttonR.click();

        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        Thread.sleep(1500);

        Assertions.assertTrue(driver.getPageSource().contains("hit"));
    }

    @Test
    void testSlip() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        WebElement xButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input._button_1a43n_45[value='2.5']")
        ));
        xButton.click();

        WebElement yInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[label[text()='Y:']]/input")
        ));
        yInput.clear();
        yInput.sendKeys("-1");

        WebElement buttonR = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[label[text()='R:']]//input[@class='_button_1a43n_45' and @value='2']")
        ));
        buttonR.click();

        WebElement paintButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='button'][value='Paint!']")
        ));
        paintButton.click();

        Thread.sleep(1500);

        Assertions.assertTrue(driver.getPageSource().contains("slip"));
    }

    @Test
    void testNextButton() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[@class='_buttonContainer_1viyq_23']//button[text()='Next']")
        ));
        nextButton.click();



        Assertions.assertTrue(driver.getPageSource().contains("Prev"));
    }

    @Test
    void testPrevButton() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("oldUser");
        password.sendKeys("oldUser");
        signIn.click();

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(1500);

        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[@class='_buttonContainer_1viyq_23']//button[text()='Next']")
        ));
        nextButton.click();

        WebElement prevButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@class='_pageButton_1viyq_41' and text()='Prev']")
        ));
        prevButton.click();


        Assertions.assertTrue(driver.getPageSource().contains("Next"));
    }

}


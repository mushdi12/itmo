import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.bson.Document;


import java.time.Duration;

public class LoginFormTest {
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

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("web_lab_4");
            MongoCollection<Document> users = database.getCollection("users");
            users.deleteOne(new Document("username", "newUser"));

        }
    }

    @Test
    void testSubmitButton() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = driver.findElement(By.id("password"));
        WebElement signIn = driver.findElement(By.id("sign"));

        username.sendKeys("validUser");
        password.sendKeys("validPassword");
        signIn.click();


        Assertions.assertTrue(driver.getPageSource().contains("Submit"));
    }

    @Test
    void testRegisterSuccess() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");


        WebElement registerBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("reg")));
        registerBtn.click();


        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        WebElement repeatPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("repeat-password")));

        username.clear();
        username.sendKeys("newUser");
        password.clear();
        password.sendKeys("newPassword");
        repeatPassword.clear();
        repeatPassword.sendKeys("newPassword");


        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(3000);

        Assertions.assertTrue(driver.getPageSource().contains("Points"));
    }

    @Test
    void testRegisterFail() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");


        WebElement registerBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("reg")));
        registerBtn.click();


        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        WebElement repeatPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("repeat-password")));

        username.clear();
        username.sendKeys("oldUser");
        password.clear();
        password.sendKeys("oldUser");
        repeatPassword.clear();
        repeatPassword.sendKeys("oldUser");


        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(3000);

        Assertions.assertTrue(driver.getPageSource().contains("User already exists"));
    }

    @Test
    void testSignSuccess() throws InterruptedException {
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

        Thread.sleep(3000);

        Assertions.assertTrue(driver.getPageSource().contains("Points"));
    }

    @Test
    void testSignFail() throws InterruptedException {
        driver.get("http://localhost:8080/backend-1.0-SNAPSHOT");

        WebElement signBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign")));
        signBtn.click();


        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

        username.clear();
        username.sendKeys("newUser2");
        password.clear();
        password.sendKeys("newUser2");

        WebElement submitBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Submit']"));
        submitBtn.click();

        Thread.sleep(3000);


        Assertions.assertTrue(driver.getPageSource().contains("Incorrect login or password"));
    }

}
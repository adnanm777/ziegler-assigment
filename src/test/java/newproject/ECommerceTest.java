package newproject;

import org.testng.annotations.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.poi.xssf.usermodel.*;

public class ECommerceTest {

    WebDriver driver;
    List<String> reportLogs = new ArrayList<>();
    String basePath = System.getProperty("user.dir");
    List<Map<String, String>> addedProducts = new ArrayList<>();

    // ✅ Dynamic absolute path to Excel file
    String excelPath = "C:\\Users\\HP\\eclipse-workspace\\a\\src\\test\\java\\testdata.xlsx";

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        new File(basePath + "/screenshots").mkdirs();
    }

    @Test(priority = 1)
    public void homepageCategoryTest() {
        driver.get("https://automationteststore.com/");
        List<WebElement> categories = driver.findElements(By.cssSelector("ul.nav-pills.categorymenu > li > a"));

        System.out.println("Main Categories:");
        for (WebElement cat : categories) {
            System.out.println("- " + cat.getText());
        }

        Random rand = new Random();
        WebElement randomCat = categories.get(rand.nextInt(categories.size()));
        String catName = randomCat.getText();
        randomCat.click();

        List<WebElement> products = driver.findElements(By.cssSelector(".fixed_wrapper .prdocutname"));
        Assert.assertTrue(products.size() >= 3, "Less than 3 products found in category: " + catName);
    }

    @Test(priority = 2)
    public void addProductsToCart() throws InterruptedException {
        Random rand = new Random();

        for (int i = 0; i < 2; i++) {
            List<WebElement> productElements = driver.findElements(By.cssSelector(".fixed_wrapper .prdocutname"));
            WebElement prod = productElements.get(rand.nextInt(productElements.size()));
            String productURL = prod.getAttribute("href");
            prod.click();

            try {
                String name = driver.findElement(By.cssSelector(".maintext")).getText();
                String price = driver.findElement(By.cssSelector(".productfilneprice")).getText();

                WebElement qtyElem = driver.findElement(By.name("quantity"));
                qtyElem.clear();
                qtyElem.sendKeys("1");

                WebElement addBtn = driver.findElement(By.cssSelector("a.cart"));
                addBtn.click();

                Map<String, String> productInfo = new HashMap<>();
                productInfo.put("name", name);
                productInfo.put("price", price);
                productInfo.put("url", productURL);
                productInfo.put("qty", "1");
                addedProducts.add(productInfo);

                Thread.sleep(2000);
                driver.navigate().back();
            } catch (Exception e) {
                reportLogs.add("Add to cart failed for product at URL: " + driver.getCurrentUrl());
                driver.navigate().back();
            }
        }

        String cartText = driver.findElement(By.cssSelector("#cart_total")).getText();
        Assert.assertTrue(cartText.contains("item"), "Cart count was not updated.");
    }

    @Test(priority = 3)
    public void validateCartAndCheckout() throws Exception {
        driver.findElement(By.cssSelector("div.header_cart a")).click();
        Thread.sleep(2000);

        for (Map<String, String> item : addedProducts) {
            Assert.assertTrue(driver.getPageSource().contains(item.get("name")));
            Assert.assertTrue(driver.getPageSource().contains(item.get("price")));
        }

        driver.findElement(By.cssSelector("a[href*='checkout']")).click();

        String[] data = readExcelData(excelPath);

        driver.findElement(By.name("firstname")).sendKeys(data[0]);
        driver.findElement(By.name("lastname")).sendKeys(data[1]);
        driver.findElement(By.name("email")).sendKeys(data[2]);
        driver.findElement(By.name("telephone")).sendKeys(data[3]);
        driver.findElement(By.name("password")).sendKeys(data[4]);
        driver.findElement(By.name("confirm")).sendKeys(data[4]);

        driver.findElement(By.name("agree")).click();
        driver.findElement(By.cssSelector("div.buttons input.btn")).click();
    }

    @Test(priority = 4)
    public void negativeRegistrationTest() throws Exception {
        driver.get("https://automationteststore.com/index.php?rt=account/create");

        driver.findElement(By.name("firstname")).sendKeys("Test");
        driver.findElement(By.name("email")).sendKeys("test@mail.com");
        driver.findElement(By.name("telephone")).sendKeys("1234567890");
        driver.findElement(By.name("password")).sendKeys("Test@123");

        driver.findElement(By.name("agree")).click();
        driver.findElement(By.cssSelector("div.buttons input.btn")).click();

        WebElement error = driver.findElement(By.cssSelector(".alert"));
        if (error.isDisplayed()) {
            File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(scr.toPath(), Paths.get(basePath + "/screenshots/validation_error.png"));
            reportLogs.add("Validation error occurred during registration (screenshot captured).");
        }

        Assert.assertTrue(error.isDisplayed(), "Error message was not displayed.");
    }

    @AfterClass
    public void generateReport() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(basePath + "/report.txt"));
        writer.write("========= Test Execution Report =========\n\n");

        writer.write("Products Added to Cart:\n");
        for (Map<String, String> item : addedProducts) {
            writer.write("- " + item.get("name") + " | " + item.get("price") + " | " + item.get("url") + "\n");
        }

        writer.write("\nFailures and Logs:\n");
        for (String log : reportLogs) {
            writer.write("- " + log + "\n");
        }

        writer.close();
        driver.quit();
    }

    public String[] readExcelData(String file) throws Exception {
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\HP\\eclipse-workspace\\a\\src\\test\\java\\testdata.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFRow row = sheet.getRow(1); // 2nd row (index 1) = first data row
        String[] data = new String[5];
        for (int i = 0; i < 5; i++) {
            data[i] = row.getCell(i).getStringCellValue();
        }

        workbook.close();
        fis.close();
        return data;
    }
}

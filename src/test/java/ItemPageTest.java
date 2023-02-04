import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import rest.Item;
import rest.ItemAPI;

import java.time.Duration;

public class ItemPageTest {
	private WebDriver chrome;

	@BeforeAll
	static void beforeAll() {
		// This is method will be executed before all tests in the class
		WebDriverManager.chromedriver().setup(); // Download chrome driver and configure it
	}

	@BeforeEach
	void beforeEach() {
		// This method will be executed before each test in the class
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--incognito");
		chrome = new ChromeDriver(options); // Create instance of Chrome browser
		chrome.manage().window().maximize(); // Maximizes the browser window
		chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Wait for elements to appear
		chrome.navigate().to("https://6kp7lppm1bvilfq.inv.bg"); // Navigate to login page
	}

	@AfterEach
	void afterEach() {
		//This method will be executed after each test in the class
		chrome.quit();
	}

	@Test
	@DisplayName("Can navigate to Item page via direct navigation")
	void canNavigateToItemPageViaDirectNavigation() {
		login();
		chrome.navigate().to("https://6kp7lppm1bvilfq.inv.bg/objects/manage"); // Direct navigation
		WebElement itemsHeadline = chrome.findElement(By.xpath("//div[@id='headline']//h2"));
		Assertions.assertEquals("Артикули", itemsHeadline.getText()); // Check headline text
		WebElement itemsTable = chrome.findElement(By.id("fakturi_table"));
		Assertions.assertTrue(itemsTable.isDisplayed(), "Items table is not displayed");
	}

	@Test
	@DisplayName("Can search for not-existing items")
	void canSearchForNotExistingItemsByName() {
		login();
		chrome.navigate().to("https://6kp7lppm1bvilfq.inv.bg/objects/manage");
		// Expand search
		WebElement expandSearchButton = chrome.findElement(By.id("searchbtn"));
		expandSearchButton.click();
		//Populate item name
		WebElement nameField = chrome.findElement(By.name("nm"));
		nameField.clear();
		nameField.sendKeys("Coffee");
		//Trigger search
		WebElement triggerSearchButton = chrome.findElement(By.name("s"));
		System.out.println("The button size is: " + triggerSearchButton.getSize().height);
		Assertions.assertEquals(24, triggerSearchButton.getSize().height);
		triggerSearchButton.click();
		//Check that the no item is found
		WebElement noItemFoundMessage = chrome.findElement(By.id("emptylist"));
		Assertions.assertEquals("Не са намерени артикули, отговарящи на зададените критерии.", noItemFoundMessage.getText());
	}

	@Test
	@DisplayName("Can search for existing item")
	void canSearchForExistingItem() {
		//Clean all items from the system API
		ItemAPI.deleteAllItems();
		//Create item via API
		String itemName = "Coffee";
		Item coffee = new Item(itemName, "kg.", 10.24, 10.24, "ууууууууу", "BGN");
		ItemAPI.createItem(coffee);
		//Create second item
		coffee.name = "Different item";
		ItemAPI.createItem(coffee);
		login();
		chrome.navigate().to("https://6kp7lppm1bvilfq.inv.bg/objects/manage");
		//Expand search
		WebElement expandSearchButton = chrome.findElement(By.id("searchbtn"));
		expandSearchButton.click();
		//Populate item name
		WebElement nameField = chrome.findElement(By.name("nm"));
		nameField.clear();
		nameField.sendKeys(itemName); //Search the item created via API by name
		//Trigger search
		WebElement triggerSearchButton = chrome.findElement(By.name("s"));
		triggerSearchButton.click();
		//Check that the item is found and the item is only one
		WebElement table = chrome.findElement(By.id("fakturi_table"));
		System.out.println(table.getText());
		Assertions.assertTrue(table.getText().contains(itemName), "Could not find the item name in the text");
	}

	@Test
	@DisplayName("Show correct message when no items exist")
	void systemDisplayCorrectMessageWhenNoItemsExist() {
		// Delete all items via API
		ItemAPI.deleteAllItems();
		// Login
		login();
		// Navigate to Item page
		chrome.navigate().to("https://6kp7lppm1bvilfq.inv.bg/objects/manage");
		// Check correct message is displayed
		WebElement element = chrome.findElement(By.xpath("//*[@id=\"emptylist\"]"));
		Assertions.assertEquals("Не са намерени артикули, отговарящи на зададените критерии.", element.getText());
	}

	@Test
	@DisplayName("Can search items by price (from - to)")
	void canSearchForItemByPriceFromTo() {
		// Delete all items
		ItemAPI.deleteAllItems();
		// Create few items with different prices (you can create two items using the same item object)
		Item coffee = new Item("Coffee", "kg.", 10.24, 10.24, "dasdasd", "BGN");
		Item kiwi = new Item("kiwi", "kg.", 80.154, 80.154, "11111", "BGN");
		Item banana = new Item("Banana", "kg.", 100.54, 100.54, "22222", "BGN");

		ItemAPI.createItem(coffee);
		ItemAPI.createItem(kiwi);
		ItemAPI.createItem(banana);

		// Login
		login();
		// Navigate to Items page
		chrome.navigate().to("https://6kp7lppm1bvilfq.inv.bg/objects/manage");
		// Expand search form
		WebElement expandSearchButton = chrome.findElement(By.id("searchbtn"));
		expandSearchButton.click();
		// Search for items by price from to

		WebElement priceFromField1 = chrome.findElement(By.xpath("//*[@id=\"searchbox\"]/table/tbody/tr[2]/td[2]/input[1]"));
		priceFromField1.sendKeys(String.valueOf(15));
		WebElement priceFromField2 = chrome.findElement(By.xpath("//*[@id=\"searchbox\"]/table/tbody/tr[2]/td[2]/input[2]"));
		priceFromField2.sendKeys(String.valueOf(90));
		WebElement expandSearchButton1 = chrome.findElement(By.xpath("//*[@id=\"search-button\"]"));
		expandSearchButton1.click();
		chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		WebElement firstItem = chrome.findElement(By.xpath("//*[@id=\"fakturi_table\"]/tbody/tr[3]/td[2]/a"));
		Assertions.assertEquals("kiwi (80.15 kg.)", firstItem.getText());

		// WebElement priceToField = chrome.findElementBy(By.name("pr2"));
		// Check whether table contains only valid item names

	}

	private void login() {
		//Navigate to Login page
		WebElement heading1 = chrome.findElement(By.xpath("//h1"));
		Assertions.assertEquals("Вход в inv.bg", heading1.getText(), "Default text is different");
		//Enter email
		WebElement emailField = chrome.findElement(By.id("loginusername")); //locate element in the dom
		emailField.clear(); //Clear the text in the field
		emailField.sendKeys("fbinnzhivko@gmail.com"); //type text in the field
		//Enter password
		WebElement passwordField = chrome.findElement(By.name("password"));
		passwordField.sendKeys("E@Bh3Hs5zstHCz"); //type text in the field
		//Click Login button
		WebElement loginButton = chrome.findElement(By.cssSelector("input.selenium-submit-button"));
		loginButton.click(); //Clicks Login button
		//Check the homepage is loaded
		WebElement homePageHeadline = chrome.findElement(By.xpath("//div[@id='headline']//h2"));
		Assertions.assertEquals("Система за фактуриране", homePageHeadline.getText());
		//Check that user logged in (email is displayed at the top right)
		WebElement userPanel = chrome.findElement(By.cssSelector("div.userpanel-header"));
		Assertions.assertEquals("fbinnzhivko@gmail.com", userPanel.getText());
	}
}

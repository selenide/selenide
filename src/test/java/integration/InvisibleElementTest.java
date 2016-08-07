package integration;

import org.junit.*;
import org.openqa.selenium.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.*;

public class InvisibleElementTest extends IntegrationTest {
	@Before
	public void openTestPageWithJQuery() {
		openFile("page_with_selects_without_jquery.html");
	}

	@Test
	public void userCanSelectCheckbox() {
		$(By.name("rememberMe")).shouldNotBe(selected);

		$(By.name("rememberMe")).click();

		$(By.name("rememberMe")).shouldBe(selected);
		assertEquals("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\" selected:true></input>",
						$(By.name("rememberMe")).toString());
	}

	@Test
	public void userCanCheckCheckbox() {
		$(By.name("rememberMe")).setSelected(true);
		$(By.name("rememberMe")).shouldBe(selected);

		$(By.name("rememberMe")).setSelected(true);
		$(By.name("rememberMe")).shouldBe(selected);
	}

	@Test
	public void userCanUnCheckCheckbox() {
		$(By.name("rememberMe")).setSelected(true);
		$(By.name("rememberMe")).shouldBe(selected);

		$(By.name("rememberMe")).setSelected(false);
		$(By.name("rememberMe")).shouldNotBe(selected);

		$(By.name("rememberMe")).setSelected(false);
		$(By.name("rememberMe")).shouldNotBe(selected);
	}

}

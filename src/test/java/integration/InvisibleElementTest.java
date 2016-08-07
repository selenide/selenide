package integration;

import org.junit.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class InvisibleElementTest extends IntegrationTest {
	@Before
	public void clickHidesElement() {
		openFile("elements_disappear_on_click.html");
		$("#hide").click();
	}

	@Test
	public void shouldBeHidden() {
		$("#hide").shouldBe(hidden);
	}

	@Test
	public void shouldNotBeVisible() {
		$("#hide").shouldNotBe(visible);
	}

	@Test
	public void shouldHaveTextHide() {
		$("#hide").shouldHave(text("Hide me"));
	}

	@Test
	public void shouldNotHaveTextRemove() {
		$("#hide").shouldNotHave(text("Remove me"));
	}

}

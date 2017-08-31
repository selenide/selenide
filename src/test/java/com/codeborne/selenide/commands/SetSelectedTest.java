package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Command.NO_ARGS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SetSelectedTest {
    WebElement textbox = mock(WebElement.class);
    WebElement button = mock(WebElement.class);
    WebElement checkbox = mock(WebElement.class);
    WebElement radio = mock(WebElement.class);
    WebElement option = mock(WebElement.class);

    SelenideElement proxy = mock(SelenideElement.class);
    WebElementSource locator = mock(WebElementSource.class);

    SetSelected command = new SetSelected();

    @Before
    public void setUp() {
        command.click = mock(Click.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                WebElement element = ((WebElementSource) invocation.getArgument(1)).getWebElement();
                doReturn(!element.isSelected()).when(element).isSelected();
                return null;
            }
        }).when(command.click).execute(any(SelenideElement.class), any(WebElementSource.class), any());

        doReturn("input").when(textbox).getTagName();
        doReturn("button").when(button).getTagName();
        doReturn("input").when(checkbox).getTagName();
        doReturn("checkbox").when(checkbox).getAttribute("type");
        doReturn("input").when(radio).getTagName();
        doReturn("radio").when(radio).getAttribute("type");
        doReturn("option").when(option).getTagName();
    }

    @Test(expected = InvalidStateException.class)
    public void fails_to_select_textbox() {
        doReturn(textbox).when(locator).getWebElement();

        command.execute(proxy, locator, new Object[]{true});

        verify(command.click, never()).execute(proxy, locator, NO_ARGS);
    }

    @Test(expected = InvalidStateException.class)
    public void fails_to_select_button() {
        // TODO: add code
        doReturn(button).when(locator).getWebElement();

        command.execute(proxy, locator, new Object[]{true});

        verify(command.click, never()).execute(proxy, locator, NO_ARGS);
    }

    @Test
    public void selects_checkbox() {
        doReturn(checkbox).when(locator).getWebElement();

        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);

        command.execute(proxy, locator, new Object[]{false});
        verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
        command.execute(proxy, locator, new Object[]{false});
        verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
    }

    @Test(expected = InvalidStateException.class)
    public void fails_to_select_checkbox() {
        doReturn(checkbox).when(locator).getWebElement();
        doReturn("true").when(checkbox).getAttribute("readonly");

        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, never()).execute(proxy, locator, NO_ARGS);
    }

    @Test
    public void selects_radio() {
        // TODO: add code
        doReturn(radio).when(locator).getWebElement();

        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);

        command.execute(proxy, locator, new Object[]{false});
        verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
        command.execute(proxy, locator, new Object[]{false});
        verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
    }

    @Test(expected = InvalidStateException.class)
    public void fails_to_select_radio() {
        // TODO: add code
        doReturn(radio).when(locator).getWebElement();
        doReturn("true").when(radio).getAttribute("readonly");

        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, never()).execute(proxy, locator, NO_ARGS);
    }

    @Test
    public void selects_option() {
        // TODO: add code
        doReturn(option).when(locator).getWebElement();

        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);

        command.execute(proxy, locator, new Object[]{false});
        verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
        command.execute(proxy, locator, new Object[]{false});
        verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
    }

    @Test(expected = InvalidStateException.class)
    public void fails_to_select_option() {
        // TODO: add code
        doReturn(option).when(locator).getWebElement();
        doReturn("true").when(option).getAttribute("readonly");

        command.execute(proxy, locator, new Object[]{true});
        verify(command.click, never()).execute(proxy, locator, NO_ARGS);
    }
}

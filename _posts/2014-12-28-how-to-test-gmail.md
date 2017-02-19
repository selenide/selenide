---
layout: post
title: "How to test GMail"
description: ""
category:
header-text: ""
tags: [GMail, ajax, examples]
---
{% include JB/setup %}

Hi all!

How would you test GMail if you were Google developer?
It's a great exercise, try it once! 

Automated testing of GMail is not trivial. It heavily uses ajax, elements are loaded dynamically, loading may take 
more than few seconds, and there is no reasonable IDs/selectors. You would need to add "wait" for almost every operator!

### Project "GMail test"

But it's possible!
In [Selenide Examples](https://github.com/selenide-examples) series we present a 
[github project for testing GMail](https://github.com/selenide-examples/gmail). It checks inbox content and composes a 
new message. Then it clicks "Undo", edits the message body and sends it again. 
And finally waits until the "Undo" button disappears.


### Video
Here is a short video for demonstrating how it works:
<iframe src="//player.vimeo.com/video/115448433" width="800" height="526" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>

## Let's analyze this
Here is the github project: https://github.com/selenide-examples/gmail

Let's take a look under the hood!

### Settings

Before test run we need to set longer timeout, because GMail element tend to load longer than 4 seconds (which is a
default timeout in Selenide). Let's set 10 seconds. Thought at some conferences with slow WiFi even 10 was not
sufficient long. 

```java
  @BeforeClass
  public static void openInbox() {
    timeout = 10000;
    baseUrl = "http://gmail.com";
    
    open("/");
    $(byText("Loading")).should(disappear);
    login();
  }
```

Have you noticed how we wait until the page gets loaded? This show the power of Selenide: it's easy to find element
by text, and it's easy to wait until it disappears.

### Login

Performing user login is easy:

```java
  private static void login() {
    $("#Email").val(System.getProperty("gmail.username", "enter-your-gmail-username"));
    $("#Passwd").val(System.getProperty("gmail.password", "enter-your-gmail-password"));
    $("#signIn").click();
    $(".error-msg").waitUntil(disappears, 2000);
  }
```

The last line is needed to fail fast if you configured wrong username or password.

### Unread messages counter

In a real life, I would run tests with a predefined data set. Say, with 4 unread messages. And check for presence
of text "Inbox (4)":

```java
  @Test
  public void showsNumberOfUnreadMessages() {
    $(By.xpath("//div[@role='navigation']")).find(withText("Inbox (4)")).shouldBe(visible);
  }
```

(But our case is harder: we need to test web application with real data that is not constant. Therefore let's limit
ourselves to just check availability of text "Inbox".)

### Verifying inbox content

Unfortunately all the GMail selectors are obfuscated. We cannot ID or other selectors.
So, let's just verify that some texts are present on a page. We know that such messages should be definitely in my inbox:

```java
  @Test
  public void inboxShowsUnreadMessages() {
    $$(byText("Gmail Team")).filter(visible).shouldHave(size(1));
    $$(byText("LastPass")).filter(visible).shouldHave(size(3));
    $$(byText("Pivotal Tracker")).filter(visible).shouldHave(size(3));
  }
```

### Refreshing inbox

GMail interface has a "Refresh" button for reloading inbox. Let's find it by attribute `title`=`Refresh`.

```java
  @Test
  public void userCanRefreshMessages() {
    // In a real life:: INSERT INTO messages ...
    $(by("title", "Refresh")).click();
    // In a real life: verify that the new message has appeared in the inbox
  }
```

In a real project I would add a new message into database, and verify that the new message has appeared in the inbox
after pressing "Refresh" button.

### New message

To compose a new message, let's click the "COMPOSE" button:

```java
    $(byText("COMPOSE")).click();
```

Enter recipient address, subject and text:

```java
    $(By.name("to")).val("andrei.solntsev@gmail.com").pressTab();
    $(by("placeholder", "Subject")).val("ConfetQA demo!").pressTab();

    $(".editable").val("Hello braza!").pressEnter();
    $(byText("Send")).click();
```

Isn't it easy?

And finally, let's verify that the message has been sent:

```java
    $(withText("Your message has been sent.")).shouldBe(visible);
```

### Undo - redo

There is a very cool (but _experimental_) feature in GMail - "Undo". After an email has been sent,
you can undo it during next 10 seconds. By clicking the "Undo" button, user can cancel sending the last message and
go back to editing. It's incredibly useful when, say, you sent message to a wrong recipient.

So, let's try to test "Undo" feature. In the end of previous test, let's add the following lines:

```java
    $(byText("Undo")).click();
    highlight($(byText("Sending has been undone.")).should(appear));
```

Edit the message body and send again:

```java
    $(".editable").should(appear).append("Hello from ConfetQA Selen").pressEnter().pressEnter();

    $(byText("Send")).click();
```

And finally wait for 10 seconds until the "Undo" button disappears:

```java
    highlight($(withText("Your message has been sent.")).should(appear));
    highlight($(byText("Undo")).should(appear)).waitUntil(disappears, 12000);
    $(byText("Sent Mail")).click();
```

The message has been finally sent.

Now GMail is tested. Google can sleep peacefully. :)

## Findings

As you see, web interface without IDs can also be tested. Long loading time and ajax requests are not obstacles.
Dynamic content is not stopper. The following are simple receipts to overcome these problems:

* *Find elements by text* <br>
This is how real users behave. This is less dependent on application implementation details.
* *Test only important things* <br>
There are still a lot of things in GMail that left untested. It seems that it's not possible to test them (unable to
use pre-generated test data in tests, absence of static locators). But it's not that important - important is the fact 
that the critical functionality is tested: inbox content and sending new message. Always start from testing 
functionality that is most critical for business. 
* *Use appropriate instruments* <br>
Selenide automatically resolves problems with timeouts, ajax, dynamic content, searching by text.
Tests are not polluted with long scary XPath. Tests contain only business logic. Tests are readable.

<br>
Wish you simple tests!

<div class="author">
  Andrei Solntsev<br/> 
  <a href="http://selenide.org">selenide.org</a>
</div>

<br/>
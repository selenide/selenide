# Selenide = UI Testing Framework powered by Selenium WebDriver

![Build Status](https://github.com/selenide/selenide/actions/workflows/test.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/com.codeborne/selenide.svg)](https://central.sonatype.com/search?q=selenide&namespace=com.codeborne)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg)](https://github.com/selenide/selenide/blob/main/LICENSE)
![Free](https://img.shields.io/badge/free-open--source-green.svg)

[![Join the chat at https://gitter.im/codeborne/selenide](https://img.shields.io/badge/welcome%20to-chat-green.svg)](https://gitter.im/codeborne/selenide?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Присоединяйся к чату https://gitter.im/codeborne/selenide-ru](https://img.shields.io/badge/%D0%B7%D0%B0%D1%85%D0%BE%D0%B4%D0%B8%20%D0%B2-%D1%87%D0%B0%D1%82-green.svg)](https://gitter.im/codeborne/selenide-ru?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Follow](https://img.shields.io/twitter/follow/selenide.svg?style=social&label=Follow)](https://twitter.com/selenide)
[![Telegram channel](https://img.shields.io/badge/Telegram-channel-blue.svg)](https://t.me/selenide)
[![Telegram чат](https://img.shields.io/badge/Telegram-%D1%87%D0%B0%D1%82-blue.svg)](https://t.me/selenide_ru)

## What is Selenide?

Selenide is a framework for writing easy-to-read and easy-to-maintain automated tests in Java.
It defines concise fluent API, natural language assertions and does some magic for ajax-based applications to let you focus entirely on the business logic of your tests.

Selenide is based on and is compatible to Selenium WebDriver 4.0+

```java
@Test
public void login() {
  open("/login");
  $(By.name("user.name")).setValue("johny");
  $("#submit").click();
  $("#username").shouldHave(text("Hello, Johny!"));
}
```

Look for [detailed comparison of Selenide and Selenium WebDriver API](https://github.com/selenide/selenide/wiki/Selenide-vs-Selenium).

#### Selenide for mobile apps
You can use Selenide for testing mobile applications. See plugin [selenide-appium](https://github.com/selenide/selenide/tree/main/modules/appium).

#### Selenide with Selenoid
You can use Selenide for running tests in Selenoid containers. See plugin [selenide-selenoid](https://github.com/selenide/selenide/tree/main/modules/selenoid).

#### Selenide with Selenium Grid
You can use Selenide for running tests in Selenium Grid. See plugin [selenide-grid](https://github.com/selenide/selenide/tree/main/modules/grid).


## Changelog

Here is [CHANGELOG](https://github.com/selenide/selenide/blob/main/CHANGELOG.md)

## How to start?

Just put selenide.jar to your project and import the following methods: `import static com.codeborne.selenide.Selenide.*;`

Look for [Quick Start](https://github.com/selenide/selenide/wiki/Quick-Start) for details.

## Resources

* First of all, [selenide.org](http://selenide.org)
* For bustlers: [How to start writing UI tests in 10 minutes](http://selenide.org/2014/10/01/how-to-start-writing-ui-tests/)
* For developers: [Selenide presentation on Devoxx 2015](http://selenide.org/2015/11/13/selenide-on-devoxx/)
* For QA engineers: [Selenide presentation on SeleniumConf 2015](http://selenide.org/2015/09/23/selenide-on-seleniumconf/)
* For russians: [Selenide presentation on SeleniumCamp 2015](http://seleniumcamp.com/materials/good-short-test/)

## FAQ

See [Frequently asked questions](http://selenide.org/faq.html)

## Posts
- Set-up environment with gradle, junit5, allure and selenide -- read a [post](https://medium.com/@rosolko/simple-allure-2-configuration-for-gradle-8cd3810658dd) on medium, grab from [GitHub](https://github.com/rosolko/allure-gradle-configuration)
- Small step do dramatically improve your tests speed -- read a [post](https://medium.com/@rosolko/boost-you-autotests-with-fast-authorization-b3eee52ecc19) on medium
- Another way to improve tests speed -- read a [post](https://medium.com/@rosolko/fast-authorization-level-local-storage-6c84e9b3cef1) on medium
- [Configure Selenide to work with Selenoid](https://medium.com/@rosolko/configure-selenide-to-work-with-selenoid-8835cd6dc7d2)

## Contributing

Contributions to Selenide are both welcomed and appreciated. 
See [CONTRIBUTING.md](CONTRIBUTING.md) for specific guidelines.

Feel free to fork, clone, build, run tests and contribute pull requests for Selenide!


## Authors

Selenide was originally designed and developed by [Andrei Solntsev](http://asolntsev.github.io/) in 2011-2025
 and is maintained by [a group of enthusiast](https://github.com/orgs/selenide/people).

## Thanks

Many thanks to these incredible tools that help us create open-source software:

[![Intellij IDEA](https://selenide.org/images/jetbrains.svg)](https://jb.gg/OpenSource)
[![BrowserStack](https://www.browserstack.com/images/mail/browserstack-logo-footer.png)](https://www.browserstack.com)
[![LambdaTest](https://www.lambdatest.com/support/img/logo.svg)](https://www.lambdatest.com/)

## License

Selenide is open-source project, and distributed under the [MIT](http://choosealicense.com/licenses/mit/) license

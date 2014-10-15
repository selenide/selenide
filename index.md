---
layout: default
title: "Selenide: concise UI tests in Java"
tagline:
---
{% include JB/setup %}

<script>
  $(function(){
    function showNewsLine(newsLine) {
      newsLine.effect( "slide", "slow" );
      newsLine.effect( "shake", "slow" );
    }

    $("header .news .news-line").each(function(i, newsLine) {
      setTimeout(function() {
        showNewsLine($(newsLine));
      }, 500 + i * 1000);
    });
  });
</script>

<div class="short wiki">
  <div class="wrapper-color-content">
    <h3>What is Selenide?</h3>
    <h4>Selenide is a wrapper for <a href="http://docs.seleniumhq.org/projects/webdriver/" target="_blank">Selenium WebDriver</a> that brings the following advantages:</h4>

    ✓ Concise API for tests&nbsp;&nbsp;
    ✓ Ajax support&nbsp;&nbsp;
    ✓ True Page Objects&nbsp;&nbsp;
    ✓ jQuery-style selectors<br>
    You don't need to think how to shutdown browser, handle timeouts or write monstrous code! Read wiki for more details<br>

    <a href="https://github.com/codeborne/selenide/wiki" target="_blank"> <img style="margin-top: 15px; margin-bottom: -33px" src="{{ BASE_PATH }}/images/arrow-down.png" width="30" height="55" border="0"/> </a>
  </div>
</div>

{% include themes/ingmar/_quicklinks.html %}


<div class="short howto">
  <div class="wrapper-color-content">
    <h3>Quick start</h3>
    <h4>It's extremely easy to start using Selenide. Definitely not a rocket science.</h4>

    Just add <a href="http://search.maven.org/remotecontent?filepath=com/codeborne/selenide/{{site.SELENIDE_VERSION}}/selenide-{{site.SELENIDE_VERSION}}.jar">selenide.jar</a> to your project and you are done.<br>
    See Quick start guide for more details.<br>

    <a href="{{ BASE_PATH }}/quick-start.html"> <img style="margin-top: 15px; margin-bottom: -33px" src="{{ BASE_PATH }}/images/arrow-down.png" width="30" height="55" border="0"/> </a>
  </div>
</div>

<div class="short docs">
  <div class="wrapper-color-content">
    <h3>Documentation</h3>
    <h4>Poor software <span class="bold">doesn't have</span> documentation.
    Brilliant software <span class="bold">doesn't need</span> documentation.</h4>

    We are proud to claim that Selenide is so simple that you don't need to read tons of documentation.<br/>
    The whole work with Selenide consists of three simple things! <br>
    <a href="{{ BASE_PATH }}/documentation.html"> <img style="margin-top: 15px; margin-bottom: -33px" src="{{ BASE_PATH }}/images/arrow-down.png" width="30" height="55" border="0"/> </a>
  </div>
</div>

<div class="short testimonials">
  <div class="wrapper-color-content">
    <h3>Testimonials</h3>
    <h4>"Selenide is really nice and capable tool for writing functional/acceptance tests for your browser-based UI. I encourage you to check Selenide out and give it a try."</h4>

    KAUR MÄTAS, <br>
    LiveRebel engineer at ZeroTurnaround<br>

    <a href="{{ BASE_PATH }}/users.html"> <img style="margin-top: 15px; margin-bottom: -33px" src="{{ BASE_PATH }}/images/arrow-down.png" width="30" height="55" border="0"/> </a>
  </div>
</div>


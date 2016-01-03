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
    <h4>Selenide is a framework for test automation powered by <a href="http://docs.seleniumhq.org/projects/webdriver/" target="_blank">Selenium WebDriver</a> that brings the following advantages:</h4>
    <div class="highlights">
      <a href="/documentation.html">Concise fluent API for tests</a>
      <span>Ajax support for stable tests</span>
      <span>Powerful selectors</span>
      <a href="/documentation/page-objects.html">True Page Objects</a>
    </div>
    You don't need to think how to shutdown browser, handle timeouts and StaleElement Exceptions or search for relevant log lines, debugging your tests.<br>
    Just focus on your business logic and let Selenide do the rest!<br>

    <a href="/quick-start.html"> <img style="margin-top: 15px; margin-bottom: -33px" src="{{ BASE_PATH }}/images/arrow-down.png" width="30" height="55" border="0"/> </a>
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

<div class="short feedback">
<div class="wrapper-color-content">

  <h3>Contacts</h3>
  <h4>Do you want to talk about it?</h4>

  <div>Where You can ask question or discuss any topic about Selenide in English:</div>
  <div class="highlights">
    <a href="mailto:selenide@googlegroups.com">Google group</a>
    <a href="mailto:andrei dot solntsev at gmail dot com">My email</a>
  </div>

</div></div>

<div class="short testimonials">
  <div class="wrapper-color-content">
    <h3>Testimonials</h3>
    <h4>"Selenide is really nice and capable tool for writing functional/acceptance tests for your browser-based UI. I encourage you to check Selenide out and give it a try."</h4>

    KAUR MÄTAS, <br>
    LiveRebel engineer at ZeroTurnaround<br>

    <a href="{{ BASE_PATH }}/users.html"> <img style="margin-top: 15px; margin-bottom: -33px" src="{{ BASE_PATH }}/images/arrow-down.png" width="30" height="55" border="0"/> </a>
  </div>
</div>

<div class="short">
  <a class="twitter-timeline" href="https://twitter.com/jselenide" data-widget-id="397446026996359168">Tweets by @jselenide</a>
  <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
</div>

<a name="thanks"></a>
<div class="short thanks">
  <h4>Many thanks to:</h4>
  <img src="https://www.jetbrains.com/idea/docs/logo_intellij_idea.png">
  <img src="{{BASE_PATH}}/images/yourkit.png" target="_blank" alt="YourKit" style="width: 150px;"/>
</div>

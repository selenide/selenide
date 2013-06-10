<ul class="gray-boxes">
  <li><a href="https://github.com/codeborne/selenide" target="_blank">View on <strong>GitHub</strong></a></li>
  <li><a href="http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.codeborne%22%20AND%20a%3A%22selenide%22" target="_blank">Search in <strong>Maven</strong></a></li>
  <li><a href="http://twitter.com/jselenide" target="_blank">Follow at <strong>Twitter</strong></a></li>
</ul>

<a class="right" title="Subscribe to this blog" href="{{ BASE_PATH }}/rss.xml">
  <abbr title="Really Simple Syndication">RSS</abbr>
</a>

<ul class="main-menu-pages">
  <li><a href="{{ BASE_PATH }}/quick-start.html">Quick start</a></li>
  <li><a href="{{ BASE_PATH }}/users.html">Who uses Selenide?</a></li>
  <li><a href="{{ BASE_PATH }}/contacts.html">Tell us about yourself!</a></li>
</ul>

<h3>Blog</h3>
<div class="archive">
  {% assign posts_collate = site.posts %}
  {% include JB/posts_collate %}
  <a href="{{ BASE_PATH }}/archive.html" class="right small">Blog archive</a>
</div>



<a style="display:none;" class="right" title="Subscribe to this blog" href="{{ BASE_PATH }}/rss.xml">
  <abbr title="Really Simple Syndication">RSS</abbr>
</a>

<ul class="main-menu-pages">
  <li><a href="{{ BASE_PATH }}/quick-start.html">Quick start</a></li>
  <li><a href="{{ BASE_PATH }}/documentation.html">Documentation</a></li>
  <li style="display:none;"><a href="https://github.com/codeborne/selenide/wiki">Wiki</a></li>
  <li><a href="{{ BASE_PATH }}/javadoc/2.3" target="_blank">Javadoc</a></li>
  <li><a href="{{ BASE_PATH }}/users.html">Who uses Selenide?</a></li>
  <li style="display:none;"><a href="{{ BASE_PATH }}/quotes.html">What users say?</a></li>
  <li><a href="{{ BASE_PATH }}/contacts.html">Feedback</a></li>
  <li style="display:none;"><a href="{{ BASE_PATH }}/thanks.html">Our thanks</a></li>
</ul>

<h3 style="display:none">Blog</h3>
<div class="archive" style="display:none">
  {% assign posts_collate = site.posts %}
  {% include JB/posts_collate %}
  <a href="{{ BASE_PATH }}/archive.html" class="right small">Blog archive</a>
</div>

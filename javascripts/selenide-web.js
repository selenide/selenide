function shortTweet(tweetText) {
  return tweetText.length < 80 ? tweetText : tweetText.substring(0, 80) + "...";
}

function showTwitter() {
  $.getJSON("https://api.twitter.com/1/statuses/user_timeline.json"
    + "?screen_name=jselenide&count=5&callback=?", function(tweets) {
    var b = $("#twitter").find("tbody");
    $.each(tweets, function(i, tweet) {
      var date = new Date(tweet.created_at.replace(/ (UTC ?)?\+/, " UTC+"));
      var h = $("<tr><td class=title><span class='date'></span> <a></a></td></tr>");
      h.find(".date").html(date.getDate() + "." + date.getMonth() + "." + (1900+date.getYear()));
      h.find(".title a")
        .attr("href", "http://twitter.com/" + tweet.user.screen_name + "/status/" + tweet.id_str)
        .attr('title', tweet.text)
        .text(shortTweet(tweet.text));

      b.append(h);
    });
  });
}

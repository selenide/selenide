---
layout: post
title: "You find wrong bugs"
description: ""
category:
header-text: ""
tags: [Quality assurance, SQA]
---
{% include JB/setup %}

When I was on the last SQA Days conference in Saint-Petersburg, there happened an incident.
At that moment, I didn't realize what happened. I just smiled. And only a few days later I got the idea.

### So,

when I finished [my speech](http://sqadays.com/ru/talk/25882) about really effective automated testing, a man 
took a microphone and started asking criticizing questions. After a few answers of mine, he sat down discordant, saying:
 
> You find wrong bugs.

I just smiled.

Only a few days later I catched up why he was basically wrong.

No, I am not going to say that we find good bugs.

I am going to say that

<div class="center">
<b>WE DO NOT SEARCH FOR BUGS AT ALL.</b>
</div>

<br>

### What is Quality Assurance

This is very, very important. Every QA engineer should know it from the childhood. 

QA means "Quality Assurance". It means "being sure that software works". 
Our first job is not to <u>find all bugs</u>. Our first job is to be sure that <u>software does its job</u>.

### For example
If we develop an internet-shop for selling TVs, the job of QA is to assure that user can find TV, press the big "Buy" 
button and pay money. This is the most important job.

Maybe banner looks bad. Maybe "Print PDF" button opens a new empty window. May be the "login" disappears sometimes.
Surely, these are errors, but these are secondary errors. Surely, it's bad if you have such errors, but the
business still *will work*. 
But if the "Buy" button is not clickable - it's the end of the business. User cannot pay his money. It's a big difference!

And there are a plenty of thirdly errors - when user enters 1000 chinese characters into "login" field and gets 500 error.
No one normal user will do that. And if he does, let him get this 500 error.

I am not saying that these secondary and thirdly errors should not be searched and fixed. Sure, You should find and fix them. 
But only when you are sure that *the business works*. 

So,

### the main conclusion:

1. Your **first test** should be "**green path**". A typical scenario that bring the money:<br>
  Find TV - "Buy" - pay. 
  Whatever you do: write automated tests or test manually. Your first test - green path.
  
  This approach has one psychological disadvantage. This is the most boring test.
  This is a guarantee to find less bugs than your colleagues.
  This is a guarantee to make your boss feeling that you are not doing anything useful.
  But this is still the most important job of QA department.

2. All these **stupid tests** about "**1000 chinese characters** in the login field" - you can do them, but only if
 all other things are ok. And you have free time that you cannot spend to anything else. In practice, that means - never.
 
 But this is exactly the sort of bugs that QA engineers do generate during every release. Again and again. 
 These meaningless bug reports waste your time, waste your resources, waste your attention, thus letting the real bugs
 appear unnoticed. 

 But this is a guarantee to find 50 new bugs at every release.
 This is a guarantee to get a top employee of the month.
 It's call "local optimization", and this is the most powerful of devil's inventions. 
   

Do not run chase for bugs. You cannot catch all bugs in the world.
It's like pimple: you squeeze one - three more will appear.
You don't need to watch the pimples - you need to monitor the overall health. Do sport, do healthy eating, do hygiene - and pimples will disappear automatically. 
Do write the most important tests first. Make them fast. Make them stable. 
It's better to have tests green (and you will feel alarm when then get red once) than having a lot of tests red 
(and nobody will catch sight of the real problem).

That's because I am saying:

### We do not search for bugs 

We assure that software works. We write automated test to assure that user can buy TV. 
It allows us to avoid wasting time for maintaining plenty of meaningless bug reports and useless tests. It frees up 
our resources for quickly reacting to real problems that will always appear in production, not depending on QA department existence.
It allows us to develop and maintain a complicated software with a small team. Small, but very effective team.

*In testo veritas!*

<br/>
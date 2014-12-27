---
layout: post
title: "How to allocate time for refactoring?"
description: ""
category:
header-text: ""
tags: [Refactoring, Clean Code, TDD, Test driven development]
---
{% include JB/setup %}

One of the most common IT questions is: _How to allocate time for refactoring_?

It's common for both developers and testers. Both get very quickly a heap of dirty unreadable code, a plenty of
slow, unmaintainable, unreadable tests. It's getting harder and harder to live with it.
Development/testing is getting slower. Motivation decreases. Sometimes you happen to clean up
some lines in the evening time, but... it seems to be something wrong with the evening-time work?..

* Where is the justice?
* Am I the only who needs it?
* Why doesn't my boss allocate me time for refactoring?

I know. I also went through it.

### No way

Now, let's face the truth. If somebody comes to me and asks to allocate one week for refactoring, **I will refuse**. 

Let me explain why.
Because refactoring can last endlessly, but somebody should do _the work_.

10 years ago I was hurt to hear this. Now I am saying it by myself.
Moreover, I claim that cleaning code at evening hours is not unfairly - it's **unprofessional**.

So, have I lost my ideal? 

### Absolutely not.
Exactly the opposite. Now I am pretty sure that refactoring is absolutely required. 
Code must be continuously cleaned up through thick and thin. It just needs to be performed a little bit differently.

You must not ask anybody to allocate a separate time for refactoring. Refactoring is not a separate thing, 
independent from development/testing. Refactoring **is** part of coding. Refactoring **is** part of writing automated 
tests. It's the same natural and necessary part as typing, compilation and run.

The following is my receipt:

## **Small steps**!

Recall TDD. Your process should look like this:

 * Write test (red) - 1 hour
 * Write code (to make test green) - 1 hour
 * Refactor (to make code/tests readable and clean) - 1 hour
 * Make a tea, go to step 1.
 
This is a continuous process. Small steps. Continuous progress. Hot tea.

(Depending on programming language, project and people "1 hour" can be replaced by "5 minutes" or whatever else.)

### But what my boss would think about it?

Nobody will blame you that you are wasting time, **if** you show **continuous progress**. If you create new features
every day. In case of testers - create new automated tests every day.

> Nobody ever got fired for doing feature in 6 hours instead of 5 hours. 

Think about it. You boss does not know exactly, how long this feature should be implemented. It means that he can
never prove that you wasted additional time for writing tests or refactoring. The opposite is also true - you cannot 
prove (with facts and numbers) that refactoring speeds up the development time. Both of you can prove nothing. So, 
just work so as you believe is good to work. 

Don't get me wrong, I am not calling for sabotage. I am not calling for doing your job longer than needed.
Just opposite: I believe that this simple receipt lets you do your job faster:

> Small steps: Test. Code. Refactor.

### So, I don't need to ask for permission?

If somebody comes to me asking to allocate time for refactoring, I will reject. For the very simple reason: he does not
know the _TDD mantra_. He will almost certainly _refucktor_ this week, and nothing gets remarkably better as a result.

Good doctor doesn't ask permission to wash his hands before a surgery. Good artist doesn't ask permission to spend time 
for tuning his instrument. Somehow all the people understand that this is needed. Without _wasting time_ on washing hands 
and tuning the instrument, they just cannot do their job well. So, are you worse, my dear _professional_ 
developers and testers? 

But don't forget that doctor manages to cure at least somebody in a day. And artist manages to play at least few songs
per concert. That's why refactoring for a week, and not refactoring at all are both bad.

Small steps. Continuous progress. Tea with ginger.

And with cookies.

<br/>
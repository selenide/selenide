
Any contributions to Selenide are both welcomed and appreciated.  

## How can you help?

It doesn't necessarily have to be code. There are many ways you can help Selenide:
- Answer questions in chats/forums/stackoverflow/etc.
- Write in your blog/twitter/medium about Selenide or its usages in specific situations.
- Discuss ideas in [our brainstorms](https://github.com/selenide/selenide/issues?q=is%3Aissue+is%3Aopen+label%3Abrainstorm)
- Give your feedback on [feature requests](https://github.com/selenide/selenide/issues?q=is%3Aissue+is%3Aopen+label%3Afeature)
- Help to justify or protest requests that [we are not sure about](https://github.com/selenide/selenide/issues?q=is%3Aissue+is%3Aopen+label%3A%22not+sure%22)
- Help to reproduce an issue that [we cannot reproduce](https://github.com/selenide/selenide/issues?q=is%3Aissue+is%3Aclosed+label%3A%22cannot+preproduce%22)
- Make code review for any of [pull requests](https://github.com/selenide/selenide/pulls)  -  especially with label "not sure".
- And finally, code! 
  - Find any [issue with label "help wanted"](https://github.com/selenide/selenide/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
  - Add a comment like "I am working on it" (provide some details what's your plan)
  - Send a pull request


## Branches
Selenide has a very simple branching strategy
- The latest state is always in `main` branch
- Every bugfix/feature is developed in a separate branches created from `main` branch
- Once bugfix/feature is accepted, someone of maintainers merges it to `main` branch
- When we make a release, we make it from `main` branches

## How to send a pull request?
- Create a GitHub account
- Fork Selenide repo  
  - go to https://github.com/selenide/selenide
  - click "Fork" link in the right upper corner
- Check out your Selenide copy:  
  - `git clone https://github.com/username/selenide`
- Create a new branch from `main` branch
  - `git checkout -b remove-duplicate-screenshots-in-allure`   (choose any reasonable branch name)
- Make your changes
  - it's recommended to add a line to [CHANGELOG.md](CHANGELOG.md)
- Run tests:
  - required: `./gradlew check`
  - optional: some other tests (see below) 
- Commit, say a little prayer and push
  - it's recommended to start commit message with issue id, like `#ID remove duplicate screenshots in Allure`
  - also read a [commit message tutorial](https://chris.beams.io/posts/git-commit/)
  - prefer small independent commits to one big commit with many changes
- go to http://github.com/username/selenide/
  - you will see an automatic suggestion to create a pull request
  - accept it, fill all the required fields.
  - wait. Don't worry, we review pull requests often. We will contact you.  


## How to build Selenide locally?

    git clone https://github.com/selenide/selenide.git
    cd selenide
    ./gradlew    (or `gradlew.bat` on Windows)

After build, you'll find Selenide's .jar file under `build/libs/` directory as well as in local maven repository at `~/.m2/repository/com/codeborne/selenide/<version>/selenide-<version>.jar`.


### How to run tests?
Selenide project has two types of tests: unit tests and UI tests. 

- Command `./gradlew` already runs unit-tests
- To run other types of tests, use the following commands:
  - `./gradlew firefox`
  - `./gradlew firefox_headless`
  - `./gradlew chrome`
  - `./gradlew chrome_headless`
  - `./gradlew grid`
  - `./gradlew ie`
  - `./gradlew edge`
  - `./gradlew testng`  
  Note that you typically don't need to run them locally because our Continuous Integration server will run them anyway when you send a pull request.

## Continuous integration

Selenide uses [Travis CI](https://travis-ci.org/github/selenide/selenide/branches) to automatically build any branches and pull requests, running all the automated tests.  
However, you don't need to open Travis in most cases. When you send a pull request, you will see its build status right in the PR.  

## Welcome to the club!
Feel free to fork, clone, build, run tests and contribute pull requests for Selenide!

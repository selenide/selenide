## How to build Selenide?

    git clone https://github.com/selenide/selenide.git
    cd selenide
    ./gradlew jar

After build you'll find Selenide's .jar file under `build/libs/` directory.

To build Selenide on Windows use `gradlew.bat jar` command.


## Run test locally inside docker containers

- Syncing browser images from existing configuration file
  - Install [jq](https://stedolan.github.io/jq)
  - Extract image names from JSON and automatically pull them:
     > cat /config/selenoid/browsers.json | jq -r '..|.image?|strings' | xargs -I{} docker pull {}
- Start selenoid container
  - Install [docker](https://www.docker.com/products/docker-desktop)
  - Based on your operation system execute script. Check official [document](https://aerokube.com/selenoid/latest/#_option_2_start_selenoid_container) for correct script.
- Start tests passing `selenide.remote` configuration variable with `http://localhost:4444/wd/hub` value



## Welcome to the club!
Feel free to fork, clone, build, run tests and contribute pull requests for Selenide!

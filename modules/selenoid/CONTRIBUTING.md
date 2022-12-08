# How to run tests

## Running unit-tests
> ./gradlew test

## Running UI tests
> ./gradlew test-chrome

To run browser tests, you need to have Selenoid installed.
Also, we assume you have file `~/.aerokube/selenoid/browsers.json` with such a block:

```json
{
  "chrome": {
    "default": "chrome",
    "versions": {
      "91.0": {
        "image": "dumbdumbych/selenium_vnc_chrome_arm64:91.0.b",
        "port": "4444",
        "path": "/",
        "privileged": true
      }
    }
  }
}
```
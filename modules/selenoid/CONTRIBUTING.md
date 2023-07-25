# How to run tests

## Running unit-tests
> ./gradlew test

## Running integration tests
> ./gradlew selenoidTests

To run browser tests, you need to have Selenoid installed.
Also, we assume you have file `~/.aerokube/selenoid/browsers.json` with such a block:

```json
{
  "chrome": {
    "default": "chrome",
    "versions": {
      "100.0": {
        "image": "sskorol/selenoid_chromium_vnc:100.0",
        "port": "4444",
        "path": "/",
        "privileged": true
      }
    }
  }
}
```

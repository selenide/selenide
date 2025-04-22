Selenide Grid plugin
================================

A [Selenide](https://selenide.org) extension for working with Selenium Grid

## Why it's needed?

Most Selenide features work with Selenium Grid out of the box.  
There are only a few things that might not work: proxy and downloading files. 
1. Selenide proxy generally works with Selenium Grid, except one case: when tests and Selenium Grid are run on different machines, 
and "test machine" is not accessible from "grid machine". 
2. Downloading files: Selenide has 4 methods for downloading files: `HTTPGET`, `PROXY`, `FOLDER` and `CDP`.   
   * `HTTPGET` and `PROXY` work in Selenium Grid as usually
   * `FOLDER` and `CDP` don't work in Selenium Grid because the downloads folder is inside the container.   

This problem is addressed by `selenide-grid` plugin: _it can get files from Selenium Grid container_. 

## Features
Currently, the plugin supports the following features:
* File download 

# Not yet supported
* Access to clipboard  (get & set & verify clipboard contents)
* Saving videos

## Usage
1. Import `com.codeborne:selenide-grid:7.9.1`
2. Set `Configuration.fileDownload = FOLDER;`
3. Use method `$.download()` as usually.

## How it works?
When you call `$("a#report").download()` method, selenide-grid plugin
1. clicks the element,
2. lets the browser download file to its default location, 
3. uses [Selenium Grid API](https://www.selenium.dev/documentation/grid/configuration/cli_options/#enabling-managed-downloads-by-the-node) to get the downloaded file.  

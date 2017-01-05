/*
*   This package contains useful helpers for integration tests
*   HtmlBuilderForGivens
*       contains tools to build some html page in given actions
*           example
*                Given.openedPageWithBody(
*                        "<h2 id='second'>Heading 2</h2>"
*                        );
*                When.withBodyTimedOut(250,
*                        "<a href='#second' style='display:none'>go to Heading 2</a>",
*                        "<h2 id='second'>Heading 2</h2>"
*                        );
*                When.executeScriptWithTimeout(500,
*                        "document.getElementsByTagName('a')[0].style = 'display:block';"
*                        );
*/
package integration.helpers;

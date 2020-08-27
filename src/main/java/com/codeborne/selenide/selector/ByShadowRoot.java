package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class ByShadowRoot {

  /**
   * Find target elements inside shadow-root that attached to shadow-host.
   * <br/> Supports inner shadow-hosts.
   *
   * <br/> For example: shadow-host &gt; target-element
   * <pre>
   * ex1) find all shadow-host
   *   //*
   * ex2) find shadow-host chidlens and tagName is div
   *   div
   * ex3) find all shadow-host and tagName is span in div shadow-host
   *   div//span
   * ex4) find shadow-host chidlens and tagName is span in div shadow-host
   *   div/span
   * ex5) find current shadow-host
   *   null or ""
   * </pre>
   *
   * @param shadowHost       CSS expression of the shadow-host with attached shadow-root and shadow-host tree split is "/" or "//".
   * @param target           CSS expression of target element
   * @return A By which locates elements by CSS inside shadow-root.
   */
  @CheckReturnValue
  @Nonnull
  public static By cssSelector(final String shadowHost, final String target) {
    return new ByShadowRootCss(shadowHost, target);
  }
  public static class ShadowRootFinder {
    private Function<SearchContext, Boolean> func;
    private boolean finished = false;
    public static void find(final SearchContext host, String selector, Function<SearchContext, Boolean> func) {
      selector = selector.replace("\\(>{3,}\\)", "(>>)").trim();
      if(0<selector.length()){
        if(!Pattern.compile("^\\s*\\(>+\\)").matcher(selector).find()){
          selector = "(>)"+selector;
        }
      }
      ShadowRootFinder finder = new ShadowRootFinder();
      finder.func = func;
      if(isShadowRootAttached(host)){
        finder.findShadow(host, selector);
      } else {
        finder.findDeeply(host, selector);
      }
    }
    
    private void findShadow(final SearchContext host, final String selector) {
      if(finished || !isShadowRootAttached(host)){
        return;
      }
      if(0==selector.length()) {
        if(this.func.apply(host)){
          finished = true;
        }
        return;
      }
      findDeeply(host, selector);
    }

    private void findDeeply(final SearchContext host, final String selector) {
      final Pattern ptn = Pattern.compile("\\s*(\\(>+\\))\\s*(.*?)(\\(>+\\).*$|$)");
      final Matcher m = ptn.matcher(selector);
      if (!m.find()) {
          return;
      }
      final String breakkey = m.group(1);
      final String query = m.group(2);
      final String nextSelector = m.group(3);

      if ("(>)".equals(breakkey)) {
        findElementsByCss(host, query).forEach(elm -> {
          findShadow(elm, nextSelector);
        });
      }
      if ("(>>)".equals(breakkey)) {
        findElementsByCss(host, query).forEach(elm -> {
          findShadow(elm, nextSelector);
        });
        findElementsByCss(host, "*").forEach(elm -> {
          findShadow(elm, selector);
        });
      }
    }

    private static JavascriptExecutor getJavascriptExecutor(final SearchContext context) {
      JavascriptExecutor jsExecutor;
      if (context instanceof JavascriptExecutor) {
        jsExecutor = (JavascriptExecutor) context;
      } else {
        jsExecutor = (JavascriptExecutor) ((WrapsDriver) context).getWrappedDriver();
      }
      return jsExecutor;
    }

    private static boolean isShadowRootAttached(final SearchContext host) {
      return getJavascriptExecutor(host).executeScript("return arguments[0].shadowRoot", host) != null;
    }

    @SuppressWarnings("unchecked")
    private static List<SearchContext> findElementsByCss(final SearchContext host, final String css) {
      return (List<SearchContext>) getJavascriptExecutor(host)
          .executeScript("return (arguments[0].shadowRoot||arguments[0]).querySelectorAll(arguments[1])", host, css);
    }

    @SuppressWarnings("unchecked")
    private static List<SearchContext> findElementsByXpath(final SearchContext host, final String xpath) {
      final StringBuilder sb = new StringBuilder();
      sb.append("const r=[],x=document.evaluate(arguments[1], arguments[0].shadowRoot||arguments[0], null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);");
      sb.append("while(n=result.iterateNext()){r.push(n)}return r;");
      return (List<SearchContext>) getJavascriptExecutor(host)
          .executeScript(sb.toString(), host, xpath);
    }
  }

  @ParametersAreNonnullByDefault
  public static class ByShadowRootCss extends By implements Serializable {
    private static final long serialVersionUID = -1230258723099459238L;

    private final String shadowHost;
    private final String target;

    ByShadowRootCss(final String shadowHost, final String target) {
      this.shadowHost = shadowHost;
      this.target = target;
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public WebElement findElement(final SearchContext context) {
      final SearchContext root = context instanceof WebElement ? context : context.findElement(By.xpath("/*"));
      if (null != this.target && 0 < this.target.length()) {
        final List<WebElement> hosts = getShadowRoots(root, shadowHost, -1);
        final WebElement targetWebElement = getElementInsideShadowTree(hosts, target);
        if (targetWebElement == null) {
          throw new NoSuchElementException("The element was not found: {" + getSelectorString() + "}");
        }
        return targetWebElement;
      }
      final List<WebElement> hosts = getShadowRoots(root, shadowHost, 1);
      if (0 == hosts.size()) {
        throw new NoSuchElementException("The element was not found: {" + getSelectorString() + "}");
      }
      return hosts.get(0);
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public List<WebElement> findElements(final SearchContext context) {
      final SearchContext root = context instanceof WebElement ? context : context.findElement(By.xpath("/*"));
      final List<WebElement> hosts = getShadowRoots(root, shadowHost, -1);
      if (null != this.target && 0 < this.target.length()) {
        return getElementsInsideShadowTree(hosts, target);
      }
      return hosts;
    }

    private WebElement getElementInsideShadowTree(final List<WebElement> hosts, final String target) {
      WebElement targetWebElement = null;
      for (final WebElement host : hosts) {
        targetWebElement = (WebElement) getJavascriptExecutor(host)
          .executeScript("return arguments[0].shadowRoot.querySelector(arguments[1])", host, target);
        if (targetWebElement != null) break;
      }
      return targetWebElement;
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> getElementsInsideShadowTree(final List<WebElement> hosts, final String target) {
      final List<WebElement> elems = new ArrayList<WebElement>();
      hosts.forEach(host -> {
        elems.addAll((List<WebElement>) getJavascriptExecutor(host)
          .executeScript("return arguments[0].shadowRoot.querySelectorAll(arguments[1])", host, target));
      });
      return elems;
    }

    private JavascriptExecutor getJavascriptExecutor(final SearchContext context) {
      JavascriptExecutor jsExecutor;
      if (context instanceof JavascriptExecutor) {
        jsExecutor = (JavascriptExecutor) context;
      } else {
        jsExecutor = (JavascriptExecutor) ((WrapsDriver) context).getWrappedDriver();
      }
      return jsExecutor;
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> getShadowRoots(final SearchContext host, String sh, final int limit) {
      /*
      console.info((function(){
          const sh=arguments[1].replace(/\/{3,}/g,'//').replace(/^\/([^\/])/,'$1').replace(/^\/+/,'/'),
          ptn=/(^[^\/]*)\/?(.*$)/,es=[],doc=arguments[0],lmt=arguments[2],f1=function(e,s){
            const rt=e.shadowRoot;
            if(rt&&(0>=lmt||lmt>es.length))0==s.length?(es[es.length]=e):f2(rt,s);
          },f2=function(rt,s){
            const p1=s.match(ptn),p2=p1[2].match(ptn);
            if(''==p1[1]){
              rt.querySelectorAll(p2[1]).forEach(function(c){f1(c,p2[2])});
              rt.querySelectorAll('*').forEach(function(c){f1(c,s)});
            }else{
              rt.querySelectorAll(p1[1]).forEach(function(c){f1(c,p1[2])});
            }
          };
          (doc.shadowRoot?f1:f2)(doc,sh);
          return es;
      })(document, '*', -1));
      */
      if (null == sh) sh = "";
      //sh = sh.replaceAll("/{3,}","//").replaceAll("^/([^/])","$1").replaceAll("^/+","/");
      sh = sh.replace("//", " (>>) ").replace("/", " (>) ");
      ArrayList<WebElement> elems = new ArrayList<WebElement>();
      ShadowRootFinder.find(host, sh, (elem) ->{
        elems.add((WebElement)elem);
        return 0<limit && limit <= elems.size();
      });
      System.out.println("elems.size:"+elems.size());
      return elems;
      /*System.out.println("ddddddddddddddddddddddddddddddddddd");
      final StringBuilder sb = new StringBuilder();
      sb.append("const sh=arguments[1],");
      sb.append("ptn=/(^[^\\/]*)\\/?(.*$)/,es=[],doc=arguments[0],lmt=arguments[2],f1=function(e,s){");
      sb.append("  const rt=e.shadowRoot;");
      sb.append("  if(rt&&(0>=lmt||lmt>es.length))0==s.length?(es[es.length]=e):f2(rt,s);");
      sb.append("},f2=function(rt,s){");
      sb.append("  const p1=s.match(ptn),p2=p1[2].match(ptn);");
      sb.append("  if(''==p1[1]){");
      sb.append("    rt.querySelectorAll(p2[1]).forEach(function(c){f1(c,p2[2])});");
      sb.append("    rt.querySelectorAll('*').forEach(function(c){f1(c,s)});");
      sb.append("  }else{");
      sb.append("    rt.querySelectorAll(p1[1]).forEach(function(c){f1(c,p1[2])});");
      sb.append("  }");
      sb.append("};");
      sb.append("(doc.shadowRoot?f1:f2)(doc,sh);");
      sb.append("return es;");
      return (List<WebElement>) getJavascriptExecutor(host).executeScript(sb.toString(), host, sh, limit);*/
      
    }
    /*private List<WebElement> aaa(final SearchContext host, String sh) {
      return (List<WebElement>) getJavascriptExecutor(host).executeScript("return arguments[0].shadowRoot.querySelectorAll(arguments[1])", host, sh);
    }*/

    @Override
    @CheckReturnValue
    @Nonnull
    public String toString() {
      final StringBuilder sb = new StringBuilder("By.cssSelector: ");
      sb.append(getSelectorString());
      return sb.toString();
    }
    private String getSelectorString() {
      final StringBuilder sb = new StringBuilder();
      if (null != shadowHost) {
        sb.append("[");
        sb.append(shadowHost);
        sb.append("]");
      }
      if (null != target) {
        sb.append(" ");
        sb.append(target);
      }
      return sb.toString();
    }
  }
}

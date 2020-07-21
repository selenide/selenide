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
      final SearchContext root = context instanceof JavascriptExecutor ? context.findElement(By.xpath("/*")) : context;
      if (null != this.target && 0 < this.target.length()) {
        final List<WebElement> hosts = getShadowRoots(root, shadowHost, -1);
        final WebElement targetWebElement = getElementInsideShadowTree(hosts, target);
        if (targetWebElement == null) {
          throw new NoSuchElementException("The element was not found: " + toString());
        }
        return targetWebElement;
      }
      final List<WebElement> hosts = getShadowRoots(root, shadowHost, 1);
      if (0 == hosts.size()) {
        throw new NoSuchElementException("The element was not found: " + toString());
      }
      return hosts.get(0);
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public List<WebElement> findElements(final SearchContext context) {
      final SearchContext root = context instanceof JavascriptExecutor ? context.findElement(By.xpath("/*")) : context;
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
      final StringBuilder sb = new StringBuilder();
      sb.append("const sh=arguments[1].replace(/\\/{3,}/g,'//').replace(/^\\/([^\\/])/,'$1').replace(/^\\/+/,'/'),");
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
      return (List<WebElement>) getJavascriptExecutor(host).executeScript(sb.toString(), host, sh, limit);
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public String toString() {
      final StringBuilder sb = new StringBuilder("By.ByShadowRoot: ");
      sb.append(null != shadowHost ? shadowHost : "null").append(" ").append(null != target ? target : "null");
      return sb.toString();
    }
  }
}

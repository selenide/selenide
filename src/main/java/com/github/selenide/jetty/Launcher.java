package com.github.selenide.jetty;

import com.github.selenide.Navigation;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Launcher {
  private final int port;
  private final Server server;

  private static final String DEFAULT_WEBAPP_CONTEXT = "/tested_webapp";
  private static final String DEFAULT_WEBAPP_FOLDER = "src/main/webapp";

  private String defaultWebapp;
  private final Map<String, String> webapps = new LinkedHashMap<String, String>(3);

  public Launcher() {
    this(findFreePort());
  }

  public Launcher(int port) {
    this.port = port;
    this.server = new Server();

    if (new File(DEFAULT_WEBAPP_FOLDER).exists()) {
      addWebapp(DEFAULT_WEBAPP_CONTEXT, DEFAULT_WEBAPP_FOLDER);
    }

    Navigation.baseUrl = "http://localhost:" + port;
  }

  public int getPort() {
    return port;
  }

  public String getDefaultWebapp() {
    return defaultWebapp;
  }

  public Launcher addWebapp(String context, String folder) {
    if (!new File(folder).exists()) {
      throw new IllegalArgumentException("Webapp folder does not exist: " + folder);
    }

    if (webapps.isEmpty()) {
      defaultWebapp = context;
      Navigation.baseUrl = "http://localhost:" + port + "/" + defaultWebapp;
    }
    webapps.put(context, folder);
    return this;
  }

  public Launcher run() throws Exception {
    System.out.println("Start jetty launcher at " + port);

    server.setThreadPool(new QueuedThreadPool(100));

    Connector connector = new SelectChannelConnector();
    connector.setPort(port);
    connector.setMaxIdleTime(30000);
    server.setConnectors(new Connector[]{connector});

    server.setHandlers(createWebapps());
    addShutdownHook();
    server.start();
    return this;
  }

  private void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Launcher.this.stop();
      }
    });
  }

  public final void stop() {
    if (server != null) {
      try {
        System.out.println("Shutdown jetty launcher at " + port);
        server.stop();
      } catch (Exception ignore) {
      }
    }
  }

  private WebAppContext createWebApp(String contextPath, String webappLocation) {
    WebAppContext webapp = new WebAppContext();
    webapp.setContextPath(contextPath);
    webapp.setWar(webappLocation);
    return webapp;
  }

  private Handler[] createWebapps() {
    List<Handler> handlers = new ArrayList<Handler>(webapps.size());
    for (Map.Entry<String, String> entry : webapps.entrySet()) {
      handlers.add(createWebApp(entry.getKey(), entry.getValue()));
    }
    return handlers.toArray(new Handler[webapps.size()]);
  }

  public static int findFreePort() {
    try {
      ServerSocket server = new ServerSocket(0);
      int port = server.getLocalPort();
      server.close();
      return port;
    }
    catch (IOException e) {
      throw new IllegalStateException("Cannot get free port: " + e.getMessage(), e);
    }
  }
}
package com.codeborne.selenide.impl;

import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class ResponseFilterImpl implements ResponseFilter {

  private Set<String> contentTypes = new HashSet<>(1);
  private File tempDir;
  private File tempFile;
  private String outputTextContent;
  private String fileName;


  public ResponseFilterImpl(String pathToFolder)  {
    tempDir = new File(pathToFolder);
  }


  public ResponseFilterImpl withContentTypes(String ... contentTypes) {
    this.contentTypes.addAll(Arrays.asList(contentTypes));
    return this;
  }

  public ResponseFilterImpl withContentTypes(List<String> contentTypes) {
    this.contentTypes.addAll(contentTypes);
    return this;
  }


  public ResponseFilterImpl withFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }


  @Override
  public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    String contentType = response.headers().get("Content-Type");
    if (contentTypes.contains(contentType)) {
      String actualFileName = getFileName(messageInfo.getUrl(), response);
      if (fileName.isEmpty() && !actualFileName.isEmpty()) fileName = actualFileName;

      String postfix = contentType.substring(contentType.indexOf('/') + 1);
      try {
        if (fileName.isEmpty()) {
          tempFile = File.createTempFile("downloaded", "." + postfix, tempDir);
        }
        else {
          tempFile =  new File(tempDir.getAbsolutePath() + "/" + fileName);
        }

        outputTextContent = tempFile.getAbsolutePath();
        Files.write(tempFile.toPath(),
                    contents.getBinaryContents(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
      } catch (IOException e) {
        outputTextContent = e.getMessage();
      }

      response.headers().clear();
      response.headers().add("Content-Type", "text/html");
      response.headers().add("Content-Length", "" + outputTextContent.length());
      contents.setTextContents(outputTextContent);
    }
  }


  protected String getFileName(String requestUrl, HttpResponse response)  {
    for (Map.Entry<String, String> header : response.headers().entries()) {
      String fileName = FileDownloader.instance
                                      .getFileNameFromContentDisposition(header.getKey(), header.getValue());
      if (fileName != null) {
        return fileName;
      }
    }

    String ret;
    if (requestUrl.indexOf("\\") > requestUrl.indexOf("/")) {
      ret = StringUtils.substringAfterLast(requestUrl, "\\");
    } else  {
      ret = StringUtils.substringAfterLast(requestUrl, "/");
    }

    if (ret.indexOf("?") > -1
            && (ret.indexOf("?") < ret.indexOf("&") || ret.indexOf("&") < 0)) {
      ret = StringUtils.substringBefore(ret, "?");
    } else if (ret.indexOf("&") > -1) {
      ret = StringUtils.substringBefore(ret, "&");
    }

    return ret;
  }
}

package integration.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;

class FileUploadHandler extends BaseHandler {
  private static final Logger log = Logger.getLogger(FileUploadHandler.class.getName());
  private final List<FileItem> uploadedFiles;

  FileUploadHandler(List<FileItem> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long start = System.nanoTime();

    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
    ServletFileUpload upload = new ServletFileUpload(factory);
    try {
      List<FileItem> items = upload.parseRequest(request);
      for (FileItem item : items) {
        if (item.getSize() > 0) {
          uploadedFiles.add(item);
        }
      }

      String message = "<h3>Uploaded " + uploadedFiles.size() + " files</h3>" + items;
      response.setContentType(CONTENT_TYPE_HTML_TEXT);
      printResponse(response, message.getBytes(UTF_8));
      logRequest(request, message, start);
    } catch (FileUploadException e) {
      logRequest(request, e.getMessage(), start);
      log.log(SEVERE, e.getMessage(), e);
    }
  }
}

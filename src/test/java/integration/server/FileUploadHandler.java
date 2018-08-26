package integration.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;

class FileUploadHandler extends BaseHandler {
  private static final Logger log = Logger.getLogger(FileUploadHandler.class.getName());
  private final List<FileItem> uploadedFiles;

  FileUploadHandler(List<FileItem> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
  }

  @Override
  public Result post(HttpServletRequest request, HttpServletResponse response) {
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
      return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, message.getBytes(UTF_8));
    } catch (FileUploadException e) {
      log.log(SEVERE, e.getMessage(), e);
      return new Result(SC_INTERNAL_SERVER_ERROR, CONTENT_TYPE_HTML_TEXT, e.toString());
    }
  }
}

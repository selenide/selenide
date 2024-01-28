package integration.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.MultiPartFormInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

class FileUploadHandler extends BaseHandler {
  private static final Logger log = LoggerFactory.getLogger(FileUploadHandler.class);
  private final List<UploadedFile> uploadedFiles;

  FileUploadHandler(List<UploadedFile> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
  }

  @Override
  public Result post(HttpServletRequest request, HttpServletResponse response) {
    try {
      Collection<Part> parts = request.getParts();
      for (Part item : parts) {
        if (item.getSize() > 0) {
          uploadedFiles.add(multipartToFile(item));
        }
      }

      String message = "<h3>Uploaded %s files</h3>%s".formatted(uploadedFiles.size(), parts);
      return new Result(SC_OK, CONTENT_TYPE_HTML_TEXT, message.getBytes(UTF_8));
    } catch (IOException | ServletException e) {
      log.error(e.getMessage(), e);
      return new Result(SC_INTERNAL_SERVER_ERROR, CONTENT_TYPE_HTML_TEXT, e.toString());
    }
  }

  @Nonnull
  private static UploadedFile multipartToFile(Part item) throws IOException {
    MultiPartFormInputStream.MultiPart i = (MultiPartFormInputStream.MultiPart) item;
    byte[] content = IOUtils.toByteArray(i.getInputStream());
    return new UploadedFile(i.getContentDispositionFilename(), content);
  }
}

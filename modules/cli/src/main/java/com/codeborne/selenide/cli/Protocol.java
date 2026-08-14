package com.codeborne.selenide.cli;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Wire framing between the CLI client and the daemon over a loopback socket.
 *
 * <p>Request = the command tokens (client argv). Response = a status plus a (possibly large,
 * e.g. generated code) UTF-8 text payload. Every string is framed as {@code int byteLength} +
 * raw UTF-8 bytes, so - unlike {@link DataOutputStream#writeUTF}, whose modified-UTF-8 encoding
 * is capped at 65535 bytes - there is no cap on argument or payload size besides {@link
 * #MAX_FRAME_BYTES}.
 */
final class Protocol {
  private static final int MAX_ARGS = 10_000;
  private static final int MAX_FRAME_BYTES = 16 * 1024 * 1024;

  record Response(boolean ok, boolean shutdown, String text) {
  }

  private Protocol() {
  }

  static void writeRequest(OutputStream out, List<String> args) throws IOException {
    DataOutputStream data = new DataOutputStream(out);
    data.writeInt(args.size());
    for (String arg : args) {
      writeFrame(data, arg);
    }
    data.flush();
  }

  static List<String> readRequest(InputStream in) throws IOException {
    DataInputStream data = new DataInputStream(in);
    int count = data.readInt();
    if (count < 0 || count > MAX_ARGS) {
      throw new IOException("invalid request: implausible argument count " + count);
    }
    List<String> args = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      args.add(readFrame(data));
    }
    return args;
  }

  static void writeResponse(OutputStream out, boolean ok, boolean shutdown, String text) throws IOException {
    DataOutputStream data = new DataOutputStream(out);
    data.writeBoolean(ok);
    data.writeBoolean(shutdown);
    writeFrame(data, text);
    data.flush();
  }

  static Response readResponse(InputStream in) throws IOException {
    DataInputStream data = new DataInputStream(in);
    boolean ok = data.readBoolean();
    boolean shutdown = data.readBoolean();
    return new Response(ok, shutdown, readFrame(data));
  }

  private static void writeFrame(DataOutputStream data, String text) throws IOException {
    byte[] bytes = text.getBytes(UTF_8);
    data.writeInt(bytes.length);
    data.write(bytes);
  }

  private static String readFrame(DataInputStream data) throws IOException {
    int length = data.readInt();
    if (length < 0 || length > MAX_FRAME_BYTES) {
      throw new IOException("invalid frame: implausible length " + length);
    }
    byte[] bytes = data.readNBytes(length);
    if (bytes.length != length) {
      throw new EOFException("truncated frame: expected " + length + " bytes, got only " + bytes.length);
    }
    return new String(bytes, UTF_8);
  }
}

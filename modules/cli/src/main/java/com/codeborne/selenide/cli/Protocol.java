package com.codeborne.selenide.cli;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
 * e.g. generated code) UTF-8 text payload.
 */
final class Protocol {
  record Response(boolean ok, boolean shutdown, String text) {
  }

  private Protocol() {
  }

  static void writeRequest(OutputStream out, List<String> args) throws IOException {
    DataOutputStream data = new DataOutputStream(out);
    data.writeInt(args.size());
    for (String arg : args) {
      data.writeUTF(arg);
    }
    data.flush();
  }

  static List<String> readRequest(InputStream in) throws IOException {
    DataInputStream data = new DataInputStream(in);
    int count = data.readInt();
    List<String> args = new ArrayList<>(Math.max(0, count));
    for (int i = 0; i < count; i++) {
      args.add(data.readUTF());
    }
    return args;
  }

  static void writeResponse(OutputStream out, boolean ok, boolean shutdown, String text) throws IOException {
    DataOutputStream data = new DataOutputStream(out);
    data.writeBoolean(ok);
    data.writeBoolean(shutdown);
    byte[] bytes = text.getBytes(UTF_8);
    data.writeInt(bytes.length);
    data.write(bytes);
    data.flush();
  }

  static Response readResponse(InputStream in) throws IOException {
    DataInputStream data = new DataInputStream(in);
    boolean ok = data.readBoolean();
    boolean shutdown = data.readBoolean();
    int length = data.readInt();
    byte[] bytes = data.readNBytes(length);
    return new Response(ok, shutdown, new String(bytes, UTF_8));
  }
}

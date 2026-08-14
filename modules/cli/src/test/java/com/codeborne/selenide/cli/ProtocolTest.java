package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProtocolTest {
  @Test
  void requestRoundTripPreservesTokens() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Protocol.writeRequest(buffer, List.of("setValue", "#q", "hello world"));

    List<String> got = Protocol.readRequest(new ByteArrayInputStream(buffer.toByteArray()));

    assertThat(got).containsExactly("setValue", "#q", "hello world");
  }

  @Test
  void requestArgumentCanExceedTheOldWriteUtf64kCap() throws IOException {
    String longValue = "x".repeat(100_000);
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Protocol.writeRequest(buffer, List.of("setValue", "#q", longValue));

    List<String> got = Protocol.readRequest(new ByteArrayInputStream(buffer.toByteArray()));

    assertThat(got).containsExactly("setValue", "#q", longValue);
  }

  @Test
  void rejectsImplausiblyLargeArgumentCount() {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream data = new DataOutputStream(buffer);
    assertThatThrownBy(() -> {
      data.writeInt(Integer.MAX_VALUE);
      data.flush();
      Protocol.readRequest(new ByteArrayInputStream(buffer.toByteArray()));
    }).isInstanceOf(IOException.class).hasMessageContaining("argument count");
  }

  @Test
  void rejectsImplausiblyLargeFrameLength() {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream data = new DataOutputStream(buffer);
    assertThatThrownBy(() -> {
      data.writeBoolean(true);
      data.writeBoolean(false);
      data.writeInt(Integer.MAX_VALUE);
      data.flush();
      Protocol.readResponse(new ByteArrayInputStream(buffer.toByteArray()));
    }).isInstanceOf(IOException.class).hasMessageContaining("length");
  }

  @Test
  void writeRequestRejectsImplausiblyLargeArgumentCount() {
    List<String> tooManyArgs = new ArrayList<>();
    for (int i = 0; i <= 10_000; i++) {
      tooManyArgs.add("arg");
    }
    assertThatThrownBy(() -> Protocol.writeRequest(new ByteArrayOutputStream(), tooManyArgs))
      .isInstanceOf(IOException.class)
      .hasMessageContaining("argument count");
  }

  @Test
  void writeResponseRejectsAnOversizedFrame() {
    String tooLong = "x".repeat(16 * 1024 * 1024 + 1);
    assertThatThrownBy(() -> Protocol.writeResponse(new ByteArrayOutputStream(), true, false, tooLong))
      .isInstanceOf(IOException.class)
      .hasMessageContaining("length");
  }

  @Test
  void rejectsATruncatedFrame() {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream data = new DataOutputStream(buffer);
    assertThatThrownBy(() -> {
      data.writeBoolean(true);
      data.writeBoolean(false);
      data.writeInt(10);
      data.write("short".getBytes(UTF_8));
      data.flush();
      Protocol.readResponse(new ByteArrayInputStream(buffer.toByteArray()));
    }).isInstanceOf(EOFException.class).hasMessageContaining("truncated");
  }

  @Test
  void emptyRequestRoundTrips() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Protocol.writeRequest(buffer, List.of());

    assertThat(Protocol.readRequest(new ByteArrayInputStream(buffer.toByteArray()))).isEmpty();
  }

  @Test
  void okResponseRoundTripsMultilineText() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Protocol.writeResponse(buffer, true, false, "line1\nline2\n");

    Protocol.Response response = Protocol.readResponse(new ByteArrayInputStream(buffer.toByteArray()));

    assertThat(response.ok()).isTrue();
    assertThat(response.shutdown()).isFalse();
    assertThat(response.text()).isEqualTo("line1\nline2\n");
  }

  @Test
  void errorAndShutdownFlagsRoundTrip() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Protocol.writeResponse(buffer, false, true, "boom");

    Protocol.Response response = Protocol.readResponse(new ByteArrayInputStream(buffer.toByteArray()));

    assertThat(response.ok()).isFalse();
    assertThat(response.shutdown()).isTrue();
    assertThat(response.text()).isEqualTo("boom");
  }
}

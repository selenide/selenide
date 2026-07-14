package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolTest {
  @Test
  void requestRoundTripPreservesTokens() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    Protocol.writeRequest(buffer, List.of("setValue", "#q", "hello world"));

    List<String> got = Protocol.readRequest(new ByteArrayInputStream(buffer.toByteArray()));

    assertThat(got).containsExactly("setValue", "#q", "hello world");
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

package com.codeborne.selenide.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressResolver {
  InetAddress getInetAddressByName(String hostname) {
    try {
      return InetAddress.getByName(hostname);
    }
    catch (UnknownHostException e) {
      throw new IllegalArgumentException(e);
    }
  }
}

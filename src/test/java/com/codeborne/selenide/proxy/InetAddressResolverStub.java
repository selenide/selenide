package com.codeborne.selenide.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

class InetAddressResolverStub extends InetAddressResolver {
  @Override
  InetAddress getInetAddressByName(String hostname) {
    try {
      return InetAddress.getByAddress(hostname, new byte[4]);
    }
    catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
}

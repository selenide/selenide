package com.codeborne.selenide.proxy;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetAddress;
import java.net.UnknownHostException;

@ParametersAreNonnullByDefault
class InetAddressResolverStub extends InetAddressResolver {
  @Override
  @CheckReturnValue
  @Nonnull
  InetAddress getInetAddressByName(String hostname) {
    try {
      return InetAddress.getByAddress(hostname, new byte[4]);
    }
    catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
}

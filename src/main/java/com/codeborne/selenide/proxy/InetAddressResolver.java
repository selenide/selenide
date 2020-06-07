package com.codeborne.selenide.proxy;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetAddress;
import java.net.UnknownHostException;

@ParametersAreNonnullByDefault
public class InetAddressResolver {
  @CheckReturnValue
  @Nonnull
  InetAddress getInetAddressByName(String hostname) {
    try {
      return InetAddress.getByName(hostname);
    }
    catch (UnknownHostException e) {
      throw new IllegalArgumentException(e);
    }
  }
}

package com.codeborne.security.mobileid;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.digidoc.DigiDocServicePortType;
import com.codeborne.security.digidoc.DigiDocService_Service;
import com.codeborne.security.digidoc.DigiDocService_ServiceLocator;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static com.codeborne.security.AuthenticationException.Code.*;

/**
 * Authenticates with Mobile-ID.<br>
 * Can be used as a preconfigured singleton (eg Spring bean).<br>
 * <p>Use {@link #startLogin} to initiate the login session and display challenge to the user,
 * then {@link #waitForLogin(MobileIDSession)} to wait for the authentication to complete.</p>
 * <p>Various setters can be used to configure the behaviour.</p>
 */
public class MobileIDAuthenticator {
  private String language = "EST";
  private String serviceName = "Testimine";
  private String loginMessage = "";
  private int retryCount = 60;
  private int pollIntervalMs = 3000;
  private final String messagingMode = "asynchClientServer";

  DigiDocServicePortType digiDocServicePortType;

  public MobileIDAuthenticator() {
  }

  public MobileIDAuthenticator(String digidocServiceURL) throws MalformedURLException {
    setDigidocServiceURL(new URL(digidocServiceURL));
  }

  public MobileIDAuthenticator(String digidocServiceURL, String serviceName) throws MalformedURLException {
    setDigidocServiceURL(new URL(digidocServiceURL));
    this.serviceName = serviceName;
  }

  public MobileIDAuthenticator(URL digidocServiceURL) {
    setDigidocServiceURL(digidocServiceURL);
  }

  public MobileIDAuthenticator(URL digidocServiceURL, String serviceName) {
    setDigidocServiceURL(digidocServiceURL);
    this.serviceName = serviceName;
  }

  public final MobileIDAuthenticator setDigidocServiceURL(URL digidocServiceURL) {
    DigiDocService_Service digiDocService = new DigiDocService_ServiceLocator();
    try {
      digiDocServicePortType = digiDocService.getDigiDocService(digidocServiceURL);
    } catch (ServiceException e) {
      throw new RuntimeException("Failed to initialize Mobile-ID support", e);
    }
    return this;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setLoginMessage(String loginMessage) {
    this.loginMessage = loginMessage;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  public void setPollIntervalMs(int pollIntervalMs) {
    this.pollIntervalMs = pollIntervalMs;
  }

  /**
   * Initiates the login session. Note: returned session already contains user's info, but the authenticity is not yet verified.
   * @param personalCode user's personal code
   * @param countryCode two letter country code, eg EE
   * @throws AuthenticationException is authentication unsuccessful
   */
  public MobileIDSession startLogin(String personalCode, String countryCode) throws AuthenticationException {
    return startLogin(personalCode, countryCode, null);
  }

  /**
   * Initiates the login session. Note: returned session already contains user's info, but the authenticity is not yet verified.
   * @param phone phone number, either a local one (work for EE) or with country code (eg +37255667788).
   * @throws AuthenticationException is authentication unsuccessful
   */
  public MobileIDSession startLogin(String phone) throws AuthenticationException {
    return startLogin(null, null, phone);
  }
  
  protected MobileIDSession startLogin(String personalCode, String countryCode, String phone) throws AuthenticationException {
    if (digiDocServicePortType == null) {
      throw new IllegalStateException("digidocServiceURL is not initialized");
    }

    if (phone.startsWith("+")) phone = phone.substring(1);
    if (!phone.startsWith("372")) phone = "372" + phone;

    IntHolder sessCode = new IntHolder();
    StringHolder result = new StringHolder();
    StringHolder firstName = new StringHolder();
    StringHolder lastName = new StringHolder();
    StringHolder personalCodeHolder = new StringHolder();
    StringHolder challenge = new StringHolder();

    try {
      digiDocServicePortType.mobileAuthenticate(personalCode, countryCode, phone, language, serviceName, loginMessage, generateSPChallenge(),
          messagingMode, 0, false, false, sessCode, result,
          personalCodeHolder, firstName, lastName, new StringHolder(), new StringHolder(), new StringHolder(), challenge,
          new StringHolder(), new StringHolder());
    }
    catch (RemoteException e) {
      throw new AuthenticationException(e);
    }

    if (!"OK".equals(result.value))
      throw new AuthenticationException(AUTHENTICATION_ERROR, result.value, null);

    return new MobileIDSession(sessCode.value, challenge.value, firstName.value, lastName.value, personalCodeHolder.value);
  }

  protected String generateSPChallenge() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 20; i++) sb.append((int)(Math.random() * 10));
    return sb.toString();
  }

  /**
   * Waits until user confirms their identity using the mobile device.
   * @param session previously returned by {@link #startLogin}
   * @throws AuthenticationException is authentication unsuccessful
   */
  public MobileIDSession waitForLogin(MobileIDSession session) throws AuthenticationException {
    StringHolder status = new StringHolder("OUTSTANDING_TRANSACTION");
    int tryCount = 0;
    while (sleep(pollIntervalMs) && "OUTSTANDING_TRANSACTION".equals(status.value) && tryCount < retryCount) {
      try {
        digiDocServicePortType.getMobileAuthenticateStatus(session.sessCode, false, status, new StringHolder());
      }
      catch (RemoteException e) {
        throw new AuthenticationException(e);
      }
      tryCount++;
    }

    if (!"USER_AUTHENTICATED".equals(status.value))
      throw new AuthenticationException(AUTHENTICATION_ERROR, status.value, null);

    return session;
  }

  private boolean sleep(int sleepTimeMilliseconds) {
    try {
      Thread.sleep(sleepTimeMilliseconds);
      return true;
    }
    catch (InterruptedException e) {
      return false;
    }
  }
}

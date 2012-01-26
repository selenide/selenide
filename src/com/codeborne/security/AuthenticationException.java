package com.codeborne.security;

import org.apache.axis.AxisFault;

import java.rmi.RemoteException;

import static java.lang.Integer.parseInt;

public class AuthenticationException extends Exception {
  public enum Code {
    SERVICE_ERROR(100, "Teenuse üldine veasituatsioon"),
    INVALID_INPUT(101, "Sisendparameetrid mittekorrektsel kujul"),
    MISSING_INPUT(102, "Mõni kohustuslik sisendparameeter on määramata"),
    METHOD_NOT_ALLOWED(103, "Ligipääs antud meetodile antud parameetritega piiratud (näiteks kasutatav ServiceName ei ole teenuse pakkuja juures registreeritud)"),
    AUTHENTICATION_ERROR(200, "Teenuse üldine viga"),
    USER_CERTIFICATE_MISSING(201, "Kasutaja sertifikaat puudub"),
    UNABLE_TO_TEST_USER_CERTIFICATE(202, "Kasutaja sertifikaadi kehtivus ei ole võimalik kontrollida"),
    USER_PHONE_ERROR(300, "Kasutajaga telefoniga seotud üldine viga"),
    NO_AGREEMENT(301, "Kasutajal pole Mobiil-ID lepingut"),
    CERTIFICATE_REVOKED(302, "Kasutaja sertifikaat ei kehti (OCSP vastus REVOKED)."),
    NOT_ACTIVATED(303, "Kasutajal pole Mobiil-ID aktiveeritud. Aktiveerimiseks tuleks minna aadressile http://mobiil.id.ee");

    private int code;
    private String descriptionInEstonian;

    Code(int code, String descriptionInEstonian) {
      this.code = code;
      this.descriptionInEstonian = descriptionInEstonian;
    }

    public int getCode() {
      return code;
    }
  }

  private Code code;
  
  public AuthenticationException(Code code, String details, Throwable throwable) {
    super(code + ": " + details, throwable);
    this.code = code;
  }

  public AuthenticationException(Code code) {
    super(code.toString());
    this.code = code;
  }

  public AuthenticationException(RemoteException e) {
    this(findCode(e.getMessage()), e instanceof AxisFault ? ((AxisFault)e).getFaultDetails()[0].getTextContent() : null, e);
  }
  
  private static Code findCode(String codeAsString) {
    try {
      int code = parseInt(codeAsString);
      for (Code c : Code.values()) {
        if (c.code == code) return c;
      }
    }
    catch (NumberFormatException e) {
      // just return below
    }
    return Code.SERVICE_ERROR;
  }

  public Code getCode() {
    return code;
  }
}

package com.codeborne.security.mobileid;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.client.DigiDocServicePortType;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;
import java.rmi.RemoteException;

import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class MobileIDAuthenticatorTest {
  MobileIDAuthenticator mid = new MobileIDAuthenticator();

  @Test
  public void startSession() throws RemoteException, AuthenticationException {
    mid.digiDocServicePortType = mockMobileIdAuthenticator("OK", "Bruce", "Willis", "38105060708", "4567", 123);

    MobileIDSession session = mid.startSession("+37255667788");
    assertThat(session.firstName, equalTo("Bruce"));
    assertThat(session.lastName, equalTo("Willis"));
    assertThat(session.personalCode, equalTo("38105060708"));
    assertThat(session.sessCode, equalTo(123));
    assertThat(session.challenge, equalTo("4567"));
  }

  @Test(expected = AuthenticationException.class)
  public void throwsExceptionIfError() throws RemoteException, AuthenticationException {
    mid.digiDocServicePortType = mockMobileIdAuthenticatorError(100);
    mid.startSession("+37255667788");
  }

  private DigiDocServicePortType mockMobileIdAuthenticator(final String result, final String firstName,
         final String lastName, final String personalCode, final String challenge, final int sessCode) throws RemoteException {
    DigiDocServicePortType service = mock(DigiDocServicePortType.class);
    doAnswer(new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((IntHolder)invocation.getArguments()[11]).value = sessCode;
        ((StringHolder)invocation.getArguments()[12]).value = result;
        ((StringHolder)invocation.getArguments()[13]).value = personalCode;
        ((StringHolder)invocation.getArguments()[14]).value = firstName;
        ((StringHolder)invocation.getArguments()[15]).value = lastName;
        ((StringHolder)invocation.getArguments()[19]).value = challenge;
        return null;
      }
    }).when(service).mobileAuthenticate(anyString(), anyString(), eq("+37255667788"), anyString(),
        anyString(), anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean(), any(IntHolder.class),
        any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class),
        any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class));

    return service;
  }

  private DigiDocServicePortType mockMobileIdAuthenticatorError(int errorCode) throws RemoteException {
    DigiDocServicePortType service = mock(DigiDocServicePortType.class);
    doThrow(new RemoteException(valueOf(errorCode)))
    .when(service).mobileAuthenticate(anyString(), anyString(), eq("+37255667788"), anyString(),
        anyString(), anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean(), any(IntHolder.class),
        any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class),
        any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class));

    return service;
  }
}

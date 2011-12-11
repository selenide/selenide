package com.codeborne.security.mobileid;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.client.DigiDocServicePortType;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;
import java.rmi.RemoteException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MobileIDAuthenticatorTest {
  @Test
  public void startSession() throws RemoteException, AuthenticationException {
    MobileIDAuthenticator mid = new MobileIDAuthenticator();
    mid.digiDocServicePortType = mock(DigiDocServicePortType.class);
    doAnswer(new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        IntHolder sessCode = (IntHolder)invocation.getArguments()[11];
        StringHolder result = (StringHolder)invocation.getArguments()[12];
        StringHolder personalCode = (StringHolder)invocation.getArguments()[13];
        StringHolder firstName = (StringHolder)invocation.getArguments()[14];
        StringHolder lastName = (StringHolder)invocation.getArguments()[15];
        StringHolder challenge = (StringHolder)invocation.getArguments()[19];
        sessCode.value = 123;
        result.value = "OK";
        personalCode.value = "38105060708";
        firstName.value = "Bruce";
        lastName.value = "Willis";
        challenge.value = "4567";
        return null;
      }
    }).when(mid.digiDocServicePortType).mobileAuthenticate(anyString(), anyString(), eq("+37255667788"), anyString(),
        anyString(), anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyBoolean(), any(IntHolder.class),
        any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class),
        any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class), any(StringHolder.class));

    MobileIDSession session = mid.startSession("+37255667788");
    assertThat(session.firstName, equalTo("Bruce"));
    assertThat(session.lastName, equalTo("Willis"));
    assertThat(session.personalCode, equalTo("38105060708"));
    assertThat(session.sessCode, equalTo(123));
    assertThat(session.challenge, equalTo("4567"));
  }
}

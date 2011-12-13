package com.codeborne.security.mobileid;

import com.codeborne.security.AuthenticationException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class HelloMobileID {
  private MobileIDAuthenticator mid;
  private JFrame frame;
  private JTextField phone;
  private JLabel message;

  public static void main(String[] args) throws MalformedURLException {
    System.setProperty("javax.net.ssl.trustStore", "test/keystore.jks");
    HelloMobileID app = new HelloMobileID();
    app.mid = new MobileIDAuthenticator("https://www.openxades.org:8443/");
    app.create();
  }

  public final void login(String phoneNumber) {
    try {
      final MobileIDSession mobileIDSession = mid.startSession(phoneNumber);
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          message.setText("Challenge: " + mobileIDSession.challenge);
        }
      });

      mid.waitForLogin(mobileIDSession);
      showMessageDialog(frame, "Your have logged in." +
          "\nFirst name: " + mobileIDSession.firstName +
          "\nLast name: " + mobileIDSession.lastName +
          "\nPersonal code: " + mobileIDSession.personalCode,
          "Logged in", INFORMATION_MESSAGE);
    } catch (AuthenticationException e) {
      showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void create() {
    frame = new JFrame("Hello MobileID world");
    frame.setContentPane(createContent());

    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setMinimumSize(new Dimension(400, 200));
    frame.pack();
    frame.setVisible(true);
  }

  private JComponent createContent() {
    JButton button = new JButton("Login with MobileID");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new Thread(){
          @Override
          public void run() {
            login(phone.getText());
          }
        }.start();
      }
    });

    message = new JLabel("Enter your phone");
    phone = new JTextField("+372", 30);
    phone.setMaximumSize(new Dimension(50, 20));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(message, NORTH);
    panel.add(phone, CENTER);
    panel.add(button, SOUTH);
    return panel;
  }
}

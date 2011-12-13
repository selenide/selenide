package com.codeborne.security.mobileid;

import com.codeborne.security.AuthenticationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import static java.awt.BorderLayout.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
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
      showMessage("Challenge: " + mobileIDSession.challenge);

      mid.waitForLogin(mobileIDSession);
      showMessage("Your have logged in." +
          "\nFirst name: " + mobileIDSession.firstName +
          "\nLast name: " + mobileIDSession.lastName +
          "\nPersonal code: " + mobileIDSession.personalCode);
    } catch (AuthenticationException e) {
      e.printStackTrace();
      showMessage(e.getMessage());
      frame.pack();
    }
  }

  private void showMessage(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        HelloMobileID.this.message.setText(message);
      }
    });
  }

  private void create() {
    frame = new JFrame("Hello MobileID world");
    frame.setContentPane(createContent());

    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setMinimumSize(new Dimension(300, 100));
    frame.setPreferredSize(new Dimension(400, 200));
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

    JPanel panel = new JPanel(new FlowLayout());
    panel.add(message);
    panel.add(phone);
    panel.add(button);
    return panel;
  }
}

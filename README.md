Mobile-ID
=========

[Mobile-ID](http://www.id.ee/?id=10995&&langchange=1) (Mobiil-ID) is a personal mobile identity in Estonia and Lithuania,
provided by an additional application on a SIM card. The good thing is that it 
is backed by government and provides the same level of security for authentication 
and digital signatures as a national ID card without the need of having a smart card reader.

Java and Mobile-ID
==================

The official Mobile-ID API is a SOAP web service, so it usually takes time to generate the code and
start using it in a Java application.

This small library tries to solve this problem: just add the *mobileid.jar* (with dependencies) to your
project and you have a working Mobile-ID support.

The same jar works in Scala as well or any other JVM-based language.

Usage
=====

Just use the public methods in com.codeborne.security.mobileid.MobileIDAuthenticator class.

See working example in (HelloMobileID.java)[test/com/codeborne/security/mobileid/HelloMobileID.java] - run the main() method.

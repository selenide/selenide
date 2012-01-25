/**
 * DigiDocService_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.digidoc;

public interface DigiDocService_Service extends javax.xml.rpc.Service {

/**
 * Digital signature service
 */
    public java.lang.String getDigiDocServiceAddress();

    public com.codeborne.security.digidoc.DigiDocServicePortType getDigiDocService() throws javax.xml.rpc.ServiceException;

    public com.codeborne.security.digidoc.DigiDocServicePortType getDigiDocService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

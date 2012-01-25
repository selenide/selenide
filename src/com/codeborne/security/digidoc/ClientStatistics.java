/**
 * ClientStatistics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.digidoc;

public class ClientStatistics  implements java.io.Serializable {
    private java.lang.String client;

    private int totalClientSessions;

    private int activeClientSessions;

    private int callRate;

    public ClientStatistics() {
    }

    public ClientStatistics(
           java.lang.String client,
           int totalClientSessions,
           int activeClientSessions,
           int callRate) {
           this.client = client;
           this.totalClientSessions = totalClientSessions;
           this.activeClientSessions = activeClientSessions;
           this.callRate = callRate;
    }


    /**
     * Gets the client value for this ClientStatistics.
     * 
     * @return client
     */
    public java.lang.String getClient() {
        return client;
    }


    /**
     * Sets the client value for this ClientStatistics.
     * 
     * @param client
     */
    public void setClient(java.lang.String client) {
        this.client = client;
    }


    /**
     * Gets the totalClientSessions value for this ClientStatistics.
     * 
     * @return totalClientSessions
     */
    public int getTotalClientSessions() {
        return totalClientSessions;
    }


    /**
     * Sets the totalClientSessions value for this ClientStatistics.
     * 
     * @param totalClientSessions
     */
    public void setTotalClientSessions(int totalClientSessions) {
        this.totalClientSessions = totalClientSessions;
    }


    /**
     * Gets the activeClientSessions value for this ClientStatistics.
     * 
     * @return activeClientSessions
     */
    public int getActiveClientSessions() {
        return activeClientSessions;
    }


    /**
     * Sets the activeClientSessions value for this ClientStatistics.
     * 
     * @param activeClientSessions
     */
    public void setActiveClientSessions(int activeClientSessions) {
        this.activeClientSessions = activeClientSessions;
    }


    /**
     * Gets the callRate value for this ClientStatistics.
     * 
     * @return callRate
     */
    public int getCallRate() {
        return callRate;
    }


    /**
     * Sets the callRate value for this ClientStatistics.
     * 
     * @param callRate
     */
    public void setCallRate(int callRate) {
        this.callRate = callRate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClientStatistics)) return false;
        ClientStatistics other = (ClientStatistics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.client==null && other.getClient()==null) || 
             (this.client!=null &&
              this.client.equals(other.getClient()))) &&
            this.totalClientSessions == other.getTotalClientSessions() &&
            this.activeClientSessions == other.getActiveClientSessions() &&
            this.callRate == other.getCallRate();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getClient() != null) {
            _hashCode += getClient().hashCode();
        }
        _hashCode += getTotalClientSessions();
        _hashCode += getActiveClientSessions();
        _hashCode += getCallRate();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClientStatistics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "ClientStatistics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("client");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Client"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalClientSessions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "TotalClientSessions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeClientSessions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ActiveClientSessions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callRate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "CallRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

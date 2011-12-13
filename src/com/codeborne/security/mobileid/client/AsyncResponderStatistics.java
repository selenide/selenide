/**
 * AsyncResponderStatistics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class AsyncResponderStatistics  implements java.io.Serializable {
    private java.lang.String asyncResponder;

    private java.lang.String status;

    private int totalResponsesSent;

    private int queueSize;

    public AsyncResponderStatistics() {
    }

    public AsyncResponderStatistics(
           java.lang.String asyncResponder,
           java.lang.String status,
           int totalResponsesSent,
           int queueSize) {
           this.asyncResponder = asyncResponder;
           this.status = status;
           this.totalResponsesSent = totalResponsesSent;
           this.queueSize = queueSize;
    }


    /**
     * Gets the asyncResponder value for this AsyncResponderStatistics.
     * 
     * @return asyncResponder
     */
    public java.lang.String getAsyncResponder() {
        return asyncResponder;
    }


    /**
     * Sets the asyncResponder value for this AsyncResponderStatistics.
     * 
     * @param asyncResponder
     */
    public void setAsyncResponder(java.lang.String asyncResponder) {
        this.asyncResponder = asyncResponder;
    }


    /**
     * Gets the status value for this AsyncResponderStatistics.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AsyncResponderStatistics.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the totalResponsesSent value for this AsyncResponderStatistics.
     * 
     * @return totalResponsesSent
     */
    public int getTotalResponsesSent() {
        return totalResponsesSent;
    }


    /**
     * Sets the totalResponsesSent value for this AsyncResponderStatistics.
     * 
     * @param totalResponsesSent
     */
    public void setTotalResponsesSent(int totalResponsesSent) {
        this.totalResponsesSent = totalResponsesSent;
    }


    /**
     * Gets the queueSize value for this AsyncResponderStatistics.
     * 
     * @return queueSize
     */
    public int getQueueSize() {
        return queueSize;
    }


    /**
     * Sets the queueSize value for this AsyncResponderStatistics.
     * 
     * @param queueSize
     */
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AsyncResponderStatistics)) return false;
        AsyncResponderStatistics other = (AsyncResponderStatistics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.asyncResponder==null && other.getAsyncResponder()==null) || 
             (this.asyncResponder!=null &&
              this.asyncResponder.equals(other.getAsyncResponder()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            this.totalResponsesSent == other.getTotalResponsesSent() &&
            this.queueSize == other.getQueueSize();
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
        if (getAsyncResponder() != null) {
            _hashCode += getAsyncResponder().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        _hashCode += getTotalResponsesSent();
        _hashCode += getQueueSize();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AsyncResponderStatistics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "AsyncResponderStatistics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("asyncResponder");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AsyncResponder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalResponsesSent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "TotalResponsesSent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queueSize");
        elemField.setXmlName(new javax.xml.namespace.QName("", "QueueSize"));
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

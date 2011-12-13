/**
 * ConfirmationInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class ConfirmationInfo  implements java.io.Serializable {
    private java.lang.String responderID;

    private java.lang.String producedAt;

    private com.codeborne.security.mobileid.client.CertificateInfo responderCertificate;

    public ConfirmationInfo() {
    }

    public ConfirmationInfo(
           java.lang.String responderID,
           java.lang.String producedAt,
           com.codeborne.security.mobileid.client.CertificateInfo responderCertificate) {
           this.responderID = responderID;
           this.producedAt = producedAt;
           this.responderCertificate = responderCertificate;
    }


    /**
     * Gets the responderID value for this ConfirmationInfo.
     * 
     * @return responderID
     */
    public java.lang.String getResponderID() {
        return responderID;
    }


    /**
     * Sets the responderID value for this ConfirmationInfo.
     * 
     * @param responderID
     */
    public void setResponderID(java.lang.String responderID) {
        this.responderID = responderID;
    }


    /**
     * Gets the producedAt value for this ConfirmationInfo.
     * 
     * @return producedAt
     */
    public java.lang.String getProducedAt() {
        return producedAt;
    }


    /**
     * Sets the producedAt value for this ConfirmationInfo.
     * 
     * @param producedAt
     */
    public void setProducedAt(java.lang.String producedAt) {
        this.producedAt = producedAt;
    }


    /**
     * Gets the responderCertificate value for this ConfirmationInfo.
     * 
     * @return responderCertificate
     */
    public com.codeborne.security.mobileid.client.CertificateInfo getResponderCertificate() {
        return responderCertificate;
    }


    /**
     * Sets the responderCertificate value for this ConfirmationInfo.
     * 
     * @param responderCertificate
     */
    public void setResponderCertificate(com.codeborne.security.mobileid.client.CertificateInfo responderCertificate) {
        this.responderCertificate = responderCertificate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfirmationInfo)) return false;
        ConfirmationInfo other = (ConfirmationInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.responderID==null && other.getResponderID()==null) || 
             (this.responderID!=null &&
              this.responderID.equals(other.getResponderID()))) &&
            ((this.producedAt==null && other.getProducedAt()==null) || 
             (this.producedAt!=null &&
              this.producedAt.equals(other.getProducedAt()))) &&
            ((this.responderCertificate==null && other.getResponderCertificate()==null) || 
             (this.responderCertificate!=null &&
              this.responderCertificate.equals(other.getResponderCertificate())));
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
        if (getResponderID() != null) {
            _hashCode += getResponderID().hashCode();
        }
        if (getProducedAt() != null) {
            _hashCode += getProducedAt().hashCode();
        }
        if (getResponderCertificate() != null) {
            _hashCode += getResponderCertificate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConfirmationInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "ConfirmationInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responderID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ResponderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("producedAt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ProducedAt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responderCertificate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ResponderCertificate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "CertificateInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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

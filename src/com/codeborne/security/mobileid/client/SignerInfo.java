/**
 * SignerInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class SignerInfo  implements java.io.Serializable {
    private java.lang.String commonName;

    private java.lang.String IDCode;

    private com.codeborne.security.mobileid.client.CertificateInfo certificate;

    public SignerInfo() {
    }

    public SignerInfo(
           java.lang.String commonName,
           java.lang.String IDCode,
           com.codeborne.security.mobileid.client.CertificateInfo certificate) {
           this.commonName = commonName;
           this.IDCode = IDCode;
           this.certificate = certificate;
    }


    /**
     * Gets the commonName value for this SignerInfo.
     * 
     * @return commonName
     */
    public java.lang.String getCommonName() {
        return commonName;
    }


    /**
     * Sets the commonName value for this SignerInfo.
     * 
     * @param commonName
     */
    public void setCommonName(java.lang.String commonName) {
        this.commonName = commonName;
    }


    /**
     * Gets the IDCode value for this SignerInfo.
     * 
     * @return IDCode
     */
    public java.lang.String getIDCode() {
        return IDCode;
    }


    /**
     * Sets the IDCode value for this SignerInfo.
     * 
     * @param IDCode
     */
    public void setIDCode(java.lang.String IDCode) {
        this.IDCode = IDCode;
    }


    /**
     * Gets the certificate value for this SignerInfo.
     * 
     * @return certificate
     */
    public com.codeborne.security.mobileid.client.CertificateInfo getCertificate() {
        return certificate;
    }


    /**
     * Sets the certificate value for this SignerInfo.
     * 
     * @param certificate
     */
    public void setCertificate(com.codeborne.security.mobileid.client.CertificateInfo certificate) {
        this.certificate = certificate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignerInfo)) return false;
        SignerInfo other = (SignerInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.commonName==null && other.getCommonName()==null) || 
             (this.commonName!=null &&
              this.commonName.equals(other.getCommonName()))) &&
            ((this.IDCode==null && other.getIDCode()==null) || 
             (this.IDCode!=null &&
              this.IDCode.equals(other.getIDCode()))) &&
            ((this.certificate==null && other.getCertificate()==null) || 
             (this.certificate!=null &&
              this.certificate.equals(other.getCertificate())));
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
        if (getCommonName() != null) {
            _hashCode += getCommonName().hashCode();
        }
        if (getIDCode() != null) {
            _hashCode += getIDCode().hashCode();
        }
        if (getCertificate() != null) {
            _hashCode += getCertificate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignerInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignerInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commonName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "CommonName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IDCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "IDCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certificate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Certificate"));
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

/**
 * SignedDocInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class SignedDocInfo  implements java.io.Serializable {
    private java.lang.String format;

    private java.lang.String version;

    private com.codeborne.security.mobileid.client.DataFileInfo[] dataFileInfo;

    private com.codeborne.security.mobileid.client.SignatureInfo[] signatureInfo;

    public SignedDocInfo() {
    }

    public SignedDocInfo(
           java.lang.String format,
           java.lang.String version,
           com.codeborne.security.mobileid.client.DataFileInfo[] dataFileInfo,
           com.codeborne.security.mobileid.client.SignatureInfo[] signatureInfo) {
           this.format = format;
           this.version = version;
           this.dataFileInfo = dataFileInfo;
           this.signatureInfo = signatureInfo;
    }


    /**
     * Gets the format value for this SignedDocInfo.
     * 
     * @return format
     */
    public java.lang.String getFormat() {
        return format;
    }


    /**
     * Sets the format value for this SignedDocInfo.
     * 
     * @param format
     */
    public void setFormat(java.lang.String format) {
        this.format = format;
    }


    /**
     * Gets the version value for this SignedDocInfo.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this SignedDocInfo.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the dataFileInfo value for this SignedDocInfo.
     * 
     * @return dataFileInfo
     */
    public com.codeborne.security.mobileid.client.DataFileInfo[] getDataFileInfo() {
        return dataFileInfo;
    }


    /**
     * Sets the dataFileInfo value for this SignedDocInfo.
     * 
     * @param dataFileInfo
     */
    public void setDataFileInfo(com.codeborne.security.mobileid.client.DataFileInfo[] dataFileInfo) {
        this.dataFileInfo = dataFileInfo;
    }

    public com.codeborne.security.mobileid.client.DataFileInfo getDataFileInfo(int i) {
        return this.dataFileInfo[i];
    }

    public void setDataFileInfo(int i, com.codeborne.security.mobileid.client.DataFileInfo _value) {
        this.dataFileInfo[i] = _value;
    }


    /**
     * Gets the signatureInfo value for this SignedDocInfo.
     * 
     * @return signatureInfo
     */
    public com.codeborne.security.mobileid.client.SignatureInfo[] getSignatureInfo() {
        return signatureInfo;
    }


    /**
     * Sets the signatureInfo value for this SignedDocInfo.
     * 
     * @param signatureInfo
     */
    public void setSignatureInfo(com.codeborne.security.mobileid.client.SignatureInfo[] signatureInfo) {
        this.signatureInfo = signatureInfo;
    }

    public com.codeborne.security.mobileid.client.SignatureInfo getSignatureInfo(int i) {
        return this.signatureInfo[i];
    }

    public void setSignatureInfo(int i, com.codeborne.security.mobileid.client.SignatureInfo _value) {
        this.signatureInfo[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignedDocInfo)) return false;
        SignedDocInfo other = (SignedDocInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.format==null && other.getFormat()==null) || 
             (this.format!=null &&
              this.format.equals(other.getFormat()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.dataFileInfo==null && other.getDataFileInfo()==null) || 
             (this.dataFileInfo!=null &&
              java.util.Arrays.equals(this.dataFileInfo, other.getDataFileInfo()))) &&
            ((this.signatureInfo==null && other.getSignatureInfo()==null) || 
             (this.signatureInfo!=null &&
              java.util.Arrays.equals(this.signatureInfo, other.getSignatureInfo())));
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
        if (getFormat() != null) {
            _hashCode += getFormat().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getDataFileInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDataFileInfo());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDataFileInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSignatureInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignatureInfo());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignatureInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignedDocInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignedDocInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("format");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Format"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataFileInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DataFileInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "DataFileInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SignatureInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignatureInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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

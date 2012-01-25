/**
 * SignatureType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.digidoc;

public class SignatureType  implements java.io.Serializable {
    private java.lang.String XMLSignature;

    private byte[] base64Signature;

    private java.lang.String XAdESSignature;

    public SignatureType() {
    }

    public SignatureType(
           java.lang.String XMLSignature,
           byte[] base64Signature,
           java.lang.String XAdESSignature) {
           this.XMLSignature = XMLSignature;
           this.base64Signature = base64Signature;
           this.XAdESSignature = XAdESSignature;
    }


    /**
     * Gets the XMLSignature value for this SignatureType.
     * 
     * @return XMLSignature
     */
    public java.lang.String getXMLSignature() {
        return XMLSignature;
    }


    /**
     * Sets the XMLSignature value for this SignatureType.
     * 
     * @param XMLSignature
     */
    public void setXMLSignature(java.lang.String XMLSignature) {
        this.XMLSignature = XMLSignature;
    }


    /**
     * Gets the base64Signature value for this SignatureType.
     * 
     * @return base64Signature
     */
    public byte[] getBase64Signature() {
        return base64Signature;
    }


    /**
     * Sets the base64Signature value for this SignatureType.
     * 
     * @param base64Signature
     */
    public void setBase64Signature(byte[] base64Signature) {
        this.base64Signature = base64Signature;
    }


    /**
     * Gets the XAdESSignature value for this SignatureType.
     * 
     * @return XAdESSignature
     */
    public java.lang.String getXAdESSignature() {
        return XAdESSignature;
    }


    /**
     * Sets the XAdESSignature value for this SignatureType.
     * 
     * @param XAdESSignature
     */
    public void setXAdESSignature(java.lang.String XAdESSignature) {
        this.XAdESSignature = XAdESSignature;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignatureType)) return false;
        SignatureType other = (SignatureType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.XMLSignature==null && other.getXMLSignature()==null) || 
             (this.XMLSignature!=null &&
              this.XMLSignature.equals(other.getXMLSignature()))) &&
            ((this.base64Signature==null && other.getBase64Signature()==null) || 
             (this.base64Signature!=null &&
              java.util.Arrays.equals(this.base64Signature, other.getBase64Signature()))) &&
            ((this.XAdESSignature==null && other.getXAdESSignature()==null) || 
             (this.XAdESSignature!=null &&
              this.XAdESSignature.equals(other.getXAdESSignature())));
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
        if (getXMLSignature() != null) {
            _hashCode += getXMLSignature().hashCode();
        }
        if (getBase64Signature() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBase64Signature());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBase64Signature(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getXAdESSignature() != null) {
            _hashCode += getXAdESSignature().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignatureType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl", "SignatureType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMLSignature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl", "XMLSignature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("base64Signature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl", "Base64Signature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XAdESSignature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl", "XAdESSignature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

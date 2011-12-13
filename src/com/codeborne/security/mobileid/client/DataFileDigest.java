/**
 * DataFileDigest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class DataFileDigest  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String digestType;

    private java.lang.String digestValue;

    public DataFileDigest() {
    }

    public DataFileDigest(
           java.lang.String id,
           java.lang.String digestType,
           java.lang.String digestValue) {
           this.id = id;
           this.digestType = digestType;
           this.digestValue = digestValue;
    }


    /**
     * Gets the id value for this DataFileDigest.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this DataFileDigest.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the digestType value for this DataFileDigest.
     * 
     * @return digestType
     */
    public java.lang.String getDigestType() {
        return digestType;
    }


    /**
     * Sets the digestType value for this DataFileDigest.
     * 
     * @param digestType
     */
    public void setDigestType(java.lang.String digestType) {
        this.digestType = digestType;
    }


    /**
     * Gets the digestValue value for this DataFileDigest.
     * 
     * @return digestValue
     */
    public java.lang.String getDigestValue() {
        return digestValue;
    }


    /**
     * Sets the digestValue value for this DataFileDigest.
     * 
     * @param digestValue
     */
    public void setDigestValue(java.lang.String digestValue) {
        this.digestValue = digestValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DataFileDigest)) return false;
        DataFileDigest other = (DataFileDigest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.digestType==null && other.getDigestType()==null) || 
             (this.digestType!=null &&
              this.digestType.equals(other.getDigestType()))) &&
            ((this.digestValue==null && other.getDigestValue()==null) || 
             (this.digestValue!=null &&
              this.digestValue.equals(other.getDigestValue())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getDigestType() != null) {
            _hashCode += getDigestType().hashCode();
        }
        if (getDigestValue() != null) {
            _hashCode += getDigestValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DataFileDigest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "DataFileDigest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("digestType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DigestType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("digestValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DigestValue"));
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

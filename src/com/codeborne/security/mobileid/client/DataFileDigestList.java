/**
 * DataFileDigestList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class DataFileDigestList  implements java.io.Serializable {
    private com.codeborne.security.mobileid.client.DataFileDigest[] dataFileDigest;

    public DataFileDigestList() {
    }

    public DataFileDigestList(
           com.codeborne.security.mobileid.client.DataFileDigest[] dataFileDigest) {
           this.dataFileDigest = dataFileDigest;
    }


    /**
     * Gets the dataFileDigest value for this DataFileDigestList.
     * 
     * @return dataFileDigest
     */
    public com.codeborne.security.mobileid.client.DataFileDigest[] getDataFileDigest() {
        return dataFileDigest;
    }


    /**
     * Sets the dataFileDigest value for this DataFileDigestList.
     * 
     * @param dataFileDigest
     */
    public void setDataFileDigest(com.codeborne.security.mobileid.client.DataFileDigest[] dataFileDigest) {
        this.dataFileDigest = dataFileDigest;
    }

    public com.codeborne.security.mobileid.client.DataFileDigest getDataFileDigest(int i) {
        return this.dataFileDigest[i];
    }

    public void setDataFileDigest(int i, com.codeborne.security.mobileid.client.DataFileDigest _value) {
        this.dataFileDigest[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DataFileDigestList)) return false;
        DataFileDigestList other = (DataFileDigestList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataFileDigest==null && other.getDataFileDigest()==null) || 
             (this.dataFileDigest!=null &&
              java.util.Arrays.equals(this.dataFileDigest, other.getDataFileDigest())));
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
        if (getDataFileDigest() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDataFileDigest());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDataFileDigest(), i);
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
        new org.apache.axis.description.TypeDesc(DataFileDigestList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "DataFileDigestList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataFileDigest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DataFileDigest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "DataFileDigest"));
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

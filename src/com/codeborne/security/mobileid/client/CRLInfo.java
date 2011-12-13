/**
 * CRLInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class CRLInfo  implements java.io.Serializable {
    private java.lang.String issuer;

    private java.util.Calendar lastUpdate;

    private java.util.Calendar nextUpdate;

    private com.codeborne.security.mobileid.client.RevokedInfo[] revocations;

    public CRLInfo() {
    }

    public CRLInfo(
           java.lang.String issuer,
           java.util.Calendar lastUpdate,
           java.util.Calendar nextUpdate,
           com.codeborne.security.mobileid.client.RevokedInfo[] revocations) {
           this.issuer = issuer;
           this.lastUpdate = lastUpdate;
           this.nextUpdate = nextUpdate;
           this.revocations = revocations;
    }


    /**
     * Gets the issuer value for this CRLInfo.
     * 
     * @return issuer
     */
    public java.lang.String getIssuer() {
        return issuer;
    }


    /**
     * Sets the issuer value for this CRLInfo.
     * 
     * @param issuer
     */
    public void setIssuer(java.lang.String issuer) {
        this.issuer = issuer;
    }


    /**
     * Gets the lastUpdate value for this CRLInfo.
     * 
     * @return lastUpdate
     */
    public java.util.Calendar getLastUpdate() {
        return lastUpdate;
    }


    /**
     * Sets the lastUpdate value for this CRLInfo.
     * 
     * @param lastUpdate
     */
    public void setLastUpdate(java.util.Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    /**
     * Gets the nextUpdate value for this CRLInfo.
     * 
     * @return nextUpdate
     */
    public java.util.Calendar getNextUpdate() {
        return nextUpdate;
    }


    /**
     * Sets the nextUpdate value for this CRLInfo.
     * 
     * @param nextUpdate
     */
    public void setNextUpdate(java.util.Calendar nextUpdate) {
        this.nextUpdate = nextUpdate;
    }


    /**
     * Gets the revocations value for this CRLInfo.
     * 
     * @return revocations
     */
    public com.codeborne.security.mobileid.client.RevokedInfo[] getRevocations() {
        return revocations;
    }


    /**
     * Sets the revocations value for this CRLInfo.
     * 
     * @param revocations
     */
    public void setRevocations(com.codeborne.security.mobileid.client.RevokedInfo[] revocations) {
        this.revocations = revocations;
    }

    public com.codeborne.security.mobileid.client.RevokedInfo getRevocations(int i) {
        return this.revocations[i];
    }

    public void setRevocations(int i, com.codeborne.security.mobileid.client.RevokedInfo _value) {
        this.revocations[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CRLInfo)) return false;
        CRLInfo other = (CRLInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.issuer==null && other.getIssuer()==null) || 
             (this.issuer!=null &&
              this.issuer.equals(other.getIssuer()))) &&
            ((this.lastUpdate==null && other.getLastUpdate()==null) || 
             (this.lastUpdate!=null &&
              this.lastUpdate.equals(other.getLastUpdate()))) &&
            ((this.nextUpdate==null && other.getNextUpdate()==null) || 
             (this.nextUpdate!=null &&
              this.nextUpdate.equals(other.getNextUpdate()))) &&
            ((this.revocations==null && other.getRevocations()==null) || 
             (this.revocations!=null &&
              java.util.Arrays.equals(this.revocations, other.getRevocations())));
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
        if (getIssuer() != null) {
            _hashCode += getIssuer().hashCode();
        }
        if (getLastUpdate() != null) {
            _hashCode += getLastUpdate().hashCode();
        }
        if (getNextUpdate() != null) {
            _hashCode += getNextUpdate().hashCode();
        }
        if (getRevocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRevocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRevocations(), i);
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
        new org.apache.axis.description.TypeDesc(CRLInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "CRLInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issuer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Issuer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "LastUpdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nextUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NextUpdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("revocations");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Revocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "RevokedInfo"));
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

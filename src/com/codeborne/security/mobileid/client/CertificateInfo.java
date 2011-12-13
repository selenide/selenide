/**
 * CertificateInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class CertificateInfo  implements java.io.Serializable {
    private java.lang.String issuer;

    private java.lang.String subject;

    private java.util.Calendar validFrom;

    private java.util.Calendar validTo;

    private java.lang.String issuerSerial;

    private com.codeborne.security.mobileid.client.CertificatePolicy[] policies;

    public CertificateInfo() {
    }

    public CertificateInfo(
           java.lang.String issuer,
           java.lang.String subject,
           java.util.Calendar validFrom,
           java.util.Calendar validTo,
           java.lang.String issuerSerial,
           com.codeborne.security.mobileid.client.CertificatePolicy[] policies) {
           this.issuer = issuer;
           this.subject = subject;
           this.validFrom = validFrom;
           this.validTo = validTo;
           this.issuerSerial = issuerSerial;
           this.policies = policies;
    }


    /**
     * Gets the issuer value for this CertificateInfo.
     * 
     * @return issuer
     */
    public java.lang.String getIssuer() {
        return issuer;
    }


    /**
     * Sets the issuer value for this CertificateInfo.
     * 
     * @param issuer
     */
    public void setIssuer(java.lang.String issuer) {
        this.issuer = issuer;
    }


    /**
     * Gets the subject value for this CertificateInfo.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this CertificateInfo.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }


    /**
     * Gets the validFrom value for this CertificateInfo.
     * 
     * @return validFrom
     */
    public java.util.Calendar getValidFrom() {
        return validFrom;
    }


    /**
     * Sets the validFrom value for this CertificateInfo.
     * 
     * @param validFrom
     */
    public void setValidFrom(java.util.Calendar validFrom) {
        this.validFrom = validFrom;
    }


    /**
     * Gets the validTo value for this CertificateInfo.
     * 
     * @return validTo
     */
    public java.util.Calendar getValidTo() {
        return validTo;
    }


    /**
     * Sets the validTo value for this CertificateInfo.
     * 
     * @param validTo
     */
    public void setValidTo(java.util.Calendar validTo) {
        this.validTo = validTo;
    }


    /**
     * Gets the issuerSerial value for this CertificateInfo.
     * 
     * @return issuerSerial
     */
    public java.lang.String getIssuerSerial() {
        return issuerSerial;
    }


    /**
     * Sets the issuerSerial value for this CertificateInfo.
     * 
     * @param issuerSerial
     */
    public void setIssuerSerial(java.lang.String issuerSerial) {
        this.issuerSerial = issuerSerial;
    }


    /**
     * Gets the policies value for this CertificateInfo.
     * 
     * @return policies
     */
    public com.codeborne.security.mobileid.client.CertificatePolicy[] getPolicies() {
        return policies;
    }


    /**
     * Sets the policies value for this CertificateInfo.
     * 
     * @param policies
     */
    public void setPolicies(com.codeborne.security.mobileid.client.CertificatePolicy[] policies) {
        this.policies = policies;
    }

    public com.codeborne.security.mobileid.client.CertificatePolicy getPolicies(int i) {
        return this.policies[i];
    }

    public void setPolicies(int i, com.codeborne.security.mobileid.client.CertificatePolicy _value) {
        this.policies[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CertificateInfo)) return false;
        CertificateInfo other = (CertificateInfo) obj;
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
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject()))) &&
            ((this.validFrom==null && other.getValidFrom()==null) || 
             (this.validFrom!=null &&
              this.validFrom.equals(other.getValidFrom()))) &&
            ((this.validTo==null && other.getValidTo()==null) || 
             (this.validTo!=null &&
              this.validTo.equals(other.getValidTo()))) &&
            ((this.issuerSerial==null && other.getIssuerSerial()==null) || 
             (this.issuerSerial!=null &&
              this.issuerSerial.equals(other.getIssuerSerial()))) &&
            ((this.policies==null && other.getPolicies()==null) || 
             (this.policies!=null &&
              java.util.Arrays.equals(this.policies, other.getPolicies())));
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
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        if (getValidFrom() != null) {
            _hashCode += getValidFrom().hashCode();
        }
        if (getValidTo() != null) {
            _hashCode += getValidTo().hashCode();
        }
        if (getIssuerSerial() != null) {
            _hashCode += getIssuerSerial().hashCode();
        }
        if (getPolicies() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPolicies());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPolicies(), i);
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
        new org.apache.axis.description.TypeDesc(CertificateInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "CertificateInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issuer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Issuer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validFrom");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ValidFrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validTo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ValidTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issuerSerial");
        elemField.setXmlName(new javax.xml.namespace.QName("", "IssuerSerial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("policies");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Policies"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "CertificatePolicy"));
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

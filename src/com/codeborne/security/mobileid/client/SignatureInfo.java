/**
 * SignatureInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class SignatureInfo  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String status;

    private com.codeborne.security.mobileid.client.Error error;

    private java.util.Calendar signingTime;

    private com.codeborne.security.mobileid.client.SignerRole[] signerRole;

    private com.codeborne.security.mobileid.client.SignatureProductionPlace signatureProductionPlace;

    private com.codeborne.security.mobileid.client.SignerInfo signer;

    private com.codeborne.security.mobileid.client.ConfirmationInfo confirmation;

    private com.codeborne.security.mobileid.client.TstInfo[] timestamps;

    private com.codeborne.security.mobileid.client.CRLInfo CRLInfo;

    public SignatureInfo() {
    }

    public SignatureInfo(
           java.lang.String id,
           java.lang.String status,
           com.codeborne.security.mobileid.client.Error error,
           java.util.Calendar signingTime,
           com.codeborne.security.mobileid.client.SignerRole[] signerRole,
           com.codeborne.security.mobileid.client.SignatureProductionPlace signatureProductionPlace,
           com.codeborne.security.mobileid.client.SignerInfo signer,
           com.codeborne.security.mobileid.client.ConfirmationInfo confirmation,
           com.codeborne.security.mobileid.client.TstInfo[] timestamps,
           com.codeborne.security.mobileid.client.CRLInfo CRLInfo) {
           this.id = id;
           this.status = status;
           this.error = error;
           this.signingTime = signingTime;
           this.signerRole = signerRole;
           this.signatureProductionPlace = signatureProductionPlace;
           this.signer = signer;
           this.confirmation = confirmation;
           this.timestamps = timestamps;
           this.CRLInfo = CRLInfo;
    }


    /**
     * Gets the id value for this SignatureInfo.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this SignatureInfo.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the status value for this SignatureInfo.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this SignatureInfo.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the error value for this SignatureInfo.
     * 
     * @return error
     */
    public com.codeborne.security.mobileid.client.Error getError() {
        return error;
    }


    /**
     * Sets the error value for this SignatureInfo.
     * 
     * @param error
     */
    public void setError(com.codeborne.security.mobileid.client.Error error) {
        this.error = error;
    }


    /**
     * Gets the signingTime value for this SignatureInfo.
     * 
     * @return signingTime
     */
    public java.util.Calendar getSigningTime() {
        return signingTime;
    }


    /**
     * Sets the signingTime value for this SignatureInfo.
     * 
     * @param signingTime
     */
    public void setSigningTime(java.util.Calendar signingTime) {
        this.signingTime = signingTime;
    }


    /**
     * Gets the signerRole value for this SignatureInfo.
     * 
     * @return signerRole
     */
    public com.codeborne.security.mobileid.client.SignerRole[] getSignerRole() {
        return signerRole;
    }


    /**
     * Sets the signerRole value for this SignatureInfo.
     * 
     * @param signerRole
     */
    public void setSignerRole(com.codeborne.security.mobileid.client.SignerRole[] signerRole) {
        this.signerRole = signerRole;
    }

    public com.codeborne.security.mobileid.client.SignerRole getSignerRole(int i) {
        return this.signerRole[i];
    }

    public void setSignerRole(int i, com.codeborne.security.mobileid.client.SignerRole _value) {
        this.signerRole[i] = _value;
    }


    /**
     * Gets the signatureProductionPlace value for this SignatureInfo.
     * 
     * @return signatureProductionPlace
     */
    public com.codeborne.security.mobileid.client.SignatureProductionPlace getSignatureProductionPlace() {
        return signatureProductionPlace;
    }


    /**
     * Sets the signatureProductionPlace value for this SignatureInfo.
     * 
     * @param signatureProductionPlace
     */
    public void setSignatureProductionPlace(com.codeborne.security.mobileid.client.SignatureProductionPlace signatureProductionPlace) {
        this.signatureProductionPlace = signatureProductionPlace;
    }


    /**
     * Gets the signer value for this SignatureInfo.
     * 
     * @return signer
     */
    public com.codeborne.security.mobileid.client.SignerInfo getSigner() {
        return signer;
    }


    /**
     * Sets the signer value for this SignatureInfo.
     * 
     * @param signer
     */
    public void setSigner(com.codeborne.security.mobileid.client.SignerInfo signer) {
        this.signer = signer;
    }


    /**
     * Gets the confirmation value for this SignatureInfo.
     * 
     * @return confirmation
     */
    public com.codeborne.security.mobileid.client.ConfirmationInfo getConfirmation() {
        return confirmation;
    }


    /**
     * Sets the confirmation value for this SignatureInfo.
     * 
     * @param confirmation
     */
    public void setConfirmation(com.codeborne.security.mobileid.client.ConfirmationInfo confirmation) {
        this.confirmation = confirmation;
    }


    /**
     * Gets the timestamps value for this SignatureInfo.
     * 
     * @return timestamps
     */
    public com.codeborne.security.mobileid.client.TstInfo[] getTimestamps() {
        return timestamps;
    }


    /**
     * Sets the timestamps value for this SignatureInfo.
     * 
     * @param timestamps
     */
    public void setTimestamps(com.codeborne.security.mobileid.client.TstInfo[] timestamps) {
        this.timestamps = timestamps;
    }

    public com.codeborne.security.mobileid.client.TstInfo getTimestamps(int i) {
        return this.timestamps[i];
    }

    public void setTimestamps(int i, com.codeborne.security.mobileid.client.TstInfo _value) {
        this.timestamps[i] = _value;
    }


    /**
     * Gets the CRLInfo value for this SignatureInfo.
     * 
     * @return CRLInfo
     */
    public com.codeborne.security.mobileid.client.CRLInfo getCRLInfo() {
        return CRLInfo;
    }


    /**
     * Sets the CRLInfo value for this SignatureInfo.
     * 
     * @param CRLInfo
     */
    public void setCRLInfo(com.codeborne.security.mobileid.client.CRLInfo CRLInfo) {
        this.CRLInfo = CRLInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignatureInfo)) return false;
        SignatureInfo other = (SignatureInfo) obj;
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
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.error==null && other.getError()==null) || 
             (this.error!=null &&
              this.error.equals(other.getError()))) &&
            ((this.signingTime==null && other.getSigningTime()==null) || 
             (this.signingTime!=null &&
              this.signingTime.equals(other.getSigningTime()))) &&
            ((this.signerRole==null && other.getSignerRole()==null) || 
             (this.signerRole!=null &&
              java.util.Arrays.equals(this.signerRole, other.getSignerRole()))) &&
            ((this.signatureProductionPlace==null && other.getSignatureProductionPlace()==null) || 
             (this.signatureProductionPlace!=null &&
              this.signatureProductionPlace.equals(other.getSignatureProductionPlace()))) &&
            ((this.signer==null && other.getSigner()==null) || 
             (this.signer!=null &&
              this.signer.equals(other.getSigner()))) &&
            ((this.confirmation==null && other.getConfirmation()==null) || 
             (this.confirmation!=null &&
              this.confirmation.equals(other.getConfirmation()))) &&
            ((this.timestamps==null && other.getTimestamps()==null) || 
             (this.timestamps!=null &&
              java.util.Arrays.equals(this.timestamps, other.getTimestamps()))) &&
            ((this.CRLInfo==null && other.getCRLInfo()==null) || 
             (this.CRLInfo!=null &&
              this.CRLInfo.equals(other.getCRLInfo())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getError() != null) {
            _hashCode += getError().hashCode();
        }
        if (getSigningTime() != null) {
            _hashCode += getSigningTime().hashCode();
        }
        if (getSignerRole() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignerRole());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignerRole(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSignatureProductionPlace() != null) {
            _hashCode += getSignatureProductionPlace().hashCode();
        }
        if (getSigner() != null) {
            _hashCode += getSigner().hashCode();
        }
        if (getConfirmation() != null) {
            _hashCode += getConfirmation().hashCode();
        }
        if (getTimestamps() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTimestamps());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTimestamps(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCRLInfo() != null) {
            _hashCode += getCRLInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignatureInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignatureInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Id"));
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
        elemField.setFieldName("error");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Error"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "Error"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signingTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SigningTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signerRole");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SignerRole"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignerRole"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureProductionPlace");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SignatureProductionPlace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignatureProductionPlace"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Signer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignerInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confirmation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Confirmation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "ConfirmationInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamps");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Timestamps"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "TstInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CRLInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "CRLInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "CRLInfo"));
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

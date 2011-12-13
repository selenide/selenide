/**
 * SignatureModulesArray.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.mobileid.client;

public class SignatureModulesArray  implements java.io.Serializable {
    private com.codeborne.security.mobileid.client.SignatureModule[] modules;

    public SignatureModulesArray() {
    }

    public SignatureModulesArray(
           com.codeborne.security.mobileid.client.SignatureModule[] modules) {
           this.modules = modules;
    }


    /**
     * Gets the modules value for this SignatureModulesArray.
     * 
     * @return modules
     */
    public com.codeborne.security.mobileid.client.SignatureModule[] getModules() {
        return modules;
    }


    /**
     * Sets the modules value for this SignatureModulesArray.
     * 
     * @param modules
     */
    public void setModules(com.codeborne.security.mobileid.client.SignatureModule[] modules) {
        this.modules = modules;
    }

    public com.codeborne.security.mobileid.client.SignatureModule getModules(int i) {
        return this.modules[i];
    }

    public void setModules(int i, com.codeborne.security.mobileid.client.SignatureModule _value) {
        this.modules[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignatureModulesArray)) return false;
        SignatureModulesArray other = (SignatureModulesArray) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.modules==null && other.getModules()==null) || 
             (this.modules!=null &&
              java.util.Arrays.equals(this.modules, other.getModules())));
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
        if (getModules() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getModules());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModules(), i);
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
        new org.apache.axis.description.TypeDesc(SignatureModulesArray.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignatureModulesArray"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modules");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Modules"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "SignatureModule"));
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

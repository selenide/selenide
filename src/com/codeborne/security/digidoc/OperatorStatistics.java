/**
 * OperatorStatistics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.digidoc;

public class OperatorStatistics  implements java.io.Serializable {
    private java.lang.String operator;

    private int totalOperatorSessions;

    private int activeOperatorSessions;

    public OperatorStatistics() {
    }

    public OperatorStatistics(
           java.lang.String operator,
           int totalOperatorSessions,
           int activeOperatorSessions) {
           this.operator = operator;
           this.totalOperatorSessions = totalOperatorSessions;
           this.activeOperatorSessions = activeOperatorSessions;
    }


    /**
     * Gets the operator value for this OperatorStatistics.
     * 
     * @return operator
     */
    public java.lang.String getOperator() {
        return operator;
    }


    /**
     * Sets the operator value for this OperatorStatistics.
     * 
     * @param operator
     */
    public void setOperator(java.lang.String operator) {
        this.operator = operator;
    }


    /**
     * Gets the totalOperatorSessions value for this OperatorStatistics.
     * 
     * @return totalOperatorSessions
     */
    public int getTotalOperatorSessions() {
        return totalOperatorSessions;
    }


    /**
     * Sets the totalOperatorSessions value for this OperatorStatistics.
     * 
     * @param totalOperatorSessions
     */
    public void setTotalOperatorSessions(int totalOperatorSessions) {
        this.totalOperatorSessions = totalOperatorSessions;
    }


    /**
     * Gets the activeOperatorSessions value for this OperatorStatistics.
     * 
     * @return activeOperatorSessions
     */
    public int getActiveOperatorSessions() {
        return activeOperatorSessions;
    }


    /**
     * Sets the activeOperatorSessions value for this OperatorStatistics.
     * 
     * @param activeOperatorSessions
     */
    public void setActiveOperatorSessions(int activeOperatorSessions) {
        this.activeOperatorSessions = activeOperatorSessions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OperatorStatistics)) return false;
        OperatorStatistics other = (OperatorStatistics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.operator==null && other.getOperator()==null) || 
             (this.operator!=null &&
              this.operator.equals(other.getOperator()))) &&
            this.totalOperatorSessions == other.getTotalOperatorSessions() &&
            this.activeOperatorSessions == other.getActiveOperatorSessions();
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
        if (getOperator() != null) {
            _hashCode += getOperator().hashCode();
        }
        _hashCode += getTotalOperatorSessions();
        _hashCode += getActiveOperatorSessions();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OperatorStatistics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl", "OperatorStatistics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Operator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalOperatorSessions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "TotalOperatorSessions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeOperatorSessions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ActiveOperatorSessions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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

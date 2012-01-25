/**
 * DigiDocServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.codeborne.security.digidoc;

public interface DigiDocServicePortType extends java.rmi.Remote {

    /**
     * Service definition of function d__StartSession
     */
    public void startSession(java.lang.String signingProfile, java.lang.String sigDocXML, boolean bHoldSession, com.codeborne.security.digidoc.DataFileData datafile, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.IntHolder sesscode, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__CloseSession
     */
    public java.lang.String closeSession(int sesscode) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__CreateSignedDoc
     */
    public void createSignedDoc(int sesscode, java.lang.String format, java.lang.String version, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__AddDataFile
     */
    public void addDataFile(int sesscode, java.lang.String fileName, java.lang.String mimeType, java.lang.String contentType, int size, java.lang.String digestType, java.lang.String digestValue, java.lang.String content, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__RemoveDataFile
     */
    public void removeDataFile(int sesscode, java.lang.String dataFileId, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetSignedDoc
     */
    public void getSignedDoc(int sesscode, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder signedDocData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetSignedDocInfo
     */
    public void getSignedDocInfo(int sesscode, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetDataFile
     */
    public void getDataFile(int sesscode, java.lang.String dataFileId, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.DataFileDataHolder dataFileData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetSignersCertificate
     */
    public void getSignersCertificate(int sesscode, java.lang.String signatureId, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder certificateData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetNotarysCertificate
     */
    public void getNotarysCertificate(int sesscode, java.lang.String signatureId, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder certificateData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetNotary
     */
    public void getNotary(int sesscode, java.lang.String signatureId, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder ocspData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetTSACertificate
     */
    public void getTSACertificate(int sesscode, java.lang.String timestampId, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder certificateData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetTimestamp
     */
    public void getTimestamp(int sesscode, java.lang.String timestampId, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder timestampData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetCRL
     */
    public void getCRL(int sesscode, java.lang.String signatureId, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder CRLData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetSignatureModules
     */
    public void getSignatureModules(int sesscode, java.lang.String platform, java.lang.String phase, java.lang.String type, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignatureModulesArrayHolder modules) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__PrepareSignature
     */
    public void prepareSignature(int sesscode, java.lang.String signersCertificate, java.lang.String signersTokenId, java.lang.String role, java.lang.String city, java.lang.String state, java.lang.String postalCode, java.lang.String country, java.lang.String signingProfile, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder signatureId, javax.xml.rpc.holders.StringHolder signedInfoDigest) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__FinalizeSignature
     */
    public void finalizeSignature(int sesscode, java.lang.String signatureId, java.lang.String signatureValue, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__RemoveSignature
     */
    public void removeSignature(int sesscode, java.lang.String signatureId, javax.xml.rpc.holders.StringHolder status, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetVersion
     */
    public void getVersion(javax.xml.rpc.holders.StringHolder name, javax.xml.rpc.holders.StringHolder version, javax.xml.rpc.holders.StringHolder libraryVersion) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__MobileSign
     */
    public void mobileSign(int sesscode, java.lang.String signerIDCode, java.lang.String signersCountry, java.lang.String signerPhoneNo, java.lang.String serviceName, java.lang.String additionalDataToBeDisplayed, java.lang.String language, java.lang.String role, java.lang.String city, java.lang.String stateOrProvince, java.lang.String postalCode, java.lang.String countryName, java.lang.String signingProfile, java.lang.String messagingMode, int asyncConfiguration, boolean returnDocInfo, boolean returnDocData, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder statusCode, javax.xml.rpc.holders.StringHolder challengeID) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetStatusInfo
     */
    public void getStatusInfo(int sesscode, boolean returnDocInfo, boolean waitSignature, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder statusCode, com.codeborne.security.digidoc.holders.SignedDocInfoHolder signedDocInfo) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__MobileAuthenticate
     */
    public void mobileAuthenticate(java.lang.String IDCode, java.lang.String countryCode, java.lang.String phoneNo, java.lang.String language, java.lang.String serviceName, java.lang.String messageToDisplay, java.lang.String SPChallenge, java.lang.String messagingMode, int asyncConfiguration, boolean returnCertData, boolean returnRevocationData, javax.xml.rpc.holders.IntHolder sesscode, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder userIDCode, javax.xml.rpc.holders.StringHolder userGivenname, javax.xml.rpc.holders.StringHolder userSurname, javax.xml.rpc.holders.StringHolder userCountry, javax.xml.rpc.holders.StringHolder userCN, javax.xml.rpc.holders.StringHolder certificateData, javax.xml.rpc.holders.StringHolder challengeID, javax.xml.rpc.holders.StringHolder challenge, javax.xml.rpc.holders.StringHolder revocationData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetMobileAuthenticateStatus
     */
    public void getMobileAuthenticateStatus(int sesscode, boolean waitSignature, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder signature) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__MobileCreateSignature
     */
    public void mobileCreateSignature(java.lang.String IDCode, java.lang.String signersCountry, java.lang.String phoneNo, java.lang.String language, java.lang.String serviceName, java.lang.String messageToDisplay, java.lang.String role, java.lang.String city, java.lang.String stateOrProvince, java.lang.String postalCode, java.lang.String countryName, java.lang.String signingProfile, com.codeborne.security.digidoc.DataFileDigestList dataFiles, java.lang.String format, java.lang.String version, java.lang.String signatureID, java.lang.String messagingMode, int asyncConfiguration, javax.xml.rpc.holders.IntHolder sesscode, javax.xml.rpc.holders.StringHolder challengeID, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetMobileCreateSignatureStatus
     */
    public void getMobileCreateSignatureStatus(javax.xml.rpc.holders.IntHolder sesscode, boolean waitSignature, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder signature) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetMobileCertificate
     */
    public void getMobileCertificate(java.lang.String IDCode, java.lang.String country, java.lang.String phoneNo, java.lang.String returnCertData, javax.xml.rpc.holders.StringHolder authCertStatus, javax.xml.rpc.holders.StringHolder signCertStatus, javax.xml.rpc.holders.StringHolder authCertData, javax.xml.rpc.holders.StringHolder signCertData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__GetStatistics
     */
    public void getStatistics(javax.xml.rpc.holders.StringHolder serviceStart, javax.xml.rpc.holders.StringHolder configurationChange, javax.xml.rpc.holders.StringHolder DBStatus, javax.xml.rpc.holders.IntHolder totalSessions, javax.xml.rpc.holders.IntHolder activeSessions, com.codeborne.security.digidoc.holders.ClientStatisticsHolder clientStats, com.codeborne.security.digidoc.holders.OperatorStatisticsHolder operatorStats, com.codeborne.security.digidoc.holders.AsyncResponderStatisticsHolder asyncResponderStats) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__CheckCertificate
     */
    public void checkCertificate(java.lang.String certificate, boolean returnRevocationData, javax.xml.rpc.holders.StringHolder status, javax.xml.rpc.holders.StringHolder userIDCode, javax.xml.rpc.holders.StringHolder userGivenname, javax.xml.rpc.holders.StringHolder userSurname, javax.xml.rpc.holders.StringHolder userCountry, javax.xml.rpc.holders.StringHolder userOrganisation, javax.xml.rpc.holders.StringHolder userCN, javax.xml.rpc.holders.StringHolder issuerCN, javax.xml.rpc.holders.StringHolder keyUsage, javax.xml.rpc.holders.StringHolder enhancedKeyUsage, javax.xml.rpc.holders.StringHolder revocationData) throws java.rmi.RemoteException;

    /**
     * Service definition of function d__ReportMSSPStatus
     */
    public void reportMSSPStatus(org.apache.axis.types.PositiveInteger MSSP_TransID, com.codeborne.security.digidoc.StatusType status, com.codeborne.security.digidoc.SignatureType MSS_Signature) throws java.rmi.RemoteException;
}

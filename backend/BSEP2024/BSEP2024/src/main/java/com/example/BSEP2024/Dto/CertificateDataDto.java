package com.example.BSEP2024.Dto;

import java.util.Date;

public class CertificateDataDto {
    private String publicKey;
    private String subjectInfo;
    private String issuerInfo;
    private Date startDate;
    private Date endDate;

    public CertificateDataDto() {
    }

    public CertificateDataDto(String publicKey, String subjectInfo, String issuerInfo, Date startDate, Date endDate) {
        this.publicKey = publicKey;
        this.subjectInfo = subjectInfo;
        this.issuerInfo = issuerInfo;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(String subjectInfo) {
        this.subjectInfo = subjectInfo;
    }

    public String getIssuerInfo() {
        return issuerInfo;
    }

    public void setIssuerInfo(String issuerInfo) {
        this.issuerInfo = issuerInfo;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

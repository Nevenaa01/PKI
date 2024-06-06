package com.example.BSEP2024.Dto;

import com.example.BSEP2024.models.User;

public class CreateCertificateDto {
    public User issuerData;
    public User subjectData;
    public String startDate;
    public String endDate;

    public String serialNum;

    public CreateCertificateDto() {
    }

    public CreateCertificateDto(User issuerData, User subjectData, String startDate, String endDate, String serialNum) {
        this.issuerData = issuerData;
        this.subjectData = subjectData;
        this.startDate = startDate;
        this.endDate = endDate;
        this.serialNum = serialNum;
    }

    public User getIssuerData() {
        return issuerData;
    }

    public void setIssuerData(User issuerData) {
        this.issuerData = issuerData;
    }

    public User getSubjectData() {
        return subjectData;
    }

    public void setSubjectData(User subjectData) {
        this.subjectData = subjectData;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}

package com.example.BSEP2024.Dto;

import com.example.BSEP2024.models.User;

public class SelfSignedCertificateDto {
    public User issuerData;

    public String startDate;
    public String endDate;

    public SelfSignedCertificateDto(User issuerData, String startDate, String endDate) {
        this.issuerData = issuerData;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public User getIssuerData() {
        return issuerData;
    }

    public void setIssuerData(User issuerData) {
        this.issuerData = issuerData;
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
}

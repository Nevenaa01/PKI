package com.example.BSEP2024.models;

import org.bouncycastle.asn1.x500.X500Name;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;

public class SubjectData {
    private PublicKey publicKey;
    private X500Name x500name;
    private String serialNumber;
    private Date startDate;
    private Date endDate;

    public SubjectData() {
    }

    public SubjectData(PublicKey publicKey, X500Name x500name, String serialNumber, Date startDate, Date endDate) {
        this.publicKey = publicKey;
        this.x500name = x500name;
        this.serialNumber = serialNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public X500Name getX500name() {
        return this.x500name;
    }

    public void setX500name(X500Name x500name) {
        this.x500name = x500name;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


}

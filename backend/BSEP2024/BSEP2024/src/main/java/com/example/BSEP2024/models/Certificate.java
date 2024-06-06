package com.example.BSEP2024.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.PublicKey;

@Getter
@Setter
@Entity
@Table
public class Certificate {
    @Id
    @SequenceGenerator(
            name="certificate_sequence",
            sequenceName = "certificate_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "certificate_sequence"
    )
    private Long id;
    private String serialNumber;
    private Long issuerId;
    public Long subjectId;
    @Column(length = 4096)
    private String publicKey;
    private TypeCertificate typeCertificate;
    private Boolean valid;

    public Certificate(){
        super();
    }

    public Certificate(Long id, String serialNumber, Long issuerId, Long subjectId, String publicKey, TypeCertificate typeCertificate, Boolean valid) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.issuerId = issuerId;
        this.subjectId = subjectId;
        this.publicKey = publicKey;
        this.typeCertificate = typeCertificate;
        this.valid = valid;
    }

    public Certificate(String serialNumber, Long issuerId, Long subjectId, String publicKey, TypeCertificate typeCertificate, Boolean valid) {
        this.serialNumber = serialNumber;
        this.issuerId = issuerId;
        this.subjectId = subjectId;
        this.publicKey = publicKey;
        this.typeCertificate = typeCertificate;
        this.valid = valid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public TypeCertificate getTypeCertificate() {
        return typeCertificate;
    }

    public void setTypeCertificate(TypeCertificate typeCertificate) {
        this.typeCertificate = typeCertificate;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}

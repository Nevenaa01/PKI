package com.example.BSEP2024.models;

import org.bouncycastle.asn1.x500.X500Name;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class IssuerData {
    private X500Name x500name;
    private PrivateKey privateKey;

    public IssuerData() {
    }

    public IssuerData(PrivateKey privateKey, X500Name x500name) {
        this.privateKey = privateKey;
        this.x500name = x500name;
    }

    public X500Name getX500name() {
        return this.x500name;
    }

    public void setX500name(X500Name x500name) {
        this.x500name = x500name;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}

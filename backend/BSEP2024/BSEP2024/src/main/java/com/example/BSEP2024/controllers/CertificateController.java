package com.example.BSEP2024.controllers;

import com.example.BSEP2024.Dto.CertificateDataDto;
import com.example.BSEP2024.Dto.CreateCertificateDto;
import com.example.BSEP2024.Dto.SelfSignedCertificateDto;
import com.example.BSEP2024.models.*;
import com.example.BSEP2024.models.Certificate;
import com.example.BSEP2024.services.CertificateService;
import com.example.BSEP2024.services.UserService;
import com.example.BSEP2024.utils.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/certificate")
public class CertificateController {
    private final CertificateService certificateService;
    private final UserService userService;

    @Autowired
    public CertificateController(CertificateService cerService, UserService userService){
        this.certificateService = cerService;
        this.userService = userService;
    }

    @PostMapping("/create-certificate")
    public String createNewCertificate(@RequestBody CreateCertificateDto certData)  throws  Exception{
        this.certificateService.createNewCertificate(certData);

        return "Ok";

    }



    @GetMapping("/get_certificate/{serial}/{issuerId}")
    public String createSelfSignedCertificate(@PathVariable String serial,@PathVariable Long issuerId)  throws  Exception{
        String path=certificateService.findCertificateInFolder(serial);
        int lastIndex = path.lastIndexOf("\\");
        path = path.substring(0, lastIndex);
        String filename = serial + ".jks";
        User issuer = userService.getUserById(issuerId);
        String username = issuer.getUsername();
        KeyStoreReader kr = new KeyStoreReader();

        String pass = DecryptPassword.decrypt(issuer.getPassword(), issuer.getId()+issuer.getEmail()+issuer.getUsername() + issuer.getCity() + issuer.getCountry() + issuer.getOrganization());
        return kr.readKeyStoreFile(path +"/" + filename,  pass, issuerId.toString());
    }

    @GetMapping("/get_certificates_byissuer/{userId}")
    public List<Certificate> getCertificatesIssuer(@PathVariable Long userId){
        return this.certificateService.getCertificatesIssuedByMe(userId);
    }

    @GetMapping("/getAllValidCertificates")
    public ArrayList<Certificate> getAllValidCertificates(){
        return this.certificateService.GetAllValidCertificates();
    }

    @PutMapping("/admin/withdraw")
    public Certificate withdrawCertificate(@RequestBody Certificate certificate)  throws  Exception {
        return certificateService.withdrawCertificate(certificate);
    }
    @GetMapping("/get_certificates_bysubject/{userId}")
    public List<Certificate> getCertificatesSubject(@PathVariable Long userId){
        return this.certificateService.getCertificatesIssuedToMe(userId);
    }
    @GetMapping("/getAll")
    public List<Certificate> getAll(){
        return this.certificateService.getAll();
    }

}
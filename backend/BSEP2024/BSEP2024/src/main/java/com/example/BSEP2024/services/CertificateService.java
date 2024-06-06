package com.example.BSEP2024.services;

import com.example.BSEP2024.Dto.CreateCertificateDto;
import com.example.BSEP2024.models.*;
import com.example.BSEP2024.models.Certificate;
import com.example.BSEP2024.repositories.CertificateRepository;
import com.example.BSEP2024.repositories.UserRepository;
import com.example.BSEP2024.utils.*;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserService userService;

    KeyStoreReader keyStoreReader;

    @Autowired
    public CertificateService(CertificateRepository certRepository,
                              UserService userService) {
        this.certificateRepository = certRepository;
        this.keyStoreReader = new KeyStoreReader();
        this.userService = userService;
    }


    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public List<Certificate> getCertificatesIssuedByMe(Long issuerId){
        List<Certificate> certs = getAllCertificates();
        List<Certificate> issuedByMe = new ArrayList<>();

        for (Certificate cert : certs) {
            if (cert.getIssuerId().equals(issuerId)) {
                issuedByMe.add(cert);
            }
        }
        return issuedByMe;
    }
    public List<Certificate> getCertificatesIssuedToMe(Long subjectId){
        List<Certificate> certs = getAllCertificates();
        List<Certificate> subject_certs = new ArrayList<>();

        for (Certificate cert : certs) {
            if (cert.getSubjectId().equals(subjectId)) {
                subject_certs.add(cert);
            }
        }
        return subject_certs;


    }

    public Certificate getCertificateBySerialNumber(String serialNumber) {
        List<Certificate> certs = getAllCertificates();

        Optional<Certificate> certificate = certs.stream()
                .filter(cert -> cert.getSerialNumber().equals(serialNumber))
                .findFirst();
        if(certificate.isPresent())return certificate.get();
        return null;
    }

    public void createCertificate(Certificate cert){
        this.certificateRepository.save(cert);
    }

    public Certificate createNewCertificate(CreateCertificateDto certData) throws  Exception{
        String routeFromCertificate = null;
        PrivateKey issuerPrivateKey = null;
        //validira da li je isti subj i issuer i da li je admin
        if(certData.getIssuerData().getId() != certData.getSubjectData().getId()){
            //pronadji cert iz jks i sacuvat putanju do njega
            routeFromCertificate = findCertificateInFolder(certData.getSerialNum());
            System.out.println(routeFromCertificate);
            int lastIndex = routeFromCertificate.lastIndexOf("\\");
            String rutaGdjeSertifikatIssuera = routeFromCertificate.substring(0, lastIndex);
            //izvuci priv iz jks
            KeyStoreReader krs = new KeyStoreReader();
            User issuer = certData.getIssuerData();
            String passKey = DecryptPassword.decrypt(issuer.getPassword(), issuer.getId()+issuer.getEmail()+issuer.getUsername() + issuer.getCity() + issuer.getCountry() + issuer.getOrganization());
            String pass = (issuer.getUsername()+certData.getSerialNum());
            issuerPrivateKey = krs.readPrivateKey( rutaGdjeSertifikatIssuera + "\\" + certData.getSerialNum() +  ".jks", passKey, issuer.getId().toString(), pass );
        }

        //kljucevi za subjecta
        KeyPair keyPairSubject = generateKeyPair();

        X500NameBuilder builderIssuer = getX500NameBuilder(certData.getIssuerData());
        X500NameBuilder builderSubject = getX500NameBuilder(certData.getSubjectData());

        SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = iso8601Formater.parse(certData.getStartDate());
        Date endDate = iso8601Formater.parse(certData.getEndDate());

        String serialNumber = generateSerialNumber();

        IssuerData id = null;
        if(issuerPrivateKey == null){
            id = new IssuerData(keyPairSubject.getPrivate(),builderSubject.build());
        }else{
            id = new IssuerData(issuerPrivateKey,builderIssuer.build());
        }

        SubjectData sd = new SubjectData(keyPairSubject.getPublic(),builderSubject.build(),serialNumber,startDate,endDate);

        TypeCertificate type = TypeCertificate.ROOT_CA;
        if(certData.getSubjectData().getUserType() == 1){
            type = TypeCertificate.INTERMEDIATE_CA;
        }else if(certData.getSubjectData().getUserType() ==2){
            type = TypeCertificate.END_ENTITY;
        }

        Certificate newCert = new Certificate(serialNumber,certData.getIssuerData().getId(),certData.getSubjectData().getId(), Base64Utility.encode(keyPairSubject.getPublic().getEncoded()) , type, true);
        createCertificate(newCert);


        CertificateGenerator cg = new CertificateGenerator();
        X509Certificate cert = cg.generateCertificate(sd, id);

        KeyStoreWriter kw = new KeyStoreWriter();
        User user=certData.getSubjectData();
        //pass je sifra za pristup keystore fajlu u kom je privatni kljuc kao i sertifikat
        String pass = DecryptPassword.decrypt(user.getPassword(), user.getId()+user.getEmail()+user.getUsername() + user.getCity() + user.getCountry() + user.getOrganization());
        kw.loadKeyStore(null,pass.toCharArray());
        //sifra za privatni kljuc ce da bude username + serijski broj sertifikata

        if(user.getUserType() == 2){
            kw.writeOnlyCertificate(user.getId().toString(), cert);
        }else{
            kw.write(user.getId().toString(), keyPairSubject.getPrivate(), (user.getUsername()+serialNumber).toCharArray(), cert);
        }



        String fileName = serialNumber + ".jks";

        if(issuerPrivateKey == null){
            createNewDirectoryForCertificate(certData.getIssuerData());
            kw.saveKeyStore("files/certificates/"  + fileName,pass.toCharArray());

        }else{
            createNewCert(certData.getSubjectData(),routeFromCertificate);
            kw.saveKeyStore(routeFromCertificate + "\\"  + serialNumber +  ".jks" ,pass.toCharArray());
        }

        return newCert;
    }
    private void createNewDirectoryForCertificate(User issuerData) {
        // Kreiranje File objekta za postojeći direktorijum
        File directory = new File("./files/certificates/");
        File newDirectory = new File(directory, issuerData.getUsername());

        // Provera da li je direktorijum već kreiran
        if (!newDirectory.exists()) {
            // Kreiranje direktorijuma
            boolean created = newDirectory.mkdir();
            if (created) {
                System.out.println("Direktorijum uspešno kreiran.");
            } else {
                System.out.println("Greška prilikom kreiranja direktorijuma.");
            }
        } else {
            System.out.println("Direktorijum već postoji.");
        }
    }
    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateSerialNumber() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    private static X500NameBuilder getX500NameBuilder(User userData) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, userData.getFirstName() + " " + userData.getLastName());
        builder.addRDN(BCStyle.SURNAME, userData.getFirstName());
        builder.addRDN(BCStyle.GIVENNAME, userData.getLastName());
        builder.addRDN(BCStyle.O, userData.getOrganization());
        builder.addRDN(BCStyle.C, userData.getCountry());
        builder.addRDN(BCStyle.E, userData.getEmail());
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, (userData.getId()).toString());
        return builder;
    }


    private void createNewCert(User issuerData, String route) {
        // Kreiranje File objekta za postojeći direktorijum
        File directory = new File(route + "/");
        File newDirectory = new File(directory, issuerData.getUsername());

        // Provera da li je direktorijum već kreiran
        if (!newDirectory.exists()) {
            // Kreiranje direktorijuma
            boolean created = newDirectory.mkdir();
            if (created) {
                System.out.println("Direktorijum uspešno kreiran.");
            } else {
                System.out.println("Greška prilikom kreiranja direktorijuma.");
            }
        } else {
            System.out.println("Direktorijum već postoji.");
        }
    }

    public String findCertificateInFolder(String serialNumber){
        Certificate cert = findBySerialNumber(serialNumber);
        String usernameIssuera = userService.getUserById(cert.getSubjectId()).getUsername();

        String putanja = findFolderPath("files/certificates", usernameIssuera);

        File file = findCertificateInFolder(putanja,serialNumber);

        return putanja;
    }

    public String findFolderPath(String rootPath, String folderName) {
        File rootDir = new File(rootPath);
        File[] files = rootDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.getName().equals(folderName)) {
                        return file.getAbsolutePath(); // Pronađen je odgovarajući folder
                    } else {
                        String foundPath = findFolderPath(file.getAbsolutePath(), folderName);
                        if (foundPath != null) {
                            return foundPath; // Pronađena je putanja u pod-direktorijima
                        }
                    }
                }
            }
        }
        return null; // Folder nije pronađen
    }

    public File findCertificateInFolder(String route, String serialNumber) {
        File directory = new File(route);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(serialNumber + ".jks")) {
                    return file;
                }
            }
        }

        return null; // Vraća null ako nije pronađena odgovarajuća datoteka
    }

    public ArrayList<Certificate> GetAllValidCertificates(){
        ArrayList<Certificate> rootCertificates = new ArrayList<>();

        String adminFolderPath = "files/certificates";
        List<File> jksFiles = findJKSFiles(adminFolderPath);

        for (File file : jksFiles) {
            Certificate certificateFromDb = certificateRepository.findBySerialNumber(file.getName().replace(".jks", ""));

            rootCertificates.add(certificateFromDb);
        }
        return rootCertificates;
    }

    public List<File> findJKSFiles(String folderPath) {
        List<File> jksFiles = new ArrayList<>();
        File folder = new File(folderPath);
        findJKSFilesRecursive(folder, jksFiles);
        return jksFiles;
    }

    private void findJKSFilesRecursive(File folder, List<File> jksFiles) {
        if (folder.isDirectory()) {

            //izbaciti fajlove koji su vec nevalidni po flag-u
            File[] files = folder.listFiles();

            files = Arrays.stream(files)
                    .filter(f -> !f.getName().contains(".jks") ||
                            certificateRepository.findBySerialNumber(f.getName().replace(".jks", "")).getValid()).toArray(File[]::new);

            //sortiranje fajlova tako da prvo budu svi .jks fajlovi da bi se proverila validnost sertifikata IM CA
            files = Arrays.stream(files)
                    .sorted((file1, file2) -> {
                        if(file1.getName().endsWith(".jks") && !file2.getName().endsWith(".jks"))
                            return -1;
                        else if (!file1.getName().endsWith(".jks") && file2.getName().endsWith(".jks"))
                            return 1;
                        else return file1.getName().compareTo(file2.getName());
                    }).toArray(File[]::new);


            ArrayList<File> foldersToSkip = new ArrayList<>();

            if (files != null) {

                for (File file : files) {
                    if (foldersToSkip.contains(file))
                        continue;
                    if (file.isDirectory()) {
                        findJKSFilesRecursive(file, jksFiles);
                    } else if (file.isFile() && file.getName().toLowerCase().endsWith(".jks")) {
                        try {
                            Certificate certificateFromDb = certificateRepository.findBySerialNumber(file.getName().replace(".jks", ""));
                            User issuer = userService.getUserById(certificateFromDb.getIssuerId());
                            User subject = userService.getUserById(certificateFromDb.getSubjectId());
                            Certificate issuerCert = findValidBySubjectId(issuer.getId());

                            String keyStoreFile = getFilePath(file.getAbsolutePath());
                            String keyStorePass = DecryptPassword.decrypt(subject.getPassword(),
                                    subject.getId() + subject.getEmail() + subject.getUsername() + subject.getCity() + subject.getCountry() + subject.getOrganization());
                            String alias = subject.getId().toString();

                            //citanje sertifikta iz keyStore file-a
                            X509Certificate certificate = (X509Certificate) keyStoreReader.readCertificate(keyStoreFile, keyStorePass, alias);

                            boolean isNotExpired = certificate.getNotBefore().before(new Date()) && certificate.getNotAfter().after(new Date());

                            if (isNotExpired && verifyCertificateSignature(certificate, issuerCert.getPublicKey()) && certificateFromDb.getValid())
                                jksFiles.add(file);
                            else {
                                //trenutni sertifikat vise nije validan
                                certificateFromDb.setValid(false);
                                certificateRepository.save(certificateFromDb);

                                //uzimanje serijskog broja sertifikata koji se koristi za potpisivanje
                                String issuerDN = certificate.getIssuerX500Principal().getName();
                                int commaIndex = issuerDN.indexOf(',');
                                System.out.println(issuerDN.substring(4, commaIndex));
                                Certificate signerCertificate = findValidBySubjectId(Long.parseLong(issuerDN.substring(4, commaIndex)));


                                //kreiranje novog sertifikata koji treba da zameni trenutni i dodavnje u listu validnih
                                SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
                                String startDate = iso8601Formater.format(new Date());
                                String endDate = iso8601Formater.format(new Date().getTime() + 365L * 24 * 60 * 60 * 1000);

                                CreateCertificateDto newCertificateDto = new CreateCertificateDto(issuer, subject,
                                        startDate, endDate, signerCertificate.getSerialNumber());

                                Certificate newCertificate = createNewCertificate(newCertificateDto);
                                String absolutePath = getFilePath(file.getAbsolutePath());
                                int lastIndex = absolutePath.lastIndexOf("\\");
                                File newFile = new File(absolutePath.substring(0, lastIndex + 1) + newCertificate.getSerialNumber());
                                jksFiles.add(newFile);


                                //pronalazenje foldera koji ima isto ime kao i nevalidni sertifikat
                                File folderIMCA = Arrays.stream(files).filter(folderIM -> folderIM.getName().equals(subject.getUsername())).toList().get(0);

                                List<File> jksReplacedFiles = new ArrayList<>();
                                if (folderIMCA != null) {
                                    files = Arrays.stream(files)
                                            .filter(f -> !f.equals(folderIMCA))
                                            .toArray(File[]::new);
                                    replaceCertifikates(folderIMCA, jksReplacedFiles);

                                    foldersToSkip.add(folderIMCA);

                                    for (var replacedFile : jksReplacedFiles) {
                                        jksFiles.add(replacedFile);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error while validating certificates: " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private void replaceCertifikates(File folder, List<File> jksFiles){
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        replaceCertifikates(file, jksFiles);
                    } else if (file.isFile() && file.getName().toLowerCase().endsWith(".jks")) {
                        try {
                            Certificate certificateFromDb = certificateRepository.findBySerialNumber(file.getName().replace(".jks", ""));
                            User issuer = userService.getByUsername(folder.getName());
                            User subject = userService.getUserById(certificateFromDb.getSubjectId());

                            String keyStoreFile = getFilePath(file.getAbsolutePath());
                            String keyStorePass = DecryptPassword.decrypt(subject.getPassword(),
                                    subject.getId() + subject.getEmail() + subject.getUsername() + subject.getCity() + subject.getCountry() + subject.getOrganization());
                            String alias = subject.getId().toString();

                            //citanje sertifikta iz keyStore file-a
                            X509Certificate certificate = (X509Certificate) keyStoreReader.readCertificate(keyStoreFile, keyStorePass, alias);

                            //trenutni sertifikat vise nije validan
                            certificateFromDb.setValid(false);
                            certificateRepository.save(certificateFromDb);

                            //uzimanje serijskog broja sertifikata koji se koristi za potpisivanje
                            String issuerDN = certificate.getIssuerX500Principal().getName();
                            int commaIndex = issuerDN.indexOf(',');
                            Certificate signerCertificate = findValidBySubjectId(Long.parseLong(issuerDN.substring(4, commaIndex)));

                            //kreiranje novog sertifikata koji treba da zameni trenutni i dodavnje u listu validnih
                            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
                            String startDate = iso8601Formater.format(new Date());
                            String endDate = iso8601Formater.format(new Date().getTime() + 365L * 24 * 60 * 60 * 1000);

                            CreateCertificateDto newCertificateDto = new CreateCertificateDto(issuer, subject,
                                    startDate, endDate, signerCertificate.getSerialNumber());

                            Certificate newCertificate = createNewCertificate(newCertificateDto);
                            String absolutePath = getFilePath(file.getAbsolutePath());
                            int lastIndex = absolutePath.lastIndexOf("\\");
                            File newFile = new File(absolutePath.substring(0, lastIndex+1) + newCertificate.getSerialNumber());
                            jksFiles.add(newFile);
                        }
                        catch (Exception e){
                            System.out.println("Error while replacing certificates: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public Certificate withdrawCertificate(Certificate certificate){
        certificate.setValid(false);
        certificateRepository.save(certificate);

        return certificate;
    }

    private static String getFilePath(String absolutePath){
        int index = absolutePath.indexOf("files");

        return absolutePath.substring(index);
    }
    public List<Certificate> getAll(){return this.certificateRepository.findAll();}

    public static boolean verifyCertificateSignature(java.security.cert.Certificate certificate, String publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateException {

        try {
            PublicKey pk = decodePublicKey(publicKey);
            certificate.verify(pk);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | CertificateException |
                 NoSuchProviderException | IOException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicKey decodePublicKey(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] publicKeyBytes = Base64Utility.decode(publicKeyBase64);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    public Certificate findBySerialNumber(String serialNumber){
        Certificate certificate =  certificateRepository.findBySerialNumber(serialNumber);
        return certificate;
    }

    public Certificate findValidBySubjectId(Long subjectId){
        return certificateRepository.findAll()
                .stream()
                .filter(c -> c.subjectId.equals(subjectId) && (c.getValid().equals(true) || c.getIssuerId().equals(subjectId))).toList().get(0);
    }
}

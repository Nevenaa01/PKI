package com.example.BSEP2024.repositories;

import com.example.BSEP2024.models.Certificate;
import com.example.BSEP2024.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Certificate findByIssuerId(Long id);
    Certificate findBySerialNumber(String serialNumber);
}

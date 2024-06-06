package com.example.BSEP2024;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.security.Security;

@SpringBootApplication
public class Bsep2024Application {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(Bsep2024Application.class, args);
	}

}

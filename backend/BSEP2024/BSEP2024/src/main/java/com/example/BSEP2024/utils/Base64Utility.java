package com.example.BSEP2024.utils;

import java.io.IOException;
import java.util.Base64;

public class Base64Utility {
	 //Pomocna funkcija za enkodovanje bajtova u string
	 public static String encode(byte[] data){
		 Base64.Encoder encoder = Base64.getEncoder();
		 return encoder.encodeToString(data);
	 }
	 
	 //Pomocna funkcija za dekodovanje stringa u bajt niz
	 public static byte[] decode(String base64Data) throws IOException{
		 Base64.Decoder decoder = Base64.getDecoder();
		 return decoder.decode(base64Data);
	 }
}

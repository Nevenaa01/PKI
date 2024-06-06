import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Certificate, CertificateDto, CreateCertificateDto, User, UserWithDates } from 'src/models/user';
@Injectable({
  providedIn: 'root'
})
export class AppserviceService {

  constructor(private http: HttpClient) { }

  login(username:string,password:string): Observable<User> {
    
      return this.http.get<User>('http://localhost:8090/api/v1/user/login/'+username+'/'+password);
    
  }

  selfSignCert(userData:UserWithDates): Observable<string> {
    
    return this.http.request('post','http://localhost:8090/api/v1/certificate/self-signed-certificate', {
      body: userData,
      responseType: 'text'
    });
  
  }

  getUserByid(id:number):Observable<User> {
    
    return this.http.get<User>('http://localhost:8090/api/v1/user/'+id);
  
  }

  getCertificatesIssuedByMe(userid:number):Observable<Certificate[]>{
    return this.http.get<Certificate[]>('http://localhost:8090/api/v1/certificate/get_certificates_byissuer/'+userid);
  }
  getCertificatesIssuedToMe(userid:number):Observable<Certificate[]>{
    return this.http.get<Certificate[]>('http://localhost:8090/api/v1/certificate/get_certificates_bysubject/'+userid);
  }

  getCertificate(issuerid:number,serial:string):Observable<string>{
    return this.http.request('get','http://localhost:8090/api/v1/certificate/get_certificate/'+serial+'/'+issuerid, {
      responseType: 'text'
    });
  }
  getAllUsers(): Observable<User[]> {
    
    return this.http.get<User[]>('http://localhost:8090/api/v1/user');
  
  }
  getAllCertificates(): Observable<Certificate[]> {
    
    return this.http.get<Certificate[]>('http://localhost:8090/api/v1/certificate/getAll');
  
  }

  getAllValidCertificates(): Observable<Certificate[]>{
    return this.http.get<Certificate[]>('http://localhost:8090/api/v1/certificate/getAllValidCertificates')
  }
  createCertificate(data:CreateCertificateDto):Observable<string>{
    return this.http.request('post','http://localhost:8090/api/v1/certificate/create-certificate', {
      body: data,
      responseType: 'text'
    });
  }

  withdrawCertificate(certificate:CertificateDto){
    return this.http.put('http://localhost:8090/api/v1/certificate/admin/withdraw', certificate)
  }

}

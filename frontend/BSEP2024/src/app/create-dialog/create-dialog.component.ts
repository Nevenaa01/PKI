import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppserviceService } from '../appservice.service';
import { Certificate, CreateCertificateDto, User } from 'src/models/user';
import { ActivatedRoute } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-create-dialog',
  templateUrl: './create-dialog.component.html',
  styleUrls: ['./create-dialog.component.css']
})
export class CreateDialogComponent {
  constructor(public dialogRef: MatDialogRef<CreateDialogComponent>,@Inject(MAT_DIALOG_DATA) public data: any,private service:AppserviceService,private route: ActivatedRoute) { }
  public user!:User;
  public users!:User[];
  selectedUser!:User;

  public subjects: User[] = [];
  public issuers: User[] = [];
  public certificates!:Certificate[];
  public validCertificates:Certificate[] = [];

  types:string[]=['Admin','CA','End entity'];

  CreateForm=new FormGroup({
    usernameSubject: new FormControl(0),
    date: new FormControl('',[Validators.required]),
    certificate: new FormControl(''),
    usernameIssuer: new FormControl(''),
  });

  ngOnInit(): void {
      this.service.getUserByid(this.data.message).subscribe({
        next:(result)=>{
          this.user=result;
          if(result.userType==0){
            this.service.getAllCertificates().subscribe({
              next:(res)=>{
                this.certificates=res.filter(item=>{return item.typeCertificate!="END_ENTITY";});
              }
            });
          }else{
            this.service.getCertificatesIssuedToMe(result.id).subscribe({
              next:(res)=>{
                
                this.certificates=res.filter(item=>{return item.typeCertificate!="END_ENTITY";});
              }
            })
          }
         
        }
      });
    
    this.getValidCertificates()
    
    this.service.getAllUsers().subscribe({
      next:(result)=>{
        this.users=result
    }});
  }
  createCert(){
    if(this.CreateForm.valid){
      //console.log(this.CreateForm.value.usernameSubject);
      this.service.getUserByid(this.CreateForm.value.usernameSubject||0).subscribe({
        next:(userSub)=>{
          let createDto:CreateCertificateDto={
            issuerData: {
              id: 0,
              username: '',
              firstName: '',
              lastName: '',
              email: '',
              password: '',
              country: '',
              organization: '',
              userType: 0,
              city: ''
            },
            subjectData: {
              id: 0,
              username: '',
              firstName: '',
              lastName: '',
              email: '',
              password: '',
              country: '',
              organization: '',
              userType: 0,
              city: ''
            },
            startDate: '',
            endDate: '',
            serialNum: ''
          };
          let issuerId:number=this.certificates.find(certificate => {
            return certificate.serialNumber === this.CreateForm.value.certificate;
          })?.subjectId || 0;
          this.service.getUserByid(issuerId).subscribe({
            next:(userIsu)=>{
              createDto.subjectData=userSub;
              createDto.issuerData=userIsu;
              createDto.serialNum=this.CreateForm.value.certificate||"";
              createDto.startDate=new Date().toISOString();
              createDto.endDate=new Date(this.CreateForm.value.date||"").toISOString();
              this.service.createCertificate(createDto).subscribe({
                next:(res)=>{
                  this.dialogRef.close();
                }
              });
            }
          });
        }
      });
    }
  }

  getValidIssuers(){
    
        this.validCertificates.forEach(c => {
          this.service.getUserByid(c.subjectId).subscribe({
            next: (result:User)=>{
              if(c.typeCertificate != "END_ENTITY")
                this.issuers.push(result);
            }
          })
        })
  }

  getValidCertificates(){
    this.service.getAllValidCertificates().subscribe({
      next: (result:Certificate[]) =>{this.validCertificates = result
        this.getValidIssuers()
        this.getValidSubjects()
      }
      })
  }

  getValidSubjects() {
    const requests = this.validCertificates.map(certificate => {
        return this.service.getUserByid(certificate.subjectId);
    });

    forkJoin(requests).subscribe({
        next: (users: User[]) => {
            let validUsers = users;
            console.log('Valid users:', validUsers);

            this.users.forEach(user => {
                if (!validUsers.find(u => u.id == user.id)) {
                    this.subjects.push(user);
                }
            });
            console.log('Valid subjects:', this.subjects);
        },
        complete: () => {
            console.log('Inner observables completed.');
        },
        error: (error) => {
            console.error('Error occurred while fetching users:', error);
        }
    });

    console.log('Outer observable completed.');
}
  
}
            

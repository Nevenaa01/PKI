import { Component, Inject } from '@angular/core';
import { AppserviceService } from '../appservice.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Certificate, CreateCertificateDto, User, UserType, UserWithDates,CertificateDto } from 'src/models/user';
import { HttpErrorResponse } from '@angular/common/http';
import {MatDialog, MAT_DIALOG_DATA, MatDialogModule} from '@angular/material/dialog';
import { DialogComponent } from '../dialog/dialog.component';
import { CreateDialogComponent } from '../create-dialog/create-dialog.component';

@Component({
  selector: 'app-user-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.css']
})
export class UserViewComponent {

  constructor(private service:AppserviceService,private router: Router,private route: ActivatedRoute,public dialog: MatDialog){}

  public displayedColumns: string[] = ['Serial Number','Issuer' ,'Subject', 'Type'];
  public dataSourceIssuer!:Certificate[];
  public dataSourceSubject!:Certificate[];
  public dataSourceAll!:Certificate[];
  public user!:User;

  public issuerCertificateDtos: CertificateDto[] = [];
  public subjectCertificateDtos: CertificateDto[] = [];
  public allCertificateDtos: CertificateDto[] = [];

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      let id = +params['id'];
      this.service.getUserByid(id).subscribe({
        next:(result:User)=>{
          this.user=result;
          console.log(result);
          this.service.getCertificatesIssuedByMe(id).subscribe({
            next:(result:Certificate[])=>{
              this.dataSourceIssuer=result;

              let certDto: CertificateDto[] = [];

              result.forEach(c => {
                this.getUsernamesForCertificateDto(c.subjectId, certDto, c)
              })
              this.issuerCertificateDtos = certDto;
              console.log(result);
            }
          });
          this.service.getCertificatesIssuedToMe(id).subscribe({
            next:(result:Certificate[])=>{
              this.dataSourceSubject=result;

              let certDto: CertificateDto[] = [];

              result.forEach(c => {
                this.getUsernamesForCertificateDto(c.subjectId, certDto, c)
              })
              this.subjectCertificateDtos = certDto;

              console.log(result);
            }
          });
          this.service.getAllCertificates().subscribe({
            next:(result:Certificate[])=>{
              this.dataSourceAll=result;

              let certDto: CertificateDto[] = [];

              result.forEach(c => {
                this.getUsernamesForCertificateDto(c.subjectId, certDto, c)
              })
              this.allCertificateDtos = certDto;
            }
          });

        }
        
      });
    });
  }
  onSelfSigned(){
    let currentDate = new Date();

    currentDate.setFullYear(currentDate.getFullYear() + 5);
    
    let createDto:CreateCertificateDto= {
      issuerData: this.user,
      subjectData: this.user,
      startDate: new Date().toISOString(),
      endDate: currentDate.toISOString(),
      serialNum: ''
    };
    console.log(createDto);

    this.service.createCertificate(createDto).subscribe({
      next: (result: any) => {
        console.log(result);
        this.service.getCertificatesIssuedByMe(this.user.id).subscribe({
          next:(result:Certificate[])=>{
            this.dataSourceIssuer=result;
            console.log(result);
          }
        });
        this.service.getCertificatesIssuedToMe(this.user.id).subscribe({
          next:(result:Certificate[])=>{
            this.dataSourceSubject=result;
            console.log(result);
          }
        });
        
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      }
    }); 

    
  }

  onRowClicked(issuerid:number,serial:string){
    console.log(this.user);
    this.service.getCertificate(issuerid,serial).subscribe({
      next:(result:string)=>{
        this.openMessageBox(result);
        
      }
    });
  }
  openMessageBox(message: string): void {
    const dataToSend = { message: message };
    const dialogRef = this.dialog.open(DialogComponent, {
      width: '1000px',
      height:'80vh',
      data: dataToSend
    });
  }

  onCreateCert(){
    const dataToSend = { message: this.user.id };
    const dialogRef = this.dialog.open(CreateDialogComponent, {
      width: '1000px',
      height:'80vh',
      data: dataToSend
    });
  }

  getUsernamesForCertificateDto(subjectId:number, certDto: CertificateDto[], c:Certificate){
    this.service.getUserByid(subjectId).subscribe({
      next:(user:User) =>{
        certDto.push({                  
          issuerId: c.issuerId,
          serialNumber: c.serialNumber,
          subjectName: user.username,
          typeCertificate: c.typeCertificate,
          subjectId:c.subjectId,
          valid: c.valid
        })
      }
    })
  }

  onWithdrawCertificate(c:CertificateDto){
  }

  onLogout(){
    this.router.navigate(['/']);
  }
}

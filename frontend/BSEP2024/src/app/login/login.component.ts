import { Component } from '@angular/core';
import { AppserviceService } from '../appservice.service';
import { User } from 'src/models/user';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, NavigationExtras } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
    constructor(private service:AppserviceService,private router: Router){}

    LoginForm=new FormGroup({
      username: new FormControl('',[Validators.required]),
      password: new FormControl('',[Validators.required])
    });
    OnLogin(){
      if(this.LoginForm.valid){
        console.log('hi')
        this.service.login(this.LoginForm.value.username || "",this.LoginForm.value.password || "").subscribe({
          next: (result: User) => {
            console.log(result);
            if(result!=null){
              
              this.router.navigate(['/user/'+result.id]);
              
            }
          }
        });  
      }
    }
}

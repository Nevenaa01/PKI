import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { UserViewComponent } from './user-view/user-view.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'user/:id', component: UserViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

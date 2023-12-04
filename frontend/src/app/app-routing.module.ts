import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TutorialsListComponent} from './components/tutorials-list/tutorials-list.component';
import {TutorialDetailsComponent} from './components/tutorial-details/tutorial-details.component';
import {AddTutorialComponent} from './components/add-tutorial/add-tutorial.component';
import {ModalComponent} from "./modal/modal.component";
import {TutorialsInformationComponent} from "./tutorials.information/tutorials.information.component";
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./registration/registration.component";
import {AuthGuard} from "./services/auth-guard.service";
import {UserRepresentationComponent} from "./user-representation/user-representation.component";
import {OrderRepresentationComponent} from "./order-representation/order-representation.component";
import {AdminPanelComponent} from "./admin-panel/admin-panel.component";
import {MakeRequestComponent} from "./make-request/make-request.component";
import {RequestRepresentationComponent} from "./request-representation/request-representation.component";

const routes: Routes = [
  {path: '', redirectTo: 'tutorials', pathMatch: 'full'},
  {path: 'tutorials', component: TutorialsListComponent, canActivate: [AuthGuard]},
  {path: 'tutorials/:id', component: TutorialDetailsComponent, canActivate: [AuthGuard]},
  {path: 'tutorials/info/:id', component: TutorialsInformationComponent, canActivate: [AuthGuard]},
  {path: 'add', component: AddTutorialComponent, canActivate: [AuthGuard]},
  {path: 'error/fatal', component: ModalComponent},
  {path: 'login', component: LoginComponent},
  {path: 'userInfo', component: UserRepresentationComponent, canActivate: [AuthGuard]},
  {path: 'register', component: RegisterComponent},
  {path: 'orders', component: OrderRepresentationComponent, canActivate: [AuthGuard]},
  {path: 'admin/panel', component: AdminPanelComponent, canActivate: [AuthGuard]},
  {path: 'admin/requests', component: RequestRepresentationComponent, canActivate: [AuthGuard]},
  {path: 'make-request', component: MakeRequestComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

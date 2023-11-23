import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TutorialsListComponent} from './components/tutorials-list/tutorials-list.component';
import {TutorialDetailsComponent} from './components/tutorial-details/tutorial-details.component';
import {AddTutorialComponent} from './components/add-tutorial/add-tutorial.component';
import {ModalComponent} from "./modal/modal.component";
import {TutorialsInformationComponent} from "./tutorials.information/tutorials.information.component";

const routes: Routes = [
  {path: '', redirectTo: 'tutorials', pathMatch: 'full'},
  {path: 'tutorials', component: TutorialsListComponent},
  {path: 'tutorials/:id', component: TutorialDetailsComponent},
  {path: 'tutorials/info/:id', component: TutorialsInformationComponent},
  {path: 'add', component: AddTutorialComponent},
  {path: 'error/fatal', component: ModalComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

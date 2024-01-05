import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AddTutorialComponent} from './components/add-tutorial/add-tutorial.component';
import {TutorialDetailsComponent} from './components/tutorial-details/tutorial-details.component';
import {TutorialsListComponent} from './components/tutorials-list/tutorials-list.component';
import {ModalComponent} from './modal/modal.component';
import {TutorialsInformationComponent} from './tutorials.information/tutorials.information.component';
import {NgOptimizedImage} from "@angular/common";
import {ImageUploadComponent} from "./image-upload/image-upload.component";
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './registration/registration.component';
import {UserRepresentationComponent} from './user-representation/user-representation.component';
import {OrderRepresentationComponent} from './order-representation/order-representation.component';
import {NgxStripeModule, StripeService} from "ngx-stripe";
import {AdminPanelComponent} from './admin-panel/admin-panel.component';
import {NgxPaginationModule} from "ngx-pagination";
import {MakeRequestComponent} from './make-request/make-request.component';
import {RequestRepresentationComponent} from './request-representation/request-representation.component';
import {TextingComponent} from './texting/texting.component';
import {PickerModule} from '@ctrl/ngx-emoji-mart';
import {CalendarComponent} from './calendar/calendar.component';
import {AzureCalendarComponent} from './azure-calendar/azure-calendar.component';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent,
    AddTutorialComponent,
    TutorialDetailsComponent,
    TutorialsListComponent,
    ModalComponent,
    TutorialsInformationComponent,
    ImageUploadComponent,
    LoginComponent,
    RegisterComponent,
    UserRepresentationComponent,
    OrderRepresentationComponent,
    AdminPanelComponent,
    MakeRequestComponent,
    RequestRepresentationComponent,
    TextingComponent,
    CalendarComponent,
    AzureCalendarComponent
  ],
  imports: [
    PickerModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgxPaginationModule,
    NgxStripeModule.forRoot('pk_test_51OHikCAiCn0BUr4JPSqR5FDOhT5nGSiIvCkqFv5urEAAg12ymu317v7gAkfbFAPfK0D10L8AhnJzzdScYZOFP5WX00aITNen2Z'),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    NgOptimizedImage,
    ReactiveFormsModule
  ],
  providers: [StripeService],
  bootstrap: [AppComponent]
})
export class AppModule {
}

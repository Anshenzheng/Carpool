import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { HeaderComponent } from './components/header/header.component';
import { CarpoolListComponent } from './components/carpool-list/carpool-list.component';
import { CarpoolDetailComponent } from './components/carpool-detail/carpool-detail.component';
import { CreatePostComponent } from './components/create-post/create-post.component';
import { MyPostsComponent } from './components/my-posts/my-posts.component';
import { MyApplicationsComponent } from './components/my-applications/my-applications.component';
import { ReceivedApplicationsComponent } from './components/received-applications/received-applications.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AdminAuditComponent } from './components/admin-audit/admin-audit.component';
import { AdminPostsComponent } from './components/admin-posts/admin-posts.component';
import { AdminStatisticsComponent } from './components/admin-statistics/admin-statistics.component';

import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    HeaderComponent,
    CarpoolListComponent,
    CarpoolDetailComponent,
    CreatePostComponent,
    MyPostsComponent,
    MyApplicationsComponent,
    ReceivedApplicationsComponent,
    AdminDashboardComponent,
    AdminAuditComponent,
    AdminPostsComponent,
    AdminStatisticsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    AuthGuard,
    AdminGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

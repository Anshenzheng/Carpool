import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
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
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';
import { UserOnlyGuard } from './guards/user-only.guard';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent },
  { path: 'carpool', component: CarpoolListComponent },
  { path: 'carpool/:id', component: CarpoolDetailComponent },
  { path: 'create-post', component: CreatePostComponent, canActivate: [UserOnlyGuard] },
  { path: 'my-posts', component: MyPostsComponent, canActivate: [UserOnlyGuard] },
  { path: 'my-applications', component: MyApplicationsComponent, canActivate: [UserOnlyGuard] },
  { path: 'received-applications', component: ReceivedApplicationsComponent, canActivate: [UserOnlyGuard] },
  { 
    path: 'admin', 
    component: AdminDashboardComponent, 
    canActivate: [AuthGuard, AdminGuard],
    children: [
      { path: '', redirectTo: 'audit', pathMatch: 'full' },
      { path: 'audit', component: AdminAuditComponent },
      { path: 'posts', component: AdminPostsComponent },
      { path: 'statistics', component: AdminStatisticsComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

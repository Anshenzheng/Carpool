import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CarpoolService, CarpoolPost, ApplyRequest } from '../../services/carpool.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-carpool-detail',
  templateUrl: './carpool-detail.component.html',
  styleUrls: ['./carpool-detail.component.css']
})
export class CarpoolDetailComponent implements OnInit {
  post: CarpoolPost | null = null;
  loading = true;
  errorMessage = '';
  successMessage = '';
  applyForm: FormGroup;
  isLoggedIn = false;
  isOwner = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private carpoolService: CarpoolService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {
    this.applyForm = this.formBuilder.group({
      applicantName: ['', [Validators.required]],
      applicantPhone: ['', [Validators.required, Validators.pattern(/^1[3-9]\d{9}$/)]],
      passengers: [1, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.loadPost();
    
    if (this.isLoggedIn) {
      const user = this.authService.getCurrentUser();
      if (user) {
        this.applyForm.patchValue({
          applicantName: user.realName,
          applicantPhone: user.phone
        });
      }
    }
  }

  private loadPost(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = '无效的拼车信息';
      this.loading = false;
      return;
    }

    this.carpoolService.getPostById(parseInt(id)).subscribe({
      next: (post) => {
        this.post = post;
        const currentUser = this.authService.getCurrentUser();
        this.isOwner = currentUser?.id === post.user?.id;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || '加载失败';
        this.loading = false;
      }
    });
  }

  onApply(): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }

    if (this.applyForm.invalid) {
      return;
    }

    if (!this.post) return;

    const passengers = this.applyForm.value.passengers;
    if (passengers > this.post.availableSeats) {
      this.errorMessage = '座位不足';
      return;
    }

    const request: ApplyRequest = {
      postId: this.post.id,
      ...this.applyForm.value
    };

    this.carpoolService.applyForCarpool(request).subscribe({
      next: () => {
        this.successMessage = '申请成功！请等待车主确认。';
        this.errorMessage = '';
        setTimeout(() => {
          this.router.navigate(['/my-applications']);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || '申请失败';
        this.successMessage = '';
      }
    });
  }

  formatDateTime(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  goBack(): void {
    this.router.navigate(['/carpool']);
  }
}

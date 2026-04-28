import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CarpoolService, CreatePostRequest } from '../../services/carpool.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.css']
})
export class CreatePostComponent implements OnInit {
  postForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  minDate: string;

  constructor(
    private formBuilder: FormBuilder,
    private carpoolService: CarpoolService,
    private authService: AuthService,
    private router: Router
  ) {
    const now = new Date();
    this.minDate = now.toISOString().slice(0, 16);

    this.postForm = this.formBuilder.group({
      departure: ['', [Validators.required]],
      destination: ['', [Validators.required]],
      departureTime: ['', [Validators.required]],
      seats: [1, [Validators.required, Validators.min(1)]],
      contactName: ['', [Validators.required]],
      contactPhone: ['', [Validators.required, Validators.pattern(/^1[3-9]\d{9}$/)]],
      description: ['']
    });
  }

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.postForm.patchValue({
        contactName: user.realName,
        contactPhone: user.phone
      });
    }
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.postForm.value;
    const request: CreatePostRequest = {
      departure: formValue.departure,
      destination: formValue.destination,
      departureTime: new Date(formValue.departureTime).toISOString(),
      seats: formValue.seats,
      contactName: formValue.contactName,
      contactPhone: formValue.contactPhone,
      description: formValue.description || undefined
    };

    this.carpoolService.createPost(request).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '发布成功！等待管理员审核后即可发布。';
        setTimeout(() => {
          this.router.navigate(['/my-posts']);
        }, 2000);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || '发布失败，请稍后重试';
      }
    });
  }
}

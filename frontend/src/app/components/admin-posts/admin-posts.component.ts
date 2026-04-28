import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-posts',
  templateUrl: './admin-posts.component.html',
  styleUrls: ['./admin-posts.component.css']
})
export class AdminPostsComponent implements OnInit {
  posts: any[] = [];
  loading = true;
  errorMessage = '';
  showTakeDownModal = false;
  selectedPost: any = null;
  takeDownReason = '';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadAllPosts();
  }

  private loadAllPosts(): void {
    this.adminService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || '加载失败';
        this.loading = false;
      }
    });
  }

  openTakeDownModal(post: any): void {
    this.selectedPost = post;
    this.takeDownReason = '';
    this.showTakeDownModal = true;
  }

  closeTakeDownModal(): void {
    this.showTakeDownModal = false;
    this.selectedPost = null;
    this.takeDownReason = '';
  }

  submitTakeDown(): void {
    if (!this.selectedPost) return;

    this.adminService.takeDownPost(this.selectedPost.id, this.takeDownReason || '管理员下架').subscribe({
      next: () => {
        this.selectedPost.status = 2;
        this.selectedPost.auditRemark = this.takeDownReason || '管理员下架';
        this.closeTakeDownModal();
      },
      error: (error) => {
        alert(error.error?.message || '下架失败');
      }
    });
  }

  formatDateTime(dateStr: string): string {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getStatusText(status: number): string {
    const statusMap: { [key: number]: string } = {
      0: '待审核',
      1: '已发布',
      2: '已下架',
      3: '已取消'
    };
    return statusMap[status] || '未知';
  }

  getStatusBadgeClass(status: number): string {
    const classMap: { [key: number]: string } = {
      0: 'badge-pending',
      1: 'badge-success',
      2: 'badge-danger',
      3: 'badge-info'
    };
    return classMap[status] || 'badge-pending';
  }

  canTakeDown(post: any): boolean {
    return post.status === 1;
  }
}

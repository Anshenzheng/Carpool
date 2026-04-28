import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-audit',
  templateUrl: './admin-audit.component.html',
  styleUrls: ['./admin-audit.component.css']
})
export class AdminAuditComponent implements OnInit {
  posts: any[] = [];
  loading = true;
  errorMessage = '';
  auditRemark = '';
  showAuditModal = false;
  selectedPost: any = null;
  auditAction: 'approve' | 'reject' | null = null;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadPendingPosts();
  }

  private loadPendingPosts(): void {
    this.adminService.getPendingAuditPosts().subscribe({
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

  openAuditModal(post: any, action: 'approve' | 'reject'): void {
    this.selectedPost = post;
    this.auditAction = action;
    this.auditRemark = '';
    this.showAuditModal = true;
  }

  closeAuditModal(): void {
    this.showAuditModal = false;
    this.selectedPost = null;
    this.auditAction = null;
    this.auditRemark = '';
  }

  submitAudit(): void {
    if (!this.selectedPost || !this.auditAction) return;

    const auditStatus = this.auditAction === 'approve' ? 1 : 2;

    this.adminService.auditPost({
      postId: this.selectedPost.id,
      auditStatus: auditStatus,
      auditRemark: this.auditRemark || undefined
    }).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.id !== this.selectedPost.id);
        this.closeAuditModal();
      },
      error: (error) => {
        alert(error.error?.message || '审核失败');
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
}

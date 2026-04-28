import { Component, OnInit } from '@angular/core';
import { CarpoolService, CarpoolApplication } from '../../services/carpool.service';

@Component({
  selector: 'app-received-applications',
  templateUrl: './received-applications.component.html',
  styleUrls: ['./received-applications.component.css']
})
export class ReceivedApplicationsComponent implements OnInit {
  applications: CarpoolApplication[] = [];
  loading = true;
  errorMessage = '';
  rejectReason = '';
  showRejectModal = false;
  selectedAppId: number | null = null;

  constructor(private carpoolService: CarpoolService) { }

  ngOnInit(): void {
    this.loadApplications();
  }

  private loadApplications(): void {
    this.carpoolService.getReceivedApplications().subscribe({
      next: (applications) => {
        this.applications = applications;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || '加载失败';
        this.loading = false;
      }
    });
  }

  confirmApplication(app: CarpoolApplication): void {
    if (!confirm('确定要确认该申请吗？')) {
      return;
    }

    this.carpoolService.confirmApplication(app.id).subscribe({
      next: () => {
        app.status = 1;
        app.confirmedAt = new Date().toISOString();
      },
      error: (error) => {
        alert(error.error?.message || '确认失败');
      }
    });
  }

  openRejectModal(appId: number): void {
    this.selectedAppId = appId;
    this.rejectReason = '';
    this.showRejectModal = true;
  }

  closeRejectModal(): void {
    this.showRejectModal = false;
    this.selectedAppId = null;
    this.rejectReason = '';
  }

  submitReject(): void {
    if (this.selectedAppId === null) return;

    this.carpoolService.rejectApplication(this.selectedAppId, this.rejectReason).subscribe({
      next: () => {
        const app = this.applications.find(a => a.id === this.selectedAppId);
        if (app) {
          app.status = 2;
          app.rejectReason = this.rejectReason;
        }
        this.closeRejectModal();
      },
      error: (error) => {
        alert(error.error?.message || '拒绝失败');
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
      0: '待确认',
      1: '已确认',
      2: '已拒绝',
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
}

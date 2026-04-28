import { Component, OnInit } from '@angular/core';
import { CarpoolService, CarpoolApplication } from '../../services/carpool.service';

@Component({
  selector: 'app-my-applications',
  templateUrl: './my-applications.component.html',
  styleUrls: ['./my-applications.component.css']
})
export class MyApplicationsComponent implements OnInit {
  applications: CarpoolApplication[] = [];
  loading = true;
  errorMessage = '';

  constructor(private carpoolService: CarpoolService) { }

  ngOnInit(): void {
    this.loadMyApplications();
  }

  private loadMyApplications(): void {
    this.carpoolService.getMyApplications().subscribe({
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

  cancelApplication(app: CarpoolApplication): void {
    if (!confirm('确定要取消该申请吗？出发前30分钟内无法取消。')) {
      return;
    }

    this.carpoolService.cancelMyApplication(app.id).subscribe({
      next: () => {
        app.status = 3;
        app.cancelledAt = new Date().toISOString();
      },
      error: (error) => {
        alert(error.error?.message || '取消失败');
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

  canCancel(app: CarpoolApplication): boolean {
    if (app.status === 2 || app.status === 3) {
      return false;
    }
    const departureTime = new Date(app.post.departureTime);
    const now = new Date();
    const diffMinutes = (departureTime.getTime() - now.getTime()) / (1000 * 60);
    return diffMinutes > 30;
  }
}

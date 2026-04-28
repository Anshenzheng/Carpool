import { Component, OnInit } from '@angular/core';
import { AdminService, OverviewStats } from '../../services/admin.service';

@Component({
  selector: 'app-admin-statistics',
  templateUrl: './admin-statistics.component.html',
  styleUrls: ['./admin-statistics.component.css']
})
export class AdminStatisticsComponent implements OnInit {
  stats: OverviewStats | null = null;
  routeStats: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadStatistics();
  }

  private loadStatistics(): void {
    this.adminService.getOverviewStatistics().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || '加载统计数据失败';
        this.loading = false;
      }
    });

    this.adminService.getRouteStatistics().subscribe({
      next: (stats) => {
        this.routeStats = stats;
      },
      error: () => {
        // 忽略路线统计错误
      }
    });
  }

  exportPosts(): void {
    this.adminService.exportPosts().subscribe({
      next: (blob) => {
        this.downloadFile(blob, 'carpool_posts.xlsx');
      },
      error: () => {
        alert('导出失败');
      }
    });
  }

  exportApplications(): void {
    this.adminService.exportApplications().subscribe({
      next: (blob) => {
        this.downloadFile(blob, 'carpool_applications.xlsx');
      },
      error: () => {
        alert('导出失败');
      }
    });
  }

  private downloadFile(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }

  getDepartureStats(): { [key: string]: number } {
    const departure = this.routeStats.find(s => s.type === 'departure');
    return departure?.data || {};
  }

  getDestinationStats(): { [key: string]: number } {
    const destination = this.routeStats.find(s => s.type === 'destination');
    return destination?.data || {};
  }
}

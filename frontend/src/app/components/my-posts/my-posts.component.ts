import { Component, OnInit } from '@angular/core';
import { CarpoolService, CarpoolPost } from '../../services/carpool.service';

@Component({
  selector: 'app-my-posts',
  templateUrl: './my-posts.component.html',
  styleUrls: ['./my-posts.component.css']
})
export class MyPostsComponent implements OnInit {
  posts: CarpoolPost[] = [];
  loading = true;
  errorMessage = '';

  constructor(private carpoolService: CarpoolService) { }

  ngOnInit(): void {
    this.loadMyPosts();
  }

  private loadMyPosts(): void {
    this.carpoolService.getMyPosts().subscribe({
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

  cancelPost(post: CarpoolPost): void {
    if (!confirm('确定要取消该拼车信息吗？出发前30分钟内无法取消。')) {
      return;
    }

    this.carpoolService.cancelMyPost(post.id).subscribe({
      next: () => {
        post.status = 3;
      },
      error: (error) => {
        alert(error.error?.message || '取消失败');
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

  canCancel(post: CarpoolPost): boolean {
    if (post.status !== 1) {
      return false;
    }
    const departureTime = new Date(post.departureTime);
    const now = new Date();
    const diffMinutes = (departureTime.getTime() - now.getTime()) / (1000 * 60);
    return diffMinutes > 30;
  }
}

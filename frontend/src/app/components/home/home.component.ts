import { Component, OnInit } from '@angular/core';
import { CarpoolService, CarpoolPost } from '../../services/carpool.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  latestPosts: CarpoolPost[] = [];
  loading = true;

  constructor(private carpoolService: CarpoolService) { }

  ngOnInit(): void {
    this.loadLatestPosts();
  }

  private loadLatestPosts(): void {
    this.carpoolService.getActivePosts().subscribe({
      next: (posts) => {
        this.latestPosts = posts.slice(0, 6);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
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
}

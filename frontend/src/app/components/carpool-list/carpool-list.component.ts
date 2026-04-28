import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { CarpoolService, CarpoolPost } from '../../services/carpool.service';

@Component({
  selector: 'app-carpool-list',
  templateUrl: './carpool-list.component.html',
  styleUrls: ['./carpool-list.component.css']
})
export class CarpoolListComponent implements OnInit {
  posts: CarpoolPost[] = [];
  filteredPosts: CarpoolPost[] = [];
  loading = true;
  searchForm: FormGroup;

  constructor(
    private carpoolService: CarpoolService,
    private formBuilder: FormBuilder
  ) {
    this.searchForm = this.formBuilder.group({
      departure: [''],
      destination: ['']
    });
  }

  ngOnInit(): void {
    this.loadPosts();
  }

  private loadPosts(): void {
    this.carpoolService.getActivePosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.filteredPosts = posts;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    const { departure, destination } = this.searchForm.value;
    
    if (!departure && !destination) {
      this.filteredPosts = this.posts;
      return;
    }

    this.carpoolService.searchPosts(departure || undefined, destination || undefined).subscribe({
      next: (posts) => {
        this.filteredPosts = posts;
      },
      error: () => {
        this.filteredPosts = [];
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
}

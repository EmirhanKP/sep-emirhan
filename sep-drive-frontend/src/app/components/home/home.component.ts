import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink}  from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  isCustomer: boolean = false;
  isDriver: boolean = false;
  username: string = 'Benutzer';

  constructor(private router: Router) {}

  ngOnInit() {
    this.isCustomer = true;
    this.isDriver = false;
    this.username = 'TestBenutzer';
  }

  createRideRequest() {
    this.router.navigate(['/ride-request']);
  }

  viewProfile() {
    this.router.navigate(['/profile']);
  }
  logout() {
    this.router.navigate(['/login']);
  }
  searchUsers() {
    this.router.navigate(['/search'])
  }
}

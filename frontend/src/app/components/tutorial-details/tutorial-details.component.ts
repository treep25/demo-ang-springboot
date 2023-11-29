import {Component, Input} from '@angular/core';
import {TutorialService} from 'src/app/services/tutorial.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Status, Tutorial} from 'src/app/models/tutorial.model';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-tutorial-details',
  templateUrl: './tutorial-details.component.html',
  styleUrls: ['./tutorial-details.component.css'],
})
export class TutorialDetailsComponent {
  @Input() viewMode = false;

  @Input() currentTutorial: Tutorial = {
    title: '',
    description: '',
    status: Status.PENDING
  };

  message = '';
  role? = '';

  constructor(
    private authService: AuthService,
    private tutorialService: TutorialService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.viewMode) {
      this.message = '';
      this.getTutorial(this.route.snapshot.params['id']);
    }
    this.authService.meInfo().subscribe(
      value => {
        this.role = value.role;
      }
    );
  }

  getTutorial(id: string): void {
    this.tutorialService.get(id).subscribe({
      next: (data) => {
        this.currentTutorial = data;
        console.log(data);
      },
      error: (e) => console.error(e)
    });
  }

  updatePublished(status: any): void {
    this.message = '';

    this.tutorialService.updateStatus(this.currentTutorial.id, status).subscribe({
      next: (res) => {
        console.log(res);
        this.currentTutorial.status = status.toString();
        this.message = res.message
          ? res.message
          : 'The status was updated successfully!';
      },
      error: (e) => console.error(e)
    });
  }

  updateTutorial(): void {
    this.message = '';

    this.tutorialService
      .update(this.currentTutorial.id, this.currentTutorial)
      .subscribe({
        next: (res) => {
          console.log(res);
          this.message = res.message
            ? res.message
            : 'This tutorial was updated successfully!';
        },
        error: (e) => console.error(e)
      });
  }

  deleteTutorial(): void {
    this.tutorialService.delete(this.currentTutorial.id).subscribe({
      next: (res) => {
        console.log(res);
        this.router.navigate(['/tutorials']);
      },
      error: (e) => console.error(e)
    });
  }

  bookTutorial(): void {
    this.authService.createOrder(this.currentTutorial).subscribe(value => {
      console.log(value.tutorialsOrder)
    }, error => {
      console.log(error)
    })
  }
}

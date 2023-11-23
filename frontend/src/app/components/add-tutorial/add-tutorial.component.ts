import {Component} from '@angular/core';
import {Status, Tutorial} from 'src/app/models/tutorial.model';
import {TutorialService} from 'src/app/services/tutorial.service';

@Component({
  selector: 'app-add-tutorial',
  templateUrl: './add-tutorial.component.html',
  styleUrls: ['./add-tutorial.component.css'],
})
export class AddTutorialComponent {
  isModalOpen = false;
  tutorial: Tutorial = {
    title: '',
    description: '',
    status: Status.PENDING
  };
  submitted = false;
  errorMessage = '';

  constructor(private tutorialService: TutorialService) {}

  saveTutorial(): void {
    const data = {
      title: this.tutorial.title,
      description: this.tutorial.description
    };

    this.tutorialService.create(data).subscribe({
      next: (res) => {
        console.log(res);
        this.submitted = true;
      },
      error: (e) => this.openModal(e)
    });
  }

  openModal(errorMessage: string): void {
    this.isModalOpen = true;
    this.errorMessage = errorMessage
  }

  handleModalClose(): void {
    this.isModalOpen = false;
  }

  newTutorial(): void {
    this.submitted = false;
    this.tutorial = {
      title: '',
      description: '',
      status: Status.PENDING
    };
  }
}

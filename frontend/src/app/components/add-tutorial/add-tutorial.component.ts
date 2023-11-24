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
    const tutorialDto = new FormData();
    tutorialDto.append('title', this.tutorial.title || '');
    tutorialDto.append('description', this.tutorial.description || '');
    tutorialDto.append('overview', this.tutorial.overview || '');
    tutorialDto.append('content', this.tutorial.content || '');
    if (this.tutorial.image) {
      tutorialDto.append('image', this.tutorial.image, this.tutorial.image.name);
    }

    this.tutorialService.create(tutorialDto).subscribe({
      next: (res) => {
        document.cookie = `jwtToken=hello; path=/; SameSite=None`;
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

  onFileChange(event: any): void {
    const file = event.target.files[0];

    if (file) {
      this.tutorial.image = file;

      const reader = new FileReader();
      reader.onload = (e) => {
        const imageDataUrl = e.target?.result as string;
        console.log('Image data URL:', imageDataUrl);
      };
      reader.readAsDataURL(file);
    }
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

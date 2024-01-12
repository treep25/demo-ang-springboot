import {Component, Input} from '@angular/core';
import {Status, Tutorial} from "../models/tutorial.model";
import {TutorialService} from "../services/tutorial.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-tutorials.information',
  templateUrl: './tutorials.information.component.html',
  styleUrls: ['./tutorials.information.component.css']
})
export class TutorialsInformationComponent {
  @Input() viewMode = false;

  @Input() currentTutorial: Tutorial = {
    title: '',
    description: '',
    imagePath: '',
    status: Status.PENDING
  };
  message = '';
  imageData: ArrayBuffer | undefined;

  constructor(
    private tutorialService: TutorialService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    if (!this.viewMode) {
      this.message = '';
      this.getTutorial(this.route.snapshot.params['id']);
    }
  }

  getTutorial(id: string): void {
    this.tutorialService.get(id).subscribe({
      next: (data) => {
        this.currentTutorial = data;
        this.tutorialService.getImageData(this.currentTutorial.id).subscribe(
          {
            next: (data) => {
              this.imageData = data
            },
            error: (e) => console.error()
          }
        )
        console.log(data);
      },
      error: (e) => console.error()
    });
  }

  displayImage(): string {
    if (this.imageData) {
      const base64Image = btoa(String.fromCharCode(...new Uint8Array(this.imageData)));
      return `data:image/jpeg;base64,${base64Image}`;
    } else {
      return '';
    }
  }

}

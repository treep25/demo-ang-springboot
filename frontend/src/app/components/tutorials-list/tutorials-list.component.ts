import {Component, OnInit} from '@angular/core';
import {Tutorial} from 'src/app/models/tutorial.model';
import {TutorialService} from 'src/app/services/tutorial.service';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-tutorials-list',
  templateUrl: './tutorials-list.component.html',
  styleUrls: ['./tutorials-list.component.css'],
})
export class TutorialsListComponent implements OnInit {
  tutorials?: Tutorial[];
  currentTutorial: Tutorial = {};
  currentIndex = -1;
  title = '';
  description = '';
  sortedTitleType = '';

  role? = '';

  constructor(private tutorialService: TutorialService, private authService: AuthService) {
    this.authService.meInfo().subscribe(
      value => {
        this.role = value.role;
      }
    );
  }

  ngOnInit(): void {
    this.retrieveTutorials();
  }

  retrieveTutorials(): void {
    this.tutorialService.getAll().subscribe({
      next: (data) => {
        this.tutorials = data;
        console.log(data);
      },
      error: () => console.error()
    });
  }

  refreshList(): void {
    this.retrieveTutorials();
    this.currentTutorial = {};
    this.currentIndex = -1;
  }

  setActiveTutorial(tutorial: Tutorial, index: number): void {
    this.currentTutorial = tutorial;
    this.currentIndex = index;
  }

  removeAllTutorials(): void {
    this.tutorialService.deleteAll().subscribe({
      next: (res) => {
        console.log(res);
        this.refreshList();
      },
      error: () => console.error()
    });
  }

  searchTitle(): void {
    this.currentTutorial = {};
    this.currentIndex = -1;

    this.tutorialService.findByTitle(this.title).subscribe({
      next: (data) => {
        this.tutorials = data;
        console.log(data);
      },
      error: () => console.error()
    });
  }

  searchDescription(): void {
    this.currentTutorial = {};
    this.currentIndex = -1;

    this.tutorialService.findByDescription(this.description).subscribe({
      next: (data) => {
        this.tutorials = data;
        console.log(data);
      },
      error: () => console.error()
    });
  }

  toggleSortOrder(): void {
    this.sortedTitleType = this.sortedTitleType === 'DESC' ? 'ASC' : 'DESC';
  }

  sortTutorialsByTitle(): void {
    this.toggleSortOrder();
    this.tutorialService.getAllSortedByTitle(this.sortedTitleType).subscribe({
      next: (data) => {
        this.tutorials = data;
        console.log(data);
      },
      error: () => console.error()
    });
  }

  setStatus(status: any): void {
    this.tutorialService.getTutorialByStatus(status).subscribe({
      next: (data) => {
        this.tutorials = data;
        console.log(data);
      },
      error: () => console.error()
    });
  }

}

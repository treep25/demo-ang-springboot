import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Tutorial} from '../models/tutorial.model';

const baseUrl = 'http://localhost:8080/api/v1/tutorials';

@Injectable({
  providedIn: 'root',
})
export class TutorialService {
  constructor(private http: HttpClient) {
  }

  getAll(): Observable<Tutorial[]> {
    return this.http.get<Tutorial[]>(baseUrl);
  }

  get(id: any): Observable<Tutorial> {
    return this.http.get<Tutorial>(`${baseUrl}/${id}`);
  }

  getImageData(id: any): Observable<ArrayBuffer> {
    return this.http.get(`${baseUrl}/getImage/${id}`, {responseType: 'arraybuffer'});
  }

  create(data: any): Observable<any> {
    return this.http.post(baseUrl, data, {withCredentials: true});
  }

  update(id: any, data: any): Observable<any> {
    return this.http.patch(`${baseUrl}/${id}`, data, {withCredentials: true});
  }

  updateStatus(id: any, status: any): Observable<any> {
    return this.http.patch(`${baseUrl}/status/${id}`, status, {withCredentials: true});
  }

  delete(id: any): Observable<any> {
    return this.http.delete(`${baseUrl}/${id}`, {withCredentials: true});
  }

  deleteAll(): Observable<any> {
    return this.http.delete(baseUrl, {withCredentials: true});
  }

  findByTitle(title: any): Observable<Tutorial[]> {
    return this.http.get<Tutorial[]>(`${baseUrl}/search?title=${title}`, {withCredentials: true});
  }

  findByDescription(description: any): Observable<Tutorial[]> {
    return this.http.get<Tutorial[]>(`${baseUrl}/search?description=${description}`, {withCredentials: true});
  }

  getAllSortedByTitle(sortedType: any): Observable<Tutorial[]> {
    return this.http.get<Tutorial[]>(`${baseUrl}/search?sortedType=${sortedType}`, {withCredentials: true});
  }

  getTutorialByStatus(status: any): Observable<Tutorial[]> {
    return this.http.get<Tutorial[]>(`${baseUrl}/search?status=${status}`, {withCredentials: true});
  }
}

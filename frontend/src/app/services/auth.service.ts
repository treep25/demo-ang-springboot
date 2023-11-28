import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthResponse, User} from "../models/tutorial.model";

const baseUrlAuth = 'http://localhost:8080/api/v1/auth';
const baseUrlUser = 'http://localhost:8080/api/v1/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) {
  }

  login(loginRequest: any): Observable<AuthResponse> {
    return this.http.post(`${baseUrlAuth}/login`, loginRequest);
  }

  register(registerRequest: any): void {
    this.http.post(`${baseUrlAuth}/register`, registerRequest);
  }

  meInfo(): Observable<User> {
    return this.http.get(`${baseUrlUser}/me/info`, {withCredentials: true});
  }

  refreshToken(refreshToken: any): Observable<AuthResponse> {
    return this.http.post(`${baseUrlAuth}/refresh`, refreshToken)
  }
}

import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthResponse, Order, Request, User} from "../models/tutorial.model";

const baseUrlAuth = 'http://localhost:8080/api/v1/auth';
const baseUrlUser = 'http://localhost:8080/api/v1/user';
const baseUrlRequest = 'http://localhost:8080/api/v1/request';

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

  refreshToken(refreshToken: any): Observable<AuthResponse> {
    return this.http.post(`${baseUrlAuth}/refresh`, refreshToken)
  }


  meInfo(): Observable<User> {
    return this.http.get(`${baseUrlUser}/me/info`, {withCredentials: true});
  }

  getAllUsersAdmin(): Observable<User[]> {
    return this.http.get<User[]>(`${baseUrlUser}/users`, {withCredentials: true})
  }

  changeUserStatus(userId: any): Observable<any> {
    return this.http.post(`${baseUrlUser}/${userId}`, null, {withCredentials: true})
  }

  searchByFirstName(firstName: any): Observable<User[]> {
    return this.http.get<User[]>(`${baseUrlUser}/search?firstName=${firstName}`, {withCredentials: true})
  }

  searchByLastName(lastName: any): Observable<User[]> {
    return this.http.get<User[]>(`${baseUrlUser}/search?lastName=${lastName}`, {withCredentials: true})
  }

  searchByIsEnabledTrue(): Observable<User[]> {
    return this.http.get<User[]>(`${baseUrlUser}/enabled`, {withCredentials: true})
  }

  clearBucketOrder(): Observable<User> {
    return this.http.delete(`${baseUrlUser}/clear-orders`, {withCredentials: true})
  }

  createRequest(email: any, issue: any): Observable<any> {
    return this.http.post(`${baseUrlRequest}`, {email, issue})
  }

  getALlRequestsWithUsers(): Observable<Request[]> {
    return this.http.get<Request[]>(`${baseUrlRequest}`, {withCredentials: true})
  }

  closeRequest(id: any): Observable<any> {
    return this.http.post(`${baseUrlRequest}/close/${id}`, null, {withCredentials: true})
  }

  cancelRequest(id: any): Observable<any> {
    return this.http.post(`${baseUrlRequest}/cancele/${id}`, null, {withCredentials: true})
  }

  //TODO remove to another class
  createOrder(tutorial: any): Observable<Order> {
    console.log("creating order")
    return this.http.post(`${baseUrlUser}/order`, tutorial, {withCredentials: true})
  }

  //TODO mb into another class
  getOrders(): Observable<Order> {
    return this.http.get(`${baseUrlUser}/orders`, {withCredentials: true});
  }
}

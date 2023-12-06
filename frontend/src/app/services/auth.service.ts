import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthResponse, Message, Order, Request, User} from "../models/tutorial.model";

const baseUrlAuth = 'http://localhost:8080/api/v1/auth';
const baseUrlUser = 'http://localhost:8080/api/v1/user';
const baseUrlRequest = 'http://localhost:8080/api/v1/request';
const baseUrlTexting = 'http://localhost:8080/api/v1/texting';

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

  getAllUsers(): Observable<User[]> {
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

  text(content: any, recipient: any): Observable<any> {
    return this.http.post(`${baseUrlTexting}`, {content, recipient}, {withCredentials: true})
  }

  getConversationDialogContent(recipientEmail: any): Observable<Message[]> {
    return this.http.get<Message[]>(`${baseUrlTexting}/find/conversation/${recipientEmail}`, {withCredentials: true})

  }

  getUnreadMessagesOfCurrentConversation(email: any): Observable<number> {
    return this.http.get<number>(`${baseUrlUser}/message/unread/${email}`, {withCredentials: true})
  }

  getUnreadMessagesOfUser(): Observable<number> {
    return this.http.get<number>(`${baseUrlUser}/message/unread`, {withCredentials: true})
  }

  createOrder(tutorial: any): Observable<Order> {
    console.log("creating order")
    return this.http.post(`${baseUrlUser}/order`, tutorial, {withCredentials: true})
  }

  getOrders(): Observable<Order> {
    return this.http.get(`${baseUrlUser}/orders`, {withCredentials: true});
  }
}

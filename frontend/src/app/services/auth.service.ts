import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from 'rxjs/operators';
import {AuthResponse, GroupMessage, Message, Order, Request, UrlDto, User} from "../models/tutorial.model";
import {Observable} from "rxjs";

const baseUrlAuth = 'http://localhost:8080/api/v1/auth';
const baseUrlUser = 'http://localhost:8080/api/v1/user';
const baseUrlRequest = 'http://localhost:8080/api/v1/request';
const baseUrlTexting = 'http://localhost:8080/api/v1/texting';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  token: string = '';

  constructor(private http: HttpClient) {
    this.csrf().subscribe(
      value => this.token = value.token
    )
  }

  login(loginRequest: any): Observable<AuthResponse> {
    return this.http.post(`${baseUrlAuth}/login`, loginRequest, {
      withCredentials: true, headers:
        new HttpHeaders({
          'Content-Type': 'application/json',
          'X-CSRF-Token': this.token
        })
    });
  }

  getUrl(): Observable<UrlDto> {
    return this.http.get<UrlDto>("http://localhost:8080/auth/url");
  }

  googleAuth(code: string): Observable<string> {
    return this.http.get(`http://localhost:8080/auth/callback?code=${code}`, {responseType: 'text'})
      .pipe(
        map(response => response as string)
      );
  }

  loginOAuth2(token: string | null | undefined) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<AuthResponse>(`http://localhost:8080/auth/login/oauth2`, {headers: headers});
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

  csrf(): Observable<any> {
    return this.http.get("http://localhost:8080/api/v1/token")
  }

  text(content: any, recipient: any): Observable<any> {
    return this.http.post(`${baseUrlTexting}`, {content, recipient}, {
      withCredentials: true, headers:
        new HttpHeaders({
          'Content-Type': 'application/json',
          'X-CSRF-Token': this.token
        })
    })
  }

  getConversationDialogContent(recipientEmail: any): Observable<Message[]> {
    return this.http.get<Message[]>(`${baseUrlTexting}/find/conversation/${recipientEmail}`, {withCredentials: true})

  }

  getUnreadMessagesOfCurrentConversation(email: any): Observable<number> {
    return this.http.get<number>(`${baseUrlUser}/message/unread/${email}`, {withCredentials: true})
  }

  getllGroupsOfCurrentUserDialogs(): Observable<GroupMessage[]> {
    return this.http.get<GroupMessage[]>(`${baseUrlTexting}/group/messages`, {withCredentials: true})
  }

  textGroup(recipients?: string[], content?: string): Observable<any> {
    return this.http.post(`${baseUrlTexting}/group/message`, {content, recipients}, {withCredentials: true})
  }

  getConversationDialogContentInGroups(recipients?: string[]): Observable<GroupMessage[]> {
    return this.http.post<GroupMessage[]>(`${baseUrlTexting}/find/conversation/groups`, recipients, {withCredentials: true})
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


  payOrder(): Observable<Order> {
    return this.http.post(`${baseUrlUser}/pay/orders`, null, {withCredentials: true})
  }

  cancelOrder(): Observable<any> {
    return this.http.delete(`${baseUrlUser}/delete/orders`, {withCredentials: true})
  }
}

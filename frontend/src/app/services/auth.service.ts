import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from 'rxjs/operators';
import {AuthResponse, AuthResponse2Fa, Event, GroupMessage, Message, Order, Request, UrlDto, User} from "../models/tutorial.model";
import {Observable} from "rxjs";

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

  create2faAfterRegister(loginRequest: any): Observable<AuthResponse2Fa> {
    return this.http.post(`http://localhost:8080/api/v2/auth/create/2fa`, loginRequest);
  }

  loginVerifyToken(loginRequest: any): Observable<AuthResponse> {
    return this.http.post(`http://localhost:8080/api/v2/auth/login/verify-code`, loginRequest);
  }

  getUrlGoogle(): Observable<UrlDto> {
    return this.http.get<UrlDto>("http://localhost:8080/auth/url");
  }

  googleAuth(code: string): Observable<string> {
    return this.http.get(`http://localhost:8080/auth/callback?code=${code}`, {responseType: 'text'})
      .pipe(
        map(response => response as string)
      );
  }

  loginOAuth2Google(token: string | null | undefined) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${"GOOGLE" + token}`,
    });

    return this.http.get<AuthResponse>(`http://localhost:8080/auth/login/oauth2`, {headers: headers});
  }

  getUrlFacebook(): Observable<UrlDto> {
    return this.http.get<UrlDto>("http://localhost:8080/auth/facebook/url");
  }

  facebookAuth(code: string): Observable<string> {
    return this.http.get(`http://localhost:8080/auth/facebook/callback?code=${code}`, {responseType: 'text'})
      .pipe(
        map(response => response as string)
      );
  }

  loginOAuth2Facebook(token: string | null | undefined) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${"FACEBOOK" + token}`,
    });

    return this.http.get<AuthResponse>(`http://localhost:8080/auth/login/facebook/oauth2`, {headers: headers});
  }

  getUrlGithub(): Observable<UrlDto> {
    return this.http.get<UrlDto>("http://localhost:8080/auth/github/url");
  }

  githubAuth(code: string): Observable<string> {
    return this.http.get(`http://localhost:8080/auth/github/callback?code=${code}`, {responseType: 'text'})
      .pipe(
        map(response => response as string)
      );
  }

  loginOAuth2Github(token: string | null | undefined) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${"GITHUB" + token}`,
    });

    return this.http.get<AuthResponse>(`http://localhost:8080/auth/login/github/oauth2`, {headers: headers});
  }

  getEventsGoogleCalendar() {
    return this.http.get(`http://localhost:8080/google/calendar`, {withCredentials: true})
  }

  setAccessTokenCalendar(token: string) {
    return this.http.get(`http://localhost:8080/google/calendar/save/access-token?token=${token}`, {withCredentials: true})
  }

  getUrlGoogleCalendar(): Observable<UrlDto> {
    return this.http.get<UrlDto>("http://localhost:8080/google/calendar/auth/url");
  }

  googleOAuth2Calendar(code: string): Observable<string> {
    return this.http.get(`http://localhost:8080/google/calendar/auth/callback?code=${code}`, {responseType: 'text'})
      .pipe(
        map(response => response as string)
      );
  }

  createEventCalendar(newEvent: Event): Observable<any> {
    return this.http.post("http://localhost:8080/google/calendar/c/event", newEvent, {withCredentials: true})
  }

  createEventAzureCalendar(newEvent: Event) {
    return this.http.post("http://localhost:8080/azure/c/event", newEvent, {withCredentials: true})
  }

  searchByDay(day: string) {
    return this.http.get(`http://localhost:8080/google/calendar/byDay?day=${day}`, {withCredentials: true})
  }

  searchByDayAzureCalendar(day: string) {
    return this.http.get(`http://localhost:8080/azure/calendar/byDay?day=${day}`, {withCredentials: true})
  }

  getEventsAzureCalendar() {
    return this.http.get(`http://localhost:8080/azure/calendar`, {withCredentials: true})
  }

  setAzureAccessTokenCalendar(token: string) {
    return this.http.get(`http://localhost:8080/azure/calendar/save/access-token?token=${token}`, {withCredentials: true})
  }

  getUrlAzureCalendar(): Observable<UrlDto> {
    return this.http.get<UrlDto>("http://localhost:8080/azure/calendar/auth/url");
  }

  azureOAuth2Calendar(code: string): Observable<string> {
    return this.http.get(`http://localhost:8080/azure/calendar/auth/callback?code=${code}`, {responseType: 'text'})
      .pipe(
        map(response => response as string)
      );
  }


  register(registerRequest: any) {
    return this.http.post(`${baseUrlAuth}/register`, registerRequest);
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
      withCredentials: true
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

  generateReport() {
    return this.http.get("http://localhost:8080/api/v1/user/me/info/report", {withCredentials: true, responseType: "blob"},)
  }
}

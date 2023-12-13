export class Tutorial {
  id?: any;
  title?: string;
  description?: string;
  status?: string;
  overview?: string;
  content?: string;
  image?: File | null
  imagePath?: string

}

export enum Status {
  PENDING = 'PENDING',
  PUBLISHED = 'PUBLISHED'
}

export class RegisterRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  repeatPassword?: string
}

export class LoginRequest {
  email?: string;
  password?: string;
}

export class AuthResponse {
  accessToken?: string;
  refreshToken?: string;
}

export class User {
  id?: any;
  firstName?: string;
  lastName?: string;
  email?: any | "";
  role?: string;
  order?: Order;
  showOrders?: boolean
  isEnabled?: boolean
}

export class Order {
  tutorialsOrder?: Tutorial[];
  orderStatus?: any;
}

export class Request {
  id?: any;
  user?: User;
  issue?: string;
  createdDate?: string;
  checkDate?: string;
  status?: string;
}

export class Message {
  dialogUUID?: any;
  messageStatus?: any
  content?: string;
  sender?: User;
  recipient?: User;
  sentDateTime?: string;
}

export class GroupMessage {
  dialogUUID?: any;
  messageStatus?: any
  content?: string;
  sender?: User;
  recipients?: User[];
  sentDateTime?: string;
}


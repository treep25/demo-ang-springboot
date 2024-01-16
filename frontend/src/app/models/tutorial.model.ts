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
  code?: string
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

export class UrlDto {
  uri?: string
}

export class GoogleAccessToken {
  accessToken?: string
}

export class Event {
  summary?: string;
  location?: string;
  startDate?: Date;
  endDate?: Date;
  token?: string;
}

export class AuthResponse2Fa {
  secretKey?: string;
  authenticatorUrl?: string;
}

export interface Email {
  historyId: number;
  id: string;
  internalDate: number;
  labelIds: string[];
  payload: {
    body: {
      size: number;
    };
    filename: string;
    headers: {
      name: string;
      value: string;
    }[];
    mimeType: string;
    partId: string;
    parts: {
      body: {
        data: string;
        size: number;
      };
      filename: string;
      headers: {
        name: string;
        value: string;
      }[];
      mimeType: string;
      partId: string;
    }[];
  };
  sizeEstimate: number;
  snippet: string;
  threadId: string;
}

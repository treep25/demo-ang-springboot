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
  email?: string;
  role?: string;
  order?: Order;
  showOrders?: boolean
  isEnabled?: any
}

export class Order {
  tutorialsOrder?: Tutorial[];
}

export class Request {
  user?: User;
  issue?: string;
  createdDate?: string;
  status?: string;
}


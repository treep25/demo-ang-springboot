export class Tutorial {
  id?: any;
  title?: string;
  description?: string;
  status?: string;
}

export enum Status {
  PENDING = 'PENDING',
  PUBLISHED = 'PUBLISHED'
}

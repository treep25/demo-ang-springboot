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

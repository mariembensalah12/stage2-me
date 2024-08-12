import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CvformService {
  private pdfUrlsSubject = new BehaviorSubject<string[]>([]);


  constructor(private httpClient: HttpClient) { }
  private baseUrl = 'http://localhost:8080'; 


  Onsave(formId: number, cv: any): Observable<any> {
    return this.httpClient.post(`http://localhost:8080/form/${formId}/insert`, cv, { responseType: 'text' });
  }
  generateCV(cvData: any): Observable<string[]> {
    console.log('Envoi de la requête de génération du CV avec les données:', cvData);
    return this.httpClient.post<string[]>(`${this.baseUrl}/cv/generate`, cvData);
  }
  setPdfUrls(pdfUrls: string[]) {
    this.pdfUrlsSubject.next(pdfUrls);
  }

  getPdfUrls(): Observable<string[]> {
    return this.pdfUrlsSubject.asObservable();
  }
  
  
}

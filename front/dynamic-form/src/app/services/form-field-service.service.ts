import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Form } from '../models/form';

@Injectable({
  providedIn: 'root'
})
export class FormFieldServiceService {

  private baseUrl = 'http://localhost:8080/form';

  constructor(private http: HttpClient) { }

  GETALL(): Observable<Form[]> {
    return this.http.get<Form[]>(`${this.baseUrl}/findall`);
  }

  
  ADD(formData: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, formData);
  }

  UPDATE(form: any): Observable<any> {
    const url = `${this.baseUrl}/update/${form.id}`;
    const payload = { nom: form.nom };
    return this.http.put(url, payload);
  }
  
  DELETE(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`);
  }
  GETBYID(id:number): Observable<Form>{
    return this.http.get<Form>(`${this.baseUrl}/${id}`);
  }
  Deletechampform(formId: number, valueId: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${formId}/deletechampform/${valueId}`);
  }
  UpdateChampform(formId: number, valueId: string, updatedValue: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${formId}/updatechampform/${valueId}`, updatedValue);
  }
  InsertChampform(formId: number,champs:any): Observable<any>{
    return this.http.post<any>(`${this.baseUrl}/${formId}/insertchampform`, champs);
  }
  saveFormData(formName: string, formData: any): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/save?formName=${encodeURIComponent(formName)}`, formData);
  }
}

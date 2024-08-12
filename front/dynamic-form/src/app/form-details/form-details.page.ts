import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormFieldServiceService } from '../services/form-field-service.service';
import { Form } from '../models/form';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-form-details',
  templateUrl: './form-details.page.html',
  styleUrls: ['./form-details.page.scss'],
})
export class FormDetailsPage implements OnInit {

  formId: string | null = null;
  myForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private ffs: FormFieldServiceService,
    private fb: FormBuilder
  ) {
    this.myForm = this.fb.group({
      tableRows: this.fb.array([])
    });
  }

  get tableRows() {
    return this.myForm.get('tableRows') as FormArray;
  }

  addRow() {
    const row = this.fb.group({
      id: [null],
      label: ['', Validators.required],
      type: ['', Validators.required],
      required: ['false', Validators.required] // Définir une valeur par défaut
    });
    this.tableRows.push(row);
  }

  ngOnInit(): void {
    this.GETDATA();
   
  }

  GETDATA(): void {
    this.formId = this.route.snapshot.paramMap.get('id');
    if (this.formId) {
      const id = Number(this.formId);
      if (!isNaN(id)) {
        this.ffs.GETBYID(id).subscribe((data: Form) => {
          console.log('Données reçues du backend:', data);
          if (Array.isArray(data.champform)) {
            this.transformToFormArray(data.champform);
          } else {
            console.error('champform is not an array:', data.champform);
          }
        });
      } else {
        console.error('Form ID is not a valid number');
      }
    } else {
      console.error('Form ID is null');
    }
  }
  private transformToFormArray(champform: any[]): void {
    const formGroups = champform.map(field => {
      return this.fb.group({
        id: [field.id],
        label: [field.label || '', Validators.required],
        type: [field.type || '', Validators.required],
        required: [
          field.required === true || field.required === 'true' ? 'true' : 
          field.required === false || field.required === 'false' ? 'false' : 
          '', 
          Validators.required
        ] 
      });
    });
  
    this.myForm.setControl('tableRows', this.fb.array(formGroups));
    console.log('Formulaire après transformation:', this.myForm.value);
  }

  removeRow(index: number): void {
    const tableRows = this.myForm.get('tableRows') as FormArray;
    if (tableRows.length > 1) {
      const rowToRemove = tableRows.at(index);
      const rowId = rowToRemove.get('id')?.value;

      tableRows.removeAt(index);

      if (rowId && this.formId) {
        this.deleteFromBackend(Number(this.formId), rowId);
      }
    } else {
      console.warn('Cannot remove the last row');
    }
  }

  private deleteFromBackend(formId: number, valueId: string): void {
    this.ffs.Deletechampform(formId, valueId).subscribe(
      response => {
        console.log('Row deleted successfully from backend', response);
      },
      error => {
        console.error('Error deleting row from backend', error);
      }
    );
  }

  onSubmit(index: number): void {
    const formId = Number(this.formId);
    if (isNaN(formId)) {
      console.error('Invalid form ID');
      return;
    }
  
    const tableRows = this.myForm.get('tableRows') as FormArray;
    const row = tableRows.at(index);
    const rowData = row.value; 
    
    if (rowData.id) {
      // Update existing record
      this.ffs.UpdateChampform(formId, rowData.id, rowData).subscribe(
        response => {
          console.log('Row updated successfully', response);
          // Optionally update the UI or show a success message
        },
        error => {
          console.error('Error updating row', error);
          // Handle the error (e.g., show an error message to the user)
        }
      );
    } else {
      // Insert new record
      this.ffs.InsertChampform(formId, rowData).subscribe(
        response => {
          console.log('Row inserted successfully', response);
          // Update the row with the new ID from the response
          row.patchValue({ id: response.id });
          // Optionally update the UI or show a success message
        },
        error => {
          console.error('Error inserting row', error);
          // Handle the error (e.g., show an error message to the user)
        }
      );
    }
  }
}

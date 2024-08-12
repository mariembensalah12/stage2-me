import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormFieldServiceService } from '../services/form-field-service.service';
import { Form } from '../models/form';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { AddFormPage } from '../add-form/add-form.page';

@Component({
  selector: 'app-home',
  templateUrl: 'admin.page.html',
  styleUrls: ['admin.page.scss'],
})
export class AdminPage implements OnInit {

  formFields: any[] = [];

  constructor( private ffs: FormFieldServiceService,private router: Router,private modalController: ModalController,  private cdr: ChangeDetectorRef
  ){}

  ngOnInit(): void {
    this.ffs.GETALL().subscribe(
      (data) => {
        this.formFields = data;
      },
      (error) => {
        console.error('Error fetching form fields:', error);
        console.log('Error details:', error.error); // Affichez les détails de l'erreur JSON
      }
    );
  }

  async edit(event: Event, form: any) {
    event.stopPropagation();
  
    const modal = await this.modalController.create({
      component: AddFormPage,
      componentProps: { 
        form: form,
        isEditing: true  // Ajoutez ce flag pour indiquer que nous sommes en mode édition
      }
    });
  
    modal.onDidDismiss().then((data) => {
      if (data.data) {
        form.nom = data.data.nom;
        console.log('Form to update:', form);  // Ajoutez cette ligne pour voir ce que vous envoyez
        this.ffs.UPDATE(form).subscribe(
          () => {
            console.log('Form updated successfully');
          },
          (error) => {
            console.error('Error updating form:', error);
          }
        );
      }
    });
  
    return await modal.present();
  }
  
  
  
  onCardClick(field: any) {
    console.log('Card clicked:', field);
    // Navigate to the detail page with the field ID
    this.router.navigate(['/form-details', field.id]);
  }

  async openAddFormModal() {
    const modal = await this.modalController.create({
      component: AddFormPage
    });
  
    modal.onDidDismiss().then((result) => {
      if (result.data) {
        const newForm = { nom: result.data.nom };
        this.ffs.ADD(newForm).subscribe(
          (addedForm) => {
            console.log('Form added successfully');
            this.formFields = [...this.formFields, addedForm];
this.cdr.detectChanges();
          },
          (error) => {
            console.error('Error adding form:', error);
          }
        );
      }
    });
  
    return await modal.present();
  }
  delete(event: Event, id: number): void {
    event.stopPropagation(); // Empêche l'événement de clic de la carte d'être déclenché
    this.ffs.DELETE(id).subscribe(
      () => {
        // Remove the deleted item from the list
        this.formFields = this.formFields.filter(field => field.id !== id);
      },
      (error) => {
        console.error('Error deleting form:', error);
      }
    );
  }

  

  }



import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ModalController, NavParams } from '@ionic/angular';
import { FormFieldServiceService } from '../services/form-field-service.service';

@Component({
  selector: 'app-add-form',
  templateUrl: './add-form.page.html',
  styleUrls: ['./add-form.page.scss'],
})
export class AddFormPage implements OnInit {

  formNameForm: FormGroup;
  isEditing: boolean = false;
  existingForm: any;

  constructor(private fb: FormBuilder, private modalController: ModalController,private ffs: FormFieldServiceService, private navParams: NavParams) {
    this.formNameForm = this.fb.group({
      name: ['', Validators.required]
    });
  }

  closeModal() {
    this.modalController.dismiss();
  }

  onSubmit() {
    if (this.formNameForm.valid) {
      const formData = { nom: this.formNameForm.value.name };
      
      if (this.isEditing) {
        // Mise à jour d'un formulaire existant
        this.ffs.UPDATE({ ...this.existingForm, ...formData }).subscribe(() => {
          this.modalController.dismiss(formData);
        });
      } else {
        // Ajout d'un nouveau formulaire
        this.ffs.ADD(formData).subscribe(() => {
          this.modalController.dismiss(formData);
        });
      }
    }
  }


  ngOnInit() {
    this.existingForm = this.navParams.get('form');
    this.isEditing = !!this.existingForm;

    if (this.isEditing) {
      this.formNameForm.patchValue({
        name: this.existingForm.nom // Assurez-vous que c'est bien 'nom' et non 'name'
      });
    }
  }
  

}

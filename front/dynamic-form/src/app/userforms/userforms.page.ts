import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormFieldServiceService } from '../services/form-field-service.service';
import { Router } from '@angular/router';
import { ModalController } from '@ionic/angular';

@Component({
  selector: 'app-userforms',
  templateUrl: './userforms.page.html',
  styleUrls: ['./userforms.page.scss'],
})
export class UserformsPage implements OnInit {
  formFields: any[] = [];

  constructor( private ffs: FormFieldServiceService,private router: Router,private modalController: ModalController,  private cdr: ChangeDetectorRef
  ){}

  ngOnInit():void {
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

  onCardClick(field: any) {
    console.log('Card clicked:', field);
    this.router.navigate(['/forms', field.id]);
  }

}

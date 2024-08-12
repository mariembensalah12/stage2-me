import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormFieldServiceService } from '../services/form-field-service.service';
import { CvformService } from '../services/cvform.service';
import { CV } from '../models/cv';
import { Form } from '../models/form';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-formcv',
  templateUrl: './forms.page.html',
  styleUrls: ['./forms.scss'],
})
export class FormsPage implements OnInit {
  form!: FormGroup;
  formFields: any[] = [];
  formData!: Form;
  pdfUrls: string[] = [];
  formName!: string;
  formId: string | null = null;
  isLoading = false; // Declare the loading variable

  


  constructor(
    private ffs: FormFieldServiceService,
    private formBuilder: FormBuilder,
    private cvformService:CvformService,private route: ActivatedRoute,private router:Router
  ) {}

  ngOnInit() {
    this.loadFormData();
  }

  loadFormData() {
    this.formId = this.route.snapshot.paramMap.get('id');
    const id = Number(this.formId);
    this.ffs.GETBYID(id).subscribe(
      (data: Form) => {
        this.formData = data;
        this.formName = data.nom!;
        if (Array.isArray(data.champform)) {
          this.formFields = data.champform;
         
        } else {
          console.error('champform is not an array', data.champform);
          this.formFields = [];
        }
        this.createForm();
      },
      error => {
        console.error('Erreur lors du chargement du formulaire', error);
      }
    );
  }

  createForm() {
    const group: { [key: string]: any } = {};
    this.formFields.forEach(field => {
      group[field.label.toLowerCase()] = ['', this.isFieldRequired(field.required) ? Validators.required : null];
    });
    this.form = this.formBuilder.group(group);
  }

  isFieldRequired(required: any): boolean {
    return required === true || required === 'true';
  }
  onSubmit() {
    if (this.form.valid && this.formName) {
      const formData = this.form.value;
      console.log('Données du formulaire envoyées:', formData);
      this.ffs.saveFormData(this.formName, formData).subscribe({
        next: (response) => {
          console.log('Données du formulaire enregistrées avec succès', response.message);
          alert(response.message);
        },
        error: (error) => {
          console.error('Erreur lors de l\'enregistrement des données du formulaire', error);
          if (error.error && error.error.error) {
            alert(error.error.error);
          } else {
            alert('Erreur lors de l\'enregistrement des données du formulaire. Veuillez réessayer.');
          }
        }
      });
  
      if (this.formName === 'cv') {
        this.isLoading = true; // Show the spinner before starting the CV generation

        console.log('Données du formulaire envoyées pour la génération du CV:', formData);
        
        this.cvformService.generateCV(this.form.value).subscribe({
          next: (pdfPaths: string[]) => {
            this.pdfUrls = pdfPaths;
            this.isLoading = false; // Hide the spinner after the PDFs are loaded

            console.log('CV généré avec succès:', pdfPaths);
            this.cvformService.setPdfUrls(pdfPaths);
           

          },
          error: (err) => {
            this.isLoading = false; // Hide the spinner after the PDFs are loaded

            console.error('Erreur lors de la génération du CV', err);
            alert('Erreur lors de la génération du CV. Veuillez réessayer.');
          }
        });
      } else {
        console.log('Le formulaire n\'est pas valide ou le nom du formulaire est manquant');
        alert('Veuillez remplir correctement tous les champs obligatoires.');
      }
    } else {
      console.log('Le formulaire n\'est pas valide ou le nom du formulaire est manquant');
      alert('Veuillez remplir correctement tous les champs obligatoires.');
    }
  }

    
}
  
 
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FormDetailsPageRoutingModule } from './form-details-routing.module';

import { FormDetailsPage } from './form-details.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FormDetailsPageRoutingModule,ReactiveFormsModule
  ],
  declarations: [FormDetailsPage]
})
export class FormDetailsPageModule {}

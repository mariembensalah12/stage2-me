import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { UserformsPageRoutingModule } from './userforms-routing.module';

import { UserformsPage } from './userforms.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    UserformsPageRoutingModule
  ],
  declarations: [UserformsPage]
})
export class UserformsPageModule {}

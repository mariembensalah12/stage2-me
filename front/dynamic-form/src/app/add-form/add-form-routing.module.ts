import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AddFormPage } from './add-form.page';

const routes: Routes = [
  {
    path: '',
    component: AddFormPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AddFormPageRoutingModule {}

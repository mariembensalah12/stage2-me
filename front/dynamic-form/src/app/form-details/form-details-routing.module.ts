import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FormDetailsPage } from './form-details.page';

const routes: Routes = [
  {
    path: '',
    component: FormDetailsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FormDetailsPageRoutingModule {}

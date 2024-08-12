import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UserformsPage } from './userforms.page';

const routes: Routes = [
  {
    path: '',
    component: UserformsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserformsPageRoutingModule {}

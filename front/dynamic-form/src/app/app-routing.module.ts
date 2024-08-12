import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then( m => m.adminPageModule)
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'forms/:id',
    loadChildren: () => import('./forms/forms.module').then( m => m.FormcvPageModule)
  },
 
  {
    path: 'form-details/:id',
    loadChildren: () => import('./form-details/form-details.module').then( m => m.FormDetailsPageModule)
  },
  {
    path: 'add-form',
    loadChildren: () => import('./add-form/add-form.module').then( m => m.AddFormPageModule)
  },
  {
    path: 'pdf-viewer',
    loadChildren: () => import('./pdf-viewer/pdf-viewer.module').then( m => m.PdfViewerPageModule)
  },
  {
    path: 'userforms',
    loadChildren: () => import('./userforms/userforms.module').then( m => m.UserformsPageModule)
  },
  {
    path: 'home',
    loadChildren: () => import('./home/home.module').then( m => m.HomePageModule)
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }

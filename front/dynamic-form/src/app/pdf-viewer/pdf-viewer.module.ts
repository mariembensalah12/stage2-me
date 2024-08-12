import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { PdfViewerPageRoutingModule } from './pdf-viewer-routing.module';
import { PdfViewerModule } from 'ng2-pdf-viewer';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    PdfViewerPageRoutingModule,PdfViewerModule
  ],
  declarations: []
})
export class PdfViewerPageModule {}

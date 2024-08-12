import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { FileOpener } from '@awesome-cordova-plugins/file-opener/ngx';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { File } from '@awesome-cordova-plugins/file/ngx';
import { FileTransfer } from '@awesome-cordova-plugins/file-transfer/ngx';
import { DocumentViewer } from '@awesome-cordova-plugins/document-viewer/ngx';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule, 
    IonicModule.forRoot(), 
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    PdfViewerModule
  ],
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    FileOpener,
    File,
    FileTransfer,
    DocumentViewer
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
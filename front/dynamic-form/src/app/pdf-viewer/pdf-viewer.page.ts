import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { CvformService } from '../services/cvform.service';

import { DocumentViewer, DocumentViewerOptions } from '@awesome-cordova-plugins/document-viewer/ngx';
import { FileTransfer } from '@awesome-cordova-plugins/file-transfer/ngx';
import { File } from '@awesome-cordova-plugins/file/ngx';
import { Platform } from '@ionic/angular';

@Component({
  selector: 'app-pdf-viewer',
  templateUrl: './pdf-viewer.page.html',
  styleUrls: ['./pdf-viewer.page.scss'],
})
export class PdfViewerPage implements OnInit {

  @Input() pdfUrls: string[] = [];
  subscription!: Subscription;


  constructor(private route: ActivatedRoute,private cvfs:CvformService,private document: DocumentViewer, private file: File, private transfer:FileTransfer,
    private platform:Platform
    ) { }

  ngOnInit() {
    this.subscription = this.cvfs.getPdfUrls().subscribe(pdfUrls => {
      this.pdfUrls = pdfUrls;
    });
  }
  async openPdf(url: string) {
    if (this.platform.is('cordova')) {
      try {
        let path = this.platform.is('ios') ? this.file.documentsDirectory : this.file.dataDirectory;
        const transfer = this.transfer.create();
        const fullurl='assets'+url;
        const entry = await transfer.download(fullurl, path + 'myfile.pdf');
        const fileUrl = entry.toURL();
        await this.document.viewDocument(fileUrl, 'application/pdf', {});
      } catch (error) {
        console.error("Erreur lors de l'ouverture du PDF:", error);
      }
    } else {
      const fullurl='assets'+url;

      // Pour le navigateur, ouvrir le PDF dans un nouvel onglet
      window.open(fullurl, '_blank');
    }
  }
  
}




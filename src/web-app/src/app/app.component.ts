import { Component, Inject, OnInit } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { TranslocoService } from '@ngneat/transloco';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(
    @Inject(DOCUMENT) private document: Document,
    private translocoService: TranslocoService,
    private titleService: Title
  ) {}

  ngOnInit() {
    this.translocoService.langChanges$.subscribe((lang) => {
      this.document.documentElement.lang = lang;
      this.translocoService
        .selectTranslate('app.title')
        .subscribe((value: string) => {
          this.titleService.setTitle(value);
        });
    });
  }
}

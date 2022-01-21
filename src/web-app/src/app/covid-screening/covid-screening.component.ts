import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslocoService } from '@ngneat/transloco';

@Component({
  selector: 'app-covidscreening',
  templateUrl: './covid-screening.component.html',
  styleUrls: ['./covid-screening.component.css'],
})
export class CovidScreeningComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private transloco: TranslocoService
  ) {}

  ngOnInit(): void {
    const lang = this.route.snapshot.paramMap.get('lang');
    switch (lang) {
      case 'fr': {
        this.transloco.setActiveLang('fr');
        break;
      }
      default: {
        this.transloco.setActiveLang('en');
        break;
      }
    }
  }
}

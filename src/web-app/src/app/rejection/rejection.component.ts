import { Component, Input, OnInit } from '@angular/core';
import { TranslocoService } from '@ngneat/transloco';
import { environment } from 'src/environments/environment';
import { Address } from '../address';

@Component({
  selector: 'app-rejection',
  templateUrl: './rejection.component.html',
  styleUrls: ['./rejection.component.css'],
})
export class RejectionComponent implements OnInit {
  @Input() fullName: string | undefined = '';
  @Input() email: string | undefined = '';
  @Input() phone: string | undefined = '';
  @Input() addressId: string | undefined = '';
  @Input() addressList: Array<Address> = [];

  FEATURE_FLAGS = environment.featureFlags;

  resultIssuedOn: Date = new Date();
  resultValidUntil: Date = new Date();
  selectedAddress: any;
  selectedLang: string = 'en';

  constructor(private translocoService: TranslocoService) {}

  ngOnInit(): void {
    this.translocoService.langChanges$.subscribe(
      (lang) => (this.selectedLang = lang)
    );

    this.resultIssuedOn = new Date();
    this.resultValidUntil = new Date(
      this.resultIssuedOn.getFullYear(),
      this.resultIssuedOn.getMonth(),
      this.resultIssuedOn.getDate(),
      23,
      59
    );

    this.selectedAddress = this.addressList.find(
      (address: any) => address.pk === this.addressId
    );
  }
}

import { Component, Output, Input, EventEmitter, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { TranslocoService } from '@ngneat/transloco';
import { Address } from '../address';

@Component({
  selector: 'app-personal-info-q',
  templateUrl: './personal-info-q.component.html',
  styleUrls: ['./personal-info-q.component.css'],
})
export class PersonalInfoQComponent implements OnInit {
  @Input() cityList = [];
  @Input() addressList: Array<Address> = [];

  FEATURE_FLAGS = environment.featureFlags;

  addressesByCity: Array<any> = [];
  selectedLang = 'en';

  model = {
    fullName: '',
    email: '',
    phone: '',
    city: '',
    address: '',
  };

  @Output() messageEvent = new EventEmitter<{
    fullName: string;
    email: string;
    phone: string;
    address: string;
  }>();

  constructor(private translocoService: TranslocoService) {}

  ngOnInit(): void {
    this.translocoService.langChanges$.subscribe(
      (lang) => (this.selectedLang = lang)
    );
  }

  /**
   * Filter locations by city when city is selected
   */
  selectCity() {
    this.addressesByCity = this.addressList.filter(
      (address: any) => address.city === this.model.city
    );
    this.model.address = '';
  }

  /**
   * Emit data to parent component
   */
  onSubmit() {
    this.messageEvent.emit(this.model);
  }
}

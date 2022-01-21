import { Component, OnInit } from '@angular/core';
import uniq from 'lodash/uniq';
import { SubmissionService } from '../submission.service';
import { Submission } from '../submission';
import { AddressService } from '../address.service';
import { Address } from '../address';

enum Step {
  ConsentQ = 'ConsentQ',
  PersonalInfoQ = 'PersonalInfoQ',
  SymptomsQ = 'SymptomsQ',
  WaitingForResultsQ = 'WaitingForResultsQ',
  DoctorIsolationQ = 'DoctorIsolationQ',
  CloseContactQ = 'CloseContactQ',
  TravelQuarantineQ = 'TravelQuarantineQ',
  CloseContactQuarantineQ = 'CloseContactQuarantineQ',
  Rejection = 'Rejection',
  Success = 'Success',
}

@Component({
  selector: 'app-form-container',
  templateUrl: './form-container.component.html',
  styleUrls: ['./form-container.component.css'],
})
export class FormContainerComponent implements OnInit {
  formData: Submission = {};

  currentStep: Step = Step.ConsentQ;
  cityList: any = [];
  addressList: Array<any> = [];

  constructor(
    private addressService: AddressService,
    private submissionService: SubmissionService
  ) {}

  ngOnInit(): void {
    this.addressService.getAddresses().subscribe((data: any) => {
      this.addressList = data;
      this.cityList = this.extractUniqueCities(data);
    });
  }

  /**
   * Generate a list of unique citied from a list of building addresses
   * @param addressList a list of addresses of buildings
   * @returns a list of addresses of buildings
   */
  private extractUniqueCities(addressList: Array<Address>) {
    const listOfCities = addressList.map((item: Address) => item.city);
    return uniq(listOfCities).sort();
  }

  private nextStep() {
    switch (this.currentStep) {
      case Step.ConsentQ: {
        this.currentStep = Step.PersonalInfoQ;
        break;
      }
      case Step.PersonalInfoQ: {
        this.currentStep = Step.SymptomsQ;
        break;
      }
      case Step.SymptomsQ: {
        this.currentStep = Step.WaitingForResultsQ;
        break;
      }
      case Step.WaitingForResultsQ: {
        this.currentStep = Step.DoctorIsolationQ;
        break;
      }
      case Step.DoctorIsolationQ: {
        this.currentStep = Step.CloseContactQ;
        break;
      }
      case Step.CloseContactQ: {
        this.currentStep = Step.TravelQuarantineQ;
        break;
      }
      case Step.TravelQuarantineQ: {
        this.currentStep = Step.CloseContactQuarantineQ;
        break;
      }
      case Step.TravelQuarantineQ: {
        this.currentStep = Step.CloseContactQuarantineQ;
        break;
      }
      case Step.CloseContactQuarantineQ: {
        this.currentStep = Step.Success;
        break;
      }
      default: {
        this.currentStep = Step.Rejection;
        break;
      }
    }
  }

  private jumpToRejection() {
    this.currentStep = Step.Rejection;
  }

  private scrollToTopOfContent() {
    const el = document.getElementById('wb-cont');
    el?.scrollIntoView();
  }

  receiveMessage($event: any) {
    this.formData[this.currentStep] = $event;

    if ($event === true && this.currentStep != Step.ConsentQ) {
      this.jumpToRejection();
      this.scrollToTopOfContent();
    } else {
      this.nextStep();
      this.scrollToTopOfContent();
    }

    if (
      this.currentStep === Step.Success ||
      this.currentStep === Step.Rejection
    ) {
      this.submissionService.saveSubmission(this.formData).subscribe();
    }
  }
}

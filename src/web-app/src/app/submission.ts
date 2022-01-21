export interface Submission {
  ConsentQ?: boolean;
  PersonalInfoQ?: {
    fullName: string;
    email: string;
    phone: string;
    address: string;
  };
  SymptomsQ?: boolean;
  WaitingForResultsQ?: boolean;
  DoctorIsolationQ?: boolean;
  CloseContactQ?: boolean;
  TravelQuarantineQ?: boolean;
  CloseContactQuarantineQ?: boolean;
  Rejection?: boolean;
  Success?: boolean;
}

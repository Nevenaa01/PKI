export interface User{
    id: number;
    username:string;
    firstName:string;
    lastName:string;
    email:string;
    password:string;
    country:string;
    organization:string;
    userType:UserType;
    city: String;
}
export interface UserWithDates{
    issuerData:User;
    startDate:string;
    endDate:string;
}

export interface Certificate {
    id: number;
    serialNumber: string;
    issuerId: number;
    subjectId: number;
    publicKey: string;
    typeCertificate: string;
    valid?: boolean;
  }
export enum UserType {
    USER = 0,
    ADMIN = 1,
    MODERATOR = 2,
}
export interface CreateCertificateDto{
    issuerData:User;
    subjectData:User;
    startDate:string;
    endDate:string;
    serialNum:string;
}
export interface CertificateDto{
    issuerId: number;
    serialNumber: string;
    subjectName: string;
    typeCertificate: string;
    subjectId: number;
    valid?: boolean;
}
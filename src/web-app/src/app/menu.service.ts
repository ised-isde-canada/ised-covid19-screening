import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class MenuService {
  canadaMenuURLEn =
    'https://www.canada.ca/content/dam/canada/sitemenu/sitemenu-v2-en.html';
  canadaMenuURLFr =
    'https://www.canada.ca/content/dam/canada/sitemenu/sitemenu-v2-fr.html';

  constructor(private http: HttpClient) {}

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(operation, error); // log to console instead
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /**
   * Send a GET request to get a list of addresses from backend
   * @returns a list of addresses of buildings
   */
  getMenu(lang: string): Observable<any> {
    const langBasedUrl =
      lang === 'fr' ? this.canadaMenuURLFr : this.canadaMenuURLEn;
    return this.http
      .get(langBasedUrl, { responseType: 'text' })
      .pipe(catchError(this.handleError<any>('getAddresses')));
  }
}

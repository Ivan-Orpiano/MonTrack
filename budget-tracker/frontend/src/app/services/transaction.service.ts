import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Transaction, TransactionRequest} from '../models/transaction.model';
import {TransactionSummary} from '../models/transaction-summary.model';
import {TransactionFilter} from '../models/transaction-filter.model';



@Injectable({providedIn: 'root'})
export class TransactionService {
    private readonly baseUrl = `${environment.apiUrl}/transactions`;

    constructor(private http: HttpClient) {}


    getTransactions(filter?: TransactionFilter): Observable <Transaction[]>{
        return this.http.get<Transaction[]>(this.baseUrl, {params: this.buildParams
        (filter)});
    }

    getTransactionById(id: string): Observable <Transaction> {
        return this.http.get<Transaction>(`${this.baseUrl}/${id}`);
    }

    

}
import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {filter, Observable} from 'rxjs';
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

    createTransaction(transaction: TransactionRequest): Observable<Transaction>{
        return this.http.post<Transaction>(this.baseUrl, transaction);
    }

    updateTransaction(id: string, transaction: TransactionRequest): Observable<Transaction>{
        return this.http.put<Transaction>{`${this.baseUrl}/${id}`, transaction};
    }

    deleteTransaction(id:string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    getSummary(filter?: TransactionFilter): Observable<TransactionSummary>{
        return this.http.get<TransactionSummary>(`${this.baseUrl}/summary`, {params: this.buildParamas(filter)});
    }

    private buildParams(filter?: TranasctionFilter): HttpParams {
        let params = new HttpParams();

        if(!filter) {
            return params;
        }
        if (filter.type) {
            params = params.set('type', filter.type);
        }
        if (filter.category) {
            params = params.set('category', filter.category);
        }
        if (filter.startDate) {
            params = params.set('startDate', filter.startDate);
        }
        if(filter.endDate) {
            params = params.set('endDate', filter.endDate);
        }
        return params
    }

}
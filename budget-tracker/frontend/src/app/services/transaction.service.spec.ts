import {TestBed} from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient   } from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Transaction } from '../models/transaction.model';
import { TransactionService } from './transaction.service';

/** INITIALIZATION OF TRANSACTION SERVICES */

describe('TransactionService', () => {
    let service: TransactionService;
    let httpMock: HttpTestingController;
    const baseUrl = `${environment.apiUrl}/transactions`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [provideHttpClient(), provideHttpClientTesting()]
        });
        service = TestBed.inject(TransactionService);
        httpMock = TestBed.inject(HttpTestingController);
    });
    afterEach(() => {
        httpMock.verify();
    });


    /** FETCHING ALL TRANSACTION USING GET METHOD */
    it('should fetch all transactions', () => {
        const mockTransactions: Transaction[] = [
            {id: '1', date: '2026-01-10', type: 'INCOME', category: 'Salary', description: 'Pay', amount: 1000}
        ];

        service.getTransactions().subscribe((transactions)=> {
            expect(transactions).toEqual(mockTransactions);
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('GET');
        req.flush(mockTransactions);
        });

/** Required filter for query parameters*/
    it('should apply filters as query params', () => {
        service.getTransactions({ type: 'EXPENSE', category: 'Food' }).subscribe();

        const req = httpMock.expectOne(
        (request) => request.url === baseUrl && request.params.get('type') === 'EXPENSE' && request.params.get('category') === 'Food'
        );
        expect(req.request.method).toBe('GET');
        req.flush([]);
    });

    /** POST METHOD TO ADD UPDATED OR LATEST TRANSACTIONS */
                
    it('should post a new transaction', () => {
        const newTransaction = {date: '2026-01-10', type: 'EXPENSE' as const, category: 'Food', description: 'Lunch', amount: 15};

        service.createTransaction(newTransaction).subscribe();

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(newTransaction);
        req.flush({id: '2', ...newTransaction});
    });

    /** UPDATE A TRANSACTION STATUS */
    it('should update a transaction by id', () => {
        const updated = {date: '2026-01-11', type: 'EXPENSE' as const, category: 'Food', description: 'Dinner', amount: 30};
    
        service.updateTransaction('1', updated).subscribe();

        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('PUT');
        req.flush({ id: '1', ...updated});
    });

    /** DELETE TRANSACTION STATUS */
    it('should delete a transaction by id', () =>{
        service.deleteTransaction('1').subscribe();
        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
    });

    /** FETCH ALL THE SUMMARY TRANSACTION */

    it('should fetch the summary', () => {
        service.getSummary().subscribe();

        const req = httpMock.expectOne(`${baseUrl}/summary`);
        expect(req.request.method).toBe('GET');
        req.flush({totalIncome:0, totalExpense: 0, balance: 0, transactionCount: 0});
    });
});

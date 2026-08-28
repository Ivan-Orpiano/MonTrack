import {Component,OnInit, signal} from '@angular/core';
import {RouterEvent, RouterLink} from '@angular/router';
import {CurrencyPipe, DatePipe} from '@angular/common';
import { TransactionService } from '../../services/transaction.service';
import { TransactionSummary } from '../../models/transaction-summary.model';
import { Transaction } from '../../models/transaction.model';
import { SummaryCardComponent } from '../../shared/components/summary-card/summary-card.component';

const RECENT_TRANSACTIONS_LIMIT = 5;

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [SummaryCardComponent, RouterLink, CurrencyPipe, DatePipe],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})

export class DashboardComponent implements OnInit {
    summary = signal <TransactionSummary | null >(null);
    recentTransactions = signal<Transaction []>([]);
    loading = signal(true);

    constructor(private transactionService: TransactionSerive) { }

    ngOnInit(): void {
        this.loadDashboard();
    }

    private loadDashboard(): void {
        this.loading.set(true);

        this.transactionService.getSummary().subscribe({
            next: (summary) => this.summary.set(summary),
            error: () => this.loading.set(false)
        });

        this.transactionService.getTransactions().subscribe({
            next:(transactions) => {
                this.recentTransactions.set(transactions.slice(0, RECENT_TRANSACTIONS_LIMIT));
                this.loading.set(false);
            }, error () => this.loading.set(false)
        });
    }
}











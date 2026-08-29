import { Component, OnInit, signal, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { TransactionService } from '../../services/transaction.service';
import { NotificationService } from '../../services/notification.service';
import { Transaction } from '../../models/transaction.model';
import { TransactionFilter } from '../../models/transaction-filter.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, CurrencyPipe, DatePipe, ConfirmDialogComponent],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.scss'
})
export class TransactionListComponent implements OnInit {
  private transactionService = inject(TransactionService);
  private notificationService = inject(NotificationService);
  private fb = inject(FormBuilder);

  transactions = signal<Transaction[]>([]);
  loading = signal(true);
  pendingDeleteId = signal<string | null>(null);

  filterForm = this.fb.group({
    type: [''],
    category: [''],
    startDate: [''],
    endDate: ['']
  });

  ngOnInit(): void {
    this.loadTransactions();

    this.filterForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b))
      )
      .subscribe(() => this.loadTransactions());
  }

  loadTransactions(): void {
    this.loading.set(true);
    const filter = this.filterForm.value as TransactionFilter;
    this.transactionService.getTransactions(filter).subscribe({
      next: (transactions) => {
        this.transactions.set(transactions);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  resetFilters(): void {
    this.filterForm.reset({ type: '', category: '', startDate: '', endDate: '' });
  }

  requestDelete(id: string): void {
    this.pendingDeleteId.set(id);
  }

  cancelDelete(): void {
    this.pendingDeleteId.set(null);
  }

  confirmDelete(): void {
    const id = this.pendingDeleteId();
    if (!id) {
      return;
    }
    this.transactionService.deleteTransaction(id).subscribe({
      next: () => {
        this.notificationService.success('Transaction deleted.');
        this.pendingDeleteId.set(null);
        this.loadTransactions();
      },
      error: () => this.pendingDeleteId.set(null)
    });
  }
}
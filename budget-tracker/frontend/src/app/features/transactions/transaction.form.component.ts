import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransactionService } from '../../services/transaction.service';
import { NotificationService } from '../../services/notification.service';
import { TransactionRequest, TransactionType } from '../../models/transaction.model';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './transaction-form.component.html',
  styleUrl: './transaction-form.component.scss'
})
export class TransactionFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private transactionService = inject(TransactionService);
  private notificationService = inject(NotificationService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isEditMode = signal(false);
  saving = signal(false);
  private transactionId: string | null = null;

  readonly categories = [
    'Salary',
    'Freelance',
    'Investments',
    'Housing',
    'Utilities',
    'Groceries',
    'Transportation',
    'Entertainment',
    'Health',
    'Other'
  ];

  form = this.fb.group({
    date: [this.today(), [Validators.required]],
    type: ['EXPENSE', [Validators.required]],
    category: ['', [Validators.required, Validators.maxLength(50)]],
    description: ['', [Validators.required, Validators.maxLength(200)]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]]
  });

  ngOnInit(): void {
    this.transactionId = this.route.snapshot.paramMap.get('id');
    if (this.transactionId) {
      this.isEditMode.set(true);
      this.transactionService.getTransactionById(this.transactionId).subscribe({
        next: (t) =>
          this.form.patchValue({
            date: t.date,
            type: t.type,
            category: t.category,
            description: t.description,
            amount: t.amount
          })
      });
    }
  }

  get f() {
    return this.form.controls;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: TransactionRequest = {
      date: value.date!,
      type: value.type as TransactionType,
      category: value.category!.trim(),
      description: value.description!.trim(),
      amount: Number(value.amount)
    };

    this.saving.set(true);
    const request$ =
      this.isEditMode() && this.transactionId
        ? this.transactionService.updateTransaction(this.transactionId, payload)
        : this.transactionService.createTransaction(payload);

    request$.subscribe({
      next: () => {
        this.notificationService.success(this.isEditMode() ? 'Transaction updated.' : 'Transaction added.');
        this.router.navigate(['/transactions']);
      },
      error: () => this.saving.set(false)
    });
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
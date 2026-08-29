import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { TransactionListComponent } from './features/transactions/transaction-list/transaction-list.component';
import { TransactionFormComponent } from './features/transactions/transaction-form/transaction-form.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent, title: 'Dashboard · Budget Tracker' },
  { path: 'transactions', component: TransactionListComponent, title: 'Transactions · Budget Tracker' },
  { path: 'transactions/new', component: TransactionFormComponent, title: 'Add Transaction · Budget Tracker' },
  { path: 'transactions/:id/edit', component: TransactionFormComponent, title: 'Edit Transaction · Budget Tracker' },
  { path: '**', redirectTo: 'dashboard' }
];
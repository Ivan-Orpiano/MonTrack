import { TransactionType } from './transaction.model';

export interface TransactionFilter {
  type?: TransactionType | '' | null;
  category?: string | null;
  startDate?: string | null;
  endDate?: string | null;
}
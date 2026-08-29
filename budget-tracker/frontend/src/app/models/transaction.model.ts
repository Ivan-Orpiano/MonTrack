export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
    id: string;
    date: string;
    type: TransactionType;
    category: string;
    description: string;
    amount: number;
}
export type TransactionRequest = Omit<Transaction, 'id'>;
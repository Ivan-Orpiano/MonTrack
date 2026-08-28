import {Component, input, Input} from '@angular/core';
import {CurrencyPipe} from '@angular/common';

export type SummaryCardVariant = 'income' | 'expense' | 'balance' | 'count';

@Component({
    selector: 'app-summary-card',
    standalone: true,
    imports: [CurrencyPipe],
    templateUrl: './summary-card.component.html',
    styleUrl: './summary-card.component.scss'
})
export class SummaryCardComponent{
    @Input({required: true}) label!: string;
    @Input({required: true}) value!: number;
    @Input() variant: SummaryCardVariant = 'balance';
    @Input() isCurrency = true;
}
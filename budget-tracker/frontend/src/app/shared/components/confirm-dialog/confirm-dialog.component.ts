import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-confirm-dialog',
    standalone: true,
    templateUrl: './confirm-dialog.component.html',
    styleUrl: './confirm-dialog.component.scss'
})

export class ConfirmDialogComponent{
    @Input() visible = false;
    @Input() title = 'Confirm';
    @Input() message = 'Are you sure? ';
    @Input() confirmLabel = 'Delete';

    @Output() confirmed = new EventEmitter<void>();
    @Output() cancelled = new EventEmitter<void>();

    onCofirm(): void{
        this.confirmed.emit();
    }

    onCancel(): void{
        this.cancelled.emit();
    }
}




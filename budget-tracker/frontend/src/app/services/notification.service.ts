import {Injectable, signal} from '@angular/core';

export type NotificationType = 'success' | 'error' | 'info';

export interface Notification {
    id : number;
    message: string;
    type: NotificationType;
}

@Injectable({providedIn: 'root'})
export class NotificationService {
    private nextId = 0;
    readonly notifications = signal<Notification[]>([]);

    success(message: string): void{
        this.push(message, 'success');
    }

    error(message: string): void {
        this.push(message, 'error');
    }

    info(message: string): void {
        this.push(message, 'info');
    }

    dismiss(id: number): void {
        this.notifications.update((list) => list.filter((n) => n.id !== id));
    }

    private push(message: string, type: NotificationType): void {
        const id = this.nextId++;
        this.notifications.update((list) => [...list, {id,message, type}]);
        setTimeout(()=> this.dismiss(id), 4000);
    }
}

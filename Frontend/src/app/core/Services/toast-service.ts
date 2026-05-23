import { Injectable, signal } from '@angular/core';
interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info';
  visible: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private counter = 0;

  toasts = signal<Toast[]>([]);

  show(message: string, type: Toast['type'] = 'info') {
    const id = this.counter++;

    this.toasts.update((prev) => [...prev, { id, message, type, visible: true }]);

    // start removing after 3s
    setTimeout(() => {
      this.hide(id);
    }, 3000);
  }

  hide(id: number) {
    // step 1: trigger animation
    this.toasts.update((prev) => prev.map((t) => (t.id === id ? { ...t, visible: false } : t)));

    // step 2: remove after animation ends
    setTimeout(() => {
      this.toasts.update((prev) => prev.filter((t) => t.id !== id));
    }, 300); // match CSS duration
  }
}

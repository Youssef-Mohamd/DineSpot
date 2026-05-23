import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ToastService } from '../../core/Services/toast-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../core/Auth/services/authentication-service';

@Component({
  selector: 'app-login-page',
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  toast = inject(ToastService);
  form = inject(FormBuilder);
  auth = inject(AuthenticationService);
  router = inject(Router);

  showPassword = signal(false);
  togglePassword() {
    this.showPassword.update((v) => !v);
  }

  loginForm: FormGroup = this.form.group({
    email: ['', [Validators.required, Validators.email]],
    password: [
      '',
      [
        Validators.required,
        Validators.pattern(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/),
      ],
    ],
  });

  loggingIn = signal<boolean>(false);
  submitLogin() {
    this.loggingIn.set(true);
    if (this.loginForm.valid) {
      this.auth.login(this.loginForm.value).subscribe({
        next: (res) => {
          this.auth.setSession(res);
          this.loggingIn.set(false);
          this.toast.show('logged In succesfully', 'success');
          if (res.role === 'ADMIN') {
            this.router.navigate(['/dashboard']);
          } else {
            this.router.navigate(['/home']);
          }
        },
        error: (err) => {
          console.log(err);
          this.loginForm.reset();
          this.loggingIn.set(false);
          this.toast.show('something went wrong', 'error');
        },
      });
    } else {
      this.loggingIn.set(false);
      this.loginForm.markAllAsTouched();
    }
  }
}

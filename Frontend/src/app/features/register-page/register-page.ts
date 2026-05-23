import { Component, inject, signal } from '@angular/core';
import { ToastService } from '../../core/Services/toast-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../core/Auth/services/authentication-service';

@Component({
  selector: 'app-register-page',
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './register-page.html',
  styleUrl: './register-page.css',
})
export class RegisterPage {
  toast = inject(ToastService);
  form = inject(FormBuilder);
  auth = inject(AuthenticationService)
  router = inject(Router)

  showPassword = signal(false);
  togglePassword() {
    this.showPassword.update((v) => !v);
  }

  signUpForm: FormGroup = this.form.group({
    fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email]],
    password: [
      '',
      [
        Validators.required,
        Validators.pattern(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/),
      ],
    ],
    phone: ['', [Validators.required, Validators.pattern(/^01[0125][0-9]{8}$/)]],
  });

  creatingAcc = signal<boolean>(false)
  createAccount() {
    this.creatingAcc.set(true)
    if (this.signUpForm.valid) {
      this.auth.register(this.signUpForm.value).subscribe({
        next:(res)=>{
          console.log(res)
          this.router.navigate(['/login'])
          this.creatingAcc.set(false)
          this.toast.show('Account Created Succefully' , 'success')
        },
        error:(err)=>{
          console.log(err)
          this.signUpForm.reset()
          this.creatingAcc.set(false)
          this.toast.show('Something Wrong happend. Please try again' , 'error')
        }
      })
    } else {
      this.signUpForm.markAllAsTouched();
      this.creatingAcc.set(false)
    }
  }
}
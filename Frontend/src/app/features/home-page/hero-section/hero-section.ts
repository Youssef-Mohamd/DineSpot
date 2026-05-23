import { AfterViewInit, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FlowBiteService } from '../../../core/Services/flow-bite-service';

@Component({
  selector: 'app-hero-section',
  imports: [RouterLink],
  templateUrl: './hero-section.html',
  styleUrl: './hero-section.css',
})
export class HeroSection implements AfterViewInit {
  constructor(private flowbiteService: FlowBiteService) {}

  ngAfterViewInit(): void {
    this.flowbiteService.loadFlowbite((flowbite) => {
      flowbite.initFlowbite();
    });
  }
}

import { AboutUsSection } from './about-us-section/about-us-section';
import { Component } from '@angular/core';
import { FeaturedSection } from './featured-section/featured-section';
import { HeroSection } from './hero-section/hero-section';
import { TopRatedSection } from './top-rated-section/top-rated-section';
import { ContactUsSection } from './contact-us-section/contact-us-section';

@Component({
  selector: 'app-home-page',
  imports: [AboutUsSection, FeaturedSection, HeroSection, TopRatedSection, ContactUsSection],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage {}

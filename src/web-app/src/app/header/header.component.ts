import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslocoService } from '@ngneat/transloco';
import { MenuService } from '../menu.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  constructor(
    private router: Router,
    private menuService: MenuService,
    private transloco: TranslocoService
  ) {}
  selectedLang = 'en';
  navMenuContent = '<p>Content not loaded</p>';

  ngOnInit(): void {
    this.transloco.langChanges$.subscribe((lang) => {
      this.selectedLang = lang;
      this.menuService.getMenu(this.selectedLang).subscribe((data: any) => {
        this.navMenuContent = data;
      });
    });
  }

  scrollToElement($element: string): void {
    var el = document.getElementById($element);
    el &&
      el.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
        inline: 'nearest',
      });
  }

  public setActiveLang(lang: string) {
    this.router.navigate(['/' + lang]).then(() => {
      window.location.reload();
    });
  }
}

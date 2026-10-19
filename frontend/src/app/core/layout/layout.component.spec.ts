
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { LayoutComponent } from './layout.component';

describe('LayoutComponent', () => {

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutComponent],
      providers: [provideRouter([])]
    }).compileComponents();
  });

  it('should create the layout', () => {
    const fixture =
      TestBed.createComponent(LayoutComponent);

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render navigation', () => {
    const fixture =
      TestBed.createComponent(LayoutComponent);

    fixture.detectChanges();

    const compiled =
      fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('nav')).not.toBeNull();

    expect(
      compiled.querySelector('router-outlet')
    ).not.toBeNull();
  });

});

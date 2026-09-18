import { Injectable, inject, PLATFORM_ID, OnDestroy } from '@angular/core';
import { isPlatformBrowser, DOCUMENT } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { BehaviorSubject, filter, Subscription } from 'rxjs';

export interface BeforeInstallPromptEvent extends Event {
  readonly platforms: string[];
  readonly userChoice: Promise<{
    outcome: 'accepted' | 'dismissed';
    platform: string;
  }>;
  prompt(): Promise<void>;
}

@Injectable({
  providedIn: 'root'
})
export class PwaService implements OnDestroy {
  private readonly document = inject(DOCUMENT);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);

  private deferredPrompt: BeforeInstallPromptEvent | null = null;
  private readonly canInstallSubject = new BehaviorSubject<boolean>(false);
  readonly canInstall$ = this.canInstallSubject.asObservable();

  private readonly isInstalledSubject = new BehaviorSubject<boolean>(false);
  readonly isInstalled$ = this.isInstalledSubject.asObservable();

  private sub: Subscription | null = null;
  private beforeInstallHandler: ((e: Event) => void) | null = null;
  private appInstalledHandler: (() => void) | null = null;

  constructor() {
    if (!isPlatformBrowser(this.platformId)) return;

    this.checkIfAlreadyInstalled();
    this.setupInstallPromptListener();
    this.setupRouteListener();
  }

  private checkIfAlreadyInstalled(): void {
    if (typeof window === 'undefined') return;
    const isStandalone = (typeof window.matchMedia === 'function' && window.matchMedia('(display-mode: standalone)').matches) ||
      (window.navigator as unknown as { standalone?: boolean })?.standalone === true;
    this.isInstalledSubject.next(Boolean(isStandalone));
  }

  private setupInstallPromptListener(): void {
    if (typeof window === 'undefined' || typeof window.addEventListener !== 'function') return;

    this.beforeInstallHandler = (e: Event) => {
      // Impede o prompt automático fora do escopo desejado
      e.preventDefault();
      this.deferredPrompt = e as BeforeInstallPromptEvent;

      if (this.isAdminRoute(this.router.url)) {
        this.canInstallSubject.next(true);
      }
    };

    this.appInstalledHandler = () => {
      this.deferredPrompt = null;
      this.canInstallSubject.next(false);
      this.isInstalledSubject.next(true);
    };

    window.addEventListener('beforeinstallprompt', this.beforeInstallHandler);
    window.addEventListener('appinstalled', this.appInstalledHandler);
  }

  private setupRouteListener(): void {
    // Avalia a rota inicial
    this.syncManifestWithRoute(this.router.url);

    this.sub = this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.syncManifestWithRoute(event.urlAfterRedirects || event.url);
      });
  }

  private syncManifestWithRoute(url: string): void {
    if (!isPlatformBrowser(this.platformId) || !this.document) return;

    const isAdmin = this.isAdminRoute(url);
    const existingManifest = this.document.getElementById('pwa-manifest') as HTMLLinkElement | null;

    if (isAdmin) {
      if (!existingManifest && this.document.head) {
        const link = this.document.createElement('link');
        link.id = 'pwa-manifest';
        link.rel = 'manifest';
        link.href = '/manifest.webmanifest';
        this.document.head.appendChild(link);
      }
      if (this.deferredPrompt) {
        this.canInstallSubject.next(true);
      }
    } else {
      if (existingManifest) {
        existingManifest.remove();
      }
      this.canInstallSubject.next(false);
    }
  }

  private isAdminRoute(url: string): boolean {
    if (!url) return false;
    const cleanUrl = url.split('?')[0].split('#')[0];
    return cleanUrl.startsWith('/admin') || cleanUrl.startsWith('/login-cms');
  }

  async promptInstall(): Promise<'accepted' | 'dismissed' | 'unavailable'> {
    if (!this.deferredPrompt) {
      return 'unavailable';
    }

    try {
      await this.deferredPrompt.prompt();
      const choice = await this.deferredPrompt.userChoice;
      this.deferredPrompt = null;
      this.canInstallSubject.next(false);
      return choice.outcome;
    } catch {
      return 'unavailable';
    }
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    if (typeof window !== 'undefined' && typeof window.removeEventListener === 'function') {
      if (this.beforeInstallHandler) {
        window.removeEventListener('beforeinstallprompt', this.beforeInstallHandler);
      }
      if (this.appInstalledHandler) {
        window.removeEventListener('appinstalled', this.appInstalledHandler);
      }
    }
  }
}

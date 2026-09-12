import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContentService } from '../../../../services/content.service';
import { SiteContent } from '../../../../models/content.model';
import { DEFAULT_SITE_CONTENT } from '../../../../core/constants/content-fallbacks';
import { ToastService } from '../../../../services/toast.service';
import { finalize, Subscription } from 'rxjs';

@Component({
  selector: 'app-content',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.css']
})
export class ContentComponent implements OnInit, OnDestroy {
  contentForm!: FormGroup;
  isSaving = false;
  openSection: string | null = null;

  private readonly fb = inject(FormBuilder);
  private readonly contentService = inject(ContentService);
  private readonly toastService = inject(ToastService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly subs = new Subscription();

  constructor() {}

  ngOnInit(): void {
    this.initForm();
    this.loadContent();
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  toggleSection(section: string): void {
    this.openSection = this.openSection === section ? null : section;
    this.cdr.markForCheck();
  }

  private initForm(): void {
    this.contentForm = this.fb.group({
      banner: this.fb.group({
        selo: ['', [Validators.maxLength(100)]],
        titulo: ['', [Validators.maxLength(150)]],
        subtitulo: ['', [Validators.maxLength(150)]],
        descricao: ['', [Validators.maxLength(500)]],
        indicadores: this.fb.array([])
      }),
      diferenciais: this.fb.group({
        selo: ['', [Validators.maxLength(100)]],
        titulo: ['', [Validators.maxLength(150)]],
        descricao: ['', [Validators.maxLength(500)]],
        cards: this.fb.array([])
      }),
      catalogo: this.fb.group({
        selo: ['', [Validators.maxLength(100)]],
        descricao: ['', [Validators.maxLength(500)]]
      }),
      sobre: this.fb.group({
        selo: ['', [Validators.maxLength(100)]],
        titulo: ['', [Validators.maxLength(150)]],
        descricao: ['', [Validators.maxLength(1000)]],
        lista: this.fb.array([])
      }),
      estatisticas: this.fb.group({
        lista: this.fb.array([])
      }),
      cta: this.fb.group({
        selo: ['', [Validators.maxLength(100)]],
        titulo: ['', [Validators.maxLength(150)]],
        descricao: ['', [Validators.maxLength(500)]]
      }),
      rodape: this.fb.group({
        descricao: ['', [Validators.maxLength(300)]],
        textoContato: ['', [Validators.maxLength(150)]],
        textoDireitos: ['', [Validators.maxLength(150)]]
      }),
      dados: this.fb.group({
        endereco: ['', [Validators.maxLength(250)]],
        horarioAbertura: [''],
        horarioFechamento: [''],
        diasFuncionamento: ['', [Validators.maxLength(100)]],
        whatsapp: ['', [Validators.maxLength(30)]],
        cnpj: ['', [Validators.pattern(/^[A-Z0-9]{2}\.[A-Z0-9]{3}\.[A-Z0-9]{3}\/[A-Z0-9]{4}-\d{2}$/)]]
      }),
      faq: this.fb.group({
        itens: this.fb.array([])
      })
    });
  }

  readonly defaultBanner = DEFAULT_SITE_CONTENT.banner;
  readonly defaultBannerIndicadores = DEFAULT_SITE_CONTENT.banner.indicadores;
  readonly defaultDiferenciais = DEFAULT_SITE_CONTENT.diferenciais;
  readonly defaultDiferenciaisCards = DEFAULT_SITE_CONTENT.diferenciais.cards;
  readonly defaultCatalogo = DEFAULT_SITE_CONTENT.catalogo;
  readonly defaultSobre = DEFAULT_SITE_CONTENT.sobre;
  readonly defaultSobreLista = DEFAULT_SITE_CONTENT.sobre.lista;
  readonly defaultEstatisticas = DEFAULT_SITE_CONTENT.estatisticas;
  readonly defaultEstatisticasLista = DEFAULT_SITE_CONTENT.estatisticas.lista;
  readonly defaultCta = DEFAULT_SITE_CONTENT.cta;
  readonly defaultRodape = DEFAULT_SITE_CONTENT.rodape;
  readonly defaultDados = DEFAULT_SITE_CONTENT.dados;
  readonly defaultFaqItens = DEFAULT_SITE_CONTENT.faq!.itens;

  private getMergedFaqItems(serverItens?: any[]): any[] {
    const serverMap = new Map<string, any>();
    if (serverItens && Array.isArray(serverItens)) {
      serverItens.forEach(it => {
        if (it?.id) serverMap.set(it.id, it);
      });
    }

    return this.defaultFaqItens.map(def => {
      const serverItem = serverMap.get(def.id);
      return {
        id: def.id,
        pergunta: def.pergunta,
        resposta: serverItem?.resposta && serverItem.resposta.trim() ? serverItem.resposta.trim() : ''
      };
    });
  }

  private populateBannerIndicadores(serverIndicadores?: any[]): void {
    this.bannerIndicadores.clear();
    for (let i = 0; i < 3; i++) {
      const serverInd = serverIndicadores && serverIndicadores[i];
      this.bannerIndicadores.push(this.createIndicador({
        nome: serverInd?.nome ?? '',
        valor: serverInd?.valor ?? ''
      }));
    }
  }

  private populateDiferenciaisCards(serverCards?: any[]): void {
    this.diferenciaisCards.clear();
    for (let i = 0; i < 3; i++) {
      const serverCard = serverCards && serverCards[i];
      this.diferenciaisCards.push(this.createCard({
        titulo: serverCard?.titulo ?? '',
        texto: serverCard?.texto ?? ''
      }));
    }
  }

  private populateSobreLista(serverLista?: any[]): void {
    this.sobreLista.clear();
    if (serverLista && Array.isArray(serverLista) && serverLista.length > 0) {
      serverLista.forEach(item => this.sobreLista.push(this.createDescricaoItem({
        nome: item?.nome ?? '',
        descricao: item?.descricao ?? ''
      })));
    } else {
      for (let i = 0; i < this.defaultSobreLista.length; i++) {
        this.sobreLista.push(this.createDescricaoItem({ nome: '', descricao: '' }));
      }
    }
  }

  private populateEstatisticasLista(serverLista?: any[]): void {
    this.estatisticasLista.clear();
    if (serverLista && Array.isArray(serverLista) && serverLista.length > 0) {
      serverLista.forEach(est => this.estatisticasLista.push(this.createIndicador({
        nome: est?.nome ?? '',
        valor: est?.valor ?? ''
      })));
    } else {
      for (let i = 0; i < this.defaultEstatisticasLista.length; i++) {
        this.estatisticasLista.push(this.createIndicador({ nome: '', valor: '' }));
      }
    }
  }

  addSobreItem(): void {
    this.sobreLista.push(this.createDescricaoItem({ nome: '', descricao: '' }));
    this.cdr.markForCheck();
  }

  removeSobreItem(index: number): void {
    if (this.sobreLista.length > 1) {
      this.sobreLista.removeAt(index);
      this.cdr.markForCheck();
    }
  }

  addEstatistica(): void {
    this.estatisticasLista.push(this.createIndicador({ nome: '', valor: '' }));
    this.cdr.markForCheck();
  }

  removeEstatistica(index: number): void {
    if (this.estatisticasLista.length > 1) {
      this.estatisticasLista.removeAt(index);
      this.cdr.markForCheck();
    }
  }

  private loadContent() {
    this.contentService.getContent().subscribe({
      next: (data: SiteContent | null) => {
        this.clearFormArrays();

        const mergedFaq = this.getMergedFaqItems(data?.faq?.itens);
        mergedFaq.forEach(item => this.faqItens.push(this.createFaqItem(item)));

        this.populateBannerIndicadores(data?.banner?.indicadores);
        this.populateDiferenciaisCards(data?.diferenciais?.cards);
        this.populateSobreLista(data?.sobre?.lista);
        this.populateEstatisticasLista(data?.estatisticas?.lista);

        if (!data) {
          this.cdr.markForCheck();
          return;
        }

        const { faq, banner, diferenciais, sobre, estatisticas, ...otherData } = data;
        this.contentForm.patchValue(otherData);

        if (banner) {
          this.contentForm.get('banner')?.patchValue({
            selo: banner.selo ?? '',
            titulo: banner.titulo ?? '',
            subtitulo: banner.subtitulo ?? '',
            descricao: banner.descricao ?? ''
          });
        }

        if (diferenciais) {
          this.contentForm.get('diferenciais')?.patchValue({
            selo: diferenciais.selo ?? '',
            titulo: diferenciais.titulo ?? '',
            descricao: diferenciais.descricao ?? ''
          });
        }

        if (sobre) {
          this.contentForm.get('sobre')?.patchValue({
            selo: sobre.selo ?? '',
            titulo: sobre.titulo ?? '',
            descricao: sobre.descricao ?? ''
          });
        }

        this.cdr.markForCheck();
      },
      error: (err) => {
        console.warn('Não foi possível carregar o conteúdo do site:', err);
        this.clearFormArrays();
        const mergedFaq = this.getMergedFaqItems();
        mergedFaq.forEach(item => this.faqItens.push(this.createFaqItem(item)));
        this.populateBannerIndicadores();
        this.populateDiferenciaisCards();
        this.populateSobreLista();
        this.populateEstatisticasLista();
        this.cdr.markForCheck();
      }
    });
  }

  private clearFormArrays(): void {
    this.bannerIndicadores.clear();
    this.diferenciaisCards.clear();
    this.sobreLista.clear();
    this.estatisticasLista.clear();
    this.faqItens.clear();
  }

  get bannerIndicadores(): FormArray { return this.contentForm.get('banner.indicadores') as FormArray; }
  get diferenciaisCards(): FormArray { return this.contentForm.get('diferenciais.cards') as FormArray; }
  get sobreLista(): FormArray { return this.contentForm.get('sobre.lista') as FormArray; }
  get estatisticasLista(): FormArray { return this.contentForm.get('estatisticas.lista') as FormArray; }
  get faqItens(): FormArray { return this.contentForm.get('faq.itens') as FormArray; }

  private createIndicador(item?: any): FormGroup {
    return this.fb.group({
      nome: [item?.nome || '', [Validators.maxLength(100)]],
      valor: [item?.valor || '', [Validators.maxLength(50)]]
    });
  }

  private createCard(item?: any): FormGroup {
    return this.fb.group({
      titulo: [item?.titulo || '', [Validators.maxLength(100)]],
      texto: [item?.texto || '', [Validators.maxLength(300)]]
    });
  }

  private createDescricaoItem(item?: any): FormGroup {
    return this.fb.group({
      nome: [item?.nome || '', [Validators.maxLength(100)]],
      descricao: [item?.descricao || '', [Validators.maxLength(300)]]
    });
  }

  private createFaqItem(item?: any): FormGroup {
    const defaultItem = this.defaultFaqItens.find(d => d.id === item?.id);
    return this.fb.group({
      id: [item?.id || defaultItem?.id || ''],
      pergunta: [item?.pergunta || defaultItem?.pergunta || ''],
      resposta: [item?.resposta || defaultItem?.resposta || '', [Validators.maxLength(1000)]]
    });
  }

  private normalizePayload(obj: any): any {
    if (obj === null || obj === undefined) {
      return null;
    }
    if (typeof obj === 'string') {
      const trimmed = obj.trim();
      return trimmed === '' ? null : trimmed;
    }
    if (Array.isArray(obj)) {
      return obj.map(v => this.normalizePayload(v));
    }
    if (typeof obj === 'object') {
      const res: any = {};
      for (const key in obj) {
        res[key] = this.normalizePayload(obj[key]);
      }
      return res;
    }
    return obj;
  }

  saveContent(): void {
    const cnpjCtrl = this.contentForm.get('dados.cnpj');
    if (cnpjCtrl?.value && cnpjCtrl.value.trim() && cnpjCtrl.invalid) {
      this.toastService.error('Formato de CNPJ inválido. Ex: 00.000.000/0000-00');
      this.cdr.markForCheck();
      return;
    }

    this.isSaving = true;
    this.cdr.markForCheck();
    
    const rawData = this.contentForm.value;
    const contentData = this.normalizePayload(rawData) || {};
    
    // Conforme arquitetura de segurança, trafega apenas { id, resposta } para o FAQ
    if (this.faqItens.length > 0) {
      contentData.faq = {
        itens: this.faqItens.controls.map(ctrl => {
          const rawResp = ctrl.get('resposta')?.value;
          return {
            id: ctrl.get('id')?.value,
            resposta: rawResp && rawResp.trim() ? rawResp.trim() : null
          };
        })
      };
    }

    // Garante que banner.indicadores envie os 3 indicadores (com valores null se vazios)
    if (this.bannerIndicadores.length > 0) {
      if (!contentData.banner) contentData.banner = {};
      contentData.banner.indicadores = this.bannerIndicadores.controls.map(ctrl => {
        const n = ctrl.get('nome')?.value;
        const v = ctrl.get('valor')?.value;
        return {
          nome: n && n.trim() ? n.trim() : null,
          valor: v && v.trim() ? v.trim() : null
        };
      });
    }

    // Garante que diferenciais.cards envie os 3 cards (com valores null se vazios)
    if (this.diferenciaisCards.length > 0) {
      if (!contentData.diferenciais) contentData.diferenciais = {};
      contentData.diferenciais.cards = this.diferenciaisCards.controls.map(ctrl => {
        const t = ctrl.get('titulo')?.value;
        const txt = ctrl.get('texto')?.value;
        return {
          titulo: t && t.trim() ? t.trim() : null,
          texto: txt && txt.trim() ? txt.trim() : null
        };
      });
    }

    // Garante que sobre.lista envie todos os itens
    if (this.sobreLista.length > 0) {
      if (!contentData.sobre) contentData.sobre = {};
      contentData.sobre.lista = this.sobreLista.controls.map(ctrl => {
        const n = ctrl.get('nome')?.value;
        const d = ctrl.get('descricao')?.value;
        return {
          nome: n && n.trim() ? n.trim() : null,
          descricao: d && d.trim() ? d.trim() : null
        };
      });
    }

    // Garante que estatisticas.lista envie todos os indicadores
    if (this.estatisticasLista.length > 0) {
      if (!contentData.estatisticas) contentData.estatisticas = {};
      contentData.estatisticas.lista = this.estatisticasLista.controls.map(ctrl => {
        const n = ctrl.get('nome')?.value;
        const v = ctrl.get('valor')?.value;
        return {
          nome: n && n.trim() ? n.trim() : null,
          valor: v && v.trim() ? v.trim() : null
        };
      });
    }
    
    this.subs.add(
      this.contentService.saveContent(contentData).pipe(
        finalize(() => {
          this.isSaving = false;
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: () => {
          this.toastService.success('Conteúdo salvo com sucesso!');
          this.contentForm.markAsPristine();
          this.cdr.markForCheck();
        },
        error: () => {
          this.toastService.error('Erro ao salvar o conteúdo. Tente novamente.');
          this.cdr.markForCheck();
        }
      })
    );
  }
}

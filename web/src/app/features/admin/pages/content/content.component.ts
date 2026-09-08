import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContentService } from '../../../../services/content.service';
import { SiteContent } from '../../../../models/content.model';
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
      })
    });
  }

  private loadContent(): void {
    this.subs.add(
      this.contentService.getContent().pipe(
        finalize(() => {
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: (data: SiteContent) => {
          this.clearFormArrays();
          data.banner.indicadores.forEach((ind: any) => this.bannerIndicadores.push(this.createIndicador(ind)));
          data.diferenciais.cards.forEach((card: any) => this.diferenciaisCards.push(this.createCard(card)));
          data.sobre.lista.forEach((item: any) => this.sobreLista.push(this.createDescricaoItem(item)));
          data.estatisticas.lista.forEach((est: any) => this.estatisticasLista.push(this.createIndicador(est)));
          this.contentForm.patchValue(data);
          this.cdr.markForCheck();
        },
        error: () => {
          this.toastService.error('Erro ao carregar os dados de conteúdo.');
          this.cdr.markForCheck();
        }
      })
    );
  }

  private clearFormArrays(): void {
    this.bannerIndicadores.clear();
    this.diferenciaisCards.clear();
    this.sobreLista.clear();
    this.estatisticasLista.clear();
  }

  get bannerIndicadores(): FormArray { return this.contentForm.get('banner.indicadores') as FormArray; }
  get diferenciaisCards(): FormArray { return this.contentForm.get('diferenciais.cards') as FormArray; }
  get sobreLista(): FormArray { return this.contentForm.get('sobre.lista') as FormArray; }
  get estatisticasLista(): FormArray { return this.contentForm.get('estatisticas.lista') as FormArray; }

  private createIndicador(item?: any): FormGroup {
    return this.fb.group({
      nome: [item?.nome || ''],
      valor: [item?.valor || '']
    });
  }

  private createCard(item?: any): FormGroup {
    return this.fb.group({
      titulo: [item?.titulo || ''],
      texto: [item?.texto || '']
    });
  }

  private createDescricaoItem(item?: any): FormGroup {
    return this.fb.group({
      nome: [item?.nome || ''],
      descricao: [item?.descricao || '']
    });
  }

  private cleanPayload(obj: any): any {
    if (Array.isArray(obj)) {
      const arr = obj.map(v => this.cleanPayload(v)).filter(v => v !== null && v !== undefined && v !== '');
      return arr;
    } else if (obj !== null && typeof obj === 'object') {
      const cleaned: any = {};
      for (const key in obj) {
        const val = this.cleanPayload(obj[key]);
        if (val !== null && val !== undefined && val !== '') {
          cleaned[key] = val;
        }
      }
      return Object.keys(cleaned).length > 0 ? cleaned : null;
    }
    return obj;
  }

  saveContent(): void {
    if (this.contentForm.invalid) {
      this.contentForm.markAllAsTouched();
      this.toastService.error('Preencha todos os campos corretamente antes de salvar.');
      this.cdr.markForCheck();
      return;
    }
    this.isSaving = true;
    this.cdr.markForCheck();
    
    const rawData = this.contentForm.value;
    const contentData = this.cleanPayload(rawData) || {};
    
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

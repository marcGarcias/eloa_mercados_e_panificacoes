import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContentService } from '../../../../services/content.service';
import { SiteContent } from '../../../../models/content.model';
import { ToastService } from '../../../../services/toast.service';

@Component({
  selector: 'app-content',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.css']
})
export class ContentComponent implements OnInit {
  contentForm!: FormGroup;
  isSaving = false;
  openSection: string | null = null;

  private fb = inject(FormBuilder);
  private contentService = inject(ContentService);
  private toastService = inject(ToastService);

  constructor() {}

  ngOnInit(): void {
    this.initForm();
    this.loadContent();
  }

  toggleSection(section: string) {
    this.openSection = this.openSection === section ? null : section;
  }

  private initForm() {
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

  private loadContent() {
    this.contentService.getContent().subscribe((data: SiteContent) => {
      this.clearFormArrays();
      data.banner.indicadores.forEach((ind: any) => this.bannerIndicadores.push(this.createIndicador(ind)));
      data.diferenciais.cards.forEach((card: any) => this.diferenciaisCards.push(this.createCard(card)));
      data.sobre.lista.forEach((item: any) => this.sobreLista.push(this.createDescricaoItem(item)));
      data.estatisticas.lista.forEach((est: any) => this.estatisticasLista.push(this.createIndicador(est)));
      this.contentForm.patchValue(data);
    });
  }

  private clearFormArrays() {
    this.bannerIndicadores.clear();
    this.diferenciaisCards.clear();
    this.sobreLista.clear();
    this.estatisticasLista.clear();
  }

  get bannerIndicadores() { return this.contentForm.get('banner.indicadores') as FormArray; }
  get diferenciaisCards() { return this.contentForm.get('diferenciais.cards') as FormArray; }
  get sobreLista() { return this.contentForm.get('sobre.lista') as FormArray; }
  get estatisticasLista() { return this.contentForm.get('estatisticas.lista') as FormArray; }

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
      // Retorna o array diretamente, mesmo se estiver vazio. 
      // Array vazio significa que o usuário excluiu todos os itens.
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

  saveContent() {
    if (this.contentForm.invalid) {
      this.contentForm.markAllAsTouched();
      this.toastService.error('Preencha todos os campos corretamente antes de salvar.');
      return;
    }
    this.isSaving = true;
    
    // Limpa o payload de strings vazias para o comportamento de PATCH
    const rawData = this.contentForm.value;
    const contentData = this.cleanPayload(rawData) || {};
    
    this.contentService.saveContent(contentData).subscribe({
      next: () => {
        this.isSaving = false;
        this.toastService.success('Conteúdo salvo com sucesso!');
      },
      error: () => {
        this.isSaving = false;
        this.toastService.error('Erro ao salvar o conteúdo. Tente novamente.');
      }
    });
  }
}

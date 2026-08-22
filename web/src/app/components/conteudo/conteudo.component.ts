import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContentService } from '../../services/content.service';
import { SiteContent } from '../../models/content.model';

@Component({
  selector: 'app-conteudo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './conteudo.component.html',
  styleUrls: ['./conteudo.component.css']
})
export class ConteudoComponent implements OnInit {
  contentForm!: FormGroup;
  isSaving = false;
  openSection: string | null = null;

  constructor(
    private fb: FormBuilder,
    private contentService: ContentService
  ) {}

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
        selo: ['', Validators.required],
        titulo: ['', Validators.required],
        subtitulo: ['', Validators.required],
        descricao: ['', Validators.required],
        indicadores: this.fb.array([])
      }),
      diferenciais: this.fb.group({
        selo: ['', Validators.required],
        titulo: ['', Validators.required],
        descricao: ['', Validators.required],
        cards: this.fb.array([])
      }),
      catalogo: this.fb.group({
        selo: ['', Validators.required],
        descricao: ['', Validators.required]
      }),
      sobre: this.fb.group({
        selo: ['', Validators.required],
        titulo: ['', Validators.required],
        descricao: ['', Validators.required],
        lista: this.fb.array([])
      }),
      estatisticas: this.fb.group({
        lista: this.fb.array([])
      }),
      cta: this.fb.group({
        selo: ['', Validators.required],
        titulo: ['', Validators.required],
        descricao: ['', Validators.required]
      }),
      rodape: this.fb.group({
        descricao: ['', Validators.required],
        textoContato: ['', Validators.required],
        textoDireitos: ['', Validators.required]
      }),
      dados: this.fb.group({
        endereco: ['', Validators.required],
        horarioAbertura: ['', Validators.required],
        horarioFechamento: ['', Validators.required],
        diasFuncionamento: ['', Validators.required],
        whatsapp: ['', Validators.required],
        cnpj: ['', Validators.required]
      })
    });
  }

  private loadContent() {
    this.contentService.getContent().subscribe(data => {
      this.clearFormArrays();
      data.banner.indicadores.forEach(ind => this.bannerIndicadores.push(this.createIndicador(ind)));
      data.diferenciais.cards.forEach(card => this.diferenciaisCards.push(this.createCard(card)));
      data.sobre.lista.forEach(item => this.sobreLista.push(this.createDescricaoItem(item)));
      data.estatisticas.lista.forEach(est => this.estatisticasLista.push(this.createIndicador(est)));
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
      nome: [item?.nome || '', Validators.required],
      valor: [item?.valor || '', Validators.required]
    });
  }

  private createCard(item?: any): FormGroup {
    return this.fb.group({
      titulo: [item?.titulo || '', Validators.required],
      texto: [item?.texto || '', Validators.required]
    });
  }

  private createDescricaoItem(item?: any): FormGroup {
    return this.fb.group({
      nome: [item?.nome || '', Validators.required],
      descricao: [item?.descricao || '', Validators.required]
    });
  }

  saveContent() {
    if (this.contentForm.invalid) {
      this.contentForm.markAllAsTouched();
      alert('Preencha todos os campos corretamente antes de salvar.');
      return;
    }
    this.isSaving = true;
    const contentData: SiteContent = this.contentForm.value;
    this.contentService.saveContent(contentData).subscribe(() => {
      this.isSaving = false;
      alert('Conteúdo salvo com sucesso!');
    });
  }
}

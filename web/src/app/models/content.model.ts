export interface ContentBanner {
  selo: string;
  titulo: string;
  subtitulo: string;
  descricao: string;
  indicadores: { nome: string; valor: string }[];
}

export interface ContentDiferenciais {
  selo: string;
  titulo: string;
  descricao: string;
  cards: { titulo: string; texto: string }[];
}

export interface ContentCatalogo {
  selo: string;
  descricao: string;
}

export interface ContentSobre {
  selo: string;
  titulo: string;
  descricao: string;
  lista: { nome: string; descricao: string }[];
}

export interface ContentEstatisticas {
  lista: { nome: string; valor: string }[];
}

export interface ContentCta {
  selo: string;
  titulo: string;
  descricao: string;
}

export interface ContentRodape {
  descricao: string;
  textoContato: string;
  textoDireitos: string;
}

export interface SiteData {
  endereco: string;
  horarioAbertura: string;
  horarioFechamento: string;
  diasFuncionamento: string;
  whatsapp: string;
  cnpj: string;
}

export interface SiteContent {
  banner: ContentBanner;
  diferenciais: ContentDiferenciais;
  catalogo: ContentCatalogo;
  sobre: ContentSobre;
  estatisticas: ContentEstatisticas;
  cta: ContentCta;
  rodape: ContentRodape;
  dados: SiteData;
}

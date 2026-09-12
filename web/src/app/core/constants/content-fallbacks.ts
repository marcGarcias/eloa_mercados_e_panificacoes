import { SiteContent } from '../../models/content.model';

export const DEFAULT_SITE_CONTENT: SiteContent = {
  banner: {
    selo: 'Feito com tradição todos os dias',
    titulo: 'Pães fresquinhos',
    subtitulo: 'com sabor de sempre.',
    descricao: 'Produzidos diariamente com ingredientes selecionados. Pães, bolos, doces, salgados e produtos de mercado — tudo em um só lugar para você e sua família.',
    indicadores: [
      { nome: 'Anos de Tradição', valor: '+10' },
      { nome: 'Produção Fresca', valor: 'Diária' },
      { nome: 'Feito com Amor', valor: '100%' }
    ]
  },
  diferenciais: {
    selo: 'Por que a Eloa?',
    titulo: 'Qualidade que você sente',
    descricao: 'Cada produto é preparado com cuidado e ingredientes frescos, para garantir o melhor sabor na sua mesa.',
    cards: [
      {
        titulo: 'Ingredientes Selecionados',
        texto: 'Usamos apenas farinha de qualidade, ovos frescos e ingredientes naturais para garantir o sabor autêntico de padaria.'
      },
      {
        titulo: 'Produção Diária',
        texto: 'Nossos pães saem do forno todos os dias, sempre frescos. Você recebe o que há de melhor, sem estoque velho.'
      },
      {
        titulo: 'Praticidade no Pedido',
        texto: 'Peça pelo WhatsApp com facilidade. Atendemos com agilidade para sua encomenda chegar no tempo certo.'
      }
    ]
  },
  catalogo: {
    selo: 'Nosso Cardápio',
    descricao: 'Escolha seus favoritos e faça o pedido diretamente via WhatsApp.'
  },
  sobre: {
    selo: 'Nossa História',
    titulo: 'Tradição e qualidade em cada pão',
    descricao: 'A Eloa Mercados & Panificações nasceu do amor pela boa panificação. Há mais de uma década, produzimos pães, doces e salgados com a dedicação de quem coloca o coração em cada receita.',
    lista: [
      {
        nome: 'Produção artesanal',
        descricao: 'cada produto feito com cuidado e atenção aos detalhes'
      },
      {
        nome: 'Ingredientes de qualidade',
        descricao: 'selecionamos apenas o melhor para a sua família'
      },
      {
        nome: 'Variedade completa',
        descricao: 'pães, bolos, doces, salgados e produtos de mercado'
      },
      {
        nome: 'Atendimento próximo',
        descricao: 'pedidos rápidos via WhatsApp, com entrega e retirada'
      }
    ]
  },
  estatisticas: {
    lista: [
      { nome: 'Anos de Tradição', valor: '+10' },
      { nome: 'Produtos no Catálogo', valor: '+50' },
      { nome: 'Produção Fresca', valor: 'Diária' },
      { nome: 'Satisfação Garantida', valor: '100%' }
    ]
  },
  cta: {
    selo: 'Torne-se um parceiro Eloá',
    titulo: 'Faça parte da nossa rede de parceiros',
    descricao: 'Entre em contato via WhatsApp e faça seu pedido agora. Pães frescos, entrega prática e um atendimento que faz a diferença.'
  },
  rodape: {
    descricao: 'Pães frescos, doces, salgados e produtos de qualidade produzidos todos os dias com cuidado e tradição.',
    textoContato: 'Contate-nos via WhatsApp para pedidos rápidos e dúvidas.',
    textoDireitos: '© 2026 Eloa Mercados. Todos os direitos reservados.'
  },
  dados: {
    endereco: 'Rua Exemplo, 123 - Centro, Sua Cidade - UF',
    horarioAbertura: '06h',
    horarioFechamento: '20h',
    diasFuncionamento: 'Seg a Sáb',
    whatsapp: 'Fazer Pedido',
    cnpj: '00.000.000/0001-00'
  },
  faq: {
    itens: [
      {
        id: 'faq-1',
        pergunta: 'Como faço para pedir ou cotar produtos da Eloá Panificações?',
        resposta: 'Você pode consultar todo o nosso catálogo online e clicar no botão de pedido para falar diretamente com nossa equipe comercial pelo WhatsApp. Oferecemos atendimento ágil e cotações personalizadas para sua necessidade.'
      },
      {
        id: 'faq-2',
        pergunta: 'Quais tipos de produtos a Eloá comercializa?',
        resposta: 'Trabalhamos com uma linha completa de panificação tradicional e especial, pães artesanais, bolos caseiros e recheados, doces finos, salgados para festas e eventos, além de fornecimento no atacado.'
      },
      {
        id: 'faq-3',
        pergunta: 'A Eloá atende compras no atacado para padarias, mercados e restaurantes?',
        resposta: 'Sim! Atendemos tanto pedidos pontuais quanto fornecimento contínuo em grande volume no atacado para estabelecimentos comerciais, com condições comerciais exclusivas e entregas programadas.'
      },
      {
        id: 'faq-4',
        pergunta: 'Quais regiões são atendidas para entregas e fornecimento?',
        resposta: 'Nossa distribuição especializada atende toda a Grande São Paulo, Região Metropolitana de Campinas, Vale do Paraíba e Litoral Paulista com frota preparada para garantir produtos frescos.'
      },
      {
        id: 'faq-5',
        pergunta: 'Quais são as formas de pagamento aceitas?',
        resposta: 'Aceitamos Pix, cartões de crédito e débito, dinheiro e condições especiais de faturamento para empresas e parceiros comerciais cadastrados.'
      }
    ]
  }
};

export function resolveSiteContent(dynamic?: Partial<SiteContent> | null): SiteContent {
  const fallback = DEFAULT_SITE_CONTENT;
  if (!dynamic) {
    return fallback;
  }

  const pickStr = (dyn?: string | null, def?: string): string => {
    return dyn && dyn.trim() ? dyn.trim() : (def || '');
  };

  const bannerSelo = pickStr(dynamic.banner?.selo, fallback.banner.selo);
  const bannerTitulo = pickStr(dynamic.banner?.titulo, fallback.banner.titulo);
  const bannerSubtitulo = pickStr(dynamic.banner?.subtitulo, fallback.banner.subtitulo);
  const bannerDescricao = pickStr(dynamic.banner?.descricao, fallback.banner.descricao);
  const bannerIndicadores = fallback.banner.indicadores.map((defInd, i) => {
    const dynInd = dynamic.banner?.indicadores?.[i];
    return {
      nome: pickStr(dynInd?.nome, defInd.nome),
      valor: pickStr(dynInd?.valor, defInd.valor)
    };
  });

  const difSelo = pickStr(dynamic.diferenciais?.selo, fallback.diferenciais.selo);
  const difTitulo = pickStr(dynamic.diferenciais?.titulo, fallback.diferenciais.titulo);
  const difDescricao = pickStr(dynamic.diferenciais?.descricao, fallback.diferenciais.descricao);
  const difCards = fallback.diferenciais.cards.map((defCard, i) => {
    const dynCard = dynamic.diferenciais?.cards?.[i];
    return {
      titulo: pickStr(dynCard?.titulo, defCard.titulo),
      texto: pickStr(dynCard?.texto, defCard.texto)
    };
  });

  const catSelo = pickStr(dynamic.catalogo?.selo, fallback.catalogo.selo);
  const catDescricao = pickStr(dynamic.catalogo?.descricao, fallback.catalogo.descricao);

  const sobreSelo = pickStr(dynamic.sobre?.selo, fallback.sobre.selo);
  const sobreTitulo = pickStr(dynamic.sobre?.titulo, fallback.sobre.titulo);
  const sobreDescricao = pickStr(dynamic.sobre?.descricao, fallback.sobre.descricao);
  const dynSobreLista = dynamic.sobre?.lista;
  let sobreLista: Array<{ nome: string; descricao: string }>;
  if (dynSobreLista && dynSobreLista.length > 0) {
    const mapped = dynSobreLista.map((item, i) => ({
      nome: pickStr(item?.nome, fallback.sobre.lista[i]?.nome),
      descricao: pickStr(item?.descricao, fallback.sobre.lista[i]?.descricao)
    })).filter(it => it.nome || it.descricao);
    sobreLista = mapped.length > 0 ? mapped : fallback.sobre.lista;
  } else {
    sobreLista = fallback.sobre.lista;
  }

  const dynEstLista = dynamic.estatisticas?.lista;
  let estatisticasLista: Array<{ nome: string; valor: string }>;
  if (dynEstLista && dynEstLista.length > 0) {
    const mapped = dynEstLista.map((item, i) => ({
      nome: pickStr(item?.nome, fallback.estatisticas.lista[i]?.nome),
      valor: pickStr(item?.valor, fallback.estatisticas.lista[i]?.valor)
    })).filter(it => it.nome || it.valor);
    estatisticasLista = mapped.length > 0 ? mapped : fallback.estatisticas.lista;
  } else {
    estatisticasLista = fallback.estatisticas.lista;
  }

  const ctaSelo = pickStr(dynamic.cta?.selo, fallback.cta.selo);
  const ctaTitulo = pickStr(dynamic.cta?.titulo, fallback.cta.titulo);
  const ctaDescricao = pickStr(dynamic.cta?.descricao, fallback.cta.descricao);

  const rodapeDescricao = pickStr(dynamic.rodape?.descricao, fallback.rodape.descricao);
  const rodapeTextoContato = pickStr(dynamic.rodape?.textoContato, fallback.rodape.textoContato);
  const rodapeTextoDireitos = pickStr(dynamic.rodape?.textoDireitos, fallback.rodape.textoDireitos);

  const faqItens = (fallback.faq?.itens || []).map(defFaq => {
    const dynFaq = dynamic.faq?.itens?.find(f => f.id === defFaq.id);
    return {
      id: defFaq.id,
      pergunta: pickStr(dynFaq?.pergunta, defFaq.pergunta),
      resposta: pickStr(dynFaq?.resposta, defFaq.resposta)
    };
  });

  const dadosEndereco = pickStr(dynamic.dados?.endereco, fallback.dados.endereco);
  const dadosHorarioAbertura = pickStr(dynamic.dados?.horarioAbertura, fallback.dados.horarioAbertura);
  const dadosHorarioFechamento = pickStr(dynamic.dados?.horarioFechamento, fallback.dados.horarioFechamento);
  const dadosDiasFuncionamento = pickStr(dynamic.dados?.diasFuncionamento, fallback.dados.diasFuncionamento);
  const dadosWhatsapp = pickStr(dynamic.dados?.whatsapp, fallback.dados.whatsapp);
  const dadosCnpj = pickStr(dynamic.dados?.cnpj, fallback.dados.cnpj);

  return {
    banner: {
      selo: bannerSelo,
      titulo: bannerTitulo,
      subtitulo: bannerSubtitulo,
      descricao: bannerDescricao,
      indicadores: bannerIndicadores
    },
    diferenciais: {
      selo: difSelo,
      titulo: difTitulo,
      descricao: difDescricao,
      cards: difCards
    },
    catalogo: {
      selo: catSelo,
      descricao: catDescricao
    },
    sobre: {
      selo: sobreSelo,
      titulo: sobreTitulo,
      descricao: sobreDescricao,
      lista: sobreLista
    },
    estatisticas: {
      lista: estatisticasLista
    },
    cta: {
      selo: ctaSelo,
      titulo: ctaTitulo,
      descricao: ctaDescricao
    },
    rodape: {
      descricao: rodapeDescricao,
      textoContato: rodapeTextoContato,
      textoDireitos: rodapeTextoDireitos
    },
    dados: {
      endereco: dadosEndereco,
      horarioAbertura: dadosHorarioAbertura,
      horarioFechamento: dadosHorarioFechamento,
      diasFuncionamento: dadosDiasFuncionamento,
      whatsapp: dadosWhatsapp,
      cnpj: dadosCnpj
    },
    faq: {
      itens: faqItens
    }
  };
}


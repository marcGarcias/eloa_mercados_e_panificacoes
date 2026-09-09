# 🗺️ Guia Definitivo: Onde Editar Cada Item de SEO no Projeto

> 📱 **Modelo do Site:** Catálogo Institucional & Vitrine de Produtos (os pedidos, cotações e negociações são realizados **diretamente pelo WhatsApp**, sem carrinho, checkout ou controle de estoque online).

Este documento mostra o **caminho exato** de cada arquivo no código-fonte onde você pode alterar ou personalizar qualquer dado de SEO, metadados, links de WhatsApp, imagens e dados estruturados da **Eloá Mercados & Panificações**.

---

## 📌 Sumário Rápido de Arquivos

| O que você quer alterar? | Arquivo exato | O que editar |
| :--- | :--- | :--- |
| **Metadados Iniciais do HTML (Title, Descrição, OpenGraph)** | [`web/src/index.html`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/index.html) | `<title>`, `<meta name="description">`, `<meta property="og:...">` |
| **Número do WhatsApp Comercial & Dados da Empresa** | [`web/src/app/services/seo.service.ts`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/app/services/seo.service.ts) | Método `setHomeStructuredData()` e `setProductStructuredData()` |
| **SEO e Textos Dinâmicos da Home** | [`web/src/app/components/home/home.ts`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/app/components/home/home.ts) | Chamada `this.seoService.updateMetaTags(...)` |
| **Estrutura de Produtos do Catálogo (JSON-LD p/ WhatsApp)** | [`web/src/app/services/seo.service.ts`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/app/services/seo.service.ts) | Método `setProductStructuredData(...)` |
| **Regras de Robôs de Busca (Bloqueios / Permissões)** | [`web/public/robots.txt`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/public/robots.txt) | Diretivas `User-agent`, `Allow`, `Disallow` |
| **Mapa do Site (URLs para Indexação no Google)** | [`web/public/sitemap.xml`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/public/sitemap.xml) | Tags `<url><loc>...</loc></url>` |
| **Checklist de Dados Manuais a Preencher** | [`SEO_ATENCAO_CONTEUDO_MANUAL.md`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/SEO_ATENCAO_CONTEUDO_MANUAL.md) | Lista de conferência de dados |

---

## 🔍 Detalhamento por Item

### 1. Dados da Empresa e WhatsApp de Atendimento
- 📂 **Arquivo:** [`web/src/app/services/seo.service.ts`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/app/services/seo.service.ts)
- **Campos a editar:**
  - `telephone`: Telefone/WhatsApp oficial para onde os clientes serão direcionados (ex: `+55-11-99999-9999`).
  - `sameAs`: Links do Instagram, Facebook e LinkedIn.
  - `streetAddress`, `addressLocality`, `postalCode`: Endereço da padaria / central.
  - `geo` (`latitude` e `longitude`): Coordenadas GPS para localização no Google Maps e busca local.
  - `areaServed`: Bairros, cidades e regiões atendidas para entrega de pedidos via WhatsApp.

---

### 2. Title, Description e Compartilhamento no WhatsApp da Home
- 📂 **Arquivo 1 (Estático):** [`web/src/index.html`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/index.html)
- 📂 **Arquivo 2 (Dinâmico Angular):** [`web/src/app/components/home/home.ts`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/app/components/home/home.ts)
- **Campos a editar:**
  ```typescript
  this.seoService.updateMetaTags({
    title: 'Catálogo de Panificação & Pedidos via WhatsApp', // Título (máx 60 caracteres)
    description: 'Catálogo de produtos da Eloá. Conheça nossa linha e faça seu pedido diretamente pelo WhatsApp.', // Descrição (máx 155 caracteres)
    canonicalUrl: 'https://eloapanificacoes.com.br/',
    ogTitle: 'Eloá Mercados & Panificações | Catálogo & Pedidos via WhatsApp',
    ogDescription: 'Consulte nossa linha completa de panificação e confeitaria. Faça seu pedido diretamente pelo WhatsApp.',
    ogImage: 'https://eloapanificacoes.com.br/assets/images/og-eloa-banner.jpg' // Imagem oficial de visualização no WhatsApp (1200x630px)
  });
  ```

---

### 3. Estruturação dos Produtos para Busca no Google (JSON-LD)
Como o site é uma **vitrine informativa** sem carrinho, o Google indexa o produto informando que o pedido é realizado sob consulta/atendimento direto:
- 📂 **Arquivo:** [`web/src/app/services/seo.service.ts`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/src/app/services/seo.service.ts)
- **Método:** `setProductStructuredData(...)`
  ```typescript
  this.seoService.setProductStructuredData({
    name: 'Pão Francês Tradicional',
    description: 'Pão francês fresquinho, crocante por fora e macio por dentro. Produção diária. Pedidos via WhatsApp.',
    category: 'Pães',
    image: 'https://eloapanificacoes.com.br/assets/images/produtos/pao-frances.webp',
    url: 'https://eloapanificacoes.com.br/#catalog'
  });
  ```

---

### 4. Bloqueios de Páginas no Google (`robots.txt`)
- 📂 **Arquivo:** [`web/public/robots.txt`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/public/robots.txt)
- Permite que o Google leia todo o catálogo e imagens, bloqueando apenas rotas administrativas (`/admin/`, `/login-cms/`, `/setup/`).

---

### 5. Mapa do Site (`sitemap.xml`)
- 📂 **Arquivo:** [`web/public/sitemap.xml`](file:///c:/Users/marke/Documents/eloa/eloa_mercados_e_panificacoes/web/public/sitemap.xml)
- Lista das páginas públicas para os robôs do Google indexarem.

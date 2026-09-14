package garcias.api.content.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public class SiteContentDto {

    @Valid
    private BannerDto banner;

    @Valid
    private DiferenciaisDto diferenciais;

    @Valid
    private CatalogoDto catalogo;

    @Valid
    private SobreDto sobre;

    @Valid
    private EstatisticasDto estatisticas;

    @Valid
    private CtaDto cta;

    @Valid
    private RodapeDto rodape;

    @Valid
    private DadosDto dados;

    @Valid
    private FaqDto faq;

    public FaqDto getFaq() {
        return faq;
    }

    public void setFaq(FaqDto faq) {
        this.faq = faq;
    }

    public BannerDto getBanner() {
        return banner;
    }

    public void setBanner(BannerDto banner) {
        this.banner = banner;
    }

    public DiferenciaisDto getDiferenciais() {
        return diferenciais;
    }

    public void setDiferenciais(DiferenciaisDto diferenciais) {
        this.diferenciais = diferenciais;
    }

    public CatalogoDto getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(CatalogoDto catalogo) {
        this.catalogo = catalogo;
    }

    public SobreDto getSobre() {
        return sobre;
    }

    public void setSobre(SobreDto sobre) {
        this.sobre = sobre;
    }

    public EstatisticasDto getEstatisticas() {
        return estatisticas;
    }

    public void setEstatisticas(EstatisticasDto estatisticas) {
        this.estatisticas = estatisticas;
    }

    public CtaDto getCta() {
        return cta;
    }

    public void setCta(CtaDto cta) {
        this.cta = cta;
    }

    public RodapeDto getRodape() {
        return rodape;
    }

    public void setRodape(RodapeDto rodape) {
        this.rodape = rodape;
    }

    public DadosDto getDados() {
        return dados;
    }

    public void setDados(DadosDto dados) {
        this.dados = dados;
    }

    public static class IndicadorDto {
        @Size(min = 3, max = 30, message = "O rótulo do indicador/estatística deve ter entre 3 e 30 caracteres.")
        private String nome;

        @Size(min = 1, max = 15, message = "O valor do indicador/estatística deve ter entre 1 e 15 caracteres.")
        private String valor;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }
    }

    public static class CardDto {
        @Size(min = 4, max = 35, message = "O título do card de diferencial deve ter entre 4 e 35 caracteres.")
        private String titulo;

        @Size(min = 15, max = 200, message = "O texto do card de diferencial deve ter entre 15 e 200 caracteres.")
        private String texto;

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getTexto() { return texto; }
        public void setTexto(String texto) { this.texto = texto; }
    }

    public static class DescricaoItemDto {
        @Size(max = 100, message = "O nome do item deve ter no máximo 100 caracteres.")
        private String nome;

        @Size(max = 300, message = "A descrição do item deve ter no máximo 300 caracteres.")
        private String descricao;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class BannerDto {
        @Size(min = 4, max = 31, message = "O selo do banner deve ter entre 4 e 31 caracteres.")
        private String selo;

        @Size(min = 4, max = 21, message = "O título do banner deve ter entre 4 e 21 caracteres.")
        private String titulo;

        @Size(min = 4, max = 26, message = "O subtítulo do banner deve ter entre 4 e 26 caracteres.")
        private String subtitulo;

        @Size(min = 10, max = 351, message = "A descrição do banner deve ter entre 10 e 351 caracteres.")
        private String descricao;

        @Valid
        private List<IndicadorDto> indicadores;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getSubtitulo() { return subtitulo; }
        public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public List<IndicadorDto> getIndicadores() { return indicadores; }
        public void setIndicadores(List<IndicadorDto> indicadores) { this.indicadores = indicadores; }
    }

    public static class DiferenciaisDto {
        @Size(min = 4, max = 31, message = "O selo dos diferenciais deve ter entre 4 e 31 caracteres.")
        private String selo;

        @Size(min = 4, max = 21, message = "O título dos diferenciais deve ter entre 4 e 21 caracteres.")
        private String titulo;

        @Size(min = 10, max = 351, message = "A descrição dos diferenciais deve ter entre 10 e 351 caracteres.")
        private String descricao;

        @Valid
        private List<CardDto> cards;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public List<CardDto> getCards() { return cards; }
        public void setCards(List<CardDto> cards) { this.cards = cards; }
    }

    public static class CatalogoDto {
        @Size(min = 4, max = 31, message = "O selo do catálogo deve ter entre 4 e 31 caracteres.")
        private String selo;

        @Size(min = 10, max = 351, message = "A descrição do catálogo deve ter entre 10 e 351 caracteres.")
        private String descricao;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class SobreDto {
        @Size(min = 4, max = 31, message = "O selo da seção sobre deve ter entre 4 e 31 caracteres.")
        private String selo;

        @Size(min = 4, max = 21, message = "O título da seção sobre deve ter entre 4 e 21 caracteres.")
        private String titulo;

        @Size(min = 10, max = 351, message = "A descrição da seção sobre deve ter entre 10 e 351 caracteres.")
        private String descricao;

        @Valid
        private List<DescricaoItemDto> lista;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public List<DescricaoItemDto> getLista() { return lista; }
        public void setLista(List<DescricaoItemDto> lista) { this.lista = lista; }
    }

    public static class EstatisticasDto {
        @Valid
        private List<IndicadorDto> lista;

        public List<IndicadorDto> getLista() { return lista; }
        public void setLista(List<IndicadorDto> lista) { this.lista = lista; }
    }

    public static class CtaDto {
        @Size(min = 4, max = 31, message = "O selo do CTA deve ter entre 4 e 31 caracteres.")
        private String selo;

        @Size(min = 4, max = 21, message = "O título do CTA deve ter entre 4 e 21 caracteres.")
        private String titulo;

        @Size(min = 10, max = 351, message = "A descrição do CTA deve ter entre 10 e 351 caracteres.")
        private String descricao;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class RodapeDto {
        @Size(min = 10, max = 351, message = "A descrição do rodapé deve ter entre 10 e 351 caracteres.")
        private String descricao;

        @Size(max = 150, message = "O texto de contato do rodapé deve ter no máximo 150 caracteres.")
        private String textoContato;

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public String getTextoContato() { return textoContato; }
        public void setTextoContato(String textoContato) { this.textoContato = textoContato; }
    }

    public static class DadosDto {
        @Size(min = 8, max = 150, message = "O endereço deve ter entre 8 e 150 caracteres.")
        @Pattern(
                regexp = "^(?=.*[a-zA-ZÀ-ÿ]).+$",
                message = "O endereço deve conter o nome do logradouro."
        )
        private String endereco;

        private String horarioAbertura;

        private String horarioFechamento;

        @Size(min = 3, max = 40, message = "Os dias de funcionamento devem ter entre 3 e 40 caracteres.")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ0-9\\s,.\\-–—/&eE]+$",
                message = "Os dias de funcionamento devem conter uma descrição válida (Ex: Seg a Sáb)."
        )
        private String diasFuncionamento;

        @Size(max = 30, message = "O WhatsApp deve ter no máximo 30 caracteres.")
        private String whatsapp;

        @Pattern(
                regexp = "^[A-Z0-9]{2}\\.[A-Z0-9]{3}\\.[A-Z0-9]{3}/[A-Z0-9]{4}-\\d{2}$",
                message = "O CNPJ deve seguir o formato válido da Receita Federal (inclusive o novo padrão alfanumérico): XX.XXX.XXX/XXXX-XX"
        )
        private String cnpj;

        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }
        public String getHorarioAbertura() { return horarioAbertura; }
        public void setHorarioAbertura(String horarioAbertura) { this.horarioAbertura = horarioAbertura; }
        public String getHorarioFechamento() { return horarioFechamento; }
        public void setHorarioFechamento(String horarioFechamento) { this.horarioFechamento = horarioFechamento; }
        public String getDiasFuncionamento() { return diasFuncionamento; }
        public void setDiasFuncionamento(String diasFuncionamento) { this.diasFuncionamento = diasFuncionamento; }
        public String getWhatsapp() { return whatsapp; }
        public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
        public String getCnpj() { return cnpj; }
        public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    }

    public static class FaqDto {
        @Valid
        private List<FaqItemDto> itens;

        public List<FaqItemDto> getItens() { return itens; }
        public void setItens(List<FaqItemDto> itens) { this.itens = itens; }
    }

    public static class FaqItemDto {
        private String id;
        private String pergunta;

        @Size(max = 1000, message = "A resposta do FAQ não pode exceder 1000 caracteres.")
        private String resposta;

        public FaqItemDto() {}

        public FaqItemDto(String id, String pergunta, String resposta) {
            this.id = id;
            this.pergunta = pergunta;
            this.resposta = resposta;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getPergunta() { return pergunta; }
        public void setPergunta(String pergunta) { this.pergunta = pergunta; }
        public String getResposta() { return resposta; }
        public void setResposta(String resposta) { this.resposta = resposta; }
    }
}


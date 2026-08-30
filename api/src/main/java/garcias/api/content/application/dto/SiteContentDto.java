package garcias.api.content.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    // Getters and Setters

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

    // --- Sub DTOs ---

    public static class IndicadorDto {
        @Size(max = 100, message = "O nome do indicador deve ter no máximo 100 caracteres.")
        private String nome;

        @Size(max = 50, message = "O valor do indicador deve ter no máximo 50 caracteres.")
        private String valor;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }
    }

    public static class CardDto {
        @Size(max = 120, message = "O título do card deve ter no máximo 120 caracteres.")
        private String titulo;

        @Size(max = 300, message = "O texto do card deve ter no máximo 300 caracteres.")
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
        @Size(max = 100, message = "O selo do banner deve ter no máximo 100 caracteres.")
        private String selo;

        @Size(max = 150, message = "O título do banner deve ter no máximo 150 caracteres.")
        private String titulo;

        @Size(max = 150, message = "O subtítulo do banner deve ter no máximo 150 caracteres.")
        private String subtitulo;

        @Size(max = 500, message = "A descrição do banner deve ter no máximo 500 caracteres.")
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
        @Size(max = 100, message = "O selo dos diferenciais deve ter no máximo 100 caracteres.")
        private String selo;

        @Size(max = 150, message = "O título dos diferenciais deve ter no máximo 150 caracteres.")
        private String titulo;

        @Size(max = 500, message = "A descrição dos diferenciais deve ter no máximo 500 caracteres.")
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
        @Size(max = 100, message = "O selo do catálogo deve ter no máximo 100 caracteres.")
        private String selo;

        @Size(max = 500, message = "A descrição do catálogo deve ter no máximo 500 caracteres.")
        private String descricao;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class SobreDto {
        @Size(max = 100, message = "O selo da seção sobre deve ter no máximo 100 caracteres.")
        private String selo;

        @Size(max = 150, message = "O título da seção sobre deve ter no máximo 150 caracteres.")
        private String titulo;

        @Size(max = 1000, message = "A descrição da seção sobre deve ter no máximo 1000 caracteres.")
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
        @Size(max = 100, message = "O selo do CTA deve ter no máximo 100 caracteres.")
        private String selo;

        @Size(max = 150, message = "O título do CTA deve ter no máximo 150 caracteres.")
        private String titulo;

        @Size(max = 500, message = "A descrição do CTA deve ter no máximo 500 caracteres.")
        private String descricao;

        public String getSelo() { return selo; }
        public void setSelo(String selo) { this.selo = selo; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class RodapeDto {
        @Size(max = 300, message = "A descrição do rodapé deve ter no máximo 300 caracteres.")
        private String descricao;

        @Size(max = 150, message = "O texto de contato do rodapé deve ter no máximo 150 caracteres.")
        private String textoContato;

        @Size(max = 150, message = "O texto de direitos autorais do rodapé deve ter no máximo 150 caracteres.")
        private String textoDireitos;

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public String getTextoContato() { return textoContato; }
        public void setTextoContato(String textoContato) { this.textoContato = textoContato; }
        public String getTextoDireitos() { return textoDireitos; }
        public void setTextoDireitos(String textoDireitos) { this.textoDireitos = textoDireitos; }
    }

    public static class DadosDto {
        @Size(max = 250, message = "O endereço deve ter no máximo 250 caracteres.")
        private String endereco;

        private String horarioAbertura;

        private String horarioFechamento;

        @Size(max = 100, message = "Os dias de funcionamento devem ter no máximo 100 caracteres.")
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
}

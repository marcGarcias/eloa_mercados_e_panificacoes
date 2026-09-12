package garcias.api.content.domain.valueobjects;

public record FaqItem(String id, String pergunta, String resposta) {

    public FaqItem withResposta(String novaResposta) {
        return new FaqItem(this.id, this.pergunta, novaResposta);
    }
}


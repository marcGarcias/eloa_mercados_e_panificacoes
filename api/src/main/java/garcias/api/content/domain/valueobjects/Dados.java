package garcias.api.content.domain.valueobjects;

public record Dados(
        String endereco,
        String horarioAbertura,
        String horarioFechamento,
        String diasFuncionamento,
        String whatsapp,
        String cnpj
) {}
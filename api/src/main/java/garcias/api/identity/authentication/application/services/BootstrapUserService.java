package garcias.api.identity.authentication.application.services;

import garcias.api.identity.authentication.application.dto.events.BootstrapUserRequestedEvent;
import garcias.api.identity.authentication.application.dto.requests.BootstrapUserRequest;
import garcias.api.identity.authentication.application.dto.responses.BootstrapUserResponse;
import garcias.api.identity.authentication.application.ports.UserAuthenticationPort;
import garcias.api.identity.authentication.application.usecases.BootstrapUserUseCase;
import garcias.api.identity.authentication.domain.exceptions.BootstrapAlreadyCompletedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BootstrapUserService implements BootstrapUserUseCase {

    private final UserAuthenticationPort userAuthenticationPort;
    private final ApplicationEventPublisher eventPublisher;

    @org.springframework.beans.factory.annotation.Value("${app.setup.access-key}")
    private String serverAccessKey;

    @org.springframework.beans.factory.annotation.Value("${app.setup.cpf}")
    private String serverCpf;

    public BootstrapUserService(
            UserAuthenticationPort userAuthenticationPort,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public BootstrapUserResponse execute(BootstrapUserRequest request) {

        if (userAuthenticationPort.existsAnyUser()) {
            throw new BootstrapAlreadyCompletedException();
        }

        // 1. Validar CPF (a formatação matemática já ocorreu via @CPF no DTO)
        // Precisamos apenas checar se bate com a variável de ambiente
        if (request.cpf() == null || !request.cpf().replaceAll("\\D", "").equals(this.serverCpf.replaceAll("\\D", ""))) {
            throw new garcias.api.identity.authentication.domain.exceptions.InvalidSetupCpfException();
        }

        // 2. Validar Chave de Acesso (Regex)
        String accessKeyRegex = "^[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}-[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]$";
        if (request.accessKey() == null || !request.accessKey().matches(accessKeyRegex)) {
            throw new garcias.api.identity.authentication.domain.exceptions.InvalidSetupAccessKeyException("Formato da chave de acesso inválido.");
        }

        // 3. Validar se a chave bate com a do servidor
        if (!request.accessKey().equals(this.serverAccessKey)) {
            throw new garcias.api.identity.authentication.domain.exceptions.InvalidSetupAccessKeyException("Chave de acesso incorreta.");
        }

        eventPublisher.publishEvent(
                new BootstrapUserRequestedEvent(
                        request.name(),
                        request.password()
                )
        );

        return new BootstrapUserResponse("Bootstrap process initiated successfully.");
    }
}
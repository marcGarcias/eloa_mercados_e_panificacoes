package garcias.api.identity.authentication.application.ports;

import garcias.api.identity.authentication.application.dto.results.UserAuthenticationDto;

import java.util.Optional;

public interface UserAuthenticationPort {

    Optional<UserAuthenticationDto> findByUserCode(String userCode);

    boolean existsAnyUser();
}

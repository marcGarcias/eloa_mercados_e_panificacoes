package garcias.api.identity.user.application.usecases;

import garcias.api.identity.user.application.dto.requests.CreateUserRequest;
import garcias.api.identity.user.domain.entities.User;

public interface CreateUserUseCase {

    User execute(CreateUserRequest request);

}

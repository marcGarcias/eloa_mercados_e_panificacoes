package garcias.api.identity.user.domain.services;

import garcias.api.identity.user.domain.valueobjects.UserCode;

public interface UserCodeProvider {

    UserCode generate();
}

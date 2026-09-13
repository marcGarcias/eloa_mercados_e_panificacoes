package garcias.api.shared.ratelimit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários do RateLimitProperties")
class RateLimitPropertiesTest {

    @Test
    @DisplayName("Deve retornar as configurações padrão e para cada nível de rate limit")
    void shouldReturnDefaultAndTierConfigs() {
        RateLimitProperties props = new RateLimitProperties();

        assertThat(props.isEnabled()).isTrue();
        props.setEnabled(false);
        assertThat(props.isEnabled()).isFalse();

        assertThat(props.getConfigFor(RateLimitTier.AUTH).getMaxRequests()).isEqualTo(5);
        assertThat(props.getConfigFor(RateLimitTier.REFRESH).getMaxRequests()).isEqualTo(20);
        assertThat(props.getConfigFor(RateLimitTier.PUBLIC).getMaxRequests()).isEqualTo(60);
        assertThat(props.getConfigFor(RateLimitTier.ADMIN).getMaxRequests()).isEqualTo(120);
        assertThat(props.getConfigFor(RateLimitTier.DEFAULT).getMaxRequests()).isEqualTo(60);

        RateLimitProperties.TierConfig customConfig = new RateLimitProperties.TierConfig();
        customConfig.setMaxRequests(10);
        customConfig.setWindowSeconds(30);
        props.setAuth(customConfig);
        props.setRefresh(customConfig);
        props.setPublicEndpoint(customConfig);
        props.setAdmin(customConfig);
        props.setDefaultTier(customConfig);

        assertThat(props.getAuth().getMaxRequests()).isEqualTo(10);
        assertThat(props.getAuth().getWindowSeconds()).isEqualTo(30);
        assertThat(props.getRefresh().getMaxRequests()).isEqualTo(10);
        assertThat(props.getPublicEndpoint().getMaxRequests()).isEqualTo(10);
        assertThat(props.getAdmin().getMaxRequests()).isEqualTo(10);
        assertThat(props.getDefaultTier().getMaxRequests()).isEqualTo(10);
    }
}

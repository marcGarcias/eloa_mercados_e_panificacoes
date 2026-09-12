package garcias.api.shared.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;

    private TierConfig auth = new TierConfig(5, 60);
    private TierConfig refresh = new TierConfig(20, 60);
    private TierConfig publicEndpoint = new TierConfig(60, 60);
    private TierConfig admin = new TierConfig(120, 60);
    private TierConfig defaultTier = new TierConfig(60, 60);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TierConfig getAuth() {
        return auth;
    }

    public void setAuth(TierConfig auth) {
        this.auth = auth;
    }

    public TierConfig getRefresh() {
        return refresh;
    }

    public void setRefresh(TierConfig refresh) {
        this.refresh = refresh;
    }

    public TierConfig getPublicEndpoint() {
        return publicEndpoint;
    }

    public void setPublicEndpoint(TierConfig publicEndpoint) {
        this.publicEndpoint = publicEndpoint;
    }

    public TierConfig getAdmin() {
        return admin;
    }

    public void setAdmin(TierConfig admin) {
        this.admin = admin;
    }

    public TierConfig getDefaultTier() {
        return defaultTier;
    }

    public void setDefaultTier(TierConfig defaultTier) {
        this.defaultTier = defaultTier;
    }

    public TierConfig getConfigFor(RateLimitTier tier) {
        return switch (tier) {
            case AUTH -> auth;
            case REFRESH -> refresh;
            case PUBLIC -> publicEndpoint;
            case ADMIN -> admin;
            case DEFAULT -> defaultTier;
        };
    }

    public static class TierConfig {
        private int maxRequests;
        private int windowSeconds;

        public TierConfig() {
        }

        public TierConfig(int maxRequests, int windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowSeconds = windowSeconds;
        }

        public int getMaxRequests() {
            return maxRequests;
        }

        public void setMaxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
        }

        public int getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(int windowSeconds) {
            this.windowSeconds = windowSeconds;
        }
    }
}


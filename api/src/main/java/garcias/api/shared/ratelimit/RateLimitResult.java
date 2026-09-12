package garcias.api.shared.ratelimit;

public record RateLimitResult(
        boolean allowed,
        long remaining,
        long retryAfterSeconds,
        int limit
) {
    public static RateLimitResult allowed(long remaining, int limit) {
        return new RateLimitResult(true, remaining, 0, limit);
    }

    public static RateLimitResult blocked(long retryAfterSeconds, int limit) {
        return new RateLimitResult(false, 0, retryAfterSeconds, limit);
    }
}


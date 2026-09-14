package garcias.api.shared.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Serializador Redis JSON seguro que encapsula valores e coleções em um wrapper
 * polimórfico para evitar falhas de desserialização de arrays JSON no Spring Data Redis / Jackson.
 */
public class SafeRedisJsonSerializer implements RedisSerializer<Object> {

    public record CacheValueEnvelope(Object value) {}

    private final RedisSerializer<Object> delegate = RedisSerializer.json();

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return delegate.serialize(new CacheValueEnvelope(t));
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Object obj = delegate.deserialize(bytes);
        if (obj instanceof CacheValueEnvelope envelope) {
            return envelope.value();
        }
        return obj;
    }
}

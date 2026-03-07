package com.jkefbq.gymentry.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.NonNull;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.ArrayList;
import java.util.List;

public class EnvelopeRedisSerializer implements RedisSerializer<@NonNull Object> {

    private static final String T = "t";   // type
    private static final String ET = "et"; // element type (for lists)
    private static final String V = "v";   // value

    private final ObjectMapper mapper;

    public EnvelopeRedisSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) return null;
        try {
            ObjectNode envelope = mapper.createObjectNode();

            if (value instanceof List<?> list) {
                envelope.put(T, "java.util.List");
                if (!list.isEmpty()) {
                    envelope.put(ET, list.get(0).getClass().getName());
                }
                envelope.set(V, mapper.valueToTree(list));
            } else {
                envelope.put(T, value.getClass().getName());
                envelope.set(V, mapper.valueToTree(value));
            }

            return mapper.writeValueAsBytes(envelope);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Serialize error", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) return null;
        try {
            JsonNode root = mapper.readTree(bytes);
            String typeName = root.get(T).asText();
            JsonNode valueNode = root.get(V);

            if ("java.util.List".equals(typeName)) {
                if (root.has(ET)) {
                    Class<?> elemClass = Class.forName(root.get(ET).asText());
                    JavaType listType = mapper.getTypeFactory()
                            .constructCollectionType(ArrayList.class, elemClass);
                    return mapper.convertValue(valueNode, listType);
                }
                return new ArrayList<>();
            }

            return mapper.convertValue(valueNode, Class.forName(typeName));
        } catch (Exception e) {
            throw new SerializationException("Deserialize error", e);
        }
    }
}

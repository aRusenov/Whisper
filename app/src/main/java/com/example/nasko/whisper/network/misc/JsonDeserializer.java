package com.example.nasko.whisper.network.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Collection;

public class JsonDeserializer {

    private ObjectMapper mapper = new ObjectMapper();
    private TypeFactory typeFactory = mapper.getTypeFactory();

    public <T> T deserializeCollection(String json,
                                       Class<? extends Collection> collectionClass,
                                       Class<?> elementClass) throws IOException {
        return mapper.readValue(json, typeFactory.constructCollectionType(collectionClass, elementClass));
    }

    public <T> T deserialize(String json, Class<T> element) throws IOException {
        return mapper.readValue(json, element);
    }
}

package com.example.nasko.whisper.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;

public class JsonDeserializer {

    private ObjectMapper mapper = new ObjectMapper();
    private TypeFactory typeFactory = mapper.getTypeFactory();

    public <T> T deserialize(String json, Class<T> element) throws IOException {
        return mapper.readValue(json, element);
    }
}

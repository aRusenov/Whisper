package com.example.nasko.whisper.network;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonDeserializer {

    private ObjectMapper mapper = new ObjectMapper();

    public <T> T deserialize(String json, Class<T> element) throws IOException {
        return mapper.readValue(json, element);
    }
}

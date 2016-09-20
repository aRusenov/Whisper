package com.example.nasko.whisper.managers;

import com.example.nasko.whisper.models.dto.Image;

public class ImageUrlResolver {

    private static String endpoint;

    public static void setEndpoint(String url) {
        endpoint = url;
    }

    public static String getEndpoint() {
        return endpoint;
    }

    public static String getFullUrl(Image image) {
        if (image == null) {
            return null;
        }

        return endpoint + image.getUrl();
    }
}

package com.example.nasko.whisper.utils.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.nasko.whisper.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {

    private static final String TAG = "ConfigLoader";

    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.app);
            Properties properties = new Properties();
            properties.load(rawResource);

            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        } finally {
             if (rawResource != null) {
                 try {
                     rawResource.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }

        return null;
    }
}
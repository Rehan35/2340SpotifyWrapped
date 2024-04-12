package com.example.spotifywrapped2340.util;

import java.io.IOException;
import java.net.MalformedURLException;

public interface CompletionListener {
    void onComplete(String result) throws IOException;
    void onError(Exception e);
}

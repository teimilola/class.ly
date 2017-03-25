package com.example.temi.huhacks;

import java.net.HttpURLConnection;

/**
 * Created by Temi on 3/25/2017.
 */

public class ImgurAuthorization {
    private static final String TAG = ImgurAuthorization.class.getSimpleName();

    private static ImgurAuthorization INSTANCE;

    static final String IMGUR_CLIENT_ID = "c9ec736b377b124";

    private ImgurAuthorization() {}

    public static ImgurAuthorization getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ImgurAuthorization();
        return INSTANCE;
    }


    public void addToHttpURLConnection(HttpURLConnection conn) {

        conn.setRequestProperty("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

    }
}

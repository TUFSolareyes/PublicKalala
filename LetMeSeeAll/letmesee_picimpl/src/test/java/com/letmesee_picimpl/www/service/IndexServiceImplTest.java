package com.letmesee_picimpl.www.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.*;

class IndexServiceImplTest {

    @Test
    void downloadPicFromUrl() throws IOException {
        String urlStr = "https://tse2-mm.cn.bing.net/th/id/OIP-C.HedJXyimMbXfWiWtCtkWXgHaEK?pid=ImgDet&rs=1";
        URL url = new URL(urlStr);
        URLConnection urlConnection = url.openConnection();
        String type = urlConnection.getContentType();
        System.out.println(type);
    }
}
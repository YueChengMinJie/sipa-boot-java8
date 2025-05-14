package com.sipa.boot.java8.iot.core.protocol.management.jar;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public class ProtocolClassLoader extends URLClassLoader {
    private final URL[] urls;

    public ProtocolClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.urls = urls;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    public URL[] getUrls() {
        return urls;
    }
}

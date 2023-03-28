package icia.js.lostandfound.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;


public class CustomServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream byteArrayInputStream;

    public CustomServletInputStream(byte[] buffer) {
        byteArrayInputStream = new ByteArrayInputStream(buffer);
    }

    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException();
    }
}


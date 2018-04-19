package com.example.restlogging.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * This class copy the servlet request for logging purpose.
 * 
 * @author Yu-Hua Chang
 *
 */
public class HttpServletRequestCopier extends HttpServletRequestWrapper {

    private MyServletInputStream inputStream;
    
    public HttpServletRequestCopier(HttpServletRequest request) throws IOException {
        super(request);
        inputStream = new MyServletInputStream(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    public byte[] getContentAsByteArray() {
        return inputStream.toByteArray();
    }
    
    private static class MyServletInputStream extends ServletInputStream {

        private ByteArrayInputStream in;
        private ByteArrayOutputStream out;

        public MyServletInputStream(ServletInputStream input) throws IOException {

            // copy request stream to output stream
            out = new ByteArrayOutputStream();
            InputStreamReader reader = new InputStreamReader(input);
            int b = reader.read();
            while (b >= 0) {
                out.write(b);
                b = reader.read();
            }
            
            // provide input stream from output stream
            in = new ByteArrayInputStream(out.toByteArray());
        }

        public byte[] toByteArray() {
            return out.toByteArray();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // no action
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }
    }
}

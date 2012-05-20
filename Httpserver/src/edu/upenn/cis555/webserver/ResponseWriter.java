package edu.upenn.cis555.webserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ResponseWriter extends PrintWriter {
    private HttpServletResponseImpl response;
    private int separatorLength;
    
    public ResponseWriter(HttpServletResponseImpl response,
            OutputStreamWriter output) {
        super(output);
        this.response = response;
        separatorLength = System.getProperty("line.separator").length();
    }
    
    @Override
    public void flush() {
        try {
        if (!response.isCommitted()) {
            response.commit();
        }
        super.flush();
        }
        catch (IOException ex) {
            // Can't throw it here, so log it?
            ex.printStackTrace();
        }
    }
 
    @Override
    public void print(boolean b) {
        super.print(b);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Boolean.toString(b).length());
    }
 
    @Override
    public void print(char c) {
        super.print(c);
        response.setContentLength(response.getContentLength() + 1);
    }
 
    @Override
    public void print(char[] s) {
        super.print(s);
        response.setContentLength(response.getContentLength() + s.length);
    }
 
    @Override
    public void print(double d) {
        super.print(d);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Double.toString(d).length());
    }
 
    @Override
    public void print(float f) {
        super.print(f);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Float.toString(f).length());
    }
 
    @Override
    public void print(int i) {
        super.print(i);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Integer.toString(i).length());
    }
 
    @Override
    public void print(long l) {
        super.print(l);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Long.toString(l).length());
    }
 
    @Override
    public void print(Object obj) {
        super.print(obj);
        response.setContentLength(response.getContentLength() + obj.toString().length());
    }
 
    @Override
    public void print(String s) {
        super.print(s);
        response.setContentLength(response.getContentLength() + s.length());
    }
    
    @Override
    public void println() {
        super.println();
        response.setContentLength(response.getContentLength() + separatorLength);
    }
 
    @Override
    public void println(boolean b) {
        super.println(b);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Boolean.toString(b).length() + separatorLength);
    }
 
    @Override
    public void println(char c) {
        super.println(c);
        response.setContentLength(response.getContentLength() + 1 + separatorLength);
    }
 
    @Override
    public void println(char[] s) {
        super.println(s);
        response.setContentLength(response.getContentLength() + s.length + separatorLength);
    }
 
    @Override
    public void println(double d) {
        super.println(d);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Double.toString(d).length() + separatorLength);
    }
 
    @Override
    public void println(float f) {
        super.println(f);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Float.toString(f).length() + separatorLength);
    }
 
    @Override
    public void println(int i) {
        super.println(i);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Integer.toString(i).length() + separatorLength);
    }
 
    @Override
    public void println(long l) {
        super.println(l);
        int contentLength = response.getContentLength();
        response.setContentLength(contentLength + Long.toString(l).length() + separatorLength);
    }
 
    @Override
    public void println(Object obj) {
        super.println(obj);
        response.setContentLength(response.getContentLength() + obj.toString().length() + separatorLength);
    }
 
    @Override
    public void println(String s) {
        super.print(s);
        response.setContentLength(response.getContentLength() + s.length() + separatorLength);
    }
    
    @Override
    public void write(char[] buf) {
        super.write(buf);
        response.setContentLength(response.getContentLength() + buf.length);
    }
    
    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
        response.setContentLength(response.getContentLength() + len);
    }
    
    @Override
    public void write(int c) {
        super.write(c);
        response.setContentLength(response.getContentLength() + 1);
    }
    
    @Override
    public void write(String s) {
        super.write(s);
        response.setContentLength(response.getContentLength() + s.length());
    }
    
    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        response.setContentLength(response.getContentLength() + len);
    }
}

package edu.upenn.cis555.mustang.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class HttpServletResponseMock extends HttpServletResponseStub {
	private String contentType;
	private StringBuilder content = new StringBuilder(); 
	
	public PrintWriter getWriter() throws IOException {
		return new PrintWriterMock(new ByteArrayOutputStream(), true);
	}

	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType; 
	}
	
	public String getContent() {
		return content.toString();
	}
	
	class PrintWriterMock extends PrintWriter {
		PrintWriterMock(OutputStream out, boolean autoFlush) {
			super(out, autoFlush);
		}
		
		public void println(String x) {
			super.print(x);
			content.append(x);
		}
	}
}

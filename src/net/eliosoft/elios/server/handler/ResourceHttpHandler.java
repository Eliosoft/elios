package net.eliosoft.elios.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * @author jeremie
 *
 */
public class ResourceHttpHandler implements HttpHandler {

	private static final int MAX_BUFFER_SIZE = 1024*512;

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		URI u = httpExchange.getRequestURI();
		String path = u.getPath();
		
		if(httpExchange.getRequestMethod().equalsIgnoreCase("GET")){			
			if(path.equals("/")){
				path = "/index.htm";
			}
			
			//TODO : move resources to a dedicated folder
			InputStream resourceInputStream = this.getClass().getResourceAsStream("/net/eliosoft/elios/server/handler/files"+path);

			if(resourceInputStream != null){
				//TODO : find another solution to know the file size...
				httpExchange.sendResponseHeaders(200, resourceInputStream.available());
				OutputStream os = httpExchange.getResponseBody();
				
				byte buffer[]=new byte[(int) Math.min(ResourceHttpHandler.MAX_BUFFER_SIZE, resourceInputStream.available())];
				int bytesRead = 0;
				
				while( (bytesRead = resourceInputStream.read(buffer)) != -1 ) {
					os.write(buffer, 0, bytesRead);
				}
				resourceInputStream.close();
				os.close();
			}
			else{
				String pageNotFound = "404 : Page not found !!!";
				httpExchange.sendResponseHeaders(404, pageNotFound.length());
				httpExchange.getResponseBody().write(pageNotFound.getBytes());
				httpExchange.getResponseBody().close();
			}
		}
		else{
			String badMethod = "405 : Method not allowed !!!";
			httpExchange.sendResponseHeaders(405, badMethod.length());
			httpExchange.getResponseBody().write(badMethod.getBytes());
			httpExchange.getResponseBody().close();
		}
	}
}

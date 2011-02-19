package net.eliosoft.elios.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import net.eliosoft.elios.main.LoggersManager;
import net.eliosoft.elios.server.ArtNetServerManager;
import net.eliosoft.elios.server.BadSyntaxException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


/**
 * This handler process data requests.
 * These request send commandLine value by POST method
 * @author Jeremie GASTON-RAOUL
 *
 */
public class ParamsHttpHandler implements HttpHandler {

	private static final int MAX_BUFFER_SIZE = 1024*512;
	private final ArtNetServerManager artNetServerManager = ArtNetServerManager.getInstance();

	private final transient Logger logger = LoggersManager.getInstance().getLogger(ParamsHttpHandler.class.getName());
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		if(httpExchange.getRequestMethod().equalsIgnoreCase("POST")){
			InputStream is = httpExchange.getRequestBody();
			
			byte buffer[]=new byte[ParamsHttpHandler.MAX_BUFFER_SIZE]; 
			int bytesRead = 0;
			
			StringBuilder paramsLine = new StringBuilder();
			
			while( (bytesRead = is.read(buffer)) != -1 ) {
				paramsLine.append(new String(buffer,0,bytesRead));
			}
			is.close();
			System.out.println(paramsLine);
			try {
				String[] paramsList = paramsLine.toString().split("&");
				
				HashMap<String, String> paramsMap = new HashMap<String, String>();
				for(String param : paramsList){
					String[] keyValue = param.split("=");
					if(keyValue.length == 2){
						paramsMap.put(keyValue[0], keyValue[1]);
					}
					else{
						throw new BadSyntaxException();
					}
				}
				
				String subnetParam = paramsMap.get("subnet");
				if(subnetParam != null){
					int subnet = Integer.parseInt(subnetParam);
					artNetServerManager.setSubnet(subnet);
				}
				
				String universeParam = paramsMap.get("universe");
				if(universeParam != null){
					int universe = Integer.parseInt(universeParam);
					artNetServerManager.setUniverse(universe);
				}
				
				String additiveModeParam = paramsMap.get("additiveMode");
				if(additiveModeParam != null){
					boolean additiveMode = Boolean.parseBoolean(additiveModeParam);
					artNetServerManager.setAdditiveModeEnabled(additiveMode);
				}

			} catch (BadSyntaxException e) {
				logger.severe("Bad syntax in params");
				String badRequest = "400 : Bad request !!!";
				httpExchange.sendResponseHeaders(400, badRequest.length());
				httpExchange.getResponseBody().write(badRequest.getBytes());
				httpExchange.getResponseBody().close();
			}

			String responseOk = "200 : OK !";
			httpExchange.sendResponseHeaders(200, responseOk.length());
			httpExchange.getResponseBody().write(responseOk.getBytes());
			httpExchange.getResponseBody().close();
		}
		else if(httpExchange.getRequestMethod().equalsIgnoreCase("GET")){
			String responseOk = "subnet="+artNetServerManager.getSubnet()+"&universe="+artNetServerManager.getUniverse()+"&additiveMode="+artNetServerManager.isAdditiveModeEnabled();
			httpExchange.sendResponseHeaders(200, responseOk.length());
			httpExchange.getResponseBody().write(responseOk.getBytes());
			httpExchange.getResponseBody().close();			
		}
		else{
			String badMethod = "405 : Method not allowed !!!";
			httpExchange.sendResponseHeaders(405, badMethod.length());
			httpExchange.getResponseBody().write(badMethod.getBytes());
			httpExchange.getResponseBody().close();
		}
	}

}

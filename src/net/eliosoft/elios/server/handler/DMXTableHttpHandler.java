package net.eliosoft.elios.server.handler;

import java.io.IOException;
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
public class DMXTableHttpHandler implements HttpHandler {

//	private static final int MAX_BUFFER_SIZE = 1024*512;
	private final ArtNetServerManager artNetServerManager = ArtNetServerManager.getInstance();

	private final transient Logger logger = LoggersManager.getInstance().getLogger(DMXTableHttpHandler.class.getName());
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		if(httpExchange.getRequestMethod().equalsIgnoreCase("GET")){
			byte[] dmxArray = null;
			try {
				String query = httpExchange.getRequestURI().getQuery();
				String[] paramsList = query.split("&");
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
				
				String typeParam = paramsMap.get("type");
				if(typeParam != null){
					if(typeParam.equals("input")){
						dmxArray = artNetServerManager.getCurrentInputDmxArray();
					}
					else if(typeParam.equals("output")){
						dmxArray = artNetServerManager.getCurrentOutputDmxArray();
					}
					else{
						throw new BadSyntaxException();
					}
				}
				else{
					throw new BadSyntaxException();
				}
			} catch (BadSyntaxException e) {
				logger.severe("Bad syntax in params");
				String badRequest = "400 : Bad request !!!";
				httpExchange.sendResponseHeaders(400, badRequest.length());
				httpExchange.getResponseBody().write(badRequest.getBytes());
				httpExchange.getResponseBody().close();
			}

			StringBuilder sb = new StringBuilder();
			for(Byte channelValue : dmxArray){
				int intValue = channelValue.intValue();
				sb.append(intValue < 0 ? intValue + 256 : intValue).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			
			String responseOk = sb.toString();
			httpExchange.sendResponseHeaders(200, responseOk.getBytes().length);
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

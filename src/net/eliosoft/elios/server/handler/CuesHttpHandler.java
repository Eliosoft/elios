package net.eliosoft.elios.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.eliosoft.elios.main.LoggersManager;
import net.eliosoft.elios.server.ArtNetServerManager;
import net.eliosoft.elios.server.BadSyntaxException;
import net.eliosoft.elios.server.Cue;
import net.eliosoft.elios.server.CuesManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This handler process data requests. These request send commandLine value by
 * POST method
 * 
 * @author Jeremie GASTON-RAOUL
 * 
 */
public class CuesHttpHandler implements HttpHandler {

    private static final int MAX_BUFFER_SIZE = 1024 * 512;
    private final CuesManager cuesManager = CuesManager.getInstance();
    private final ArtNetServerManager artNetServerManager = ArtNetServerManager
	    .getInstance();

    private final transient Logger logger = LoggersManager.getInstance()
	    .getLogger(CuesHttpHandler.class.getName());

    @Override
    public void handle(final HttpExchange httpExchange) throws IOException {
	if (httpExchange.getRequestMethod().equalsIgnoreCase("GET")) {
	    try {
		HashMap<String, String> paramsMap = extractParams(httpExchange
			.getRequestURI().getQuery());
		String actionParam = paramsMap.get("action");
		if (actionParam != null) {
		    if (actionParam.equals("unusedCueName")) {
			String unusedCueName = cuesManager.getUnusedCueName();
			httpExchange.sendResponseHeaders(200,
				unusedCueName.length());
			httpExchange.getResponseBody().write(
				unusedCueName.getBytes());
			httpExchange.getResponseBody().close();
		    } else if (actionParam.equals("cuesList")) {
			StringBuilder sb = new StringBuilder();
			List<Cue> cues = cuesManager.getCues();
			for (Cue cue : cues) {
			    sb.append(cue.getName()).append(",");
			}
			if (sb.length() > 0) {
			    sb.deleteCharAt(sb.length() - 1);
			}

			String responseOk = sb.toString();
			httpExchange.sendResponseHeaders(200,
				responseOk.getBytes().length);
			httpExchange.getResponseBody().write(
				responseOk.getBytes());
			httpExchange.getResponseBody().close();
		    } else {
			logger.severe("no action");
			throw new BadSyntaxException();
		    }
		} else {
		    logger.severe("actionParam=" + actionParam);
		    throw new BadSyntaxException();
		}
	    } catch (BadSyntaxException e) {
		logger.severe("Bad syntax in params");
		String badRequest = "400 : Bad request !!!";
		httpExchange.sendResponseHeaders(400, badRequest.length());
		httpExchange.getResponseBody().write(badRequest.getBytes());
		httpExchange.getResponseBody().close();
	    }
	}
	if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
	    try {
		HashMap<String, String> paramsMap = extractPostParams(httpExchange);

		String actionParam = paramsMap.get("action");
		String cueNameParam = paramsMap.get("cueName");
		if (actionParam != null && cueNameParam != null) {
		    if (actionParam.equals("load")) {
			artNetServerManager
				.setCurrentOutputDmxArray(cuesManager.getCue(
					cueNameParam).getDmxArray());
		    } else if (actionParam.equals("store")) {
			cuesManager
				.addCue(new Cue(cueNameParam,
					artNetServerManager
						.getCurrentOutputDmxArray()));
		    } else if (actionParam.equals("delete")) {
			cuesManager.removeCue(cueNameParam);
		    } else {
			logger.severe("no action");
			throw new BadSyntaxException();
		    }
		} else {
		    logger.severe("actionParam=" + actionParam + " cueName="
			    + cueNameParam);
		    throw new BadSyntaxException();
		}

	    } catch (BadSyntaxException e) {
		logger.severe("Bad syntax in params");
		String badRequest = "400 : Bad request !!!";
		httpExchange.sendResponseHeaders(400, badRequest.length());
		httpExchange.getResponseBody().write(badRequest.getBytes());
		httpExchange.getResponseBody().close();
	    }

	} else {
	    String badMethod = "405 : Method not allowed !!!";
	    httpExchange.sendResponseHeaders(405, badMethod.length());
	    httpExchange.getResponseBody().write(badMethod.getBytes());
	    httpExchange.getResponseBody().close();
	}
    }

    /**
     * @param httpExchange
     * @return
     * @throws IOException
     * @throws BadSyntaxException
     */
    private HashMap<String, String> extractPostParams(final HttpExchange httpExchange)
	    throws IOException, BadSyntaxException {
	InputStream is = httpExchange.getRequestBody();

	byte[] buffer = new byte[CuesHttpHandler.MAX_BUFFER_SIZE];
	int bytesRead = 0;

	StringBuilder query = new StringBuilder();

	while ((bytesRead = is.read(buffer)) != -1) {
	    query.append(new String(buffer, 0, bytesRead));
	}
	is.close();

	return extractParams(query.toString());
    }

    /**
     * @param query
     * @return
     * @throws BadSyntaxException
     */
    private HashMap<String, String> extractParams(final String query)
	    throws BadSyntaxException {
	String[] paramsList = query.split("&");
	HashMap<String, String> paramsMap = new HashMap<String, String>();
	for (String param : paramsList) {
	    String[] keyValue = param.split("=");
	    if (keyValue.length == 2) {
		paramsMap.put(keyValue[0], keyValue[1]);
	    } else {
		logger.severe("params = " + keyValue[0] + " " + keyValue.length);
		throw new BadSyntaxException();
	    }
	}
	return paramsMap;
    }

}

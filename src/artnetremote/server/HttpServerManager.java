/*
 * This file is part of ArtNet-Remote.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * ArtNet-Remote is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArtNet-Remote is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ArtNet-Remote. If not, see <http://www.gnu.org/licenses/>.
 */


package artnetremote.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import artnetremote.main.LoggersManager;
import artnetremote.server.handler.DataHttpHandler;
import artnetremote.server.handler.ResourceHttpHandler;

import com.sun.net.httpserver.HttpServer;

/**
 * The Manager of the Http Server
 *
 * @author Jeremie GASTON-RAOUL
 */
public class HttpServerManager {
	private static HttpServerManager instance;
	
	private final ResourceHttpHandler resourceHttpHandler = new ResourceHttpHandler();
	private final DataHttpHandler dataHttpHandler = new DataHttpHandler();
	
	/**
	 * default value for http port
	 */
	public static final int DEFAULT_HTTP_PORT = 8080;
	
	private HttpServer httpServer = null;
	private int inPort = HttpServerManager.DEFAULT_HTTP_PORT;
	
	private final transient Logger logger = LoggersManager.getInstance().getLogger(HttpServerManager.class
			.getName());
	
	private HttpServerManager(){}
	
	/**
	 * get the singleton instance of the HttpServerManager
	 * @return the instance
	 */
	public static HttpServerManager getInstance(){
		if(HttpServerManager.instance == null){
			HttpServerManager.instance = new HttpServerManager();
		}
		return HttpServerManager.instance;
	}
	
	
	/**
	 * Starts the Http Server.
	 * @throws IOException if the server is unable to start
	 */
	public void startHttp() throws IOException{
		this.initHttpServer();
		this.httpServer.start();
		logger.info("Http Started");
	}

	/**
	 * Stops the Http Server.
	 */
	public void stopHttp() {
		if(this.httpServer != null){
			this.httpServer.stop(0);
			this.httpServer = null;
			logger.info("Http Stopped");
		}
	}
	
	/**
	 * set the in port of the server
	 * @param inPort the value of the port
	 */
	public void setInPort(int inPort) {
		this.inPort = inPort;
	}
	
	private void initHttpServer() throws IOException{
		this.httpServer = HttpServer.create(new InetSocketAddress(this.inPort), 0);
		this.httpServer.createContext("/", this.resourceHttpHandler);
		this.httpServer.createContext("/data", this.dataHttpHandler);
	}
	
}

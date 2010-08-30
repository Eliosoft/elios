/*
 * This file is part of Elios.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * Elios is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Elios is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Elios. If not, see <http://www.gnu.org/licenses/>.
 */


package net.eliosoft.elios.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Manager of the Application Loggers
 *
 * @author Jeremie GASTON-RAOUL
 */
public class LoggersManager {
	private static LoggersManager instance;
	
	private HashMap<String, Logger> loggersMap = new HashMap<String, Logger>();
	
	private LoggersManager(){}
	
	/**
	 * get the singleton instance of the LoggersManager
	 * @return the instance
	 */
	public static LoggersManager getInstance(){
		if(LoggersManager.instance == null){
			LoggersManager.instance = new LoggersManager();
		}
		return LoggersManager.instance;
	}
	
	/**
	 * gets the logger with the given name
	 * @param loggerName the name of the logger to get
	 * @return the logger with the corresponding name
	 */
	public Logger getLogger(String loggerName){
		if(!loggersMap.containsKey(loggerName)){
			this.loggersMap.put(loggerName, Logger.getLogger(loggerName));
		}
		return this.loggersMap.get(loggerName);
	}
	
	/**
	 * get all the loggers of the application
	 * @return a list containing all the loggers
	 */
	public List<Logger> getLoggersList(){
		return new ArrayList<Logger>(this.loggersMap.values());
	}
}

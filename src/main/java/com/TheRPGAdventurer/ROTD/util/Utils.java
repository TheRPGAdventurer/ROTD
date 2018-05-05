package com.TheRPGAdventurer.ROTD.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

public class Utils {
	
	private static Logger logger;
	
	public static Logger getLogger() {
		if(logger == null) {
			logger = LogManager.getFormatterLogger(RealmOfTheDragons.MODID);
		}
		return logger;
	}
	
}

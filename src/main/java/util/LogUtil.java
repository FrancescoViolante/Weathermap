package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {

	private static Logger logger;

	static{

		logger = LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
	}

	public static void logMethodStart() {

		logger.info("START >> :" + Thread.currentThread().getStackTrace()[2].getClassName()+ "." +
				Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	public static void logMethodEnd() {

		logger.info("END >> :" + Thread.currentThread().getStackTrace()[2].getClassName()+ "." +
				Thread.currentThread().getStackTrace()[2].getMethodName());
	}
}

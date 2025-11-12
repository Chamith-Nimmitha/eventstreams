package com.test.utils;

import com.test.exceptions.ImplementationNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Chamith_Nimmitha
 */
public class ServiceLoaderUtils {
	private static final Logger logger = LoggerFactory.getLogger(ServiceLoaderUtils.class);

	public static <T> T findImplementation(Class<T> baseClass) {
		ServiceLoader<T> serviceLoader = ServiceLoader.load(baseClass);
		Iterator var2 = serviceLoader.iterator();
		if (var2.hasNext()) {
			T impl = (T)var2.next();
			logger.info("Using MyService implementation: {}", impl.getClass().getSimpleName());
			return impl;
		} else {
			logger.error("Implementation not found for base type: {}", baseClass.getName());
			throw new ImplementationNotFound("Implementation not found for base type: " + baseClass.getName());
		}
	}

	public static <T> T findImplementationOrDefault(Class<T> baseClass, T defaultImpl) {
		for(T impl : ServiceLoader.load(baseClass)) {
			if (!impl.getClass().getName().equals(defaultImpl.getClass().getName())) {
				logger.info("Implementation found. Interface: {}, Imple: {}", baseClass.getName(), impl.getClass().getSimpleName());
				return impl;
			}
		}

		logger.info("Using default implementation. Interface: {}, Default Imple: {}", baseClass.getName(), defaultImpl.getClass().getSimpleName());
		return defaultImpl;
	}
}

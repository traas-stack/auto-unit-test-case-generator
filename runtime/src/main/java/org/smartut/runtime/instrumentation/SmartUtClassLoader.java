/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and SmartUt
 * contributors
 *
 * This file is part of SmartUt.
 *
 * SmartUt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * SmartUt is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SmartUt. If not, see <http://www.gnu.org/licenses/>.
 */
package org.smartut.runtime.instrumentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.SmartUtRunner;
import org.smartut.runtime.constants.InstrumentType;
import org.smartut.runtime.util.Inputs;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instrumenting class loader used in special cases in the generated JUnit tests
 * when Java Agent is not used
 */
public class SmartUtClassLoader extends ClassLoader {
	private final static Logger logger = LoggerFactory.getLogger(SmartUtClassLoader.class);
	private final RuntimeInstrumentation instrumentation;
	private final ClassLoader classLoader;
	private final Map<String, Class<?>> classes = new HashMap<>();
	private final Set<String> skipInstrumentationForPrefix = new HashSet<>();

	public SmartUtClassLoader() {
		this(new RuntimeInstrumentation());
	}

	/**
	 * <p>
	 * Constructor for InstrumentingClassLoader.
	 * </p>
	 * 
	 * @param instrumentation
	 *            a {@link org.smartut.runtime.instrumentation.RuntimeInstrumentation}
	 *            object.
	 */
	public SmartUtClassLoader(RuntimeInstrumentation instrumentation) {
		super(SmartUtClassLoader.class.getClassLoader());
		classLoader = SmartUtClassLoader.class.getClassLoader();
		this.instrumentation = instrumentation;
	}

	public void skipInstrumentation(String prefix) throws IllegalArgumentException{
		Inputs.checkNull(prefix);
		skipInstrumentationForPrefix.add(prefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if ("<smartut>".equals(name))
			throw new ClassNotFoundException();
		Class<?> result = classes.get(name);
		String jsonPrefix = "com.alibaba.fastjson.";
		if (Objects.nonNull(result)) {
			return result;
		} else if (RuntimeInstrumentation.checkIfCanInstrument(name)) {
			return normalInstrumentClass(name);
		} else if (RuntimeSettings.jsonInstrumentationClass && name.startsWith(jsonPrefix)) {
			return instrumentClass(name,InstrumentType.JSON);
		}
		result = findLoadedClass(name);
		if (Objects.nonNull(result)) {
			return result;
		}
		result = classLoader.loadClass(name);
		return result;
	}

	private Class<?> normalInstrumentClass(String name) throws ClassNotFoundException{
		if (Thread.currentThread().getContextClassLoader() == null) {
			Thread.currentThread().setContextClassLoader(SmartUtRunner.SMART_UT_CLASS_LOADER);
		}
		logger.info("Seeing class for first time: " + name);
		return instrumentClass(name,InstrumentType.NORMAL);
	}

	private Class<?> instrumentClass(String fullyQualifiedTargetClass, InstrumentType type)
	        throws ClassNotFoundException {
		logger.info("Instrumenting class '{}'", fullyQualifiedTargetClass);
		InputStream is = null;
		try {
			String className = fullyQualifiedTargetClass.replace('.', '/');
			is = classLoader.getResourceAsStream(className + ".class");
			if (Objects.isNull(is)) {
				throw new ClassNotFoundException("Class '" + className + ".class"
						+ "' should be in target project, but could not be found!");
			}
			createPackageDefinition(fullyQualifiedTargetClass);
			byte[] byteBuffer = instrumentByte(fullyQualifiedTargetClass, className, type, is);
			Class<?> result = defineClass(fullyQualifiedTargetClass, byteBuffer, 0,
					byteBuffer.length);
			classes.put(fullyQualifiedTargetClass, result);
			logger.info("Keeping class: {}", fullyQualifiedTargetClass);
			return result;
		} catch (Throwable t) {
			logger.info("Error while loading class: " + t);
			throw new ClassNotFoundException(t.getMessage(), t);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new Error(e);
				}
			}
		}
	}

	private byte[] instrumentByte(String fullyQualifiedTargetClass,String className,InstrumentType type,InputStream is) throws Exception{
		switch (type) {
			case JSON:
				return instrumentation.transformJsonBytes(className, new ClassReader(is));
			case NORMAL:
				boolean shouldSkip = skipInstrumentationForPrefix.stream().anyMatch(fullyQualifiedTargetClass::startsWith);
				return instrumentation.transformBytes(className, new ClassReader(is), shouldSkip);
			default:
				throw new ClassNotFoundException("Class '" + className + ".class"
						+ "' should be in target project, but could not be found!");
		}
	}


	/**
	 * Before a new class is defined, we need to create a package definition for it
	 * 
	 * @param className class-name
	 */
	private void createPackageDefinition(String className){
		int i = className.lastIndexOf('.');
		if (i != -1) {
		    String pkgname = className.substring(0, i);
		    // Check if package already loaded.
		    Package pkg = getPackage(pkgname);
		    if(pkg==null){
		    		definePackage(pkgname, null, null, null, null, null, null, null);
		    		logger.info("Defined package (3): "+getPackage(pkgname)+", "+getPackage(pkgname).hashCode());
		    }
	    }
	}
	
	public RuntimeInstrumentation getInstrumentation() {
		return instrumentation;
	}
	public ClassLoader getOriginalClassLoader() {
		return classLoader;
	}
	
}

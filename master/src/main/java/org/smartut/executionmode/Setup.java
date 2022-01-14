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
package org.smartut.executionmode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.NoSuchParameterException;
import org.smartut.utils.LoggingUtils;

public class Setup {

	public static final String NAME = "setup";
	
	public static Option getOption(){
		return new Option(NAME,true,"Create smartut-files with property file");
	}

	public static Object execute(List<String> javaOpts, CommandLine line) {
		boolean inheritanceTree = line.hasOption("inheritanceTree");
		setup(line.getOptionValue("setup"), line.getArgs(), javaOpts, inheritanceTree);
		return null;
	}

	private static void addEntryToCP(String entry){
		if (!Properties.CP.isEmpty()){
			Properties.CP += File.pathSeparator;
		}
		Properties.CP += entry;
	}

	private static void setup(String target, String[] args, List<String> javaArgs,
	        boolean doInheritance) {

		Properties.CP = "";

		/*
			Important that target will be first on the CP.
			Otherwise, if for some reasons a dependency uses a same class,
			that would take precedence
		 */
		File targetFile = new File(target);
		if (targetFile.exists()) {
			if (targetFile.isDirectory() || target.endsWith(".jar")) {
				addEntryToCP(target);
			} else if (target.endsWith(".class")) {
				String pathName = targetFile.getParent();
				addEntryToCP(pathName);
			} else {
				LoggingUtils.getSmartUtLogger().info("Failed to set up classpath for "
						+ target);
				return;
			}
		}

		if (args.length > 0) {
			for (final String arg : args) {
				String element = arg.trim();
				if (element.isEmpty()) {
					continue;
				}
				addEntryToCP(element);
			}
		}

		Properties.MIN_FREE_MEM = 0; //TODO why this is done???
		File directory = new File(SmartUt.base_dir_path + File.separator + Properties.OUTPUT_DIR);
		if (!directory.exists()) {
			directory.mkdir();
		}

		if (doInheritance) {
			try {
				String fileName = SmartUt.generateInheritanceTree(Properties.CP);
				FileUtils.copyFile(new File(fileName), new File(Properties.OUTPUT_DIR
				        + File.separator + "inheritance.xml.gz"));
				
				 /* 
				  * we need to use '/' instead of File.separator because this value will be written on a text file.
				  * As the relative path will be given to a File object, this will work also on a Windows machine 
				  */
				Properties.getInstance().setValue("inheritance_file",
				                                  Properties.OUTPUT_DIR + "/"
				                                          + "inheritance.xml.gz");
			} catch (IOException | IllegalArgumentException | NoSuchParameterException | IllegalAccessException e) {
				LoggingUtils.getSmartUtLogger().error("* Error while creating inheritance tree: " + e.getMessage());
			}
		}

		LoggingUtils.getSmartUtLogger().info("* Creating new smartut.properties in "
		                                         + SmartUt.base_dir_path + File.separator
		                                         + Properties.OUTPUT_DIR);
		LoggingUtils.getSmartUtLogger().info("* Classpath: " + Properties.CP);
		Properties.getInstance().writeConfiguration(SmartUt.base_dir_path + File.separator
		                                                    + Properties.OUTPUT_DIR
		                                                    + File.separator
		                                                    + "smartut.properties");
	}
	

}

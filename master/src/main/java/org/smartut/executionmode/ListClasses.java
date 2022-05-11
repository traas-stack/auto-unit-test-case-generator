/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * Copyright (C) 2021- SmartUt contributors
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
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.TestGenerationContext;
import org.smartut.classpath.ClassPathHacker;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.classpath.ResourceList;
import org.smartut.utils.LoggingUtils;

public class ListClasses {

	public static final String NAME = "listClasses";
	
	public static Option getOption(){
		return new Option(NAME, "list the testable classes found in the specified classpath/prefix");
	}
	
	public static Object execute(Options options, CommandLine line) {
		if (line.hasOption("prefix"))
			listClassesPrefix(line.getOptionValue("prefix"));
		else if (line.hasOption("target"))
			listClassesTarget(line.getOptionValue("target"));
		else if (SmartUt.hasLegacyTargets())
			listClassesLegacy();
		else {
			LoggingUtils.getSmartUtLogger().error("Please specify target prefix ('-prefix' option) or classpath entry ('-target' option) to list testable classes");
			Help.execute(options);
		}
		return null;
	}


	private static void listClassesTarget(String target) {
		Set<String> classes = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getAllClasses(target, false);
		try {
			ClassPathHacker.addFile(target);
		} catch (IOException e) {
			// Ignore?
		}
		for (String sut : classes) {
			try {
				if (ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).isClassAnInterface(sut)) {
					continue;
				}
				if (!Properties.USE_DEPRECATED && ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).isClassDeprecated(sut)) {
					continue;
				}
				if (! ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).isClassTestable(sut)) {
					continue;
				}
			} catch (IOException e) {
				LoggingUtils.getSmartUtLogger().error("Could not load class: " + sut);
				continue;
			}
			
			String row = "";
			String groupId = Properties.GROUP_ID;
			if(groupId!=null && !groupId.isEmpty() && !groupId.equals("none")){
				row += groupId + "\t";
			}
			row += sut;
			
			LoggingUtils.getSmartUtLogger().info(row);
		}
	}
	
	private static void listClassesLegacy() {
		File directory = new File(Properties.OUTPUT_DIR);
		String[] extensions = { "task" };
		for (File file : FileUtils.listFiles(directory, extensions, false)) {
			LoggingUtils.getSmartUtLogger().info(file.getName().replace(".task", ""));
		}
	}

	private static void listClassesPrefix(String prefix) {
		
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		Set<String> classes = new LinkedHashSet<>();
		
		for (String classPathElement : cp.split(File.pathSeparator)) {
			classes.addAll(ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getAllClasses(classPathElement, prefix, false));
			try {
				ClassPathHacker.addFile(classPathElement);
			} catch (IOException e) {
				// Ignore?
			}
		}
		for (String sut : classes) {
			try {
				if (ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).isClassAnInterface(sut)) {
					continue;
				}
			} catch (IOException e) {
				LoggingUtils.getSmartUtLogger().error("Could not load class: " + sut);
				continue;
			}
			LoggingUtils.getSmartUtLogger().info(sut);
		}
	}
}

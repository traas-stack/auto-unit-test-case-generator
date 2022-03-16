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
package org.smartut.eclipse.popup.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.widgets.Shell;
import org.smartut.Properties;
import org.smartut.eclipse.Activator;
import org.smartut.shaded.org.apache.commons.lang3.SystemUtils;
import org.osgi.framework.Bundle;

/**
 * @author Gordon Fraser
 * 
 */
public abstract class TestGenerationAction extends AbstractHandler {

	private static String SMARTUT_CP = null;

	protected Shell shell;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	//@Override
	//public abstract void run(IAction action);

	@Override
	public abstract Object execute(ExecutionEvent event) throws ExecutionException;
	
	/**
	 * Main action: check if smartut-tests exists, and add test generation job
	 * 
	 * @param target
	 */
	public void generateTests(IResource target) {
		Boolean disabled = System.getProperty("smartut.disable") != null; //  && System.getProperty("smartut.disable").equals("1")
		if ( disabled ) {
			MessageDialog.openInformation(shell, "Sorry!", "The SmartUt Plugin is disabled :(");
			return;
		}
		if(SystemUtils.IS_JAVA_9 || SystemUtils.IS_JAVA_10) {
			MessageDialog.openWarning(shell, "Java version problem", Properties.JAVA_VERSION_WARN_MSG);
			return;
		}
		System.out.println("SmartUt Plugin is enabled");
		System.out.println("[TestGenerationAction] Generating tests for " + target.toString());
		IFolder folder = target.getProject().getFolder("smartut-tests");
		if (!folder.exists()) {
			// Create smartut-tests directory and add as source folder
			try {
				folder.create(false, true, null);
				IJavaProject jProject = JavaCore.create(target.getProject());

				IPackageFragmentRoot root = jProject.getPackageFragmentRoot(folder);
				IClasspathEntry[] oldEntries = jProject.getRawClasspath();
				IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
				System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
				newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
				jProject.setRawClasspath(newEntries, null);

			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		addTestJob(target);
	}

	/**
	 * Add a new test generation job to the job queue
	 * 
	 * @param target
	 */
	protected void addTestJob(final IResource target) {
		IJavaElement element = JavaCore.create(target);
		if (element == null) {
			return;
		}
		IJavaElement packageElement = element.getParent();

		String packageName = packageElement.getElementName();

		final String targetClass = (!packageName.isEmpty() ? packageName + "." : "")
		        + target.getName().replace(".java", "").replace(File.separator, ".");
		System.out.println("* Scheduling new automated job for " + targetClass);
		final String targetClassWithoutPackage = target.getName().replace(".java", "");

		final String suiteClassName = targetClass + Properties.JUNIT_SUFFIX;
		
		final String suiteFileName = target.getProject().getLocation() + "/smartut-tests/" +
										suiteClassName.replace('.', File.separatorChar) + ".java";
		System.out.println("Checking for " + suiteFileName);
		File suiteFile = new File(suiteFileName);
		Job job = null;
		if (suiteFile.exists()) {

			MessageDialog dialog = new MessageDialog(
			        shell,
			        "Existing test suite found",
			        null, // image
			        "A test suite for class \""
			                + targetClass
			                + "\" already exists. SmartUt will overwrite this test suite. Do you really want to proceed?",
			        MessageDialog.QUESTION_WITH_CANCEL, new String[] { "Overwrite", "Extend",
			                "Rename Original", "Cancel" }, 0);

			int returnCode = dialog.open();
			// 0 == overwrite
			// 1 == extend
			if(returnCode == 1) {
				IWorkspaceRoot wroot = target.getWorkspace().getRoot();
				IResource suiteResource = wroot.getFileForLocation(new Path(suiteFileName));
				job = new TestExtensionJob(shell, suiteResource, targetClass, suiteClassName);
			}
			else if (returnCode == 2) {
				// 2 == Rename
				renameSuite(target, packageName, targetClassWithoutPackage + Properties.JUNIT_SUFFIX + ".java");
			} else if (returnCode > 2) {
				// Cancel
				return;
			}
		}

		if(job == null)
			job = new TestGenerationJob(shell, target, targetClass, suiteClassName);
		
		job.setPriority(Job.SHORT);
		IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
		ISchedulingRule rule = ruleFactory.createRule(target.getProject());

		//IFolder folder = proj.getFolder(ResourceUtil.SMARTUT_FILES);
		job.setRule(rule);
		job.setUser(true);
		job.schedule(); // start as soon as possible
	}

	/**
	 * If we move away an old SmartUt test suite for a class then we rename it
	 * to <name><num>.java.
	 * 
	 * @param name
	 * @param cus
	 * @return
	 */
	public String getNewName(String name, ICompilationUnit[] cus) {
		int num = 0;
		String className = name.replace(".java", "");
		String newName = className + num + ".java";
		boolean found = true;
		while (found) {
			found = false;
			for (ICompilationUnit cu : cus) {
				if (cu.getElementName().equals(newName)) {
					num++;
					newName = className + num + ".java";
					found = true;
					break;
				}
			}
		}
		return newName;
	}

	/**
	 * If there already exists a test suite for the chosen target class, then
	 * renaming the old test suite is one of the options the user can choose
	 * 
	 * @param suite
	 * @param packageName
	 * @param suitePath
	 */
	public void renameSuite(final IResource suite, String packageName, String suitePath) {
		System.out.println("Renaming " + suitePath + " in package " + packageName);
		IPackageFragment[] packages;
		try {
			packages = JavaCore.create(suite.getProject()).getPackageFragments();
			System.out.println("Packages found: "+packages.length);
			for (IPackageFragment f : packages) {
				if ((f.getKind() == IPackageFragmentRoot.K_SOURCE) 
						&& (f.getElementName().equals(packageName))) {
					
					for (ICompilationUnit cu : f.getCompilationUnits()) {
						
						String cuName = cu.getElementName();
						System.out.println("targetPath = " + suitePath);
						System.out.println("cuName = " + cuName);
						if (cuName.equals(suitePath)) {
							RefactoringContribution contribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_COMPILATION_UNIT);
							RenameJavaElementDescriptor descriptor = (RenameJavaElementDescriptor) contribution.createDescriptor();
							descriptor.setProject(cu.getResource().getProject().getName());
							String newName = getNewName(cuName, f.getCompilationUnits());
							System.out.println("New name for Test Suite: " + newName);
							descriptor.setNewName(newName); // new name for a Class
							descriptor.setJavaElement(cu);

							RefactoringStatus status = new RefactoringStatus();
							try {
								Refactoring refactoring = descriptor.createRefactoring(status);

								IProgressMonitor monitor = new NullProgressMonitor();
								refactoring.checkInitialConditions(monitor);
								refactoring.checkFinalConditions(monitor);
								Change change = refactoring.createChange(monitor);
								change.perform(monitor);

							} catch (CoreException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * If we generate JUnit tests, we need to make sure that JUnit is on the
	 * classpath of the project, otherwise we will see compile errors
	 * 
	 * @param project
	 */
	public void fixJUnitClassPath(IJavaProject project) {
		IPath junitPath = JUnitCore.JUNIT4_CONTAINER_PATH;

		boolean hasJUnit = false;
		boolean hasSmartUt = false;
		boolean hasOldSmartUt = false;

		try {
			Path containerPath = new Path("org.smartut.eclipse.classpathContainerInitializer");
			IClasspathContainer container = JavaCore.getClasspathContainer(containerPath, project);
			System.out.println("SmartUt JAR at: " + container.getPath().toOSString());

			IClasspathEntry[] oldEntries = project.getRawClasspath();
			ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(oldEntries.length + 1);

			IClasspathEntry cpentry = JavaCore.newContainerEntry(junitPath);

			for (int i = 0; i < oldEntries.length; i++) {
				IClasspathEntry curr = oldEntries[i];

				// Check if JUnit is already in the build path
				if (curr.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
					IPath path = curr.getPath();
					if (path.equals(cpentry.getPath())) {
						hasJUnit = true;
					}
					if (path.equals(container.getPath())) {
						hasSmartUt = true;
					}
				} else if (curr.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					// Check for older SmartUt entries
					IPath path = curr.getPath();
					if (path.toFile().getName().equals(Activator.SMARTUT_JAR)) {
						if (path.equals(container.getPath())) {
							System.out.println("Is current smartut!");
							hasSmartUt = true;
						} else {
							System.out.println("Is NOT current smartut!");
							hasOldSmartUt = true;
							continue;
						}
					}
					if (path.equals(cpentry.getPath())) {
						hasJUnit = true;
					}
					if (path.equals(container.getPath())) {
						hasSmartUt = true;
					}
				}

				if (curr != null) {
					newEntries.add(curr);
				}
			}

			if (hasJUnit && hasSmartUt && !hasOldSmartUt) {
				return;
			}

			// add the entry
			if (!hasJUnit) {
				newEntries.add(cpentry);
			}

			if (!hasSmartUt && container != null) {
				for (IClasspathEntry entry : container.getClasspathEntries()) {
					newEntries.add(entry);
				}
			}
			
			System.out.println("New classpath: " + newEntries);

			// newEntries.add(JavaCore.newContainerEntry(SmartUtClasspathContainer.ID));

			// Convert newEntries to an array
			IClasspathEntry[] newCPEntries = newEntries.toArray(new IClasspathEntry[newEntries.size()]);
			project.setRawClasspath(newCPEntries, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This is tricky because the smartut jar in the deployed plugin is
	 * contained in the plugin jar, but for the client process we need it
	 * explicitly on the classpath. Hence, if we see that the smartut jar is in
	 * another jar, we extract it to a temporary location
	 * 
	 * @return Location of the jar file
	 */
	public static String getSmartUtJar() {
		if (SMARTUT_CP != null)
			return SMARTUT_CP;

		Bundle bundle = Platform.getBundle(Activator.SMARTUT_CORE_BUNDLE);
		URL url = bundle.getEntry(Activator.SMARTUT_JAR);
				
		try {
			URL smartutLib = FileLocator.resolve(url);
			System.out.println("SmartUt JAR is at " + smartutLib.getPath());
			if (smartutLib.getPath().startsWith("file")) {
				System.out.println("Need to extract jar");
				SMARTUT_CP = setupJar(smartutLib);
			} else {
				System.out.println("Don't need to extract jar");
				SMARTUT_CP = smartutLib.getFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SMARTUT_CP;
	}

	/**
	 * Performs the actual extraction of the jar file to a temporary directory
	 * 
	 * @param smartutLib
	 * @return
	 * @throws IOException
	 */
	protected static String setupJar(URL smartutLib) throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");
		String jarName = tmpDir + File.separator + Activator.SMARTUT_JAR;
		System.out.println("Copying jar file to " + jarName);
		File tempJar = new File(jarName);
		writeFile(smartutLib.openStream(), new File(jarName));
		return tempJar.getPath();
	}

	/**
	 * Helper function to copy files
	 * 
	 * @param in
	 * @param dest
	 */
	private static void writeFile(InputStream in, File dest) {
		try {
			dest.deleteOnExit();
			System.out.println("Creating file: " + dest.getPath());
			if (!dest.exists()) {
				OutputStream out = new FileOutputStream(dest);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
//	@Override
//	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
//		shell = targetPart.getSite().getShell();
//	}
}

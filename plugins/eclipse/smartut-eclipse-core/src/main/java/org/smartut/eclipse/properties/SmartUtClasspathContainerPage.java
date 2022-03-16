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
package org.smartut.eclipse.properties;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.smartut.eclipse.Activator;
import org.osgi.framework.Bundle;

/**
 * @author Gordon Fraser
 * 
 */
public class SmartUtClasspathContainerPage extends org.eclipse.jface.wizard.WizardPage
        implements IClasspathContainerPage {

	private Composite container;

	/**
	 * @param pageName
	 */
	public SmartUtClasspathContainerPage() {
		super("SmartUt classpath entry");
		setTitle("SmartUt runtime library");
		setDescription("Add SmartUt runtime library to project classpath");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		Label label1 = new Label(container, SWT.NULL);
		label1.setText("SmartUt jar found at: ");

		Label label2 = new Label(container, SWT.NULL);
		label2.setText("" + getPath());

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#finish()
	 */
	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return true;
	}

	public IPath getPath() {
		Bundle bundle = Platform.getBundle(Activator.SMARTUT_CORE_BUNDLE);
		URL url = bundle.getEntry(Activator.SMARTUT_JAR);
		try {
			URL smartutLib = FileLocator.resolve(url);
			System.out.println("SmartUt jar is at " + smartutLib.getPath());
			return new Path(smartutLib.getPath());
		} catch (Exception e) {
			System.err.println("Error accessing SmartUt jar at " + url);
		}
		System.err.println("Did not find SmartUt jar!");

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#getSelection()
	 */
	@Override
	public IClasspathEntry getSelection() {
		return JavaCore.newLibraryEntry(getPath(), null, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#setSelection(org.eclipse.jdt.core.IClasspathEntry)
	 */
	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		// TODO Auto-generated method stub

	}

}

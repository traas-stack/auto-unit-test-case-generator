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

package org.smartut.runtime.testdata;

import java.io.File;
import java.io.Serializable;

/**
 * A object wrapper for file paths accessed by the SUTs.
 *   
 * @author fraser
 */
public class SmartUtFile implements Serializable{

	private static final long serialVersionUID = -4900126189189434483L;

	private final String path;

	private final String userDir = System.getProperty("user.dir");

	/**
	 * <p>Constructor for SmartUtFile.</p>
	 *
	 * @param path a {@link java.lang.String} object.
	 */
	public SmartUtFile(String path) {
		
		if(path==null){
			this.path = null;
		} else {
			this.path = (new File(path)).getAbsolutePath();
		}
	}

	/**
	 * <p>Getter for the field <code>path</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/** {@inheritDoc} */
	@Override
	public String toString() {
		if(path.startsWith(userDir)) {
			return path.length() > userDir.length() ? path.substring(userDir.length()+1) : path;
		}
		else {
			return path;
		}
	}
}

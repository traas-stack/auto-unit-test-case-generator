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
package org.smartut.rmi.service;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.smartut.ga.Chromosome;
import org.smartut.statistics.RuntimeVariable;

public class DummyClientNodeImpl<T extends Chromosome<T>> extends ClientNodeImpl<T> {

    private static final long serialVersionUID = -354329589467033654L;

    public DummyClientNodeImpl(){
		
	}
	
	public DummyClientNodeImpl(Registry registry, String identifier) {
		super(registry, identifier);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeState(ClientState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeState(ClientState state,
			ClientStateInformation information) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatistics(T individual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trackOutputVariable(RuntimeVariable name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waitUntilDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startNewSearch() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelCurrentSearch() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean waitUntilFinished(long timeoutInMs) throws RemoteException,
			InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doCoverageAnalysis() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getClientRmiIdentifier() {
		return "dummy";
	}


}

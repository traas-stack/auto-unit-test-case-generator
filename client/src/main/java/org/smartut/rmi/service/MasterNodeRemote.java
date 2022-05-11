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
package org.smartut.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import org.smartut.config.PropertiesLoader.NoSuchParameterException;
import org.smartut.ga.Chromosome;
import org.smartut.result.TestGenerationResult;
import org.smartut.statistics.RuntimeVariable;

/**
 * Master Node view in the client process.  
 * @author arcuri
 *
 */
public interface MasterNodeRemote extends Remote {

	String RMI_SERVICE_NAME = "MasterNode";
	
	/*
	 * Note: we need names starting with 'smartut' here, because those names are accessed
	 * through reflections and used in the checks of the sandbox 
	 */
	
	void smartut_registerClientNode(String clientRmiIdentifier) throws RemoteException;
	
	void smartut_informChangeOfStateInClient(String clientRmiIdentifier, ClientState state, ClientStateInformation information) throws RemoteException;
	
	void smartut_collectStatistics(String clientRmiIdentifier, Chromosome individual) throws RemoteException;

	void smartut_collectStatistics(String clientRmiIdentifier, RuntimeVariable variable, Object value) throws RemoteException;

	void smartut_collectTestGenerationResult(String clientRmiIdentifier, List<TestGenerationResult> results) throws RemoteException;

	void smartut_flushStatisticsForClassChange(String clientRmiIdentifier) throws RemoteException;

	void smartut_updateProperty(String clientRmiIdentifier, String propertyName, Object value) throws RemoteException, IllegalArgumentException, IllegalAccessException, NoSuchParameterException;
	
	void smartut_migrate(String clientRmiIdentifier, Set<? extends Chromosome> migrants) throws RemoteException;

    void smartut_collectBestSolutions(String clientRmiIdentifier, Set<? extends Chromosome> solutions) throws RemoteException;
}

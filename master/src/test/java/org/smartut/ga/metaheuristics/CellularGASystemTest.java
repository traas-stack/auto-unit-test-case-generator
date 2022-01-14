package org.smartut.ga.metaheuristics;

import java.util.ArrayList;
import java.util.List;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.Properties.Algorithm;
import org.smartut.Properties.Criterion;
import org.smartut.Properties.StoppingCondition;
import org.smartut.ga.Chromosome;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.ClassHierarchyIncludingInterfaces;
import com.examples.with.different.packagename.XMLElement2;

/**
 * CellularGA system test
 * @author Nasser Albunian
 *
 */
public class CellularGASystemTest extends SystemTestBase{
	
	public List<Chromosome> setup(StoppingCondition sc, int budget, String cut){
		Properties.CRITERION = new Criterion[1];
		Properties.CRITERION[0] = Criterion.BRANCH;
		Properties.ALGORITHM = Algorithm.CELLULAR_GA;
	    Properties.POPULATION = 50;
	    Properties.STOPPING_CONDITION = sc;
	    Properties.SEARCH_BUDGET = budget;
	    Properties.MINIMIZE = false;

	    SmartUt smartut = new SmartUt();

	    String targetClass = cut;
	    Properties.TARGET_CLASS = targetClass;

	    String[] command = new String[] {"-generateSuite", "-class", targetClass};

	    Object result = smartut.parseCommandLine(command);
	    Assert.assertNotNull(result);

	    GeneticAlgorithm<?> ga = getGAFromResult(result);
	    
	    List<Chromosome> population = new ArrayList<>(ga.getBestIndividuals());
	    
	    return population;
	}

	@Test
	public void testCellularGAWithLimitedTime(){
		
		List<Chromosome> population = this.setup(StoppingCondition.MAXTIME, 15, XMLElement2.class.getCanonicalName());
		
	    for (Chromosome p : population) {
            Assert.assertNotEquals(p.getCoverage(), 1.0);
        }
	}
	
	@Test
	public void testCellularGAWithLimitedGenerations(){
		
	    List<Chromosome> population = this.setup(StoppingCondition.MAXGENERATIONS, 10, ClassHierarchyIncludingInterfaces.class.getCanonicalName());
	    
	    for (Chromosome p : population) {
            Assert.assertNotEquals(p.getCoverage(), 1.0);
        }
	}
}

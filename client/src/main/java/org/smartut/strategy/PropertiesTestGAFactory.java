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
package org.smartut.strategy;

import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.Properties.Strategy;
import org.smartut.Properties.TheReplacementFunction;
import org.smartut.TestGenerationContext;
import org.smartut.coverage.branch.BranchPool;
import org.smartut.coverage.mutation.MutationTimeoutStoppingCondition;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.ga.FitnessReplacementFunction;
import org.smartut.ga.metaheuristics.*;
import org.smartut.ga.metaheuristics.mapelites.MAPElites;
import org.smartut.ga.metaheuristics.mulambda.MuLambdaEA;
import org.smartut.ga.metaheuristics.mulambda.MuPlusLambdaEA;
import org.smartut.ga.metaheuristics.mulambda.OnePlusLambdaLambdaGA;
import org.smartut.ga.metaheuristics.mulambda.OnePlusOneEA;
import org.smartut.ga.operators.crossover.CrossOverFunction;
import org.smartut.ga.operators.crossover.SinglePointCrossOver;
import org.smartut.ga.operators.crossover.SinglePointFixedCrossOver;
import org.smartut.ga.operators.crossover.SinglePointRelativeCrossOver;
import org.smartut.ga.operators.crossover.UniformCrossOver;
import org.smartut.ga.operators.ranking.FastNonDominatedSorting;
import org.smartut.ga.operators.ranking.RankBasedPreferenceSorting;
import org.smartut.ga.operators.ranking.RankingFunction;
import org.smartut.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.smartut.ga.operators.selection.FitnessProportionateSelection;
import org.smartut.ga.operators.selection.RankSelection;
import org.smartut.ga.operators.selection.SelectionFunction;
import org.smartut.ga.operators.selection.TournamentSelection;
import org.smartut.ga.operators.selection.TournamentSelectionRankAndCrowdingDistanceComparator;
import org.smartut.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.StoppingCondition;
import org.smartut.ga.stoppingconditions.ZeroFitnessStoppingCondition;
import org.smartut.seeding.TestCaseRecycler;
import org.smartut.testcase.RelativeTestLengthBloatControl;
import org.smartut.testcase.TestCaseReplacementFunction;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.factories.AllMethodsTestChromosomeFactory;
import org.smartut.testcase.factories.JUnitTestCarvedChromosomeFactory;
import org.smartut.testcase.factories.RandomLengthTestFactory;
import org.smartut.testcase.secondaryobjectives.TestCaseSecondaryObjective;
import org.smartut.utils.ArrayUtil;

/**
 * Factory for GA for tests
 * 
 * @author gordon
 *
 */
public class PropertiesTestGAFactory
		extends PropertiesSearchAlgorithmFactory<TestChromosome> {

	protected ChromosomeFactory<TestChromosome> getChromosomeFactory() {
		switch (Properties.STRATEGY) {
		case ONEBRANCH:
			switch (Properties.TEST_FACTORY) {
			case ALLMETHODS:
				logger.info("Using all methods chromosome factory");
				return new AllMethodsTestChromosomeFactory();
			case RANDOM:
				logger.info("Using random chromosome factory");
				return new RandomLengthTestFactory();
			case JUNIT:
				logger.info("Using seeding chromosome factory");
				return new JUnitTestCarvedChromosomeFactory(new RandomLengthTestFactory());
			default:
			}
		case ENTBUG:
			return new RandomLengthTestFactory();
		default:
			break;
		}
		throw new RuntimeException("Unsupported test factory: "
		        + Properties.TEST_FACTORY);
	}
	
	private GeneticAlgorithm<TestChromosome> getGeneticAlgorithm(ChromosomeFactory<TestChromosome> factory) {
		switch (Properties.ALGORITHM) {
		case ONE_PLUS_ONE_EA:
			logger.info("Chosen search algorithm: (1+1)EA");
			return new OnePlusOneEA<>(factory);
		case MU_PLUS_LAMBDA_EA:
		  logger.info("Chosen search algorithm: (Mu+Lambda)EA");
          return new MuPlusLambdaEA<>(factory, Properties.MU, Properties.LAMBDA);
		case MU_LAMBDA_EA:
			logger.info("Chosen search algorithm: (Mu,Lambda)EA");
			return new MuLambdaEA<>(factory, Properties.MU, Properties.LAMBDA);
        case BREEDER_GA:
				logger.info("Chosen search algorithm: BreederGA");
				return new BreederGA<>(factory);
		case MONOTONIC_GA:
			logger.info("Chosen search algorithm: MonotonicGA");
			{
				MonotonicGA<TestChromosome> ga = new MonotonicGA<>(factory);
				if (Properties.REPLACEMENT_FUNCTION == TheReplacementFunction.FITNESSREPLACEMENT) {
					// user has explicitly asked for this replacement function
					ga.setReplacementFunction(new FitnessReplacementFunction<>());
				} else {
					ga.setReplacementFunction(new TestCaseReplacementFunction());
				}
				return ga;
			}
		case CELLULAR_GA:
			logger.info("Chosen search algorithm: CellularGA");
			{
				CellularGA<TestChromosome> ga = new CellularGA<>(Properties.MODEL, factory);
				if (Properties.REPLACEMENT_FUNCTION == TheReplacementFunction.FITNESSREPLACEMENT) {
					// user has explicitly asked for this replacement function
					ga.setReplacementFunction(new FitnessReplacementFunction<>());
				} else {
					ga.setReplacementFunction(new TestCaseReplacementFunction());
				}
				return ga;
			}
		case STEADY_STATE_GA:
			logger.info("Chosen search algorithm: Steady-StateGA");
			{
				SteadyStateGA<TestChromosome> ga = new SteadyStateGA<>(factory);
				if (Properties.REPLACEMENT_FUNCTION == TheReplacementFunction.FITNESSREPLACEMENT) {
					// user has explicitly asked for this replacement function
					ga.setReplacementFunction(new FitnessReplacementFunction<>());
				} else {
					// use default
					ga.setReplacementFunction(new TestCaseReplacementFunction());
				}
				return ga;
			}
		case RANDOM_SEARCH:
			logger.info("Chosen search algorithm: Random");
			return new RandomSearch<>(factory);
        case NSGAII:
            logger.info("Chosen search algorithm: NSGAII");
            return new NSGAII<>(factory);
        case SPEA2:
            logger.info("Chosen search algorithm: SPEA2");
            return new SPEA2<>(factory);
        case ONE_PLUS_LAMBDA_LAMBDA_GA:
            logger.info("Chosen search algorithm: 1 + (lambda, lambda)GA");
            return new OnePlusLambdaLambdaGA<>(factory, Properties.LAMBDA);
        case STANDARD_CHEMICAL_REACTION:
            logger.info("Chosen search algorithm: Standard Chemical Reaction Optimization");
            return new StandardChemicalReaction<>(factory);
        case MAP_ELITES:
          logger.info("Chosen search algorithm: MAP-Elites");
          return new MAPElites(factory);
        case LIPS:
        	logger.info("Chosen search algorithm: LIPS");
            return new LIPS(factory);
		default:
			logger.info("Chosen search algorithm: StandardGA");
			return new StandardGA<>(factory);
		}
	}
	
	private SelectionFunction<TestChromosome> getSelectionFunction() {
		switch (Properties.SELECTION_FUNCTION) {
		case ROULETTEWHEEL:
			return new FitnessProportionateSelection<>();
		case TOURNAMENT:
			return new TournamentSelection<>();
		case BINARY_TOURNAMENT:
		    return new BinaryTournamentSelectionCrowdedComparison<>();
		case RANK_CROWD_DISTANCE_TOURNAMENT:
		    return new TournamentSelectionRankAndCrowdingDistanceComparator<>();
		default:
			return new RankSelection<>();
		}
	}
	
	private CrossOverFunction<TestChromosome> getCrossoverFunction() {
		switch (Properties.CROSSOVER_FUNCTION) {
		case SINGLEPOINTFIXED:
			return new SinglePointFixedCrossOver<>();
		case SINGLEPOINTRELATIVE:
			return new SinglePointRelativeCrossOver<>();
		case SINGLEPOINT:
			return new SinglePointCrossOver<>();
		case UNIFORM:
			return new UniformCrossOver<>();
		default:
			throw new RuntimeException("Unknown crossover function: "
			        + Properties.CROSSOVER_FUNCTION);
		}
	}

	private RankingFunction<TestChromosome> getRankingFunction() {
	  switch (Properties.RANKING_TYPE) {
	    case FAST_NON_DOMINATED_SORTING:
	      return new FastNonDominatedSorting<>();
	    case PREFERENCE_SORTING:
	    default:
	      return new RankBasedPreferenceSorting<>();
	  }
	}

	@Override
	public GeneticAlgorithm<TestChromosome> getSearchAlgorithm() {
		ChromosomeFactory<TestChromosome> factory = getChromosomeFactory();
		
		// FIXXME
		GeneticAlgorithm<TestChromosome> ga = getGeneticAlgorithm(factory);

		if (Properties.NEW_STATISTICS)
			ga.addListener(new org.smartut.statistics.StatisticsListener<>());

		// How to select candidates for reproduction
		SelectionFunction<TestChromosome> selection_function = getSelectionFunction();
		selection_function.setMaximize(false);
		ga.setSelectionFunction(selection_function);

		RankingFunction<TestChromosome> ranking_function = getRankingFunction();
		ga.setRankingFunction(ranking_function);

		// When to stop the search
		StoppingCondition<TestChromosome> stopping_condition = getStoppingCondition();
		ga.setStoppingCondition(stopping_condition);
		// ga.addListener(stopping_condition);
		if (Properties.STOP_ZERO) {
			ga.addStoppingCondition(new ZeroFitnessStoppingCondition<>());
		}

		if (!(stopping_condition instanceof MaxTimeStoppingCondition)) {
			ga.addStoppingCondition(new GlobalTimeStoppingCondition<>());
		}

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.MUTATION)
		        || ArrayUtil.contains(Properties.CRITERION, Criterion.STRONGMUTATION)) {
			ga.addStoppingCondition(new MutationTimeoutStoppingCondition<>());
		}
		ga.resetStoppingConditions();
		ga.setPopulationLimit(getPopulationLimit());

		// How to cross over
		CrossOverFunction<TestChromosome> crossover_function = getCrossoverFunction();
		ga.setCrossOverFunction(crossover_function);

		// What to do about bloat
		// MaxLengthBloatControl bloat_control = new MaxLengthBloatControl();
		// ga.setBloatControl(bloat_control);

		if (Properties.CHECK_BEST_LENGTH) {
			RelativeTestLengthBloatControl<TestChromosome> bloat_control =
					new RelativeTestLengthBloatControl<>();
			ga.addBloatControl(bloat_control);
			ga.addListener(bloat_control);
		}

		TestCaseSecondaryObjective.setSecondaryObjectives();

		if (Properties.DYNAMIC_LIMIT) {
			// max_s = GAProperties.generations * getBranches().size();
			// TODO: might want to make this dependent on the selected coverage
			// criterion
			// TODO also, question: is branchMap.size() really intended here?
			// I think BranchPool.getBranchCount() was intended
			Properties.SEARCH_BUDGET = Properties.SEARCH_BUDGET
			        * (BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getNumBranchlessMethods(Properties.TARGET_CLASS) + BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranchCountForClass(Properties.TARGET_CLASS) * 2);
			stopping_condition.setLimit(Properties.SEARCH_BUDGET);
			logger.info("Setting dynamic length limit to " + Properties.SEARCH_BUDGET);
		}

		// TODO: This seems to be whole test suite specific
		// if (Properties.LOCAL_SEARCH_RESTORE_COVERAGE) {
		// 	ga.addListener(BranchCoverageMap.getInstance());
		// }

		if (Properties.RECYCLE_CHROMOSOMES) {
			if (Properties.STRATEGY == Strategy.ONEBRANCH)
				ga.addListener(TestCaseRecycler.getInstance());
		}

		// ga.addListener(new ResourceController<TestChromosome>());
		return ga;
	}

}

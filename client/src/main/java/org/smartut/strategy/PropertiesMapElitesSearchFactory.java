package org.smartut.strategy;

import org.smartut.Properties;
import org.smartut.ShutdownTestWriter;
import org.smartut.TestGenerationContext;
import org.smartut.coverage.branch.BranchPool;
import org.smartut.coverage.mutation.MutationTimeoutStoppingCondition;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.ga.archive.ArchiveTestChromosomeFactory;
import org.smartut.ga.metaheuristics.mapelites.MAPElites;
import org.smartut.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.RMIStoppingCondition;
import org.smartut.ga.stoppingconditions.SocketStoppingCondition;
import org.smartut.ga.stoppingconditions.StoppingCondition;
import org.smartut.ga.stoppingconditions.ZeroFitnessStoppingCondition;
import org.smartut.statistics.StatisticsListener;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.factories.AllMethodsTestChromosomeFactory;
import org.smartut.testcase.factories.JUnitTestCarvedChromosomeFactory;
import org.smartut.testcase.factories.RandomLengthTestFactory;
import org.smartut.testcase.secondaryobjectives.TestCaseSecondaryObjective;
import org.smartut.testsuite.RelativeSuiteLengthBloatControl;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.ResourceController;

import sun.misc.Signal;

public class PropertiesMapElitesSearchFactory
    extends PropertiesSearchAlgorithmFactory<TestChromosome> {

  private ChromosomeFactory<TestChromosome> getChromosomeFactory() {
    switch (Properties.TEST_FACTORY) {
      case ALLMETHODS:
        logger.info("Using all methods chromosome factory");
        return new AllMethodsTestChromosomeFactory();
      case RANDOM:
        logger.info("Using random chromosome factory");
        return new RandomLengthTestFactory();
      case ARCHIVE:
        logger.info("Using archive chromosome factory");
        return new ArchiveTestChromosomeFactory();
      case JUNIT:
        logger.info("Using seeding chromosome factory");
        return new JUnitTestCarvedChromosomeFactory(new RandomLengthTestFactory());
      case SERIALIZATION:
        logger.info("Using serialization seeding chromosome factory");
        return new RandomLengthTestFactory();
      default:
        throw new RuntimeException("Unsupported test factory: " + Properties.TEST_FACTORY);
    }

  }

  @Override
  public MAPElites getSearchAlgorithm() {
    ChromosomeFactory<TestChromosome> factory = getChromosomeFactory();
    MAPElites ga = new MAPElites(factory);

    if (Properties.NEW_STATISTICS)
      ga.addListener(new StatisticsListener<>());

    // When to stop the search
    StoppingCondition<TestChromosome> stopping_condition = getStoppingCondition();
    ga.setStoppingCondition(stopping_condition);

    if (Properties.STOP_ZERO) {
      ga.addStoppingCondition(new ZeroFitnessStoppingCondition<>());
    }

    if (!(stopping_condition instanceof MaxTimeStoppingCondition)) {
      ga.addStoppingCondition(new GlobalTimeStoppingCondition<>());
    }

    if (ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.MUTATION)
        || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.STRONGMUTATION)) {
      if (Properties.STRATEGY == Properties.Strategy.ONEBRANCH)
        ga.addStoppingCondition(new MutationTimeoutStoppingCondition<>());
      else {
        // ===========================================================================================
        // FIXME: The following line contains a type error.
        //  MutationTestPool is defined on TestSuiteChromosomes but the GA expects TestChromosomes.
//        ga.addListener(new MutationTestPool());
        throw new RuntimeException("Broken code :(");
        // ===========================================================================================
      }
    }
    ga.resetStoppingConditions();

    if (Properties.CHECK_BEST_LENGTH) {
      RelativeSuiteLengthBloatControl<TestChromosome> bloat_control =
              new RelativeSuiteLengthBloatControl<>();
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
          * (BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
              .getNumBranchlessMethods(Properties.TARGET_CLASS)
              + BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
                  .getBranchCountForClass(Properties.TARGET_CLASS) * 2);
      stopping_condition.setLimit(Properties.SEARCH_BUDGET);
      logger.info("Setting dynamic length limit to " + Properties.SEARCH_BUDGET);
    }

    if (Properties.LOCAL_SEARCH_RESTORE_COVERAGE) {
      // ===========================================================================================
      // FIXME: The following line contains a type error.
      //  BranchCoverageMap is defined on TestSuiteChromosomes but the GA expects TestChromosomes.
//      SearchListener<TestChromosome> map = BranchCoverageMap.getInstance();
//      ga.addListener(map);
        // Deliberately throwing an exception
        throw new RuntimeException("Broken code :(");
      // ===========================================================================================
    }

    if (Properties.SHUTDOWN_HOOK) {
      // ShutdownTestWriter writer = new
      // ShutdownTestWriter(Thread.currentThread());
      ShutdownTestWriter<TestChromosome> writer = new ShutdownTestWriter<>();
      ga.addStoppingCondition(writer);
      RMIStoppingCondition<TestChromosome> rmi = RMIStoppingCondition.getInstance();
      ga.addStoppingCondition(rmi);

      if (Properties.STOPPING_PORT != -1) {
        SocketStoppingCondition<TestChromosome> ss = SocketStoppingCondition.getInstance();
        ss.accept();
        ga.addStoppingCondition(ss);
      }

      Signal.handle(new Signal("INT"), writer);
    }

    ga.addListener(new ResourceController<>());
    return ga;
  }
}

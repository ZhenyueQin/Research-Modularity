package experiment3;

import genderGAWithHotspots.collections.GenderPopulation;
import genderGAWithHotspots.collections.SimpleGenderElitesStatistics;
import genderGAWithHotspots.components.chromosomes.SimpleGenderDiploid;
import ga.frame.GAFrame;
import genderGAWithHotspots.frame.GenderGAState;
import genderGAWithHotspots.frame.SimpleGenderGAFrame;
import genderGAWithHotspots.frame.SimpleGenderGAState;
import ga.operations.fitnessfunction.FitnessFunction;
import ga.operations.mutators.BinaryGeneMutator;
import ga.operations.mutators.Mutator;
import genderGAWithHotspots.operations.hotspotMutator.HotspotMutator;
import genderGAWithHotspots.operations.hotspotMutator.SimpleDiscreteExpHotspotMutator;
import ga.operations.postOperators.SimpleFillingOperator;
import genderGAWithHotspots.operations.priorOperators.SimpleGenderElitismOperator;
import genderGAWithHotspots.operations.recombinators.CoupleRecombinator;
import genderGAWithHotspots.operations.recombinators.SimpleGenderRecombinator;
import genderGAWithHotspots.operations.selectors.CoupleSelector;
import genderGAWithHotspots.operations.selectors.SimpleTournamentCoupleSelector;
import ga.operations.selectionOperators.selectionSchemes.SimpleTournamentScheme;

/**
 * Created by david on 30/09/16.
 */
public class Exp3Main {

    private static final int target = 0xf71b72e5;
    private static final int size = 200;
    private static final int maxGen = 2000;
    private static final int numElites = 10;
    private static final int tournamentSize = 2;
    private static final double selectivePressure = 0.7;
    private static final double mutationRate = 0.05;
    private static final double crossoverRate = .8;
    private static final double epsilon = .5;
    private static final int maxFit = 32;
    private static final String outfile = "Exp3.out";

    public static void main(String[] args) {
        // Fitness Function
        FitnessFunction fitnessFunction = new Exp3Fitness();
        // Initializer
        Exp3Initializer initializer = new Exp3Initializer(size, maxFit);
        // Population
        GenderPopulation<SimpleGenderDiploid<Integer>> population = initializer.initialize();
        // Mutator
        Mutator<SimpleGenderDiploid<Integer>> mutator = new BinaryGeneMutator<>(mutationRate);
        // Selector for reproduction
        CoupleSelector<SimpleGenderDiploid<Integer>> selector = new SimpleTournamentCoupleSelector<>(tournamentSize, selectivePressure);
        // Elitism operator for gender-based diploids
        SimpleGenderElitismOperator<SimpleGenderDiploid<Integer>> priorOperator = new SimpleGenderElitismOperator<>(numElites);
        // Simple filling operator
        SimpleFillingOperator<SimpleGenderDiploid<Integer>> postOperator = new SimpleFillingOperator<>(new SimpleTournamentScheme(tournamentSize, selectivePressure));
        // Statistics
        SimpleGenderElitesStatistics<SimpleGenderDiploid<Integer>> statistics = new SimpleGenderElitesStatistics<>(maxGen);
        // Recombinator
        CoupleRecombinator<SimpleGenderDiploid<Integer>> recombinator = new SimpleGenderRecombinator(1);
        // Hotspot mutators
        HotspotMutator<Integer> hotspotMutator = new SimpleDiscreteExpHotspotMutator(1);

        GenderGAState<SimpleGenderDiploid<Integer>,Integer> state = new SimpleGenderGAState<>(
                population, fitnessFunction, mutator, recombinator, selector, hotspotMutator, crossoverRate);

        GAFrame<SimpleGenderDiploid<Integer>> frame = new SimpleGenderGAFrame<>(state, postOperator, statistics);

        frame.setPriorOperator(priorOperator);

        state.record(statistics);

        statistics.print(0);

        for (int i = 1; i <= maxGen; i++) {
            frame.evolve();
            statistics.print(i);
            if (statistics.getOptimum(i) > maxFit - epsilon) break;
        }

        statistics.save(outfile);
    }
}

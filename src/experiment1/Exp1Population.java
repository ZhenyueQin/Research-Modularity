package experiment1;

import com.sun.istack.internal.NotNull;
import ga.collections.Population;
import ga.collections.Statistics;
import ga.components.SequentialHaploid;
import ga.components.Individual;
import ga.operations.Fitness;
import ga.operations.Mutator;
import ga.operations.Selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by david on 29/08/16.
 */
public class Exp1Population implements Population<SequentialHaploid> {

    List<Individual<SequentialHaploid>> individuals;
    List<Double> fitnessValues;
    List<Individual<SequentialHaploid>> nextGen;
    private final int size;

    public Exp1Population(final int size) {
        individuals = new ArrayList<>(size);
        nextGen = new ArrayList<>(size);
        fitnessValues = new ArrayList<>(size);
        this.size = size;
    }

    private Exp1Population(@NotNull final List<Individual<SequentialHaploid>> individuals,
                           @NotNull final List<Individual<SequentialHaploid>> nextGen,
                           final int size) {
        this.individuals = new ArrayList<>(individuals);
        this.fitnessValues = new ArrayList<>(size);
        this.nextGen = new ArrayList<>(nextGen);
        this.size = size;
    }

    @Override
    public Population<SequentialHaploid> copy() {
        return new Exp1Population(individuals, nextGen, size);
    }

    @Override
    public void evaluate(Fitness fitness) {

        if (!fitnessValues.isEmpty())
            fitnessValues.clear();

        double sum = 0;

        for (Individual<SequentialHaploid> h : individuals) {
            h.evaluate(fitness);
            fitnessValues.add(h.getFitness());
            sum += h.getFitness();
        }

        // Normalization
        for (int i = 0; i < size; i++) {
            fitnessValues.set(i, fitnessValues.get(i)/sum);
        }
        Collections.sort(individuals);
        Collections.sort(fitnessValues);
        Collections.reverse(individuals);
        Collections.reverse(fitnessValues);
    }

    @Override
    public void addChildren(List<SequentialHaploid> children) {
        int index = 0;
        while (nextGen.size() < size) {
            if (index == children.size())
                return;
            nextGen.add(new Individual<>(children.get(index)));
            index++;
        }
    }

    @Override
    public void mutate(Mutator mutator) {
        for (Individual<SequentialHaploid> h : nextGen) {
            // TODO: Mutation
        }
    }

    @Override
    public void record(Statistics<SequentialHaploid> statistics) {
        
    }

    @Override
    public boolean isReady() {
        return nextGen.size() == size;
    }

    @Override
    public boolean nextGeneration() {
        if (!isReady())
            return false;
        individuals.clear();
        individuals.addAll(nextGen);
        nextGen.clear();
        return true;
    }

    @Override
    public List<SequentialHaploid> select(Selector selector) {
        return null;
    }

    /*
    @Override
    public Map<String, List<SequentialHaploid>> getElites(int amount) {
        return null;
    }
    */
}

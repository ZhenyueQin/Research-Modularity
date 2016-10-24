package ga.collections;

import com.sun.istack.internal.NotNull;
import ga.components.chromosomes.Chromosome;
import ga.others.Copyable;

import java.util.List;

/**
 * This interface provides an abstraction for statistics recording of the genetic algorithm.
 *
 * @author Siu Kei Muk (David)
 * @since 31/08/16.
 */
public interface Statistics<C extends Chromosome> extends Copyable<Statistics<C>>{

    /**
     * Prints the information of the given generation to STDOUT.
     * @param generation
     */
    default void print(final int generation) {
        System.out.println(getSummary(generation));
        System.out.println();
    }

    /**
     * Records information from the current generation.
     * @param data list of individuals of the current generation
     */
    void record(@NotNull final List<Individual<C>> data);

    /**
     * Saves the statistics to a file with given filename
     * @param filename
     */
    void save(@NotNull final String filename);

    /**
     * Notifies the statistics bookkeeper that the following record should be in a new generation.
     */
    void nextGeneration();
    // double getDelta(final int generation);

    /**
     * Returns the optimum fitnessFunctions value reached of a given generation.
     * @param generation
     * @return optimum fitnessFunctions value of the given generation
     */
    double getOptimum(final int generation);

    /**
     * Returns a summary of a given generation as string
     * @param generation
     * @return a summary of the given generation
     */
    String getSummary(final int generation);
}

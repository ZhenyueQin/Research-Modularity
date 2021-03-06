package ga.collections;

import au.com.bytecode.opencsv.CSVWriter;
import ga.components.chromosomes.Chromosome;
import org.jetbrains.annotations.NotNull;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*
    GASEE is a Java-based genetic algorithm library for scientific exploration and experiment.
    Copyright 2016 Siu-Kei Muk

    This file is part of GASEE.

    GASEE is free library: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 2.1 of the License, or
    (at your option) any later version.

    GASEE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with GASEE.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This class is a simple implementation of statistics bookkeeping that
 * keeps records of the best, worst and median individual, also the mean fitness of each generation.
 *
 * @author Siu Kei Muk (David) and Zhenyue Qin
 * @since 11/09/16.
 */
public class SimpleElitesStatistics<C extends Chromosome> extends BaseStatistics<C> {

    List<Individual<C>> elites; // best individuals
    List<Double> deltas;

    public SimpleElitesStatistics() {
        generation = 0;
        elites = new ArrayList<>();
        deltas = new ArrayList<>();
    }

    /**
     * @param maxGen maximum number of generations, for efficiency reason
     */
    public SimpleElitesStatistics(final int maxGen) {
        generation = 0;
        elites = new ArrayList<>(maxGen);
        deltas = new ArrayList<>(maxGen);
    }

    private SimpleElitesStatistics(@NotNull final List<Individual<C>> elites,
                                   @NotNull final List<Double> deltas) {
        generation = elites.size();
        this.elites = new ArrayList<>(generation);
        this.deltas = new ArrayList<>(deltas);
        for (int i = 0; i < generation; i++)
            this.elites.add(elites.get(i).copy());
    }

    @Override
    public void record(@NotNull final List<Individual<C>> data) {
        Individual<C> elite = data.get(0).copy();
        elites.add(elite);
        if (generation == 0)
            deltas.add(elite.getFitness());
        else
            deltas.add(elite.getFitness() - elites.get(generation-1).getFitness());
    }

    @Override
    protected DefaultCategoryDataset createDataSet() {
        return null;
    }

    @Override
    public void nextGeneration() {
        generation++;
    }

    @Override
    public double getOptimum(final int generation) {
        return elites.get(generation).getFitness();
    }

    @Override
    public SimpleElitesStatistics<C> copy() {
        return new SimpleElitesStatistics<>(elites, deltas);
    }

    @Override
    public String getSummary(final int generation) {
        return String.format("Generation: %d; Delta: %.4f, Best >> %s <<",
                generation, deltas.get(generation), elites.get(generation).toString());
    }

    @Override
    public int getGeneration() {
        return this.generation;
    }

    public void generateCSVFile(String fileName) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(fileName), '\t');
        String[] entries = "Best#".split("#");
        writer.writeNext(entries);
        for (int i=0; i<=generation; i++) {
            entries = (Double.toString(elites.get(i).getFitness()) + "#").split("#");
            writer.writeNext(entries);
        }
        writer.close();
    }
}

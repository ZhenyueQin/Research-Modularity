package ga.operations.postOperators;

import ga.collections.Individual;
import ga.collections.Population;
import ga.components.chromosomes.Chromosome;
import ga.operations.selectionOperators.selectionSchemes.SelectionScheme;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * This class is an implementation of post-operator that fills up the remaining space by
 * selecting individuals from the current generation to survive in the next generation
 * according to a given selection scheme.
 *
 * @author Siu Kei Muk (David)
 * @since 9/10/16.
 */
public class SimpleFillingOperator<T extends Chromosome> implements PostOperator<T> {

    private SelectionScheme scheme;

    public SimpleFillingOperator(@NotNull final SelectionScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void postOperate(@NotNull final Population<T> population) {
        int size = population.getSize();
        List<Double> fitnessValues = new ArrayList<>(size);
        List<Individual<T>> individuals = population.getIndividualsView();
        for (int i = 0; i < size; i++) fitnessValues.add(individuals.get(i).getFitness());
        // Collections.sort(fitnessValues);
        Set<Integer> survivedIndices = population.getSurvivedIndicesView();
        Set<Integer> selected = new HashSet<>();
        while (!population.isReady()) {
            final int index = scheme.select(fitnessValues);
            if (survivedIndices.contains(index) || selected.contains(index)) continue;
            population.addCandidate(individuals.get(index).copy());
            selected.add(index);
        }
    }
}

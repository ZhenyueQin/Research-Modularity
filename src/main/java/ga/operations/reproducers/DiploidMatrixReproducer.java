package ga.operations.reproducers;

import ga.components.chromosomes.Chromosome;
import ga.components.materials.SimpleMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Zhenyue Qin (秦震岳) on 4/7/17.
 * The Australian National University.
 */
public abstract class DiploidMatrixReproducer <C extends Chromosome> extends DiploidReproducer<C> {
    protected final int matrixSideSize;

    public DiploidMatrixReproducer(final int matrixSideSize) {
        super();
        this.matrixSideSize = matrixSideSize;
    }

    public DiploidMatrixReproducer(final double matchProbability, final int matrixSideSize) {
        super(matchProbability);
        this.matrixSideSize = matrixSideSize;
    }

    public DiploidMatrixReproducer(final double matchProbability, final boolean toDoCrossover, final int matrixSideSize) {
        super(matchProbability, toDoCrossover);
        this.matrixSideSize = matrixSideSize;
    }

    protected List<SimpleMaterial> crossoverMatrix(@NotNull final C parent) {
        List<SimpleMaterial> newDNAs = new ArrayList<>(2);
        List<SimpleMaterial> materialView = parent.getMaterialsView();
        SimpleMaterial dna1Copy = materialView.get(0).copy();
        SimpleMaterial dna2Copy = materialView.get(1).copy();

        if (isToDoCrossover) {
            final int crossIndex = ThreadLocalRandom.current().nextInt(1, matrixSideSize-1);
            for (int currentCrossIndex=0; currentCrossIndex<crossIndex; currentCrossIndex++) {
                int tmpCrossIndex = currentCrossIndex;
                while (tmpCrossIndex < crossIndex * matrixSideSize) {
                    crossoverTwoDNAsAtPosition(dna1Copy, dna2Copy, tmpCrossIndex);
                    tmpCrossIndex += matrixSideSize;
                }
            }

            for (int currentCrossIndex=crossIndex; currentCrossIndex<matrixSideSize; currentCrossIndex++) {
                int tmpCrossIndex = currentCrossIndex + crossIndex * matrixSideSize;
                while (tmpCrossIndex < matrixSideSize * matrixSideSize) {
                    crossoverTwoDNAsAtPosition(dna1Copy, dna2Copy, tmpCrossIndex);
                    tmpCrossIndex += matrixSideSize;
                }
            }
        }

        newDNAs.add(dna1Copy);
        newDNAs.add(dna2Copy);
        return newDNAs;
    }
}

package ga.operations.fitnessFunctions;

import ga.components.genes.DataGene;
import ga.components.materials.SimpleMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhenyueqin on 22/6/17.
 */
public class GRNFitnessFunctionMultipleTargetsFast extends GRNFitnessFunctionMultipleTargets {

    protected final int perturbationCycleSize;
    protected List<DataGene[][]> perturbationPool;

    public GRNFitnessFunctionMultipleTargetsFast(int[][] targets, int maxCycle, int perturbations,
                                                 double perturbationRate, final int perturbationCycleSize) {
        super(targets, maxCycle, perturbations, perturbationRate);
        this.perturbationCycleSize = perturbationCycleSize;
        generatePerturbationPool();
    }

    public GRNFitnessFunctionMultipleTargetsFast(int[][] targets, int maxCycle, int perturbations,
                                                 double perturbationRate, List<Integer> thresholdOfAddingTarget,
                                                 final int perturbationCycleSize) {
        super(targets, maxCycle, perturbations, perturbationRate, thresholdOfAddingTarget);
        this.perturbationCycleSize = perturbationCycleSize;
        generatePerturbationPool();
    }

    protected void generatePerturbationPool() {
        perturbationPool = new ArrayList<>(targets.length);
        for (int[] target : targets) {
            perturbationPool.add(generateInitialAttractors(perturbationCycleSize, perturbationRate, target));
        }
    }

    public double evaluateOneTarget(@NotNull final SimpleMaterial phenotype,
                                       @NotNull final int[] target,
                                       @NotNull final DataGene[][] perturbationTargets) {
        double fitnessValue = 0;
        int perturbationIndex = 0;
        while (perturbationIndex < perturbations) {
            DataGene[] currentAttractor = perturbationTargets[perturbationIndex % perturbationCycleSize];
            int currentRound = 0;
            boolean isNotStable;
            do {
                DataGene[] updatedState = this.updateState(currentAttractor, phenotype);
                isNotStable = this.hasNotAttainedAttractor(currentAttractor, updatedState);
                currentAttractor = updatedState;
                currentRound += 1;
            } while (currentRound < this.maxCycle && isNotStable);

            if (currentRound < maxCycle) {
                double hammingDistance = this.getHammingDistance(currentAttractor, target);
                double thisFitness = Math.pow((1 - (hammingDistance / ((double) target.length))), 5);
                fitnessValue += thisFitness;
            } else {
                fitnessValue += 0;
            }
            perturbationIndex += 1;
        }

        double arithmeticMean = fitnessValue / this.perturbations;
        double networkFitness = 1 - Math.pow(Math.E, (-3 * arithmeticMean));
        return networkFitness;
    }

    @Override
    public double evaluate(SimpleMaterial phenotype, int generation) {
        currentPerturbations.clear();
        List<Integer> currentTargetIndices = this.getCurrentTargetIndices(generation);
        double fitnessValue = 0;
        for (Integer targetIndex : currentTargetIndices) {
            int[] aTarget = this.targets[targetIndex];
            DataGene[][] perturbationTargets = this.perturbationPool.get(targetIndex);
            currentPerturbations.add(perturbationTargets);
            fitnessValue += this.evaluateOneTarget(phenotype, aTarget, perturbationTargets);
        }
        return fitnessValue / currentTargetIndices.size();
    }

    @Override
    public double evaluate(SimpleMaterial phenotype) {
        return evaluate(phenotype, 0);
    }

    @Override
    public void update() {

    }
}

package ga.operations.reproducers;

import ga.components.chromosomes.SimpleHotspotDiploid;
import ga.components.hotspots.Hotspot;
import ga.components.materials.SimpleMaterial;
import ga.operations.expressionMaps.ExpressionMap;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the diploid reproducer with matrix-like crossover.
 * e.g. If the index is 2 and two chromosomes are:
 * 1, 2, 3,
 * 4, 5, 6,
 * 7, 8, 8,
 *
 * -1, -2, -3,
 * -4, -5, -6,
 * -7, -8, -9,
 *
 * after crossover:
 * -1, 2, 3,
 * 4, -5, -6,
 * 7, -8, -9,
 *
 * Crossover at the diagonal to maintain the modularity.
 *
 * Created by Zhenyue Qin (秦震岳) on 9/7/17.
 * The Australian National University.
 */
public class GRNHotspotDiploidEvolvedSPXMatrixReproducer extends HotspotDiploidMatrixReproducer<SimpleHotspotDiploid> {
    public GRNHotspotDiploidEvolvedSPXMatrixReproducer(int matrixSideSize) {
        super(matrixSideSize);
    }

    public GRNHotspotDiploidEvolvedSPXMatrixReproducer(double matchProbability, int matrixSideSize) {
        super(matchProbability, matrixSideSize);
    }

    public GRNHotspotDiploidEvolvedSPXMatrixReproducer(double matchProbability, boolean toDoCrossover, int matrixSideSize) {
        super(matchProbability, toDoCrossover, matrixSideSize);
    }

    @Override
    protected List<SimpleHotspotDiploid> recombine(List<SimpleHotspotDiploid> mates) {
        List<SimpleHotspotDiploid> rtn = new ArrayList<>();

        SimpleHotspotDiploid parent1 = mates.get(0);
        SimpleHotspotDiploid parent2 = mates.get(1);

        List<SimpleMaterial> parent1Gametes = crossoverMatrix(mates.get(0));
        List<SimpleMaterial> parent2Gametes = crossoverMatrix(mates.get(1));

//        List<SimpleMaterial> parent1Gametes = throughCrossover(mates.get(0));
//        List<SimpleMaterial> parent2Gametes = throughCrossover(mates.get(1));

        SimpleMaterial dna1_1 = parent1Gametes.get(0).copy();
        SimpleMaterial dna1_2 = parent1Gametes.get(1).copy();
        SimpleMaterial dna2_1 = parent2Gametes.get(0).copy();
        SimpleMaterial dna2_2 = parent2Gametes.get(1).copy();

        ExpressionMap mapping1 = parent1.getMapping().copy();
        ExpressionMap mapping2 = parent2.getMapping().copy();

        Hotspot hotspot1 = parent1.getHotspot().copy();
        Hotspot hotspot2 = parent2.getHotspot().copy();

//        if (Math.random() < matchProbability) {
//            SimpleMaterial tmp = dna1_2;
//            dna1_2 = dna2_2;
//            dna2_2 = tmp;
//        }
//
//        if (Math.random() < matchProbability) {
//            ExpressionMap tmp = mapping1;
//            mapping1 = mapping2;
//            mapping2 = tmp;
//        }
//
//        if (Math.random() < matchProbability) {
//            Hotspot tmp = hotspot1;
//            hotspot1 = hotspot2;
//            hotspot2 = tmp;
//        }

        rtn.add(new SimpleHotspotDiploid(dna1_1, dna2_2, mapping1, hotspot1));
        rtn.add(new SimpleHotspotDiploid(dna2_1, dna1_2, mapping2, hotspot2));
        return rtn;
    }
}

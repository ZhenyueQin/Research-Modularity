package ga.operations.initializers;

import ga.collections.Individual;
import ga.collections.Population;
import ga.components.chromosomes.GenderHotspotDiploid;
import ga.components.hotspots.Hotspot;
import ga.components.materials.GRN;
import ga.components.materials.GRNFactoryNoHiddenTarget;
import ga.components.materials.SimpleMaterial;
import ga.operations.expressionMaps.DiploidEvolvedMap;
import ga.operations.expressionMaps.ExpressionMap;

/**
 * Created by zhenyueqin on 15/6/17.
 */
public class GenderHotspotDiploidGRNInitializer implements Initializer<GenderHotspotDiploid> {
    protected int size;
    private final int targetLength;
    private final int grnSize;
    private final int edgeSize;
    private final int hotspotSize;

    public GenderHotspotDiploidGRNInitializer(final int size, final int targetLength, final int edgeSize, final int hotspotSize) {
        setSize(size);
        this.targetLength = targetLength;
        grnSize = targetLength * targetLength;
        this.edgeSize = edgeSize;
        this.hotspotSize = hotspotSize;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void setSize(int size) {
        filter(size);
        this.size = size;
    }

    private void filter(final int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be at least one");
        }
    }

    @Override
    public Population<GenderHotspotDiploid> initialize() {
        Population<GenderHotspotDiploid> population = new Population<>(size);
        for (int i = 0; i < size; i++) {
            population.addCandidate(generateIndividual());
        }
        population.nextGeneration();
        return population;
    }

    public Population<GenderHotspotDiploid> initializeSameIndividuals() {
        Population<GenderHotspotDiploid> population = new Population<>(size);
        Individual<GenderHotspotDiploid> originalIndividual = generateIndividual();
        for (int i = 0; i < size; i++) {
            population.addCandidate(originalIndividual.copy());
        }
        population.nextGeneration();
        return population;
    }

    private Individual<GenderHotspotDiploid> generateIndividual() {
        GRNFactoryNoHiddenTarget grnFactory = new GRNFactoryNoHiddenTarget(targetLength, this.edgeSize);
        ExpressionMap<SimpleMaterial,SimpleMaterial> mapping = new DiploidEvolvedMap(grnSize);
        GRN dna1 = grnFactory.generateGeneRegulatoryNetwork();
        GRN dna2 = grnFactory.generateGeneRegulatoryNetwork();
        Hotspot hotspot = new Hotspot(this.hotspotSize, grnSize);
        return new Individual<>(new GenderHotspotDiploid(dna1, dna2, mapping, hotspot, Math.random() < 0.5));
    }
}

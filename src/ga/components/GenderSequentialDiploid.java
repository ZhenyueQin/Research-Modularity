package ga.components;

import com.sun.istack.internal.NotNull;
import ga.operations.DominanceMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by david on 27/08/16.
 */
public final class GenderSequentialDiploid extends SequentialDiploid {

    private final boolean masculine;
    private final List<Double> masRecomMap;
    private final List<Double> femRecomMap;

    public GenderSequentialDiploid(@NotNull final DNAStrand dna1,
                                   @NotNull final DNAStrand dna2,
                                   @NotNull final DominanceMapping mapping,
                                   @NotNull final List<Double> masRecomMap,
                                   @NotNull final List<Double> femRecomMap,
                                   final boolean masculine) {

        super(dna1, dna2, mapping);
        this.masculine = masculine;
        this.masRecomMap = new ArrayList<>(masRecomMap);
        this.femRecomMap = new ArrayList<>(femRecomMap);
    }

    public boolean isMasculine(){
        return masculine;
    }

    public List<Double> getMasRecomMap(){
        return Collections.unmodifiableList(masRecomMap);
    }

    public List<Double> getFemRecomMap(){
        return Collections.unmodifiableList(femRecomMap);
    }

    @Override
    public Chromosome copy() {
        DNAStrand dna1 = (DNAStrand) materials[0].copy();
        DNAStrand dna2 = (DNAStrand) materials[1].copy();
        DominanceMapping mapping = super.mapping.copy();
        List<Double> mMap = new ArrayList<>(masRecomMap);
        List<Double> fMap = new ArrayList<>(femRecomMap);
        return new GenderSequentialDiploid(dna1, dna2, mapping, mMap, fMap, masculine);
    }

    @Override
    public String toString() {
        String gender = (masculine) ? "M" : "F";
        List<Double> map = (masculine) ? masRecomMap : femRecomMap;
        return "Gender: " + gender +
                ", " + super.toString() +
                ", Recombination probability: " + map.toString();
    }
}

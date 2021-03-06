package ga.components.hotspots;

import ga.others.Copyable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Zhenyue Qin on 15/06/2017.
 * The Australian National University.
 */
public class Hotspot implements Copyable<Hotspot> {

    protected final int size;
    protected final int dnaLength;
    protected Map<Integer, Double> recombinationRates;

    public Hotspot(final int dnaLength) {
        this.dnaLength = dnaLength;
        this.size = dnaLength;
        this.recombinationRates = new HashMap<>(this.size);
        this.generateRandomRecombinationRates();
    }

    public Hotspot(final int size, final int dnaLength) {
        this.size = size;
        this.recombinationRates = new HashMap<>(this.size);
        this.dnaLength = dnaLength;
        this.generateRandomRecombinationRates();
    }

    public Hotspot(final int size, final int dnaLength, @NotNull Map<Integer, Double> recombinationRates) {
        this.size = size;
        this.dnaLength = dnaLength;
        this.recombinationRates = recombinationRates;
    }

    protected void generateRandomRecombinationRates() {
        if (this.size > this.dnaLength-1) {
            throw new IllegalArgumentException("Hotspot size is larger than the length of DNA");
        }
        generateRandomRecombinationRatesWithSize(this.dnaLength-1);
    }

    protected void generateRandomRecombinationRatesWithSize(int randomSize) {
        int[] hotspotPositions = new Random().ints(
                1, randomSize).distinct().limit(size).toArray();
        double[] unNormalizedRates = new double[size];
        double rateSum = 0;
        for (int i=0; i<size; i++) {
            double aRate = Math.random();
            unNormalizedRates[i] = aRate;
            rateSum += aRate;
        }
        for (int i=0; i<size; i++) {
            setRecombinationRateAtPosition(hotspotPositions[i], unNormalizedRates[i] / rateSum);
        }
    }

    /**
     * Get the number of hotspot positions.
     */
    public int getSize() {
        return this.size;
    }

    public void setRecombinationRateAtPosition(int i, double rate) {
        this.recombinationRates.put(i, rate);
    }

    public double getRecombinationRateAtPosition(int i) {
        return this.recombinationRates.get(i);
    }

    public List<Double> getRecombinationRates() {
        return new ArrayList<>(this.recombinationRates.values());
    }

    public Map<Integer, Double> getRecombinationRateMap() {
        return this.recombinationRates;
    }

    @Override
    public Hotspot copy() {
        return new Hotspot(this.size, this.dnaLength, new HashMap<>(this.recombinationRates));
    }

    public SortedSet<Integer> getSortedHotspotPositions() {
        return new TreeSet<>(recombinationRates.keySet());
    }

    @Override
    public String toString() {
        if (this.recombinationRates == null) return "{}";
        return this.recombinationRates.toString();
    }
}

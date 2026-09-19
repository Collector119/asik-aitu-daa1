package daa;

public class Metrics {

    private int currentDepth;
    private int maxDepth;
    private long comparisons;
    private long swaps;
    private long allocations;

    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    public void exitRecursion() {
        currentDepth--;
    }

    public void addComparison() {
        comparisons++;
    }

    public void addSwap() {
        swaps++;
    }

    public void addAllocation(long elements) {
        allocations += elements;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getAllocations() {
        return allocations;
    }

    public void reset() {
        currentDepth = 0;
        maxDepth = 0;
        comparisons = 0;
        swaps = 0;
        allocations = 0;
    }
}

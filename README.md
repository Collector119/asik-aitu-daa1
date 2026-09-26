# Assignment 1: Divide-and-Conquer Algorithm Analysis

Anarov Daniyal, SE-2530, Astana IT University.

## A. Project Overview

The purpose of this assignment is to implement four classic divide-and-conquer
algorithms, measure how they behave on different inputs, and compare the measurements
with the running times predicted by recurrence analysis.

| Algorithm | Class | Complexity |
|---|---|---|
| MergeSort | `src/daa/MergeSorter.java` | Θ(n log n) |
| QuickSort | `src/daa/QuickSorter.java` | Θ(n log n) expected, O(n²) worst case |
| Deterministic Select | `src/daa/DeterministicSelector.java` | Θ(n) worst case |
| Closest Pair of Points | `src/daa/ClosestPairSolver.java` | Θ(n log n) |

`Experiment.java` runs the measurements and writes `results/results.csv`, `Point.java`
is a 2D point, `Metrics.java` counts recursion depth, comparisons, swaps and allocations,
and `Main.java` runs a short demo of each algorithm followed by the experiments.

```
mvn test
mvn compile
java -cp target/classes daa.Main
```

## B. Algorithm Analysis

### MergeSort

The array is split in half, both halves are sorted recursively, and the two sorted
halves are merged in one linear pass. The auxiliary buffer is allocated once and reused
by every merge. Ranges of 16 elements or fewer are sorted with insertion sort.

```
T(n) = 2T(n/2) + Θ(n)
```

Master Theorem: a = 2, b = 2, n^(log_b a) = n, and f(n) = Θ(n). This is case 2, so
T(n) = Θ(n log n). The insertion sort cutoff only replaces the lowest levels of the
recursion with constant-size work, Θ(n) in total, so the result does not change.

Time: Θ(n log n). Space: Θ(n) for the buffer plus Θ(log n) for the stack.

### QuickSort

A pivot is chosen at random, and the range is partitioned in place around it. The
algorithm recurses into the smaller part and continues with a loop on the larger part.

```
T(n) = T(k) + T(n - 1 - k) + Θ(n)
```

Here k is the final position of the pivot. The parts are not equal, so the Master
Theorem does not apply. Akra–Bazzi intuition: with a random pivot, half of the time the
pivot falls in the middle half of the range, so both parts are at most 3n/4. A split
like T(n/4) + T(3n/4) + Θ(n) has fractions that sum to 1, the same as MergeSort, so
every level costs Θ(n) and there are Θ(log n) levels. The expected time is Θ(n log n).
If every pivot is the smallest or the largest element, T(n) = T(n - 1) + Θ(n) = O(n²).

Time: Θ(n log n) expected, O(n²) worst case. Space: O(log n) for the stack, because the
recursive call always gets at most half of the range.

### Deterministic Select (Median-of-Medians)

The range is split into groups of 5, each group is sorted, and the median of the group
medians is found recursively. That value is the pivot for an in-place partition. The
algorithm then recurses only into the part that contains the k-th element.

```
T(n) = T(n/5) + T(7n/10) + Θ(n)
```

T(n/5) is the search for the median of medians. At least half of the n/5 medians are
≤ the pivot, and each of those groups has 3 elements ≤ the pivot, so at least 3n/10
elements are on each side and the recursive call gets at most 7n/10. Akra–Bazzi
intuition: 1/5 + 7/10 = 9/10 < 1, so the work per level shrinks geometrically and the
top level dominates: T(n) = Θ(n).

Time: Θ(n) worst case for distinct values. With many values equal to the pivot, the
two-way partition does not remove them, and a three-way partition would be needed.
Space: Θ(n), because the input is copied so the caller's array
is not changed, plus O(log n) for the stack.

### Closest Pair of Points

The points are sorted by x once. The range is split in half, both halves are solved
recursively, and d is the smaller of the two answers. The halves are then merged by y,
and the strip of points closer than d to the dividing line is checked in y-order. Each
point in the strip is compared only with the next points whose y-difference is less
than d, which is at most 7 points.

```
T(n) = 2T(n/2) + Θ(n)
```

Master Theorem case 2, as for MergeSort: T(n) = Θ(n log n). The first sort by x is
also Θ(n log n). The y-order comes from the merge, so the strip is never sorted again.

Time: Θ(n log n). Space: Θ(n) for the sorted copy and two buffers.

## C. Experimental Results

Each value is the median of 5 runs after 10 warm-up runs, timed with
`System.nanoTime()`. Sizes: small (1000, 5000), medium (10000, 50000), large (100000,
200000). The full data, including comparisons, swaps and allocations, is in
`results/results.csv`.

### Execution time, ms

| Algorithm | Input | 1000 | 5000 | 10000 | 50000 | 100000 | 200000 |
|---|---|---|---|---|---|---|---|
| MergeSort | random | 0.02 | 0.28 | 0.63 | 3.73 | 8.41 | 16.74 |
| MergeSort | sorted | 0.01 | 0.09 | 0.18 | 1.09 | 2.36 | 4.93 |
| MergeSort | reverse-sorted | 0.02 | 0.11 | 0.21 | 1.28 | 2.93 | 5.41 |
| MergeSort | duplicate-heavy | 0.02 | 0.18 | 0.37 | 2.08 | 4.50 | 8.86 |
| QuickSort | random | 0.04 | 0.23 | 0.50 | 2.85 | 5.91 | 14.21 |
| QuickSort | sorted | 0.02 | 0.12 | 0.25 | 1.33 | 2.71 | 6.08 |
| QuickSort | reverse-sorted | 0.03 | 0.13 | 0.28 | 1.51 | 3.02 | 6.78 |
| QuickSort | duplicate-heavy | 0.07 | 1.29 | 4.95 | 112.94 | 525.07 | 2254.01 |
| DeterministicSelect | random | 0.01 | 0.08 | 0.24 | 1.31 | 2.49 | 5.22 |

Closest Pair was measured on random points:

| n | 1000 | 2000 | 5000 | 10000 | 50000 | 100000 |
|---|---|---|---|---|---|---|
| ClosestPair | 0.34 | 0.56 | 1.43 | 3.43 | 18.45 | 40.76 |

### Maximum recursion depth, random input

| n | 1000 | 5000 | 10000 | 50000 | 100000 | 200000 |
|---|---|---|---|---|---|---|
| MergeSort | 7 | 10 | 11 | 13 | 14 | 15 |
| QuickSort | 7 | 8 | 10 | 11 | 11 | 12 |
| DeterministicSelect | 10 | 11 | 13 | 16 | 17 | 19 |

| n | 1000 | 2000 | 5000 | 10000 | 50000 | 100000 |
|---|---|---|---|---|---|---|
| ClosestPair | 10 | 11 | 12 | 13 | 16 | 17 |

### Plots

![Execution time vs n](docs/plots/time_vs_n.png)

![Recursion depth vs n](docs/plots/recursion_depth_vs_n.png)

## D. Discussion

**Do the results match theoretical complexity?**

Yes. When n doubles at the large sizes, the time of MergeSort, QuickSort and Closest
Pair grows by 2.0–2.4x, close to the 2.1x predicted by n log n, and the time of Select
grows by 2.1x, close to the 2x predicted by n. The comparison counts match even better,
because they do not depend on the machine: MergeSort makes 3.48M comparisons on 200000
random elements, and n log₂ n = 3.52M. Recursion depth grows by about 1 each time n
doubles, which is logarithmic.

**How does input structure affect performance?**

MergeSort always has the same depth, but on sorted input each merge uses up one half
early and copies the rest without comparisons, so it is about 3x faster. QuickSort
behaves the same on random, sorted and reverse-sorted input, because a random pivot does
not depend on the order of the elements. Duplicate-heavy input (10 distinct values) is
its bad case: the partition puts all elements equal to the pivot on one side, so a block
of equal values shrinks by only one element per pass. The time becomes quadratic, 2.25 s
at n = 200000. A three-way partition (<, =, >) would fix this.

**Why does smaller-first recursion help QuickSort?**

The smaller part has at most half of the elements, so every recursive call at least
halves the range and the stack depth is about log₂ n at most. The larger part is handled by
the loop and does not add a stack frame. The measured depth is 12 at n = 200000, below
log₂ 200000 ≈ 17.6. This protects against stack overflow, but it does not reduce the
number of comparisons: on duplicate-heavy input the depth stays at 4 while the time is
quadratic.

**Why does Median-of-Medians guarantee O(n)?**

The pivot is guaranteed to have at least 3n/10 elements on each side, so every call
removes at least 3n/10 elements. The two recursive calls get n/5 and 7n/10 elements,
9n/10 together, which is less than n. The work per level decreases geometrically, and
the total is a constant times the Θ(n) work of the first level.

**Why is divide-and-conquer Closest Pair faster than O(n²) for large inputs?**

Brute force checks all n(n-1)/2 pairs, about 5 billion at n = 100000. The
divide-and-conquer version only checks pairs across the dividing line inside the strip,
and each strip point is compared with at most 7 neighbours, so the combine step is
linear. At n = 100000 it made 1.68M comparisons in total. For small n the advantage is
smaller, because the initial sort by x is a large part of the time.

**What practical factors affect performance?**

JVM: the JIT compiler makes the first runs much slower, so every measurement uses 10
warm-up runs. Cache: small arrays fit in the CPU cache, so the time per element at
n = 1000 is lower than at larger sizes. GC and memory layout: Closest Pair works with
`Point` objects, and reading a coordinate follows a reference, which is slower than
reading an `int[]`. This is why it is slower than MergeSort with the same Θ(n log n).
Noise: single runs varied a lot, so the median of 5 runs is reported.

## E. Reflection

The algorithms themselves were not the hardest part. The tests compare them with
`Arrays.sort`, `Arrays.sort(a)[k]` and a brute-force closest pair, and they passed
early. Measuring was harder. My first results showed Closest Pair growing slower than
n log n, which is impossible, and the reason was JIT warm-up, not a bug. After that I
checked every result against the expected growth before trusting it.

The main thing I learned is that recursion depth and running time are different. On
duplicate-heavy input my QuickSort has depth 4, because it recurses into the smaller
part, but it still makes two billion comparisons. Smaller-first recursion keeps the
stack small, and only a better partition would make the time small.

## F. Screenshots

Program output:

![Program output](docs/screenshots/program-output.png)

Test results:

![Test results](docs/screenshots/test-results.png)

Plots:

![Execution time vs n](docs/plots/time_vs_n.png)

![Recursion depth vs n](docs/plots/recursion_depth_vs_n.png)

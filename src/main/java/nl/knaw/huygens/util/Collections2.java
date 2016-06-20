package nl.knaw.huygens.util;

import java.util.Comparator;
import java.util.List;

/**
 * Collection utility methods missing from java.util.Collections.
 */
public class Collections2 {
    // Cutoff for switching to selection sort. Needs to be at least 3 for
    // median-of-three partitioning to be sensible.
    static final int CUTOFF = 5;

    private static <T> void compSwap(List<T> a, Comparator<T> comp, int i, int j) {
        final T x = a.get(i);
        final T y = a.get(j);
        if (comp.compare(x, y) > 0) {
            a.set(i, y);
            a.set(j, x);
        }
    }

    private static <T> int medianOfThree(List<T> a, Comparator<T> comp, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        hi--;

        compSwap(a, comp, lo, mid);
        compSwap(a, comp, mid, hi);
        compSwap(a, comp, lo, hi);

        return mid;
    }

    /**
     * Partially sort the list l, so that the first k elements are the k smallest, in order according to comp.
     *
     * Uses C. Martínez's Partial quicksort (http://www.siam.org/meetings/analco04/abstracts/CMartinez.pdf).
     */
    public static <T> void partialSort(List<T> l, Comparator<T> comp, int k) {
        int n = l.size();
        k = Math.min(k, n);
        partialSort(l, comp, k, 0, n);
    }

    private static <T> void partialSort(List<T> a, Comparator<T> comp, int k, int lo, int hi) {
        while (hi - lo > CUTOFF) {
            int pivotIndex = partition(a, comp, lo, hi);
            if (pivotIndex < k - 1) {
                partialSort(a, comp, k, pivotIndex + 1, hi);
            }
            hi = pivotIndex;
        }

        k = Math.min(k, hi - lo);
        // Selection sort.
        for (; k > 0; k--, lo++) {
            T min = a.get(lo);
            int minIndex = lo;
            for (int i = lo + 1; i < hi; i++) {
                T x = a.get(i);
                if (comp.compare(x, min) < 0) {
                    min = x;
                    minIndex = i;
                }
            }
            swap(a, lo, minIndex);
        }
    }

    /**
     * Partially sort the list l, so that the first k elements are the k smallest, in their natural order.
     */
    public static <T extends Comparable<? super T>> void partialSort(List<T> l, int k) {
        Comparator<T> comp = Comparator.naturalOrder();
        partialSort(l, comp, k);
    }

    // Partition function from Bentley's Programming Pearls (qsort3, page 120).
    private static <T> int partition(List<T> a, Comparator<T> comp, int lo, int hi) {
        int pivotIndex = medianOfThree(a, comp, lo, hi);
        T pivot = a.get(pivotIndex);
        a.set(pivotIndex, a.get(lo));
        a.set(lo, pivot);
        int i = lo, j = hi;

        for (;;) {
            do {
                i++;
            } while (i < hi && comp.compare(a.get(i), pivot) < 0);
            do {
                j--;
            } while (comp.compare(a.get(j), pivot) > 0);
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, lo, j);
        return j;
    }

    private static <T> void swap(List<T> a, int i, int j) {
        a.set(i, a.set(j, a.get(i)));
    }
}

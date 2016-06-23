package nl.knaw.huygens.util;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Collections2Test {
  private final int[] ks = {0, 1, 2, 3, 10, 101, 1217, 99999999};
  private final int[] seeds = {1, 6, 161, 16, 0, 9612, 126, 778, 125, 991};
  private final int[] sizes = {0, 1, 2, 3, 10, 11, 1216, 1217, 61662};

  @Test
  public void testPartialSort() {
    for (int size : sizes) {
      partialSortIntegers(size);
    }
  }

  public void partialSortIntegers(int size) {
    List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());

    for (int k : ks) {
      for (int seed : seeds) {
        Collections.shuffle(list, new Random(seed));
        Collections2.partialSort(list, k);
        for (int i = 0; i < Math.min(k, list.size()); i++) {
          assertEquals(i, list.get(i).intValue());
        }
      }
    }
  }
}

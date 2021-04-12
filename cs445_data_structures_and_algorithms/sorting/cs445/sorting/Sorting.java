package cs445.sorting;

import java.lang.Math;

public class Sorting {

  /** 
   * Selection sort iterates n-1 times, first starting at 0, finding the
   * smallest value in the array, and then swapping it with the value at index
   * 0, then going to 1, finding the next smallest value in the array, and 
   * swapping it with the value at index 1, and continues giving us a best and
   * worst case of O(n^2) 
  */
  public static <T extends Comparable<? super T>> void selectionSort(T[] a) {
    for (int i = 0; i < a.length - 1; i++) {
      int smallest = i;
      for (int j = i + 1; j < a.length; j++) {
        if (a[j].compareTo(a[smallest]) < 0) {
          smallest = j;
        }
      }
      swap(a, i, smallest);
    }
  }

  public static <T extends Comparable<? super T>> void bubbleSort(T[] a) {

  }

  public static <T extends Comparable<? super T>> void betterBubbleSort(T[] a) {

  }

  /**
   * Helper method for insertion short which works with shell sort
   * 
   * @param a The array to sort
   */
  public static <T extends Comparable<? super T>> void insertionSort(T[] a) {
    insertionSort(a, 1, 0);
  }

  /**
   * Insertion sort that sorts certain "slices" of an array, decreasing the gap 
   * size everytime.
   * [0, 22, -3, 4, 98, 57, 24, 55, 2]
   * A gap size of 3 on slice 1 would cause 22, 98, and 55 to be sorted for a 
   * resulting array:
   * [0, 22, -3, 4, 55, 57, 24, 98, 2]
   * 
   * Using a gap size of 1 and starting with a slice of 0, we can sort the 
   * entire array using pure insertion sort (see the helper method)
   * 
   * @param a The array to sort
   * @param gap The gap between the numbers to be sorted
   * @param slice The slice to sort
   */
  public static <T extends Comparable<? super T>> void insertionSort(T[] a, int   gap, int slice) {
    for (int i = slice + gap; i < a.length; i = i + gap) {
      T cur = a[i];
      int index = i - gap;
      while (index >= 0 && cur.compareTo(a[index]) < 0) {
        a[index + gap] = a[index];
        index = index - gap; 
      }
      a[index + gap] = cur;
    }
  }

  /**
   * The gap size for each slice's insertion sort starts at a.length / 2.
   * 
   * After sorting each slice, the gap size is reduced by 2and the process 
   * occurs again on every new slice until gap size is 1. 
   * 
   * Insertion sort has an interesting property where if a number can be 
   * guarunteed to be less than some distance away from its sorted position, 
   * then insertion sort has an O(n) runtime. Shell sort takes advantage of this
   * and provides a runtime less than O(n^2) and greater than O(n).
   * 
   * @param a The array to sort
   */
  public static <T extends Comparable<? super T>> void shellSort(T[] a) {
    int gap = (int) (a.length / 2.2);
    if (gap <= 1) {
      gap = 1;
    }
    while (gap > 0) {
      for (int i = 0; i < gap; i++) {
        insertionSort(a, gap, i);
      }
      gap = (int)(gap / 2.2);
      if (gap < 1) {
        insertionSort(a, 1, 0);
      }
    }
    
  }

  public static <T extends Comparable<? super T>> void mergeSort(T[] a) {
    T[] aux = (T[]) new Comparable<?>[a.length];
    mergeSort(a, aux, 0, a.length);
  }

  public static <T extends Comparable<? super T>> void mergeSort(T[] a, T[] aux,  int start, int end) {
    if (end - start > 1) {
      int mid = (start + end) / 2;
      mergeSort(a, aux, start, mid);
      mergeSort(a, aux, mid, end);
      merge(a, aux, start, mid, end);
    }
  }

  public static <T extends Comparable<? super T>> void merge(T[] a, T[] aux,      int start, int mid, int end) {
    int leftIndex = start;
    int rightIndex = mid;
    int auxIndex = start;

    while (leftIndex < mid && rightIndex < end) {
      if (a[leftIndex].compareTo(a[rightIndex]) < 0) {
        aux[auxIndex] = a[leftIndex];
        leftIndex++;
      } else {
        aux[auxIndex] = a[rightIndex];
        rightIndex++;
      }
      auxIndex++;
    }

    while (leftIndex < mid) {
      aux[auxIndex] = a[leftIndex];
      leftIndex++;
      auxIndex++;
    }

    while (rightIndex < end) {
      aux[auxIndex] = a[rightIndex];
      rightIndex++;
      auxIndex++;
    }

    for (int i = start; i < end; i++) {
      a[i] = aux[i];
    }
  }

  public static <T extends Comparable<? super T>> void quickSort(T[] a) {
    quickSort(a, 0, a.length);
  }

  public static <T extends Comparable<? super T>> void quickSort(T[] a, int       start, int end) {
    if (start == end) {
      return;
    } else {
      T pivot = a[end - 1];
      int lo = start;
      int hi = end - 2;
      while (hi > lo) {
        while (lo < end - 1 && a[lo].compareTo(pivot) < 0) {
          lo++;
        } 
        while (hi >= start && a[hi].compareTo(pivot) > 0) {
          hi--;
        }
        if (hi > lo) {
          swap(a, hi, lo);
        }
        print(a, 10);
      }
      if (a[lo].compareTo(pivot) > 0) {
        swap(a, lo, end - 1);
      }
      quickSort(a, start, lo);
      quickSort(a, lo + 1, end);
    }
  
  }


  /**
   * Swaps two values in an array a at indicies b and c
   */
  public static <T> void swap(T[] a, int b, int c) {
    T temp = a[b];
    a[b] = a[c];
    a[c] = temp;
  }

  /**
   * Creates an Integer array with the given constraints:
   * 
   * @param size The size of the array
   * @param min The minimum value to place in the array (inclusive)
   * @param max The maximum value to place in the array (exclusive)
   * @return A new, filled array obeying the given constraints.
   */
  public static Integer[] generateArray(int size, int min, int max) {
    Integer[] ret = new Integer[size];
    for (int i = 0; i < ret.length; i++) {
       ret[i] = (int) (Math.random() * (max-min)) + min;
    }
    return ret;
  }

  /**
   * Prints an array. The perline argument indicates the number of 
   * elements to print per line in the console. 
   * 
   * Format with perLine = 3:
   * 
   * [a[0], a[1], a[2]
   * a[3], a[4], a[5]]
   * 
   * Where a[x] is the value of the array at index x
   */
  public static <T> void print(T[] a, int perLine) {
    System.out.print("[");
    for (int i = 0; i < a.length - 1; i++) {
      System.out.print(a[i]);

      if ((i + 1) % perLine == 0) {
        System.out.print(",\n");
      } else {
        System.out.print(", ");
      }
    }
    System.out.println(a[a.length-1] + "]");
  }

  public static void main(String[] args) {
    int arraySize = 10000;

    if (args.length != 0) {
      arraySize = Integer.parseInt(args[0]);
    }
    Integer[] a = generateArray(arraySize, 0, 200000);
    Integer[] b = generateArray(arraySize, -200000, 200000);
    Integer[] c = generateArray(arraySize, -200000, 200000);
    Integer[] d = generateArray(arraySize, 0, 200);
    Integer[] e = generateArray(arraySize, 0, 200);

    System.out.println("Generated arrays!");

    print(e, 10);
    long start = System.nanoTime();
    quickSort(e);
    long end = System.nanoTime() - start;
    print(e, 10);

    System.out.println("Quick sort took " + end / 1e9 + "(s)");

    start = System.nanoTime();
    mergeSort(d);
    end = System.nanoTime() - start;

    System.out.println("Merge sort took " + end / 1e9 + "(s)");
    start = System.nanoTime();
    shellSort(c);
    end = System.nanoTime() - start;

    System.out.println("Shell sort took " + end / 1e9 + "(s)");

    start = System.nanoTime();
    selectionSort(a);
    end = System.nanoTime() - start;

    System.out.println("Selection sort took " + end / 1e9 + "(s)");

    start = System.nanoTime();
    insertionSort(b);
    end = System.nanoTime() - start;

    System.out.println("Insertion sort took " + end / 1e9 + "(s)");
  }
}
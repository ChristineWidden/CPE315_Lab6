import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);

        Scanner sc = new Scanner(file);
        ArrayList<Integer> addresses = new ArrayList<>();
        while (sc.hasNextLine()) {
            String scanned = sc.nextLine().split("\t")[1];
            addresses.add(Integer.parseInt(scanned, 16));
        }

        Integer[] x = new Integer[addresses.size()];
        Integer[] addrArray = addresses.toArray(x);

        processCache(1, 2048, 1, 1, addrArray);

        processCache(2, 2048, 1, 2, addrArray);

        processCache(3, 2048, 1, 4, addrArray);

        processCache(4, 2048, 2, 1, addrArray);

        processCache(5, 2048, 4, 1, addrArray);

        processCache(6, 2048, 4, 4, addrArray);

        processCache(7, 4096, 1, 1, addrArray);


    }

    public static void processCache(int cacheNum, int cacheSize, int associativity, int blockSize, Integer[] addresses) {
        int hits = 0;
        int accesses = 0;

        int indexes = cacheSize / (associativity * blockSize * 4);

        int[][] cache = new int[indexes][associativity];
        int[][] lru = new int[indexes][associativity];
        // every time an index is accessed, every lru for that spot is incremented by one
        // every time a block at an index is accessed, the lru for that spot is set to zero
        // to determine the least recently used spot at a given index in the cache,
        // I find the maximum lru for that index, aka the one that was least recently set to zero

        int byteOffset = 1 << 2;
        int lowerBits = indexes * byteOffset * blockSize;

        for (int address : addresses) {

            int index = address / (byteOffset * blockSize) % (indexes);

            for (int i = 0; i < associativity; i++) {
                lru[index][i]++;
            }

            int tag = address / lowerBits;

            int stored = 0;
            for (int i = 0; i < associativity; i++) {
                if (cache[index][i] == tag) {
                    stored = 1;
                    lru[index][i] = 0;
                }
            }

            if (stored == 0) {
                int max = 0;
                for (int i = 0; i < associativity; i++) {
                    max = lru[index][max] > lru[index][i] ? max : i;
                }
                cache[index][max] = tag;
                lru[index][max] = 0;
            }

            hits += stored;

            accesses++;
        }

        printCache(cacheNum, cacheSize, associativity, blockSize, hits, (double) hits / accesses);
    }

    public static void printCache(int cacheNum, int cacheSize, int associativity, int blockSize, int hits, double hitRate) {
        System.out.printf("Cache #%d\n" +
                "Cache size: %dB\tAssociativity: %d\tBlock size: %d\n" +
                "Hits: %d\tHit Rate: %.2f%%\n" +
                "---------------------------\n", cacheNum, cacheSize, associativity, blockSize, hits, hitRate * 100.0);
    }
}
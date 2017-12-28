package it.polito.verifoo.rest.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
/**
 * 
 * This class implements a way to retrive the log with a limited number of lines
 *
 */
public class LogReader {
	/**
	 *  Ring Buffer implementation
	 */
    private static final class RingBuffer {
        private final int limit;
        private final String[] data;
        private int counter = 0;
        /**
         * Create a new Ring buffer of specified size
         * @param limit Size of buffer
         */
        public RingBuffer(int limit) {
            this.limit = limit;
            this.data = new String[limit];
        }
        /**
         * Add element to ring buffer
         * @param line Line to add
         */
        public void add(String line) {
            data[counter++ % limit] = line;
        }
        /**
         * Get RingBuffer Content
         * @return Array of all lines
         */
        public ArrayList<String> get() {
            return  IntStream.range(counter < limit ? 0 : counter - limit, counter)
                    .mapToObj(index -> data[index % limit])
                    .collect(Collectors.toCollection(ArrayList::new));
        	
        }

    }
	/**
	 * This method retrive the last n lines of a log
	 * @param source LogFile
	 * @param limit Number of lines
	 * @return The retrived n lines
	 * @throws IOException
	 */
    public static ArrayList<String> get(Path source, int limit) throws IOException {

        try (Stream<String> stream = Files.lines(source)) {
            RingBuffer buffer = new RingBuffer(limit);
            stream.forEach(line -> buffer.add(line));
            return buffer.get();
        }

    }

}

package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    private int nthread;

    public MultiThreadedSumMatrix(int nthread) {
        this.nthread = nthread;
    }

    @Override
    public double sum(double[][] matrix) {
        int numElements = (matrix.length * matrix[0].length);
        int size = (numElements % nthread) + (numElements / nthread);

        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < numElements; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (Worker w : workers) {
            w.start();
        }

        double tot = 0;
        for (Worker w : workers) {
            try {
                w.join();
                tot += w.getResult();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        return tot;
    }

    public static class Worker extends Thread {
        private double[][] matrix;
        private int startRow;
        private int startColumn;
        private int endRow;
        private int endColumn;
        private int size;
        private double res = 0;

        public Worker(double[][] matrix, int startElement, int size) {
            this.matrix = matrix;
            this.startRow = startElement / matrix[0].length;
            this.startColumn = startElement % matrix[0].length;
            this.endRow = (startElement + size - 1) / matrix[0].length;
            this.endColumn = (startElement + size - 1) % matrix[0].length;
            this.size = size;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startRow + "x" + startColumn + " to position " + endRow + "x" + endColumn);
            for (int i = 0; i < size && startRow <= matrix.length && startColumn <= matrix[0].length; i++, startColumn++) {

                if (startColumn >= matrix[0].length) {
                    startColumn = 0;
                    startRow++;
                }
                res += matrix[startRow][startColumn];
            }
        }

        public double getResult() {
            return res;
        }
    }    
}

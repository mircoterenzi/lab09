package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    private int nThread;

    /**
     * 
     * @param nthread thread richiesti per sommare la matrice
     */
    public MultiThreadedSumMatrix(int nthread) {
        this.nThread = nthread;
    }

    @Override
    public double sum(double[][] matrix) {
        int size = (matrix.length % nThread) + (matrix.length / nThread);

        final List<Worker> workers = new ArrayList<>(nThread);
        for (int start = 0; start < matrix.length; start += size) {
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
                throw new IllegalStateException(e);
            }
            
        }
        return tot;
    }
    
    public static class Worker extends Thread {
        private double[][] matrix;
        private int start;
        private int size;
        private double res = 0;

        /**
         * 
         * @param matrix la matrice da sommare
         * @param start la riga dalla quale devo iniziare a sommare
         * @param size il numero di righe da sommare 
         */
        public Worker(double[][] matrix, int start, int size) {
            this.matrix = matrix;
            this.start = start;
            this.size = size;
        }

        @Override
        public void run() {
            for (int i = start; i < start + size && i < matrix.length; i++ ) {
                for (double curr : matrix[i]) {
                    res += curr;
                }
            }
            
        }

        public double getResult() {
            return res;
        }
    }    
}

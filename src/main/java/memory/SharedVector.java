package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            this.vector[i] = vector[i];
        }
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        this.readLock();

        if (index < 0 || index >= this.length()) {
            this.readUnlock();
            throw new IllegalArgumentException("Illegal index");
        }
        double temp = this.vector[index];
        this.readUnlock();
        return temp;
    }

    public int length() {
        // TODO: return vector length
        this.readLock();
        int len = this.vector.length;
        this.readUnlock();
        return len;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        this.readLock();
        VectorOrientation currentOrientation = this.orientation;
        this.readUnlock();
        return currentOrientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        this.writeLock();
        if (this.orientation == VectorOrientation.ROW_MAJOR)
            this.orientation = VectorOrientation.COLUMN_MAJOR;
        else {
            this.orientation = VectorOrientation.ROW_MAJOR;
        }
        this.writeUnlock();
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        this.writeLock();
        other.readLock();
        if (this.length() != other.length()) {
            this.writeUnlock();
            other.readUnlock();
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        }
        if (this.getOrientation() != other.getOrientation()) {
            this.writeUnlock();
            other.readUnlock();
            throw new IllegalArgumentException("Illegal operation: orientations mismatch");
        }

        for (int i = 0; i < this.length(); i++) {
            this.vector[i] = this.vector[i] + other.vector[i];
        }
        other.readUnlock();
        this.writeUnlock();
    }

    public void negate() {
        // TODO: negate vector
        this.writeLock();
        for (int i = 0; i < this.length(); i++) {
            this.vector[i] = this.vector[i] * (-1);
        }
        this.writeUnlock();
    }

    public double dot(SharedVector other) {
        this.readLock();
        other.readLock();

        if (this.getOrientation() == other.getOrientation()) {
            this.readUnlock();
            other.readUnlock();
            throw new IllegalArgumentException("Illegal operation: orientations mismatch");
        }
        if (this.length() != other.length()) {
            this.readUnlock();
            other.readUnlock();
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        }

        double result = 0;
        for (int i = 0; i < this.length(); i++) {
            result += this.get(i) * other.get(i);
        }

        other.readUnlock();
        this.readUnlock();
        return result;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        double[] temp;
        double sum;

        this.writeLock();
        for (int i = 0; i < matrix.length(); i++) {
            matrix.get(i).readLock();
        }

        if (matrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            if (matrix.get(0).length() != this.length()) {
                for (int i = 0; i < matrix.length(); i++) {
                    matrix.get(i).readUnlock();
                }
                this.writeUnlock();
                throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
            }

            temp = new double[matrix.length()];
            for (int j = 0; j < temp.length; j++) {
                sum = 0;
                for (int i = 0; i < this.length(); i++) {
                    sum += this.get(i) * matrix.get(j).get(i);
                }
                temp[j] = sum;
            }
        } else {

            if (matrix.length() != this.length()) {
                for (int i = 0; i < matrix.length(); i++) {
                    matrix.get(i).readUnlock();
                }
                this.writeUnlock();
                throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
            }

            temp = new double[matrix.get(0).length()];
            for (int j = 0; j < temp.length; j++) {
                sum = 0;
                for (int i = 0; i < matrix.length(); i++) {
                    sum = sum + (matrix.get(i).get(j) * this.get(i));
                }
                temp[j] = sum;
            }
        }

        for (int i = 0; i < matrix.length(); i++) {
            matrix.get(i).readUnlock();
        }
        this.vector = temp;
        this.writeUnlock();
    }
}

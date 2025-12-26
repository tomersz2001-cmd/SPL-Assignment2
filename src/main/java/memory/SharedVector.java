package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        if (vector != null && orientation != null){
            for (int i = 0; i < vector.length; i++){
                this.vector[i] = vector[i];
            }

            this.orientation = orientation;
        }
        
        throw new IllegalArgumentException("Illegal values");
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)

        if (index < 0 || index >= this.vector.length){
            throw new IllegalArgumentException("Illegal index");
        }

        return this.vector[index];
    }

    public int length() {
        // TODO: return vector length
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return this.orientation;
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
        if (this.orientation == VectorOrientation.ROW_MAJOR)
            this.orientation = VectorOrientation.COLUMN_MAJOR;
        else {
            this.orientation = VectorOrientation.ROW_MAJOR;
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        }
        if (this.getOrientation() != other.getOrientation()) {
            throw new IllegalArgumentException("Illegal operation: orientations mismatch");
        }
        for (int i = 0; i < this.vector.length; i++) {
            this.vector[i] = this.vector[i] + other.vector[i];
        }
    }

    public void negate() {
        // TODO: negate vector
        for (int i = 0; i < this.vector.length; i++) {
            this.vector[i] = this.vector[i] * (-1);
        }
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

        this.readUnlock();
        other.readUnlock();
        
        return result;
}

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
    }
}

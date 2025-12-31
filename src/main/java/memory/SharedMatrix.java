package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        this.vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        this.vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            this.vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }

    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        SharedVector[] newVectors = new SharedVector[matrix.length];
        for (int i = 0; i < newVectors.length; i++) {
            newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
        this.vectors = newVectors;

    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        SharedVector[] newVectors = new SharedVector[matrix[0].length];

        for (int i = 0; i < newVectors.length; i++) {
            double[] temp = new double[matrix.length];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = matrix[j][i];
            }
            newVectors[i] = new SharedVector(temp, VectorOrientation.COLUMN_MAJOR);
        }
        this.vectors = newVectors;
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        SharedVector[] currentVecs = this.vectors;
        if (currentVecs.length <= 0) {
            return new double[0][0];
        }

        acquireAllVectorReadLocks(currentVecs);

        int rowNum;
        int colNum;

        double[][] tempArr;

        if (this.getOrientation() == VectorOrientation.ROW_MAJOR) {
            rowNum = currentVecs.length;
            colNum = currentVecs[0].length();
            tempArr = new double[rowNum][colNum];
            for (int i = 0; i < rowNum; i++) {
                for (int j = 0; j < colNum; j++) {
                    tempArr[i][j] = currentVecs[i].get(j);
                }
            }
        }

        else {
            colNum = currentVecs.length;
            rowNum = currentVecs[0].length();

            tempArr = new double[rowNum][colNum];
            for (int i = 0; i < colNum; i++) {
                for (int j = 0; j < rowNum; j++) {
                    tempArr[j][i] = currentVecs[i].get(j);
                }
            }
        }
        releaseAllVectorReadLocks(currentVecs);
        return tempArr;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        if (index < 0 || index >= vectors.length) {
            throw new IllegalArgumentException("Invalid index");
        }

        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        SharedVector[] current = this.vectors;
        if (current.length <= 0) {
            throw new IllegalArgumentException("No orientation find");
        }

        return current[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].writeUnlock();
        }
    }
}

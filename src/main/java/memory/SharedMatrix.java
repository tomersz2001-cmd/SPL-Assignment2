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
            this.vectors[i] = new SharedVector(matrix[i], getOrientation());
        }

    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        if (vectors.length <= 0){return new double[0][0];}

        int rowNum;
        int colNum;

        double[][] tempArr;

        if(this.getOrientation()==VectorOrientation.ROW_MAJOR){
            colNum = vectors[0].length();
            rowNum = vectors.length;

            tempArr = new double[rowNum][colNum];
        for(int i = 0; i < rowNum; i++){
            for(int j = 0; j < colNum; j++){
                tempArr[i][j] = vectors[i].get(j);
            }
        }
    }

        else{
            rowNum = vectors[0].length();
            colNum = vectors.length;

            tempArr = new double[colNum][rowNum];
            for(int i = 0; i < rowNum; i++){
                for(int j = 0; j < colNum; j++){
                    tempArr[j][i] = vectors[i].get(j);
            }
        }
    }
        return tempArr;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        if (index < 0 || index >= vectors.length){
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
        if(this.length() <= 0){
            throw new IllegalArgumentException("No orientation find");
        }

        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
    }
}

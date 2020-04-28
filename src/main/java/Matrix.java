import processing.core.PApplet;

class Matrix {

    private final SnakeAI snakeAI;
    int rows, cols;
    float[][] matrix;

    Matrix(SnakeAI snakeAI, int r, int c) {
        this.snakeAI = snakeAI;
        rows = r;
        cols = c;
        matrix = new float[rows][cols];
    }

    Matrix(SnakeAI snakeAI, float[][] m) {
        this.snakeAI = snakeAI;
        matrix = m;
        rows = matrix.length;
        cols = matrix[0].length;
    }

    public void output() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                PApplet.print(matrix[i][j] + " ");
            }
            PApplet.println();
        }
        PApplet.println();
    }

    public Matrix dot(Matrix n) {
        Matrix result = new Matrix(snakeAI, rows, n.cols);

        if (cols == n.rows) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < n.cols; j++) {
                    float sum = 0;
                    for (int k = 0; k < cols; k++) {
                        sum += matrix[i][k] * n.matrix[k][j];
                    }
                    result.matrix[i][j] = sum;
                }
            }
        }
        return result;
    }

    public void randomize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = snakeAI.random(-1, 1);
            }
        }
    }

    public Matrix singleColumnMatrixFromArray(float[] arr) {
        Matrix n = new Matrix(snakeAI, arr.length, 1);
        for (int i = 0; i < arr.length; i++) {
            n.matrix[i][0] = arr[i];
        }
        return n;
    }

    public float[] toArray() {
        float[] arr = new float[rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                arr[j + i * cols] = matrix[i][j];
            }
        }
        return arr;
    }

    public Matrix addBias() {
        Matrix n = new Matrix(snakeAI, rows + 1, 1);
        for (int i = 0; i < rows; i++) {
            n.matrix[i][0] = matrix[i][0];
        }
        n.matrix[rows][0] = 1;
        return n;
    }

    public Matrix activate() {
        Matrix n = new Matrix(snakeAI, rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                n.matrix[i][j] = relu(matrix[i][j]);
            }
        }
        return n;
    }

    public float relu(float x) {
        return PApplet.max(0, x);
    }

    public void mutate(float mutationRate) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float rand = snakeAI.random(1);
                if (rand < mutationRate) {
                    matrix[i][j] += snakeAI.randomGaussian() / 5;

                    if (matrix[i][j] > 1) {
                        matrix[i][j] = 1;
                    }
                    if (matrix[i][j] < -1) {
                        matrix[i][j] = -1;
                    }
                }
            }
        }
    }

    public Matrix crossover(Matrix partner) {
        Matrix child = new Matrix(snakeAI, rows, cols);

        int randC = PApplet.floor(snakeAI.random(cols));
        int randR = PApplet.floor(snakeAI.random(rows));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i < randR) || (i == randR && j <= randC)) {
                    child.matrix[i][j] = matrix[i][j];
                } else {
                    child.matrix[i][j] = partner.matrix[i][j];
                }
            }
        }
        return child;
    }

    public Matrix clone() {
        Matrix clone = new Matrix(snakeAI, rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                clone.matrix[i][j] = matrix[i][j];
            }
        }
        return clone;
    }
}

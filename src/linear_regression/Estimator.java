package linear_regression;

public interface Estimator {
    /**
     * Given two identically sized arrays, each containing x and y values
     * such that (x[i], y[i]) would form a coordinate on a 2-dimensional plot
     * generate a model which can be used to predict y-values of new x-values
     *
     * @param xValues array of all x-values matching their corresponding y-values
     * @param yValues array of all y-values matching their corresponding x-values
     * @return returns a model that fits the provided data
     */
    Model getModel(double xValues[], double yValues[]);
}

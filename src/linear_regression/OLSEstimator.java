package linear_regression;

import java.util.Arrays;

public class OLSEstimator implements Estimator {
    /**
     * Produce a linear model using the Ordinary Least Squares
     * Full method documented at CE201_liang-3/research/linear_regression/linear_regression.md
     *
     * @param xValues array of all x-values matching their corresponding y-values
     * @param yValues array of all y-values matching their corresponding x-values
     * @return produces a LinearModel, interactable via the Model interface
     */
    @Override
    public Model getModel(double[] xValues, double[] yValues) {
        double xMean = getMean(xValues);
        double yMean = getMean(yValues);
        double coefficient = getCoefficient(xValues, yValues, xMean, yMean);
        double constant = getConstant(xMean, yMean, coefficient);
        return new LinearModel(coefficient, constant);
    }

    /**
     * Calculate optimum coefficient value
     *
     * @return optimum coefficient
     */
    private double getCoefficient(double[] xValues, double[] yValues, double xMean, double yMean) {
        double numerator = 0;
        double denominator = 0;
        for (int i = 0; i < xValues.length; i++) {
            double xDiff = xValues[i] - xMean;
            double yDiff = yValues[i] - yMean;
            numerator += xDiff * yDiff;
            denominator += xDiff * xDiff;
        }
        return numerator / denominator;
    }

    /**
     * Calculate optimum constant value
     *
     * @return optimum constant
     */
    private double getConstant(double xMean, double yMean, double coefficient) {
        return yMean - coefficient * xMean;
    }

    /**
     * Calculate mean for a given array of doubles
     *
     * @return mean of array
     */
    private double getMean(double[] values) {
        double sum = Arrays.stream(values).sum();
        return sum / values.length;
    }
}

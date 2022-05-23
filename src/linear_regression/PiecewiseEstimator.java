package linear_regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PiecewiseEstimator implements Estimator {
    private static final int sliceSize = 14; // fortnightly
    private int numModels;

    @Override
    public Model getModel(double[] xValues, double[] yValues) {
        numModels = xValues.length / sliceSize - 1; // model per fortnight
        ArrayList<ModelAndBound> candidateModels = new ArrayList<>(xValues.length - (numModels - 1));
        double[] xCandidate = new double[sliceSize];
        double[] yCandidate = new double[sliceSize];
        OLSEstimator ols = new OLSEstimator();
        for (int i = 0; i <= xValues.length - sliceSize; i += sliceSize) {
            for (int j = 0; j < sliceSize; j++) {
                xCandidate[j] = xValues[i + j];
                yCandidate[j] = yValues[i + j];
            }
            ModelAndBound candidateModel = new ModelAndBound((LinearModel) ols.getModel(xCandidate, yCandidate), i);
            candidateModels.add(candidateModel);
        }
        // get turning points where the difference in gradients are the greatest
        int[] turningPoints = getTurningPoints(candidateModels);
        LinearModel[] models = getModels(xValues, yValues, turningPoints);
        double[] bounds = new double[turningPoints.length];
        for (int i = 0; i < turningPoints.length; i++) {
            bounds[i] = xValues[turningPoints[i]];
        }
        bounds[turningPoints.length - 1] = xValues[xValues.length - 1];
        return new PiecewiseModel(models, bounds);
    }

    private int[] getTurningPoints(ArrayList<ModelAndBound> models) {
        for (int i = 0; i < models.size() - 1; i++) {
            double gradient1 = models.get(i).getModel().getGradient();
            double gradient2 = models.get(i + 1).getModel().getGradient();
            double difference = Math.abs(gradient1 - gradient2);
            models.get(i).setDiff(difference);
        }
        Collections.sort(models);
        Collections.reverse(models);
        int[] turningPoints = new int[numModels];
        for (int i = 0; i < turningPoints.length; i++) {
            turningPoints[i] = models.get(i).getStartIndex();
        }
        Arrays.sort(turningPoints);
        return turningPoints;
    }

    private LinearModel[] getModels(double[] xValues, double[] yValues, int[] turningPoints) {
        LinearModel[] models = new LinearModel[numModels];
        int first = 0;
        for (int i = 0; i < turningPoints.length; i++) {
            double xdiff = xValues[turningPoints[i]] - xValues[first];
            double ydiff = yValues[turningPoints[i]] - yValues[first];
            double gradient = ydiff / xdiff;
            double constant = yValues[first] - gradient * xValues[first];
            models[i] = new LinearModel(gradient, constant);
            System.out.println("getModels - start:" + first + " end:" + turningPoints[i] + " grad:" + gradient + " const:" + constant);
            first = turningPoints[i];
        }
        return models;
    }

    private class ModelAndBound implements Comparable<ModelAndBound> {
        private LinearModel model;
        private int startIndex;
        private double diff;

        public ModelAndBound(LinearModel model, int startIndex) {
            this.model = model;
            this.startIndex = startIndex;
        }

        public LinearModel getModel() {
            return model;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setDiff(double diff) {
            this.diff = diff;
        }

        @Override
        public int compareTo(ModelAndBound other) {
            if (diff == other.diff) return 0;
            return diff > other.diff ? 1 : -1;
        }
    }
}

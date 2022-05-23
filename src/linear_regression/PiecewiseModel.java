package linear_regression;

public class PiecewiseModel implements Model {
    private double[] modelBounds;
    private LinearModel[] models;

    // modelBounds is an array of the same length of models
    // where each entry is the x value at which the next model is used
    public PiecewiseModel(LinearModel[] models, double[] modelBounds) {
        this.modelBounds = modelBounds;
        this.models = models;
    }

    public double[] getBounds() {
        return modelBounds;
    }

    @Override
    public double predict(double x) {
        for (int i = 0; i < models.length; i++) {
            if (modelBounds[i] > x) {
                return models[i].predict(x);
            }
        }
        return models[models.length - 1].predict(x);
    }
}

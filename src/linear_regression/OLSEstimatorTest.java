package linear_regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OLSEstimatorTest {
    @BeforeEach
    public static void main(String[] args) {
        Estimator estimator = new OLSEstimator();
        double xArray[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double yArray[] = {3, 5, 7, 9, 11, 13, 15, 17, 19, 21};
        Model model = estimator.getModel(xArray, yArray);
        test(model);
    }

    @Test
    @DisplayName("Testing of OrdLeastSquares class")
    public static void test(Model model) {
        try {
            assertEquals(5.0, model.predict(2));
            assertEquals(6.0, model.predict(2.5));
            assertEquals(7.0, model.predict(3));
            System.out.println("Tests successful");
        } catch (AssertionError error) {
            System.out.println(error);
        }
    }
}

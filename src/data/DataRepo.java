package data;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;

public class DataRepo {
    private CsvLoader csvLoader;

    public DataRepo() throws IOException {
        csvLoader = new CsvLoader();
    }

    public void refresh() throws IOException {
        if (csvLoader == null) csvLoader = new CsvLoader();
        csvLoader.update();
    }

    public double[] getDateOffsets() {
        Date[] dates = csvLoader.getDates();
        double[] dateOffsets = new double[dates.length];
        Date lowDate = Arrays.stream(dates).min(Date::compareTo).get();
        Temporal low = lowDate.toInstant();
        for (int i = 0; i < dates.length; i++) {
            dateOffsets[i] = ChronoUnit.DAYS.between(low, dates[i].toInstant());
        }
        return dateOffsets;
    }

    public double[] getCases() {
        return csvLoader.getCases();
    }

    public double[] getDeaths() {
        return csvLoader.getDeaths();
    }

}

package data;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class CsvLoader {

    private static final String url = "https://coronavirus.data.gov.uk/api/v1/data?filters=areaType=overview&structure=%7B%22areaType%22:%22areaType%22,%22areaName%22:%22areaName%22,%22areaCode%22:%22areaCode%22,%22date%22:%22date%22,%22newCasesBySpecimenDate%22:%22newCasesBySpecimenDate%22,%22cumCasesBySpecimenDate%22:%22cumCasesBySpecimenDate%22,%22newDeaths28DaysByDeathDate%22:%22newDeaths28DaysByDeathDate%22,%22cumDeaths28DaysByDeathDate%22:%22cumDeaths28DaysByDeathDate%22%7D&format=csv";
    private static final String filename = "resources/data.csv";

    private Date[] dates;
    private double[] cases;
    private double[] deaths;

    // initializes object data to include saved data
    public CsvLoader() throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            update();
        } else {
            readCsv();
        }
    }

    public Date[] getDates() {
        return dates;
    }

    public double[] getCases() {
        return cases;
    }

    public double[] getDeaths() {
        return deaths;
    }

    //prints the contents of the x and the y lists
    public void printLists() {
        for (int i = 0; i < dates.length; i++) {
            System.out.println(dates[i] + " cases: " + cases[i] + " deaths: " + deaths[i]);
        }
    }

    /**
     * download new csv and update in memory representation
     *
     * @throws IOException on update failure with meaningful description of cause
     */
    public void update() throws IOException {
        try {
            downloadCsv();
        } catch (IOException e) {
            throw new IOException("Failed to download CSV");
        } catch (InterruptedException e) {
            throw new IOException("Download interrupted");
        }
        try {
            readCsv();
        } catch (IOException e) {
            throw new IOException("Failed to read downloaded CSV file: " + e.getMessage());
        }
    }

    /**
     * loads CSV file from disk & attempts to store into in-memory storage
     *
     * @throws IOException
     */
    private void readCsv() throws IOException {
        List<Date> datesList = new ArrayList<>();
        List<Integer> casesList = new ArrayList<>();
        List<Integer> deathsList = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Path path = Paths.get(filename);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toAbsolutePath().toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 8) continue; // incomplete data
                try {
                    Date tmpDate = sd.parse(values[3]);
                    Integer tmpCase = Integer.parseInt(values[4]);
                    Integer tmpDeath = Integer.parseInt(values[6]);
                    // parsing was successful at this point, so add to lists
                    datesList.add(tmpDate);
                    casesList.add(tmpCase);
                    deathsList.add(tmpDeath);
                } catch (ParseException e) {
                    continue; // ignore row
                } catch (NumberFormatException e) {
                    continue; // ignore row
                }
            }
            Collections.reverse(datesList);
            Collections.reverse(casesList);
            Collections.reverse(deathsList);
            dates = datesList.toArray(new Date[datesList.size()]);
            cases = casesList.stream().mapToDouble(i -> i).toArray();
            deaths = deathsList.stream().mapToDouble(i -> i).toArray();
        }
    }

    /**
     * Downloads remote CSV file and saves into preconfigured location
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void downloadCsv() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) throw new IOException("received response code: " + response.statusCode());
        try {
            var gzip = new GZIPInputStream(response.body());
            ensureDirExists();
            Path pathToFile = Paths.get(filename);
            Files.copy(gzip, pathToFile.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (ZipException e) {
            throw new IOException("unable to read gzip compressed csv");
        }
    }

    private void ensureDirExists() throws IOException {
        String dir = filename.split("/")[0];
        Path pathToDir = Paths.get(dir);
        try {
            Files.createDirectory(pathToDir);
        } catch (IOException e) {
        }
    }
}

package data;

import java.io.IOException;

class CsvLoaderTest {
    public static void main(String[] args) {
        try {
            CsvLoader cases = new CsvLoader();
            cases.printLists();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

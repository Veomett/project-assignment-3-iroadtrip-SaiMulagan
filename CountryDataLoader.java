import java.util.*;
import java.io.*;

public class CountryDataLoader {
    private HashMap<String, List<Border>> bordersMap;
    private HashMap<String, HashMap<String, Integer>> capitalDistances;
    private HashMap<String, String> countryCodes;

    public CountryDataLoader() {
        bordersMap = new HashMap<>();
        capitalDistances = new HashMap<>();
        countryCodes = new HashMap<>();
    }

    public void loadAllData() throws IOException {
        loadBordersData("/users/Sai/Documents/Project3/borders.txt");
        loadCapDistData("/users/Sai/Documents/Project3/capdist.csv");
        loadCountryCodes("/users/Sai/Documents/Project3/state_name.tsv");
    }
    public void loadBordersData(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            addBorderInfo(line);
        }
        br.close();
    }
    private void loadCapDistData(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String countryA = data[1]; // Country code for country A
            String countryB = data[3]; // Country code for country B
            int distance = Integer.parseInt(data[4]); // Distance in km

            capitalDistances.computeIfAbsent(countryA, k -> new HashMap<>()).put(countryB, distance);
            capitalDistances.computeIfAbsent(countryB, k -> new HashMap<>()).put(countryA, distance);
        }
        br.close();
    }
    private void loadCountryCodes(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\t");
            String countryName = data[2].split(" \\(")[0].split("/")[0]; // Country name
            String countryCode = data[1]; // Country code

            countryCodes.put(countryName.toLowerCase(), countryCode);
        }
        br.close();
    }


    // Method to parse and add border information for each country
    private void addBorderInfo(String borderData) {
        String[] parts = borderData.split(" = ");
        String country = parts[0];
        List<Border> borders = new ArrayList<>();

        // Handling alias names
        String[] countryNames = country.split(" \\("); // Splits "Turkey (Turkiye)" into ["Turkey ", "Turkiye)"]
        String mainCountryName = countryNames[0].trim(); // "Turkey"
        String aliasCountryName = countryNames.length > 1 ? countryNames[1].replace(")", "").trim() : null; // "Turkiye"

        if (parts.length > 1) {
            String[] borderingCountries = parts[1].split(";");
            for (String border : borderingCountries) {
                String[] borderInfo = border.trim().split(" ");
                String borderCountry = borderInfo[0];
                int distance = borderInfo.length > 1 ? Integer.parseInt(borderInfo[1].split("km")[0].trim()) : 0;
                borders.add(new Border(borderCountry, distance));
            }
        }

        bordersMap.put(mainCountryName, borders);
        if (aliasCountryName != null) {
            bordersMap.put(aliasCountryName, borders); // Add the same border list for the alias
        }
    }

    // Getters for the data structures
    public HashMap<String, List<Border>> getBordersMap() {
        return bordersMap;
    }

    public HashMap<String, HashMap<String, Integer>> getCapitalDistances() {
        return capitalDistances;
    }

    public HashMap<String, String> getCountryCodes() {
        return countryCodes;
    }

    // Inner class to represent borders
    public static class Border {
        private final String country;
        private final int distance;

        public Border(String country, int distance) {
            this.country = country;
            this.distance = distance;
        }

        // Getters
        public String getCountry() {
            return country;
        }

        public int getDistance() {
            return distance;
        }
    }
}
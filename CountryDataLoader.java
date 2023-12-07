import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CountryDataLoader {
    private HashMap<String, List<Border>> bordersMap;
    private HashMap<String, HashMap<String, Integer>> capitalDistances;
    private HashMap<String, String> countryCodes;

    public CountryDataLoader() {
        bordersMap = new HashMap<>();
        capitalDistances = new HashMap<>();
        countryCodes = new HashMap<>();
    }

    public void loadAllData(String bordersFilePath, String capDistFilePath, String stateNameFilePath) throws IOException {
        loadBordersData(bordersFilePath);
        loadCapDistData(capDistFilePath);
        loadCountryCodes(stateNameFilePath);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String, Date> latestEntryDate = new HashMap<>();
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split("\t");
            String countryName = data[2].split(" \\(")[0].split("/")[0]; // Country name
            String countryCode = data[1]; // Country code
            Date endDate = null; // End date
            try {
                endDate = sdf.parse(data[4]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Update only if the entry is the most recent
            if (!latestEntryDate.containsKey(countryName) || latestEntryDate.get(countryName).before(endDate)) {
                latestEntryDate.put(countryName, endDate);
                countryCodes.put(countryName.toLowerCase(), countryCode);
            }
        }
        br.close();
    }


    // Method to parse and add border information for each country
    private void addBorderInfo(String borderData) {
        System.out.println("Processing border data: " + borderData); // Debug statement

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
                System.out.println("Processing border: " + border); // Debug statement
                String[] borderInfo = border.trim().split(" ");
                String borderCountry = borderInfo[0];

                if (borderInfo.length > 1) {
                    String distanceStr = borderInfo[1].split("km")[0].trim().replace(",", "");
                    System.out.println("Parsed distance string: " + distanceStr); // Debug statement
                    int distance = Integer.parseInt(distanceStr);
                    borders.add(new Border(borderCountry, distance));
                } else {
                    borders.add(new Border(borderCountry, 0));
                }
            }
        }

        bordersMap.put(mainCountryName, borders);
        if (aliasCountryName != null) {
            bordersMap.put(aliasCountryName, borders);
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
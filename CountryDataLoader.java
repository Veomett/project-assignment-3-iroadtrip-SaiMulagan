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

            // Debug print
            System.out.println("Processing capdist data: " + line);

            int distance;
            try {
                distance = Integer.parseInt(data[4].trim()); // Distance in km, with trimming
            } catch (NumberFormatException e) {
                System.err.println("Error parsing distance for " + countryA + " and " + countryB);
                continue; // Skip this line if the distance cannot be parsed
            }

            System.out.println("Parsed distance: " + distance);

            capitalDistances.computeIfAbsent(countryA, k -> new HashMap<>()).put(countryB, distance);
            capitalDistances.computeIfAbsent(countryB, k -> new HashMap<>()).put(countryA, distance);
        }
        br.close();
    }

    private void loadCountryCodes(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String line;
        boolean firstLine = true;

        HashMap<String, String[]> latestCountryData = new HashMap<>();

        while ((line = br.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] data = line.split("\t");
            if (data.length < 5) continue;

            String countryCode = data[1].trim();
            String countryName = data[2].trim();
            String endDateStr = data[4].trim();

            try {
                Date endDate = sdf.parse(endDateStr);
                String[] existingEntry = latestCountryData.get(countryCode);
                if (existingEntry == null || sdf.parse(existingEntry[1]).before(endDate)) {
                    latestCountryData.put(countryCode, new String[]{countryName, endDateStr});
                }
            } catch (ParseException e) {
                System.err.println("Error parsing date for country: " + countryName);
            }
        }
        br.close();

        for (String code : latestCountryData.keySet()) {
            countryCodes.put(code, latestCountryData.get(code)[0].toLowerCase());
        }
    }


    // Method to parse and add border information for each country
    private void addBorderInfo(String borderData) {
        System.out.println("Processing border data: " + borderData);

        String[] parts = borderData.split(" = ");
        String country = parts[0];
        List<Border> borders = new ArrayList<>();

        // Handling alias names
        String[] countryNames = country.split(" \\(");
        String mainCountryName = countryNames[0].trim();
        String aliasCountryName = countryNames.length > 1 ? countryNames[1].replace(")", "").trim() : null;

        if (parts.length > 1) {
            String[] borderingCountries = parts[1].split(";");
            for (String border : borderingCountries) {
                System.out.println("Processing border: " + border);

                // Extract the distance and country name
                int lastKmIndex = border.lastIndexOf(" km");
                if (lastKmIndex == -1) {
                    continue; // Skip if no "km" found
                }

                String borderCountry = border.substring(0, lastKmIndex).trim();
                String distanceStr = border.substring(lastKmIndex).replaceAll("[^0-9]", ""); // Remove all non-numeric characters

                System.out.println("Parsed border country: " + borderCountry);
                System.out.println("Parsed distance string: " + distanceStr);

                int distance = distanceStr.isEmpty() ? 0 : Integer.parseInt(distanceStr);
                borders.add(new Border(borderCountry, distance));
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
    public class Border {
        private String borderCountry;
        private int distance;

        public Border(String borderCountry, int distance) {
            this.borderCountry = borderCountry;
            this.distance = distance;
        }

        public String getBorderCountry() {
            return borderCountry;
        }

        public int getDistance() {
            return distance;
        }
    }
}
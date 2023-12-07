import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

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
        boolean isFirstLine = true;
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
        String[] parts = borderData.split(" = ");
        String country = parts[0].trim().toLowerCase(); // Convert country to lowercase
        List<Border> borders = new ArrayList<>();

        if (parts.length > 1) {
            String[] borderingCountries = parts[1].split(";");
            for (String border : borderingCountries) {
                String[] borderParts = border.trim().split(" ");
                if (borderParts.length < 2) continue;

                String borderCountry = borderParts[0];
                int distance;
                try {
                    // Remove km and commas from the distance part, then parse it as an integer
                    String distanceStr = borderParts[1].replace("km", "").replace(",", "").trim();
                    distance = Integer.parseInt(distanceStr);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid distance for border: " + border);
                    continue;
                }

                borders.add(new Border(borderCountry.toLowerCase(), distance)); // Convert border country to lowercase
                System.out.println(country + " -> " + borderCountry + " " + distance + " km");
            }
        }

        bordersMap.put(country, borders); // Country already converted to lowercase
    }


    public void printBordersMap() {
        System.out.println("Borders Map: ");
        for (Map.Entry<String, List<Border>> entry : bordersMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().stream()
                    .map(Border::getBorderCountry)
                    .collect(Collectors.toList()));
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
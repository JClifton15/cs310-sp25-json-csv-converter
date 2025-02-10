package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        // Step 1: Read CSV data
        CSVReader reader = new CSVReader(new StringReader(csvString));
        List<String[]> data = reader.readAll();
        reader.close();

        if (data.isEmpty()) {
            return result;
        }

        // Step 2: Extract headers
        String[] headers = data.remove(0);

        // Step 3: Create JSON structures
        JsonArray prodNums = new JsonArray();
        JsonArray jsonData = new JsonArray();

        for (String[] row : data) {
            JsonArray rowArray = new JsonArray();
            prodNums.add(row[0]); // First column is "ProdNum"

            for (int i = 1; i < row.length; i++) {
                if (i == 1 || i == 2) { 
                    rowArray.add(Integer.parseInt(row[i])); // Convert numbers to integers
                } else {
                    rowArray.add(row[i]);
                }
            }
            jsonData.add(rowArray);
        }

        // Step 4: Construct final JSON object
        JsonObject resultJson = new JsonObject();
        resultJson.put("ProdNums", prodNums);
        resultJson.put("ColHeadings", new JsonArray(List.of(headers)));
        resultJson.put("Data", jsonData);

        // Step 5: Serialize JSON object to string
        result = Jsoner.serialize(resultJson);
    } 
    catch (Exception e) {
        e.printStackTrace();
    }

    return result.trim();
}
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
           
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());

            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray jsonData = (JsonArray) jsonObject.get("Data");

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            // Write column headers
            csvWriter.writeNext(colHeadings.toArray(new String[0]));

            // Write data rows
            for (int i = 0; i < jsonData.size(); i++) {
                JsonArray rowArray = (JsonArray) jsonData.get(i);
                List<String> row = new ArrayList<>();
                row.add(prodNums.get(i).toString()); // Add ProdNum

                for (Object cell : rowArray) {
                    row.add(cell.toString());
                }
                csvWriter.writeNext(row.toArray(new String[0]));
            }

            csvWriter.close();
            result = writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class st {

    public static void main(String[] args) {
        String filePath = "sample.c";
        try {
            generateSymbolTable(filePath);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private static void generateSymbolTable(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        Map<String, SymbolEntry> symbolTable = new HashMap<>();
        int currentAddress = 0;

        // Print the header
        System.out.printf("| %-20s | %-15s | %-10s | %-10s | %n", "Name", "Type", "Length", "Address");
        System.out.println("--------------------------------------------------------------------");

        while ((line = reader.readLine()) != null) {
            if (!line.contains("main(){")) {
                // Tokenize each line and extract data type declarations
                String[] tokens = line.split("\\s+");

                // Check for data type declarations
                for (int i = 0; i < tokens.length - 1; i++) {
                    String dataType = getDataType(tokens[i]);
                    if (dataType != null) {
                        // Assuming the variable name is the next token
                        String variableName = tokens[i + 1];

                        // Calculate the length (including array size if applicable)
                        int length = getLength(dataType, variableName);

                        // Add the variable to the symbol table
                        symbolTable.put(variableName, new SymbolEntry(dataType, length, currentAddress));

                        // Print the information in tabular format
                        System.out.printf("| %-20s | %-15s | %-10s | %-10s | %n", variableName, dataType, length,
                                currentAddress);

                        // Update the current address
                        currentAddress += length;
                    }
                }
            }
        }

        reader.close();
    }

    private static String getDataType(String token) {
        // Define a basic pattern for data type identification
        Pattern dataTypePattern = Pattern.compile("(int|char|long|double|float)\\**");

        Matcher matcher = dataTypePattern.matcher(token);
        if (matcher.matches()) {
            return matcher.group(1); // Group 1 corresponds to the matched data type
        }

        return null;
    }

    private static int getLength(String dataType, String variableName) {
        // Basic assumption: Each data type has a fixed size
        switch (dataType) {
            case "int":
                return 4 * getArraySize(variableName); // Assuming int has 4 bytes
            case "char":
                return 1 * getArraySize(variableName); // Assuming char has 1 byte
            case "long":
                return 8 * getArraySize(variableName); // Assuming long has 8 bytes
            case "double":
                return 8 * getArraySize(variableName); // Assuming double has 8 bytes
            case "float":
                return 4 * getArraySize(variableName); // Assuming float has 4 bytes
            default:
                return 0; // Handle other data types accordingly
        }
    }

    private static int getArraySize(String variableName) {
        // Check if the variable name contains square brackets indicating an array
        if (variableName.contains("[")) {
            // Extract the array size from the variable name
            String sizeString = variableName.substring(variableName.indexOf("[") + 1, variableName.indexOf("]"));
            try {
                return Integer.parseInt(sizeString);
            } catch (NumberFormatException e) {
                return 1; // Default to size 1 if parsing fails
            }
        }
        return 1; // Not an array, default size is 1
    }

    private static class SymbolEntry {
        private final String dataType;
        private final int length;
        private final int address;

        public SymbolEntry(String dataType, int length, int address) {
            this.dataType = dataType;
            this.length = length;
            this.address = address;
        }
    }
}

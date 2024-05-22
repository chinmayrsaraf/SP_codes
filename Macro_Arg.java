import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// Class to handle macros and their expansion
class Macro {
    // Variables to track index and MDT
    int indexMNT = 1;
    int mdt = 4;

    // Lists to store MNT, MDT, and parameter information
    ArrayList<NodeMNT> mntList = new ArrayList<>();
    ArrayList<NodeMDT> mdtList = new ArrayList<>();
    ArrayList<NodeActPar> actparList = new ArrayList<>();
    ArrayList<NodeForPar> forparList = new ArrayList<>();

    FileWriter fw;

    // Constructor to initialize FileWriter
    Macro(FileWriter fw) {
        this.fw = fw;
    }

    // Node class to represent entries in MNT
    class NodeMNT {
        int index;
        String name;
        int mdt;

        NodeMNT(int index, String name, int mdt) {
            this.index = indexMNT;
            this.name = name;
            this.mdt = mdt;
        }
    }

    // Method to insert entries into MDT
    public void insertMDT(String card) {
        NodeMDT node = new NodeMDT(mdt, card);
        mdtList.add(node);
        mdt++;
    }

    // Method to insert entries into MNT
    public void insertMNT(String name) {
        NodeMNT node = new NodeMNT(indexMNT, name, mdt);
        mntList.add(node);
        indexMNT++;
    }

    // Method to insert formal parameters into FOR list
    public void insertFOR(String name, String formal, String position) {
        NodeForPar node = new NodeForPar(name, formal, position);
        forparList.add(node);
    }

    // Method to insert actual parameters into ACT list
    public void insertACT(String name, String actual, int position) {
        NodeActPar node = new NodeActPar(name, actual, position + "");
        actparList.add(node);
    }

    // Method to find the position of a formal parameter
    public String findPos(String arg) {
        for (NodeForPar i : forparList) {
            if (i.formal.equals(arg)) {
                return i.position;
            }
        }
        return null;
    }

    // Node class to represent entries in MDT
    class NodeMDT {
        int mdt;
        String card;

        NodeMDT(int mdt, String card) {
            this.mdt = mdt;
            this.card = card;
        }
    }

    // Node class to represent entries in ACT list
    class NodeActPar {
        String name;
        String actual;
        String position;
        boolean flag = true;

        NodeActPar(String name, String actual, String position) {
            this.name = name;
            this.actual = actual;
            this.position = position;
        }
    }

    // Node class to represent entries in FOR list
    class NodeForPar {
        String name;
        String formal;
        String position;

        NodeForPar(String name, String formal, String position) {
            this.name = name;
            this.formal = formal;
            this.position = position;
        }
    }

    // Method to expand a macro
    public void expandMacro(String data) throws IOException {
        String[] tokens = data.split("[ ,]");
        String macroName = tokens[0];
        int index = -1;

        // Find the MDT index for the macro
        for (NodeMNT i : mntList) {
            if (macroName.equals(i.name)) {
                index = i.mdt;
                break;
            }
        }

        // If macro not found, write data to output file
        if (index == -1) {
            fw.write("\n" + data);
        }

        // Insert actual parameters into ACT list
        for (int i = 1; i < tokens.length; i++) {
            insertACT(macroName, tokens[i], i);
        }

        // Expand the macro
        boolean flag = false;
        for (NodeMDT i : mdtList) {
            if (i.card.equals("mend")) {
                flag = false;
            }
            if (i.mdt == index || flag) {
                flag = true;
                String[] hashIndex = i.card.split("#");
                if (hashIndex.length == 1) {
                    fw.write(i.card + "\n");
                } else {
                    boolean fl = false;
                    for (int k = 1; k < hashIndex.length; k++) {
                        for (NodeActPar j : actparList) {
                            if ((j.name.equals(macroName.trim()) && j.position.equals(hashIndex[k].trim())) && j.flag) {
                                if (fl) {
                                    fw.write(" " + j.actual.trim());
                                } else {
                                    fw.write(hashIndex[0].trim() + " " + j.actual.trim());
                                    fl = true;
                                }
                                j.flag = false;
                            }
                        }
                    }
                    fw.write("\n");
                }
            }
        }
    }
}

public class Macro_Arg {
    public static void main(String[] args) throws IOException {
        // Read input from file
        File myObj = new File("input2.txt");
        Scanner myReader = new Scanner(myObj);

        // FileWriter to write to output file
        FileWriter fw = new FileWriter("output2.txt", true);

        // Create Macro object
        Macro macro = new Macro(fw);

        // Process input file
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if (data.startsWith("macro")) {
                // Process macro definition
                String[] tokens = data.split("[ ,]");
                String macroName = tokens[1];
                macro.insertMNT(macroName);
                for (int i = 2; i < tokens.length; i++) {
                    macro.insertFOR(macroName, tokens[i], i - 1 + "");
                }
                data = myReader.nextLine();
                while (!data.startsWith("mend")) {
                    // Process macro body
                    tokens = data.split("[ ,]");
                    for (int i = 1; i < tokens.length; i++) {
                        String pos = macro.findPos(tokens[i]);
                        if (pos != null) {
                            tokens[i] = "#" + pos;
                        }
                    }
                    String card = "";
                    for (String i : tokens) {
                        card += (i + " ");
                    }
                    macro.insertMDT(card);
                    data = myReader.nextLine();
                }
                macro.insertMDT(data.trim());
            }
            if (data.startsWith(".code")) {
                // Process code section
                while (myReader.hasNextLine()) {
                    data = myReader.nextLine();
                    macro.expandMacro(data);
                }
            }
        }

        // Close FileWriter
        fw.close();

        // Print MDT
        System.out.println("MDT");
        System.out.println("index\tCard");
        for (Macro.NodeMDT i : macro.mdtList) {
            System.out.println(i.mdt + "\t\t" + i.card);
        }

        // Print MNT
        System.out.println("MNT");
        System.out.println("index\tname\tmdtIndex");
        for (Macro.NodeMNT i : macro.mntList) {
            System.out.println(i.index + "\t\t" + i.name + "\t\t" + i.mdt);
        }

        // Print argument list array
        System.out.println("\t\t\t\tArgument List Array");
        System.out.println("macroName\tformalParameter\tpositionalParameter");
        for (Macro.NodeForPar i : macro.forparList) {
            System.out.println(i.name + "\t\t\t" + i.formal + "\t\t\t\t#" + i.position);
        }
        System.out.println("macroName   actualParameter positionalParameter");
        for (Macro.NodeActPar i : macro.actparList) {
            System.out.println(i.name + "\t\t\t" + i.actual + "\t\t\t\t#" + i.position);
        }
    }
}
 



  


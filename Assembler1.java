import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.io.File;
import java.util.Map;
import java.util.Scanner;

class Assembler{
    HashMap<String,int[]> mot=new HashMap<>();
    HashMap<String,Integer> pot=new HashMap<>();
    HashMap<String, Integer>symbolTable=new HashMap<>();
    int locationCounter=0;

    public void initializeMachineTable(){
        mot.put("add",new int[]{1,1});
        mot.put("sub",new int[]{2,1});
        mot.put("mult",new int[]{3,1});
        mot.put("jmp",new int[]{4,1});
        mot.put("jneg",new int[]{5,1});
        mot.put("jpos",new int[]{6,1});
        mot.put("jz",new int[]{7,1});
        mot.put("load",new int[]{8,1});
        mot.put("store",new int[]{9,1});
        mot.put("read",new int[]{10,1});
        mot.put("write",new int[]{11,1});
        mot.put("stop",new int[]{12,0});
    }
    
    public void initializePOT(){
        pot.put("db",1);
        pot.put("dw",1);
        pot.put("org",1);
        pot.put("endp",0);
        pot.put("const",1);
        pot.put("end",0);
    }

    public void firstPass() throws IOException {
        File file =new File("input4.txt");
        Scanner in =new Scanner(file);
        FileWriter fw =new FileWriter("output4.txt", true);

        String data =in.nextLine();
        fw.write(data+"\n");
        while(in.hasNextLine()) {
            data = in.nextLine();
            String[] tokens = data.split("\\s+");
            if (tokens[2].equals("endp")) {
                break;
            }
            if(!tokens[3].equals("-") && !symbolTable.containsKey(tokens[3])){
                symbolTable.put(tokens[3], null);
            }
            if (!tokens[1].equals("-")) {
                symbolTable.put(tokens[1], locationCounter);
            }
            int[] array=mot.get(tokens[2]);
            fw.write(data+"\t\t"+array[0]+ "\n");
            locationCounter+=array[1]+1;
        }
        System.out.println("Symbol Table in the first pass");
        displaySBT();

        secondPass(fw,in);

        System.out.println("\nSymbol Table in the second pass");
        displaySBT();
        in.close();
        fw.close();
    }

    public void secondPass(FileWriter fw,Scanner in) throws IOException{
        while(in.hasNextLine()){
            String data=in.nextLine();
            if(data.contains("end")){
                break;
            }
            String[] tokens=data.split("\\s+");
            symbolTable.put(tokens[2],locationCounter);
            locationCounter+=pot.get(tokens[3]);
        }
    }

    public void displaySBT(){
        for(Map.Entry<String,Integer> mapElement: symbolTable.entrySet()){
            System.out.println(mapElement.getKey().trim()+"-> "+mapElement.getValue());
        }
    }

    public void display()throws IOException{
        File file=new File("output4.txt");
        Scanner in=new Scanner(file);

        FileWriter fw=new FileWriter("output5.txt");
        boolean flag=true;
        String data=in.nextLine();
        fw.write(data+"\t"+"output"+"\n");
        while(in.hasNextLine()){
            if(!data.contains("endp")){
                data=in.nextLine();
                String[] tokens=data.split("\\s+");
                fw.write(data+"\t"+symbolTable.get(tokens[3])+"\n");
            }
        }
        in.close();
        fw.close();
    }
}

public class Assembler1 {
    public static void main(String[] args) throws IOException {
        Assembler assembler=new Assembler();
        assembler.initializeMachineTable();
        assembler.initializePOT();
        assembler.firstPass();
        assembler.display();
}
}

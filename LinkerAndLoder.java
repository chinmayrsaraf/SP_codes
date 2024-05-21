

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Linker{
    static int addr=100;
    static class Node{
        String identifier;
        String dataType;
        int size;
        int addr;
        Node(String identifier,String dataType){
            this.identifier=identifier;
            this.dataType=dataType;
        }
        Node(String identifier,String dataType,int size,int addr){
            this.identifier=identifier;
            this.dataType=dataType;
            this.size=size;
            this.addr=addr;
        }
    }
    static HashMap<String,Integer> sizes=new HashMap<>();
    public void initialiseSizes(){
        sizes.put("int",4);
        sizes.put("float",4);
        sizes.put("char",2);
        sizes.put("long",8);
        sizes.put("double",8);
    }

    ArrayList<Node> sbt1=new ArrayList<>();
    ArrayList<Node> et1=new ArrayList<>();
    ArrayList<Node> sbt2=new ArrayList<>();
    ArrayList<Node> et2=new ArrayList<>();
    ArrayList<Node> gvt=new ArrayList<>();

    public void read(String[] array,ArrayList<Node> list,int flag){
        String dataType;
        int i;
        int address;
        if(flag==1){
            dataType=array[1];
            i=2;
            address=-1;
        }else{
            dataType=array[0];
            i=1;
            address=addr;
        }
        while (i<array.length){
            Node node=new Node(array[i],dataType);
            boolean arr=false;
            int num=0;
            if(array[i].contains("[")){
                int j=0;
                num=0;
                while(array[i].charAt(j)!='['){
                    j++;
                }
                j++;
                while(array[i].charAt(j)!=']'){
                    num=num*10 +(array[i].charAt(j)-'0');
                    j++;
                }
                arr=true;
            }
            node.addr=address;
            int size;
            if(arr){
                size=sizes.get(dataType)*num;
            }
            else{
                size=sizes.get(dataType);
            }
            node.size=size;
            if(address!=-1){
                address+=size;
                addr=address;
            }
            list.add(node);
            i++;
        }
    }
    public void readFile(Scanner scanner,int f){
        String data;
        ArrayList<Node> sbt=(f==1)?sbt1:sbt2;
        ArrayList<Node> et=(f==1)?et1:et2;
        while(scanner.hasNextLine()){
            data=scanner.nextLine();
            if(!data.matches("^\\s*(#.*|main\\(\\)|\\{$|}$)")){
                String[] tokens=data.split("[ ,;]");
                if(tokens[0].equals("extern")){
                    read(tokens,et,1);
                }
                else{
                    read(tokens,sbt,0);
                }
            }
        }
    }
    public int findST(String identifier,ArrayList<Node> list){
        for(Node i: list){
            if(i.identifier.equals(identifier)){
                return i.addr;
            }
        }
        return -1;
    }

    public void globalTable(){
        int addr;
        for(Node i: et1){
            addr=findST(i.identifier,sbt2);
            if(addr!=-1){
                gvt.add(new Node(i.identifier,i.dataType,i.size,addr));
            }

        }
        for(Node i: et2){
            addr=findST(i.identifier,sbt1);
            if(addr!=-1){
                gvt.add(new Node(i.identifier,i.dataType,i.size,addr));
            }
        }
    }

    public void display(ArrayList<Node> list){
        System.out.println("Var \ttype\tsize\taddress");
        for(Node i : list){
            System.out.println(i.identifier+"\t\t"+i.dataType+"     "+i.size+"\t\t"+i.addr);
        }
        System.out.println("\n");
    }

}

class Loader{
    int size;
    Loader(int size){
        this.size=size;
    }
    public boolean memoryAvailable(File file){
        System.out.println("File size: "+file.length());
        if(file.length()<size){
            size-= (int) file.length();
            System.out.println("Available Memory: "+size);
            return true;
        }
        System.out.println("Insufficient memory");
        return false;
    }
}

public class LinkerAndLoader {
    public static void main(String[] args) throws FileNotFoundException {
        File f1=new File("test1.c");
        File f2=new File("test2.c");
        Scanner myReader1=new Scanner(f1);
        Scanner myReader2=new Scanner(f2);
        Linker linker=new Linker();
        linker.initialiseSizes();
        Scanner in=new Scanner(System.in);
        System.out.println("Enter file size: ");
        int size=in.nextInt();
        boolean flag=false;
        Loader loader=new Loader(size);
        if(loader.memoryAvailable(f1)){
            linker.readFile(myReader1,1);
            flag=true;
            System.out.println("Symbol Table for file 1");
            linker.display(linker.sbt1);
            System.out.println("Extern Table for file 1");
            linker.display(linker.et1);
        }
        if(loader.memoryAvailable(f2)){
            linker.readFile(myReader2,2);
            flag=true;
            System.out.println("Symbol Table for file 2");
            linker.display(linker.sbt2);
            System.out.println("Extern Table for file 2");
            linker.display(linker.et2);
        }

        if(!flag){
            return;
        }
        if(!linker.et1.isEmpty() || !linker.et2.isEmpty()){
            linker.globalTable();
            System.out.println("Global Table");
            linker.display(linker.gvt);

        }

    }
} 

/* 

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class LinkerAndLoader {
    int size;

    LinkerAndLoader(int size) {
        this.size = size;
    }

    public boolean memoryAvailable(File file) {
        System.out.println("File size: " + file.length());
        if (file.length() < size) {
            size -= (int) file.length();
            System.out.println("Available Memory: " + size);
            return true;
        }
        System.out.println("Insufficient memory");
        return false;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter file size: ");
        int size = in.nextInt();

        LinkerAndLoader loader = new LinkerAndLoader(size);

        File f1 = new File("test1.c");
        File f2 = new File("test2.c");

        boolean flag = false;

        if (loader.memoryAvailable(f1)) {
            flag = true;
            System.out.println("File 1 can be loaded into memory.");
        }

        if (loader.memoryAvailable(f2)) {
            flag = true;
            System.out.println("File 2 can be loaded into memory.");
        }

        if (!flag) {
            System.out.println("No files can be loaded into memory.");
        }
    }
}

 */
 


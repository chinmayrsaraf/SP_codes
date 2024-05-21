
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Macro{
    NodeMNT  headMNT,tailMNT;
    int indexMNT=1;
    int mdt=21;
    NodeMDT headMDT,tailMDT;

    public void insertMNT(String name){
        NodeMNT node=new NodeMNT(indexMNT,name,mdt);
        if(headMNT==null){
            headMNT=tailMNT=node;
        }
        else{
            tailMNT.next=node;
            tailMNT=node;
        }
        indexMNT++;
    }

    public void insertMDT(String card){
        NodeMDT node=new NodeMDT(mdt,card);
        if(headMDT==null){
            headMDT=tailMDT=node;
        }
        else{
            tailMDT.next=node;
            tailMDT=node;

        }
        mdt++;
    }

    public void displayMNT(){
        NodeMNT temp=headMNT;
        System.out.println("MNT: ");
        System.out.println("index   name    MDT index");
        while(temp!=null){
            System.out.println(temp.index+"       "+temp.name+"         "+ temp.mdt);
            temp=temp.next;
        }
    }


    public void displayMDT(){
        NodeMDT temp=headMDT;
        System.out.println("MDT:");
        System.out.println("index   card");
        while(temp!=null){
            System.out.println(temp.mdt+"      "+temp.card);
            temp=temp.next;
        }
    }

    public void expandMacro(String name) throws IOException {
        NodeMNT temp=headMNT;
        int index=-1;
        FileWriter fw=new FileWriter("output.txt",true);
        while(temp!=null){
            if(temp.name.equals(name)){
                index=temp.mdt;
                break;
            }
            temp=temp.next;
        }
        if(index==-1){
            fw.write(name+"\n");
        }
        else{
            NodeMDT temp2=headMDT;
            while(temp2!=null){
                if(temp2.mdt==index){
                    break;
                }
                temp2=temp2.next;
            }
            while(!temp2.card.equals("mend")){
                fw.write(temp2.card+"\n");
                temp2=temp2.next;
            }
        }
        fw.close();

    }

    
    private class NodeMDT{
        int mdt;
        String card;
        NodeMDT next;
        NodeMDT(int mdt,String card){
            this.mdt=mdt;
            this.card=card;
        }
    }


    private class NodeMNT{
        String name;
        int index;
        int mdt;
        NodeMNT next;
        NodeMNT(int index,String name,int mdt){
            this.index=index;
            this.name=name;
            this.mdt=mdt;
        }
    }
}

public class Macro_basic {
    public static void main(String[] args) throws IOException {
            File myObj = new File("input.txt");
            Scanner myReader = new Scanner(myObj);
            Macro macro=new Macro();
            boolean flag=false;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.startsWith("macro")){
                    String[] m=data.split(" ");
                    macro.insertMNT(m[1]);
                    data=myReader.nextLine();
                    while(!data.equals("mend")){
                        macro.insertMDT(data.trim());
                        data=myReader.nextLine();
                    }
                    macro.insertMDT(data.trim()); //mend
                }
                else if(data.startsWith(".code")){
                   while(myReader.hasNextLine()) {
                       data = myReader.nextLine();
                       macro.expandMacro(data);
                   }
                }
            }
            macro.displayMNT();
            macro.displayMDT();
            myReader.close();
    }
}
 


/* 
macro ABC
  load a
  add b
mend

macro PQR
   load a
   sub b
mend

.code
PQR
ABC
load X
endp
 
 */
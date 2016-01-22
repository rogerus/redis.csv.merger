package com.tpadsz.utils.merger;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Roger on 2016/1/21.
 */
public class MergeSort implements Runnable{
    public static void main(String[] argv){
        if(argv.length<1){
            System.out.println("Need at least 1 file to merge.");
            return;

        }
        List<File> files=new ArrayList<File>(argv.length);
        for(String str:argv){
            File file=new File(str);
            if(!file.exists()
                    || !file.canRead()){
                System.out.println(String.format("%s : Cannot open for reading."));
                return;
            }else{
                files.add(file);
            }
        }

        try {
            new Thread(new MergeSort(files.toArray(new File[files.size()]))).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    File[] files;
    BufferedReader readers[];
    String latestLine=null;
    String[] lineBuffer=null;
    private MergeSort(File[] argv) throws IOException {
        this.files=argv;
        this.readers=new BufferedReader[this.files.length];
        this.lineBuffer=new String[this.files.length];

        for(int i=0;i<this.files.length;i++){
            this.readers[i]=new BufferedReader(new FileReader(this.files[i]));
            lineBuffer[i]=this.readers[i].readLine();
        }
    }



    public void run() {
        StringArrayComparator cmp=new StringArrayComparator(this.lineBuffer);
        while(isNullArray(lineBuffer)==false){
            cmp.setArray(lineBuffer);
            int index=cmp.min();
            System.out.println(this.lineBuffer[index]);
            try {
                String line=this.readers[index].readLine();
                if(line!=null) {
                    this.lineBuffer[index] = line;
                }else{
                    this.readers[index].close();
                    this.lineBuffer=new NullRemoverForArrays<String>(this.lineBuffer).getNoNullArray();
                    this.readers=new NullRemoverForArrays<BufferedReader>(this.readers).getNoNullArray();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                this.lineBuffer[index]=null;
            }
        }
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isNullArray(Object[] array){
        if(array==null){
            return true;
        }
        for(Object obj:array){
            if(obj!=null){
                return false;
            }
        }
        return true;
    }

    private void close() throws IOException {
        for(BufferedReader rd:this.readers){
            rd.close();
        }
    }
}

class NullRemoverForArrays<E>{
    E[] array;
    List<E> newList;
    E[] newArray;
    public NullRemoverForArrays(E[] array) {
        this.array = array;

        if(array!=null){
            newList=new ArrayList<E>(array.length);
            for(int i=0;i<array.length;i++){
                newList.add(array[i]);
            }
            newArray=newList.toArray(newArray);
        }else{
            newList=null;
        }
    }

    public E[] getNoNullArray(){
        if(newList==null){
            return null;
        }
        return newArray;
    }



}
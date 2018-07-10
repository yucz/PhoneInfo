package com.luoye.phoneinfo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zyw on 18-7-6.
 */

public class IOUtils {

    public static   String readFile(File f){
        BufferedReader fileReader=null;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            fileReader=new BufferedReader(new FileReader(f));
            String line;
            while ((line=fileReader.readLine())!=null){
                stringBuilder.append(line+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(fileReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  stringBuilder.toString();
    }
}

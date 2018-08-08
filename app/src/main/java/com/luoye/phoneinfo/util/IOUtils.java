package com.luoye.phoneinfo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zyw on 18-7-6.
 */

public class IOUtils {

    /**
     * 读取文件
     * @param f
     * @return
     */
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

    /**
     * 写入文件
     * @param file
     * @param data
     * @return
     */
    public static String writeFile(File file, String data)
    {
        BufferedWriter bw=null;
        String msg=null;
        try
        {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(data, 0, data.length());
            bw.flush();
        }
        catch (IOException e)
        {
            msg=e.toString();
        }
        finally
        {
            try
            {
                bw.close();
            }
            catch (IOException e)
            {
                msg=e.toString();
            }
        }
        return msg;
    }
}

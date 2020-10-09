package com.weibo.weibo.util;


import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Scanner;

@Component
public class loadJsonFile {
    public static String parseJson(File file){
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try{
            scanner = new Scanner(file, "utf-8");
            while(scanner.hasNextLine()){
                buffer.append(scanner.nextLine());
            }
        }catch (Exception e){
            e.printStackTrace();;
        }finally {
            {
                if(scanner!=null)
                    scanner.close();
            }
        }
        return buffer.toString();

    }
}

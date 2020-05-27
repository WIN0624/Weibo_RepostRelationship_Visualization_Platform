package com.example.rwredis;

import java.io.*;

public class Read {
    public static Data[] readDB(File f) throws Exception {
//        File f = new File("./src/main/resources/query.csv");
        BufferedReader bf = new BufferedReader(new FileReader(f));
        String s;
        int i = 0;
        s = bf.readLine();
        String[] head = s.split(",");
        System.out.println(head.length);
        Data d[]=new Data[450]; //这里的450是前端发过来的csv文件有多少行。
        for ( i=0; i<450; i++) {
            s = bf.readLine();
            String[] record = s.split(",");
            d[i]=new Data(record[0],record[3]);
            d[i].setNumber(i);
        }
        return d;
    }
}

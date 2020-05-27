package com.example.rwredis;

import java.io.*;
import java.util.HashMap;

public class ReadCsv {
//    public static HashMap<String[],VirtualDatabase[]> readDB() throws Exception{
    public static VirtualDatabase[] readDB() throws Exception{
        File f = new File("./src/main/resources/query.csv");
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String s;
        int i=0;
        VirtualDatabase[] vd = new VirtualDatabase[450];
        s=bf.readLine();
        String[] head = s.split(",");
        System.out.println(head.length);

        for ( i=0; i<450; i++){
            s=bf.readLine();
            String[] record = s.split(",");
            vd[i]=new VirtualDatabase();
//            System.out.println(record[1]);
//            for (int j=0; j<record.length; j++)
//                vd[i].set(j, record[j]);
            vd[i].set(0,record[0]);
            vd[i].set(1,record[1]);
            vd[i].setNumber(i);
        }
//        while((s=bf.readLine())!=null){
//            System.out.println(s);
//            String[] record=s.split(",");
//            vd[i]=new VirtualDatabase();
//            vd[i].setUser_id(Long.parseLong(record[0]));
//            vd[i].setScreen_name(record[1]);
//            vd[i].setFs_user_id(Long.parseLong(record[5]));
//            i++;
//        }
//        System.out.println(vd[8].getUser_id());
//        System.out.println(vd[1].toString());
//        HashMap<String[], VirtualDatabase[]> hashMap = new HashMap<String[], VirtualDatabase[]>();
//        hashMap.put(head, vd);
//        return hashMap;
        bf.close();

        return vd;
    }
}

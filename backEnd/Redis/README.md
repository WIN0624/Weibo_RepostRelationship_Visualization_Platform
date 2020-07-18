# Redis数据写入文档说明

## Redis数据写入部分简介

> 此部分代码用于

*Redis数据写入部分完成<font color=#FF000 >从各个csv文件中的每条数据读取所需字段的内容</font>，所需字段，如：博文ID、检索词等等，将每条数据中读入的信息<font color=#FF000 >以实体类Data实例的形式</font>存储起来，并在redis数据库中以<font color=#FF000 >有序集合</font>的存储方式，以数据的检索词作为有序集合名称，在有序集合中按一定次序存放每条数据的内容。*

## Data实体类

### Data实体类简介

*实体类Data存放每一条微博博文的检索词、博文ID和排序分数等信息。*

### Data实体类代码

```java
package com.example.rwredis;

public class Data {
    private int number;     //number作为有序集合的排序分数
    private String query;       //query为检索词
    private String weibo_id;    //weibo_id是每一条微博的博文id

    public Data()
    {
    }

    public Data(String query, String weibo_id, int number)
    //实体类Data对象有参构造方法
    {
        this.query=query;
        this.weibo_id=weibo_id;
        this.number=number;
    }

    //实体类成员变量的set、get方法
    //······
}
```

## Read.java文档说明

### Read.java文档功能描述

> Read.java文档用于

*Read.java文档用于从多个csv文件中读取相应的需要存入Redis的字段，并以构建实体类Data实例数组的形式进行存储和返回*

### Read.java 代码

```java
public class Read{
    static String[] query = new String[]{"#新冠肺炎病毒#", "2019-nCoV", "新型冠状病毒"};
    static Data[] d = new Data[1175];   //创建实体类Data的数组d，大小为1175
    static int k=0;
    /*
    * readDB()方法，从csv文件中读取需要存入Redis中的数值
    * 返回值是Data实体类的数组，即存入要保存的相应数值之后的Data数组
     */
    public static Data[] readDB() throws Exception {
        File[] f = new File[3];     //File类型的数组f存放需要进行存储的csv文件集合
        f[0] = new File("./src/main/resources/hot_xinguan_1.csv");
        f[1] = new File("./src/main/resources/hot_xinguan_2.csv");
        f[2] = new File("./src/main/resources/hot_xinguan_3.csv");
        for(int j=0;j<=2;j++)   //for循环将f数组中的所有csv中的数据相应的字段以Data类的实例形式存入Data类型的数组d中
        {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f[j])));   //获取流对象
            String record;
            record = bf.readLine();     //读取csv文件中每行的数据存入record

            int i = 0;
            while(record != null){
                //校验record也就是每行的数据是否满足要求
                if(record.indexOf(',')>=0) {
                    String[] cells = record.split(",");     //将字符串的数据以","切割，存入cells数组中
                    if (isQuery(cells[3])) {
                        d[k] = new Data(cells[3], cells[2], i);
                        //以切割后cells数组中的第四项和第三项以及行数构造Data类实例
                        i++;
                        k++;
                    }
                }
                record = bf.readLine();
            }
        }
        return d;
    }

    /*
    方法isQuery判断参数中的字符串str是否属于检索词
     */
    public static boolean isQuery(String str)
}
```

## Redis工具类RedisPoolUtil.java文档说明

### Redis工具类功能描述

> Redis工具类用于

*Redis工具类首先对Jedis连接池进行简单的配置，包括：在指定时刻通过pool能够获取到的最大的连接的jedis个数、设置最大闲置jedis个数、对端口和host进行设置等等。其次还包括了Jedis链接的获取和释放方法。*

### Redis工具类代码简述

```java
public class RedisPoolUtil {
    private static JedisPool pool;
    static{
        JedisPoolConfig jpc = new JedisPoolConfig();
        //设置通过pool能够获取到的最大的连接的jedis个数
        jpc.setMaxTotal(5);

        //设置jedis链接的最大闲置数量
        jpc.setMaxIdle(1);

        //设置端口
        int port = 6379;
        //int port = 6479;

        //设置主机
        //String host = "192.168.1.116";
        String host = "127.0.0.1";

        pool = new JedisPool(jpc, host, port);      //创建Jedis连接池
    }

    //获取Jedis连接
    public static Jedis getJedis()

    //释放Jedis连接
    public static void release(Jedis jedis)

}
```

## Write.java文档说明

### Write.java功能描述

> Write.java用于

*该类获取jedis连接之后，将每一个Data实例作为一条数据，以有序集合的方式存入Redis数据库中。其中，以检索词query作为集合名称，以序号作为排序依据，以博文ID作为集合中的元素内容。*

### write.java代码简述

```java

public class Write {
    private static Jedis jedis;

    public static void main(String[] args) throws Exception {
        //获取jedis连接
        jedis = RedisPoolUtil.getJedis();
        //设置密码
        jedis.auth("nopassword");
        System.out.println(jedis.ping());
        //从csv文件中读取每条数据相应字段的内容作为Data类实例存储在数组中
        Data[] d=Read.readDB();
        //以每条数据的检索词query作为集合名称，以序号作为排序依据，以博文ID作为集合中的成员
        for(int i=0; i<d.length&&d[i]!=null; i++)
        {
            jedis.zadd(d[i].getQuery(),d[i].getNumber(),d[i].getWeibo_id());
        }
        jedis.close();
        System.exit(0);
    }
}
```

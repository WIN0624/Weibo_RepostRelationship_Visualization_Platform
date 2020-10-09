Nshead.java说明
Nshead.java主要说明mina通讯中发送消息的一些参数

Nshead.java功能说明
Nshead.java对一些无符号的参数进行配置，说明了这些参数的长度范围，其中log_id用来作为一次request到response请求以及到相应到客户端的数据的请求的唯一标识，magic_num用来标记文件或者协议的格式，body_len说明了响应体的最大长度

Nshead.java代码说明
public class Nshead {

    public final static int HEAD_LENGTH = 36;

    // unsigned short id;
    public short id;

    // unsigned short version;
    public short version;

    // unsigned int log_id;
    public int log_id;

    public byte[] provider = new byte[16];

    // unsigned int magic_num;
    public int magic_num = 0xfb709394;

    // unsigned int reserved;
    public int reserved;

    // unsigned int body_len;
    // 在DA中len最大不能超过Integer.MAX_VALUE
    public int body_len;
}
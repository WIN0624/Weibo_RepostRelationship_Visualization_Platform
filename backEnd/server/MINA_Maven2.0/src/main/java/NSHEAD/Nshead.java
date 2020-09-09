package NSHEAD;

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

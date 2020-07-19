# Util.java说明

在使用mina的时候，会出现字符间相互转换的现象，为了**解决编码**的问题，用Util类检测和转换字节序

## Util.java功能说明

Util.java提供了在不同类型的数据之间进行转换的方法，它将发送报文的数据形式转化成远程主机响应报文的数据类型。

## Util.java代码说明

```java
public final class Util {
public static int convertEndian(int i) {
	return makeInt((byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF),
	(byte) ((i >>> 24) & 0xFF));
	}
//把32位的int通过移位转换为4个byte
    
public static long convertEndian(long i) {
	return makeLong((byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF),
			(byte) ((i >>> 24) & 0xFF), (byte) ((i >>> 32) & 0xFF), (byte) ((i >>> 40) & 0xFF),
			(byte) ((i >>> 48) & 0xFF), (byte) ((i >>> 56) & 0xFF));
}
//把64位的long通过移位转换为8个byte

public static short makeShort(byte b1, byte b0) {
	return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff) << 0));
}
//byte是8位，short是16位，b1左移8位+b2就等于short

public static int makeInt(byte b3, byte b2, byte b1, byte b0) {
	return (int) ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
}
//同理makeShort

public static long makeLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) {
	return ((((long) b7 & 0xff) << 56) | (((long) b6 & 0xff) << 48) | (((long) b5 & 0xff) << 40)
			| (((long) b4 & 0xff) << 32) | (((long) b3 & 0xff) << 24) | (((long) b2 & 0xff) << 16)
			| (((long) b1 & 0xff) << 8) | (((long) b0 & 0xff) << 0));
}
//同理makeShort

private final static Gson gson = new GsonBuilder().disableInnerClassSerialization().serializeNulls()
		.disableHtmlEscaping().serializeSpecialFloatingPointValues().create();
//使用GsonBuilder构建gson,禁用内部类序列化,支持空对象序列化,跳过html特殊符号转码，支持特殊值序列化

public static String toJsonString(Object obj) {
	return gson.toJson(obj);
}
//返回JsonString

public static <T> T fromJson(Reader json, Class<T> classOfT) {
	return gson.fromJson(json, classOfT);
}
//从Json相关对象到Java实体

public static <T> T fromJson(String json, Class<T> classOfT) {
	return gson.fromJson(json, classOfT);
}
//从Json相关对象到Java实体
}
```

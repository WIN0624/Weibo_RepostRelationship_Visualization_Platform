package com.weibo.UTIL;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Util {
	public static int convertEndian(int i) {
		return makeInt((byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF),
				(byte) ((i >>> 24) & 0xFF));
	}

	public static long convertEndian(long i) {
		return makeLong((byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF),
				(byte) ((i >>> 24) & 0xFF), (byte) ((i >>> 32) & 0xFF), (byte) ((i >>> 40) & 0xFF),
				(byte) ((i >>> 48) & 0xFF), (byte) ((i >>> 56) & 0xFF));
	}

	public static short makeShort(byte b1, byte b0) {
		return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff) << 0));
	}

	public static int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (int) ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
	}

	public static long makeLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) {
		return ((((long) b7 & 0xff) << 56) | (((long) b6 & 0xff) << 48) | (((long) b5 & 0xff) << 40)
				| (((long) b4 & 0xff) << 32) | (((long) b3 & 0xff) << 24) | (((long) b2 & 0xff) << 16)
				| (((long) b1 & 0xff) << 8) | (((long) b0 & 0xff) << 0));
	}

	private final static Gson gson = new GsonBuilder().disableInnerClassSerialization().serializeNulls()
			.disableHtmlEscaping().serializeSpecialFloatingPointValues().create();

	public static String toJsonString(Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T fromJson(Reader json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}
}

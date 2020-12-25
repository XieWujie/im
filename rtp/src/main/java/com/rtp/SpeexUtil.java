package com.rtp;

public class SpeexUtil {
    /*
     * quality 1 : 4kbps (very noticeable artifacts, usually intelligible) 2 :
     * 6kbps (very noticeable artifacts, good intelligibility) 4 : 8kbps
     * (noticeable artifacts sometimes) 6 : 11kpbs (artifacts usually only
     * noticeable with headphones) 8 : 15kbps (artifacts not usually noticeable)
     */
    //设置为4时压缩比为1/16(与编解码密切相关)
    private static final int DEFAULT_COMPRESSION = 4;

    static {
        try {
            System.loadLibrary("speex");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static SpeexUtil speexUtil = null;

    SpeexUtil() {
        open(DEFAULT_COMPRESSION);
    }

    public static SpeexUtil getInstance(){
        if (speexUtil == null) {
            synchronized (SpeexUtil.class) {
                if (speexUtil == null) {
                    speexUtil = new SpeexUtil();
                }
            }
        }
        return speexUtil;
    }

    public native int open(int compression);

    public native int getFrameSize();

    public native int decode(byte encoded[], short lin[], int size);

    public native int encode(short lin[], int offset, byte encoded[], int size);

    public native void close();
}

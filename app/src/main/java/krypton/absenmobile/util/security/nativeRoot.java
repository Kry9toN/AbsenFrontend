package krypton.absenmobile.util.security;

public class nativeRoot {

    static {
        System.loadLibrary("nativeRootCheck");
    }

    static native boolean isMagiskPresentNative();
}

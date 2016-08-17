package com.github.boukefalos.jlibloader;

import java.io.File;

import com.github.boukefalos.jlibloader.internal.NativeBinaryLoader;
import com.github.boukefalos.jlibloader.internal.NativeBinaryLocator;
import com.github.boukefalos.jlibloader.internal.NativeLibraryLoader;
import com.github.boukefalos.jlibloader.internal.NativeLibraryLocator;
import com.github.boukefalos.jlibloader.internal.Platform;

public class Native {
	private static Platform platform;
    private static NativeLibraryLoader libraryLoader;
    private static NativeBinaryLoader binaryLoader;

    private Native() {
    }

    /**
     * Initialises the native integration, if not already initialized.
     *
     * @param extractDir The directory to extract native resources into. May be null, in which case a default is
     * selected.
     *
     * @throws NativeLibraryUnavailableException When the native library is not available on the current machine.
     * @throws NativeException On failure to load the native library.
     */
    static public void init(File extractDir) throws NativeException {
        synchronized (Native.class) {
        	if (platform == null) {
        		platform = Platform.current();            
                try {
                    libraryLoader = new NativeLibraryLoader(platform, new NativeLibraryLocator(extractDir));
                    binaryLoader = new NativeBinaryLoader(platform, new NativeBinaryLocator(extractDir));
                } catch (NativeException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new NativeException("Failed to initialise native integration.", t);
                }
            }
        }
    }

    public static void load(String group, String name) {
        load(group, name, name);
    }

    public static void load(String group, String name, String file) {
        init(null);
        try {
            libraryLoader.load(group, name, Platform.current().getLibraryName(file));
        } catch (NativeException e) {
            throw e;
        } catch (Throwable t) {
            throw new NativeException("Failed to load native library.");
        }        
    }

	public static void binary(String group, String name) {
		binary(group, name, name);		
	}

	public static String binary(String group, String name, String file) {
        init(null);
        try {
            return binaryLoader.load(group, name, Platform.current().getBinaryName(file));
        } catch (NativeException e) {
            throw e;
        } catch (Throwable t) {
            throw new NativeException("Failed to load native binary.");
        }		
	}
}

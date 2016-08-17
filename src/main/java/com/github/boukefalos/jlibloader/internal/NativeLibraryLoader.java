package com.github.boukefalos.jlibloader.internal;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.github.boukefalos.jlibloader.NativeException;
import com.github.boukefalos.jlibloader.NativeLibraryUnavailableException;

public class NativeLibraryLoader extends NativeLoader {
	private final Set<String> loaded = new HashSet<String>();
    private final NativeLibraryLocator nativeLibraryLocator;

    public NativeLibraryLoader(Platform platform, NativeLibraryLocator nativeLibraryLocator) {
        this.platform = platform;
        this.nativeLibraryLocator = nativeLibraryLocator;
    }

    public void load(String libraryGroupName, String libraryName, String libraryFileName) {
        if (loaded.contains(libraryFileName)) {
            return;
        }
        try {
            File libFile = nativeLibraryLocator.find(new NativeDef(libraryGroupName, libraryName, libraryFileName, platform.getId()));
            if (libFile == null) {
                throw new NativeLibraryUnavailableException(String.format("Native library '%s' is not available for %s.", libraryFileName, platform));
            }
            System.load(libFile.getCanonicalPath());
        } catch (NativeException e) {
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new NativeException(String.format("Failed to load native library '%s' for %s.", libraryFileName, platform), t);
        }
        loaded.add(libraryFileName);
    }
}
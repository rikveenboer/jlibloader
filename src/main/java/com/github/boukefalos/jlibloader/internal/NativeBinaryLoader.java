package com.github.boukefalos.jlibloader.internal;

import java.io.File;
import java.util.HashMap;

import com.github.boukefalos.jlibloader.NativeBinaryUnavailableException;
import com.github.boukefalos.jlibloader.NativeException;

public class NativeBinaryLoader extends NativeLoader {
	private HashMap<String,String> binaryFileMap = new HashMap<String,String>();
	private NativeBinaryLocator nativeBinaryLocator;

	public NativeBinaryLoader(Platform platform, NativeBinaryLocator nativeBinaryLocator) {
        this.platform = platform;
        this.nativeBinaryLocator = nativeBinaryLocator;
    }

	public String load(String binaryGroupName, String binaryName, String binaryFileName) {
        if (binaryFileMap.containsKey(binaryFileName)) {
            return binaryFileMap.get(binaryFileName);
        }
		try {
	        File binFile = nativeBinaryLocator.find(new NativeDef(binaryGroupName, binaryName, binaryFileName, platform.getId()));
	        if (binFile == null) {
	            throw new NativeBinaryUnavailableException(String.format("Native binary '%s' is not available for %s.", binaryFileName, platform));
	        }
	        String binaryPath = binFile.getAbsolutePath();
	        binaryFileMap.put(binaryFileName,  binaryPath);
		    return binaryPath;
	    } catch (NativeException e) {
	        throw e;
	    } catch (Throwable t) {
	        t.printStackTrace();
	        throw new NativeException(String.format("Failed to load native library '%s' for %s.", binaryFileName, platform), t);
	    }		
	}
}
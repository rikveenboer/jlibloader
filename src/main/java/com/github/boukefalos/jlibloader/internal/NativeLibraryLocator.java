package com.github.boukefalos.jlibloader.internal;

import java.io.File;
import java.io.IOException;

public class NativeLibraryLocator extends NativeLocator {
    public NativeLibraryLocator(File extractDir) {
		super(extractDir);
	}

	public File find(NativeDef nativeDef) throws IOException {
    	if (super.find(nativeDef) == null) {
	        String componentName = nativeDef.file.replaceFirst("^lib", "").replaceFirst("\\.\\w+$", "");
	        int pos = componentName.indexOf("-");
	        while (pos >= 0) {
	            componentName = componentName.substring(0, pos) + Character.toUpperCase(componentName.charAt(pos + 1)) + componentName.substring(pos + 2);
	            pos = componentName.indexOf("-", pos);
	        }
	        File libFile = new File(String.format("build/binaries/%sSharedLibrary/%s/%s", componentName, nativeDef.platform.replace("-", "_"), nativeDef.file));
	        if (libFile.isFile()) {
	            return libFile;
	        }
	        libFile = new File(String.format("build/binaries/mainSharedLibrary/%s/%s", nativeDef.platform.replace("-", "_"), nativeDef.file));
	        if (libFile.isFile()) {
	            return libFile;
	        }
    	}
        return null;
    }
}
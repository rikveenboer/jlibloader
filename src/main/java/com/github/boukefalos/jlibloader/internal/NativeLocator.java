package com.github.boukefalos.jlibloader.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;

import com.github.boukefalos.jlibloader.NativeException;

public class NativeLocator {
    protected final File extractDir;

    public NativeLocator(File extractDir) {
        this.extractDir = extractDir;
    }

	public File find(NativeDef nativeDef) throws IOException {
		String resourceName = String.format("%s/%s/%s/%s", nativeDef.getGroupPath(), nativeDef.name, nativeDef.platform, nativeDef.file);
        if (extractDir != null) {
            File libFile = new File(extractDir, String.format("%s/%s", nativeDef.platform, nativeDef.file));
            File lockFile = new File(libFile.getParentFile(), libFile.getName() + ".lock");
            lockFile.getParentFile().mkdirs();
            lockFile.createNewFile();
            RandomAccessFile lockFileAccess = new RandomAccessFile(lockFile, "rw");
            try {
                if (lockFile.length() > 0 && lockFileAccess.readBoolean()) {
                    // Library has been extracted
                    return libFile;
                }
                URL resource = getClass().getClassLoader().getResource(resourceName);
                if (resource != null) {
                    // Extract library and write marker to lock file
                    libFile.getParentFile().mkdirs();
                    copy(resource, libFile);
                    lockFileAccess.seek(0);
                    lockFileAccess.writeBoolean(true);
                    return libFile;
                }
            } finally {
                // Also releases lock
                lockFileAccess.close();
            }
        } else {
            URL resource = getClass().getClassLoader().getResource(resourceName);
            if (resource != null) {
                File libFile;
                File libDir = File.createTempFile(nativeDef.file, "dir");
                libDir.delete();
                libDir.mkdirs();
                libFile = new File(libDir, nativeDef.file);
                libFile.deleteOnExit();
                libDir.deleteOnExit();
                copy(resource, libFile);
                return libFile;
            }
        }
        return null;		
	}

    protected static void copy(URL source, File dest) {
        try {
            InputStream inputStream = source.openStream();
            try {
                OutputStream outputStream = new FileOutputStream(dest);
                try {
                    byte[] buffer = new byte[4096];
                    while (true) {
                        int nread = inputStream.read(buffer);
                        if (nread < 0) {
                            break;
                        }
                        outputStream.write(buffer, 0, nread);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new NativeException(String.format("Could not extract native file."), e);
        }
    }
}
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
            File file = new File(extractDir, String.format("%s/%s", nativeDef.platform, nativeDef.file));
            File lockFile = new File(file.getParentFile(), file.getName() + ".lock");
            lockFile.getParentFile().mkdirs();
            lockFile.createNewFile();
            RandomAccessFile lockFileAccess = new RandomAccessFile(lockFile, "rw");
            try {
                if (lockFile.length() > 0 && lockFileAccess.readBoolean()) {
                    // File has been extracted
                    return file;
                }
                URL resource = getClass().getClassLoader().getResource(resourceName);
                if (resource != null) {
                    // Extract file and write marker to lock file
                    file.getParentFile().mkdirs();
                    copy(resource, file);
                    lockFileAccess.seek(0);
                    lockFileAccess.writeBoolean(true);
                    return file;
                }
            } finally {
                // Also releases lock
                lockFileAccess.close();
            }
        } else {
            URL resource = getClass().getClassLoader().getResource(resourceName);
            if (resource != null) {
                File file;
                File directory = File.createTempFile(nativeDef.file, "dir");
                directory.delete();
                directory.mkdirs();
                file = new File(directory, nativeDef.file);
                file.deleteOnExit();
                directory.deleteOnExit();
                copy(resource, file);
                return file;
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
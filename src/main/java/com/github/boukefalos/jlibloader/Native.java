/*
 * Copyright 2012 Adam Murdoch
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.boukefalos.jlibloader;

import java.io.File;

import com.github.boukefalos.jlibloader.internal.NativeLibraryLoader;
import com.github.boukefalos.jlibloader.internal.NativeLibraryLocator;
import com.github.boukefalos.jlibloader.internal.Platform;

public class Native {
    private static NativeLibraryLoader loader;

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
    static public void init(File extractDir) throws NativeLibraryUnavailableException, NativeException {
        synchronized (Native.class) {
            if (loader == null) {
                Platform platform = Platform.current();
                try {
                    loader = new NativeLibraryLoader(platform, new NativeLibraryLocator(extractDir));
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
        	loader.load(group, name, Platform.current().getLibraryName(file));
        } catch (NativeException e) {
                throw e;
        } catch (Throwable t) {
                throw new NativeException("Failed to load native library.");
        }
		
	}
}

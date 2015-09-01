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

package com.github.boukefalos.jlibloader.internal;

import com.github.boukefalos.jlibloader.NativeLibraryUnavailableException;

public abstract class Platform {
    private static Platform platform;

    public static Platform current() {
        synchronized (Platform.class) {
            if (platform == null) {
                String osName = getOperatingSystem().toLowerCase();
                String arch = getArchitecture();
                if (osName.contains("windows")) {
                    if (arch.equals("x86")) {
                        platform = new Window32Bit();
                    }
                    else if (arch.equals("amd64")) {
                        platform = new Window64Bit();
                    }
                } else if (osName.contains("linux")) {
                    if (arch.equals("amd64") || arch.equals("x86_64")) {
                        platform = new Linux64Bit();
                    }
                    else if (arch.equals("i386") || arch.equals("x86")) {
                        platform = new Linux32Bit();
                    }
                } else if (osName.contains("os x") || osName.contains("darwin")) {
                    if (arch.equals("i386")) {
                        platform = new OsX32Bit();
                    }
                    else if (arch.equals("x86_64") || arch.equals("amd64") || arch.equals("universal")) {
                        platform = new OsX64Bit();
                    }
                }
                else if (osName.contains("freebsd")) {
                    if (arch.equals("amd64")) {
                        platform = new FreeBSD64Bit();
                    }
                    else if (arch.equals("i386") || arch.equals("x86")) {
                        platform = new FreeBSD32Bit();
                    }
                }
                if (platform == null) {
                    platform = new Unsupported();
                }
            }
            return platform;
        }
    }

    public boolean isWindows() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getOperatingSystem(), getArchitecture());
    }

    public String getLibraryName(String name) {
        throw new NativeLibraryUnavailableException(String.format("Native library is not available for %s.", toString()));
    }

    public abstract String getId();

    private static String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    private static String getArchitecture() {
        return System.getProperty("os.arch");
    }

    private abstract static class Windows extends Platform {
        @Override
        public boolean isWindows() {
            return true;
        }

        @Override
        public String getLibraryName(String name) {
            return String.format("%s.dll", name);
        }
    }

    private static class Window32Bit extends Windows {
        @Override
        public String getId() {
            return "windows-i386";
        }
    }

    private static class Window64Bit extends Windows {
        @Override
        public String getId() {
            return "windows-amd64";
        }
    }

    private static abstract class Posix extends Platform {}

    private abstract static class Unix extends Posix {
        @Override
        public String getLibraryName(String name) {
            return String.format("lib%s.so", name);
        }
    }

    private static class Linux32Bit extends Unix {
        @Override
        public String getId() {
            return "linux-i386";
        }
    }

    private static class Linux64Bit extends Unix {
        @Override
        public String getId() {
            return "linux-amd64";
        }
    }

    private static class FreeBSD32Bit extends Unix {
        @Override
        public String getId() {
            return "freebsd-i386";
        }
    }

    private static class FreeBSD64Bit extends Unix {
        @Override
        public String getId() {
            return "freebsd-amd64";
        }
    }

    private static abstract class OsX extends Posix {
        @Override
        public String getLibraryName(String name) {
            return String.format("lib%s.dylib", name);
        }
    }

    private static class OsX32Bit extends OsX {
        @Override
        public String getId() {
            return "osx-i386";
        }
    }

    private static class OsX64Bit extends OsX {
        @Override
        public String getId() {
            return "osx-amd64";
        }
    }

    private static class Unsupported extends Platform {
        @Override
        public String getId() {
            throw new UnsupportedOperationException();
        }
    }
}
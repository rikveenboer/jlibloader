package com.github.boukefalos.jlibloader.internal;

public class NativeDef {
    final String group;
    final String name;
    final String file;
    final String platform;

    public NativeDef(String group, String name, String file, String platform) {
        this.group = group;
        this.name = name;
        this.file = file;
        this.platform = platform;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        NativeDef other = (NativeDef) obj;
        return name.equals(other.name) && platform.equals(other.platform);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ platform.hashCode();
    }

    public String getGroupPath() {
        return group.replace(".", "/");
    }
}
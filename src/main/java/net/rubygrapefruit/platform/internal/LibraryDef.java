package net.rubygrapefruit.platform.internal;

public class LibraryDef {
	final String group;
    final String name;
    final String platform;

    public LibraryDef(String group, String name, String platform) {
    	this.group = group;
        this.name = name;
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
        LibraryDef other = (LibraryDef) obj;
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

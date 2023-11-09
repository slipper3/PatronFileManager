package Moduls;

import java.io.File;

public class Drive {
	private String driveName;
	private File file;
	private double usedPer;
	private double remPer;
	private String totalSpace;
	private String usedSpace;
	private String freeSpace;
	private double dblTotalSpace;
	private double dblUsedSpace;
	private double dblFreeSpace;

	public double getUsedPer() {
		return usedPer;
	}

	public void setDblTotalSpace(double dblTotalSpace) {
		this.dblTotalSpace = dblTotalSpace;
	}

	public double getDblUsedSpace() {
		return dblUsedSpace;
	}

	public void setDblUsedSpace(double dblUsedSpace) {
		this.dblUsedSpace = dblUsedSpace;
	}

	public double getDblFreeSpace() {
		return dblFreeSpace;
	}

	public void setDblFreeSpace(double dblFreeSpace) {
		this.dblFreeSpace = dblFreeSpace;
	}

	public String getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}

	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}

	public String getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(String usedSpace) {
		this.usedSpace = usedSpace;
	}

	public double getRemPer() {
		return remPer;
	}

	public void setRemPer(double remPer) {
		this.remPer = remPer;
	}


	public void setPer(double per) {
		this.usedPer = per;
	}

	public void setDriveName(String driveName) {
		this.driveName = driveName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return file.getAbsolutePath();
	}

}

package com.my.entity;


public class Cluster {
	private String name;
	private double maxBandWidth;
	private double curBandWidth;
	//private double addedBandWidth;
	private double load;
	private String vip;
	
	public Cluster() {
		super();
	}
	
	public Cluster(String name) {
		super();
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMaxBandWidth() {
		return maxBandWidth;
	}
	public void setMaxBandWidth(double maxBandWidth) {
		this.maxBandWidth = maxBandWidth;
	}
	public double getCurBandWidth() {
		return curBandWidth;
	}
	public void setCurBandWidth(double curBandWidth) {
		this.curBandWidth = curBandWidth;
	}
	/*public double getAddedBandWidth() {
		return addedBandWidth;
	}*/
	/*public void setAddedBandWidth(double addedBandWidth) {
		this.addedBandWidth = addedBandWidth;
	}*/
	public double getLoad() {
		return load;
	}
	public void setLoad(double load) {
		this.load = load;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	
	@Override
	public String toString() {
		return "[" + name + "-" + curBandWidth + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}

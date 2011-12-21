package com.anthonyatkins.simplebackgammon.model;

public class SimpleDie implements Comparable{
	@Override
	public String toString() {
		return "SimpleDie [value=" + value + ", color=" + color + ", used="+ used + "]";
	}

	private int value;
	private int color;
	
	private boolean used = false;

	public SimpleDie(int color) {
		this.color = color;
		roll();
	}
	
	public SimpleDie(int value, int color) {
		this.value = value;
		this.color = color;
	}
	
	public SimpleDie(SimpleDie die) {
		this.value = die.getValue();
		this.color = die.getColor();
		this.used = die.isUsed();
	}

	public void roll() {
		this.value = (int) (Math.round(Math.random() * 5) + 1);
		this.used = false;
	}
	
	public void setUsed() {
		this.used = true;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public boolean isUsed() {
		return used;
	}

	public int getColor() {
		return this.color;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + color;
		result = prime * result + (used ? 1231 : 1237);
		result = prime * result + value;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleDie))
			return false;
		SimpleDie other = (SimpleDie) obj;
		if (color != other.color)
			return false;
		if (used != other.used)
			return false;
		if (value != other.value)
			return false;
		
		return true;
	}

	public int compareTo(Object another) {
		if (!(another instanceof SimpleDie)) { return 1; }

		SimpleDie otherDie = (SimpleDie) another;

		return this.value - otherDie.value;
	}
}

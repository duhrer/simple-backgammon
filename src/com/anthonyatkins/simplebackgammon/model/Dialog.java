package com.anthonyatkins.simplebackgammon.model;


public class Dialog{
	private String message = null;
	
	public Dialog() {
	}
	
	public Dialog(Dialog dialog) {
		if (dialog.message != null) {
			this.message = String.copyValueOf(dialog.message.toCharArray());
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage()  {
		return this.message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Dialog))
			return false;
		Dialog other = (Dialog) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
	
	
}

package com.anthonyatkins.simplebackgammon.model;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.activity.SimpleBackgammon;


public class Dugout extends Slot {
	public final int color;
	public Dugout(int position, int color, Game game) {
		super(Slot.NONE, position, game);
		this.color = color;
	}
	
	public Dugout() {
		super(Slot.NONE,0,null);
		this.color = Constants.BLACK;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Dugout))
			return false;
		
		return super.equals(obj);
	}

}

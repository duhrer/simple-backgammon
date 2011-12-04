package com.anthonyatkins.simplebackgammon.model;

import java.util.Comparator;

public class SlotSetComparator implements Comparator<Slot> {
	public int compare(Slot slot1, Slot slot2) {
		return slot1.getPosition() - slot2.getPosition();
	}
}
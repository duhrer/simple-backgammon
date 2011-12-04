package com.anthonyatkins.simplebackgammon.view;

import java.util.Comparator;

public class SlotViewSetComparator implements Comparator<SimpleSlotView> {
	public int compare(SimpleSlotView slotView1, SimpleSlotView slotView2) {
		return slotView1.slot.getPosition() - slotView2.slot.getPosition();
	}
}

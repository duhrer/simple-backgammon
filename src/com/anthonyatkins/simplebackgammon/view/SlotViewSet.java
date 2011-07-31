package com.anthonyatkins.simplebackgammon.view;

import java.util.TreeSet;

public class SlotViewSet extends TreeSet<SimpleSlotView> {
	private static final long serialVersionUID = 5231465811689027339L;

	public SlotViewSet() {
		super(new SlotViewSetComparator());
	}

}

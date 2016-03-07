package com.socks.selectcity.utils;

import com.socks.selectcity.bean.CityBean;

import java.util.Comparator;

/**
 * 拼音排序器
 */
public class PinyinComparator implements Comparator<CityBean> {

	public int compare(CityBean o1, CityBean o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}

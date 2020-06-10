package com.zsinfo.guoranhao.chat.utils;

import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;

import java.util.Comparator;
import java.util.Date;


public class TimeSortCompare  implements Comparator<ChatMessageBean>{

	@Override
	public int compare(ChatMessageBean arg0, ChatMessageBean arg1) {
		Date acceptTime1= new Date(arg0.getCreateTime());
		Date acceptTime2= new Date(arg1.getCreateTime());
		if(acceptTime1.after(acceptTime2)) return 1;
		return -1;
	}
}

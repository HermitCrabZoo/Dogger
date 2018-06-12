package com.zoo.dogger.cons;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;

import com.zoo.dogger.util.ComUtil;
import com.zoo.swing.component.CheckValue;
import com.zoo.swing.component.Checker;

public final class Cnst {
	private Cnst() {}
	
	public static final int EDGE=5;
	public static final int TOP=120;
	public static final int BOTTOM=60;
	public static final int EDGE_X2=EDGE*2;
	public static final int TINY_BTN_W=35;
	public static final int TINY_BTN_H=20;
	public static final int FUN_RAD_W=295;
	public static final int FUN_RAD_H=50;
	public static final int ACT_BTN_W=100;
	public static final int ACT_BTN_H=35;
	public static final int TOOLBAR_H=100;
	public static final int FIRM_LABEL_W=40;
	public static final int FIRM_COMBO_W=80;
	public static final int SUFFIX_COMBO_W=160;
	public static final int FOLDER_TF_W=160;
	public static final int FOLDER_BTN_W=80;
	public static final int PARALLEL_BTN_W=45;
	public static final int PARALLEL_LABEL_W=50;
	public static final int STATUS_FIELD_W=380;
	
	
	
	public static final Color FONT_COLOR=new Color(177, 106, 104);//表格字体颜色
	public static final Color BODY_COLOR=new Color(0xf5f6eb);//主体面板的背景色
	public static final Color TABLE_BACK=new Color(0xffffcc);
	public static final Color TABLE_SELECTED_BACK=new Color(0x0088dd);
	public static final Color TABLE_DID_BACK=new Color(26, 220, 121);
	public static final Color TABLE_FORE=new Color(0x333);
	public static final Font HEAD_FONT=new Font("微软雅黑Light", Font.PLAIN, 16);//表头字体
	public static final Icon CHECK=ComUtil.icon("checkBox.png");//checkbox图标
	public static final Icon CHECK_UP=ComUtil.icon("checkBox_up.png");//checkbox鼠标移上图标
	public static final Icon CHECKED=ComUtil.icon("checkBox_checked.png");//checkbox选中图标
	public static final CheckValue CHECK_ALL=new CheckValue("全部",false,Checker.ALL,CHECK,CHECK_UP,CHECKED);
	
}

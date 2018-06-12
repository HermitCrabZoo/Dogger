package com.zoo.dogger.entry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import com.zoo.base.Resource;
import com.zoo.dogger.cons.Cnst;
import com.zoo.dogger.panel.DecodePanel;
import com.zoo.dogger.panel.RecogPanel;
import com.zoo.swing.component.GraceFrame;
import com.zoo.swing.util.Style;
import com.zoo.swing.util.WindowMover;
import com.zoo.system.Syss;


public class Entry extends GraceFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7549262084288637357L;

	
	private int w=1000;
	private int h=700;
	private int tiny_btn_w=35;
	private int tiny_btn_h=20;
	private int fun_rad_w=295;
	private int fun_rad_h=50;
	
	private JPanel headPanel;//顶部面板
	private JPanel centerPanel;// 中间部分面板
	private DecodePanel de_panel;// 解码面板
	private RecogPanel re_panel;// 识曲面板
	
	private JButton min_btn;
	private JButton restore_btn;
	private JButton max_btn;
	private JButton exit_btn;
	
	private JRadioButton de_radio;// 解码单选框
	private JRadioButton re_radio;// 识曲单选框
	private ButtonGroup moduleRadioGroup;// 单选框组
	
	private boolean maximum=false;
	private int lastX=100;
	private int lastY=100;
	
	private void init() {
		initialize(100, 100, w, h);// 调用父类初始化方法
		contentPanel.setBorder(new LineBorder(Color.gray,1));
		setBackground(new Color(238, 238, 238, 0));// 透明背景，此句需在setContentPane方法之后
		renderPanel();
		renderCommom();
		resizing();
	}
	
	/**
	 * 渲染各个面板
	 */
	private void renderPanel() {
		//头部面板
		headPanel = new JPanel();
		headPanel.setBorder(null);
		headPanel.setLayout(null);
//		headPanel.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if (e.getClickCount()==2) {
//					max_btn.setVisible(!max_btn.isVisible());
//					restore_btn.setVisible(!restore_btn.isVisible());
//					resizing();
//				}else {
//					super.mouseClicked(e);
//				}
//			}
//		});
		contentPanel.add(headPanel);
		
		//中间面板
		centerPanel = new JPanel();
		centerPanel.setBackground(Cnst.BODY_COLOR);
		centerPanel.setBorder(null);
		centerPanel.setLayout(null);
		centerPanel.setFocusable(true);
		contentPanel.add(centerPanel);
		
		//解码面板
		de_panel=new DecodePanel(0, 0, centerPanel.getWidth(), centerPanel.getHeight());
		centerPanel.add(de_panel);
		
		//识曲面板
		re_panel=new RecogPanel(0, 0, centerPanel.getWidth(), centerPanel.getHeight());
		re_panel.setVisible(false);
		centerPanel.add(re_panel);
	}

	
	/**
	 * 渲染通用组件
	 */
	private void renderCommom() {
		
		// 设置退出按钮的属性
		exit_btn = new JButton(icon("exit.png"));
		exit_btn.setRolloverIcon(icon("exit_up.png"));
		exit_btn.setPressedIcon(icon("exit_press.png"));
		exit_btn.setContentAreaFilled(false);
		exit_btn.setToolTipText("退出");
		exit_btn.addActionListener(e -> System.exit(0));// 退出
		headPanel.add(exit_btn);
		
		// 最大化按钮
		max_btn = new JButton(icon("max.png"));
		max_btn.setRolloverIcon(icon("max_up.png"));
		max_btn.setPressedIcon(icon("max_press.png"));
		max_btn.setContentAreaFilled(false);
		max_btn.setToolTipText("最大化");
		max_btn.addActionListener(e -> {
			max_btn.setVisible(false);
			restore_btn.setVisible(true);
			resizing();
		});// 最大化
		headPanel.add(max_btn);
		
		//恢复窗体大小按钮
		restore_btn = new JButton(icon("restore.png"));
		restore_btn.setRolloverIcon(icon("restore_up.png"));
		restore_btn.setPressedIcon(icon("restore_press.png"));
		restore_btn.setContentAreaFilled(false);
		restore_btn.setToolTipText("恢复");
		restore_btn.setVisible(false);
		restore_btn.addActionListener(e -> {
			restore_btn.setVisible(false);
			max_btn.setVisible(true);
			resizing();
		});//恢复窗体尺寸
		headPanel.add(restore_btn);
		
		// 最小化按钮
		min_btn = new JButton(icon("min.png"));
		min_btn.setRolloverIcon(icon("min_up.png"));
		min_btn.setPressedIcon(icon("min_press.png"));
		min_btn.setContentAreaFilled(false);
		min_btn.setToolTipText("最小化");
		min_btn.addActionListener(e -> Entry.this.setExtendedState(ICONIFIED));// 最小化
		headPanel.add(min_btn);

		//解码面板radio
		de_radio = new JRadioButton(icon("radio_de.png"));
		de_radio.setRolloverIcon(icon("radio_de_up.png"));
		de_radio.setSelectedIcon(icon("radio_de_selected.png"));
		de_radio.setContentAreaFilled(false);
		de_radio.setSelected(true);
		de_radio.setMargin(new Insets(0,0,0,0));
		de_radio.setFocusPainted(false);
		de_radio.addItemListener(radioListener);
		headPanel.add(de_radio);

		//识曲面板radio
		re_radio = new JRadioButton(icon("radio_re.png"));
		re_radio.setRolloverIcon(icon("radio_re_up.png"));
		re_radio.setSelectedIcon(icon("radio_re_selected.png"));
		re_radio.setContentAreaFilled(false);
		re_radio.setMargin(new Insets(0,0,0,0));
		re_radio.setFocusPainted(false);
		re_radio.addItemListener(radioListener);
		headPanel.add(re_radio);

		//radio加到group中
		moduleRadioGroup = new ButtonGroup();
		moduleRadioGroup.add(de_radio);
		moduleRadioGroup.add(re_radio);
		
	}

	/**
	 * 调整各组件尺寸
	 */
	private void resizing() {
		if (maximum=!maximum) {
			setBounds(lastX,lastY, w, h);
			setLocationRelativeTo(null);
		}else {
			lastX=getX();
			lastY=getY();
			Dimension size=Syss.screenDimension().orElseGet(()->new Dimension(w, h));
			Insets insets=Syss.insets().orElseGet(()->new Insets(0, 0, 0, 0));
			setBounds(0, 0, size.width-insets.left-insets.right, size.height-insets.top-insets.bottom);
		}
		//调整各组件的坐标、尺寸
		headPanel.setBounds(1, 1, getWidth()-2, Cnst.TOP-1);
		centerPanel.setBounds(1, Cnst.TOP, getWidth()-2, getHeight()-Cnst.TOP-1);
		exit_btn.setBounds(headPanel.getWidth()-tiny_btn_w, 0, tiny_btn_w, tiny_btn_h);
		max_btn.setBounds(exit_btn.getX()-tiny_btn_w, 0, tiny_btn_w, tiny_btn_h);
		restore_btn.setBounds(exit_btn.getX()-tiny_btn_w, 0, tiny_btn_w, tiny_btn_h);
		min_btn.setBounds(max_btn.getX()-tiny_btn_w, 0, tiny_btn_w, tiny_btn_h);
		de_radio.setBounds(0, headPanel.getHeight()-fun_rad_h, fun_rad_w, fun_rad_h);
		re_radio.setBounds(de_radio.getX()+de_radio.getWidth(), headPanel.getHeight()-fun_rad_h, fun_rad_w, fun_rad_h);
		
		de_panel.resize(0, 0, centerPanel.getWidth(), centerPanel.getHeight());
		re_panel.resize(0, 0, centerPanel.getWidth(), centerPanel.getHeight());
		
	}
	
	
	/**
	 * 单选框的选择事件监听器对象
	 */
	private ItemListener radioListener = e -> {
		Object obj = e.getItem();
		if (obj == de_radio) {
			boolean isDecode = de_radio.isSelected();
			de_panel.setVisible(isDecode);
			re_panel.setVisible(!isDecode);
			contentPanel.repaint();
		}
	};
	
	
	
	
	private ImageIcon icon(String filename) {
		return Resource.getIcon("res", filename);
	}

	public Entry() {
		Style.defaultStyle();// 优雅的windows风格窗体
		init();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Entry entry = new Entry();
					entry.setVisible(true);
					WindowMover.dragable(entry);// 可拖动
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

}

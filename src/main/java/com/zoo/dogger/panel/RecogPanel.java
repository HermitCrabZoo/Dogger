package com.zoo.dogger.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.zoo.base.Arrs;
import com.zoo.base.Strs;
import com.zoo.base.Typer;
import com.zoo.cons.Funcs;
import com.zoo.dogger.cons.Cnst;
import com.zoo.dogger.cons.RegStatus;
import com.zoo.dogger.media.audio.APEV2Info;
import com.zoo.dogger.media.audio.AudioInfo;
import com.zoo.dogger.media.audio.ID3V1Info;
import com.zoo.dogger.media.audio.ID3V2Info;
import com.zoo.dogger.media.audio.TagInfo;
import com.zoo.dogger.util.ComUtil;
import com.zoo.mix.Beaner;
import com.zoo.mix.Filer;
import com.zoo.swing.component.Hints;
import com.zoo.swing.component.ProcessHint;

public class RecogPanel extends JPanel {
	private static final long serialVersionUID = 7633613021183896397L;

	
	private Object[] re_heads=new Object[] {"序号","源文件","歌名","艺术家","专辑","流派","年份"};//识曲模块的表头
	private Object[][] re_rows=null;
	private int re_table_source_index=1;
	private int re_table_status_index=7;
	
	
	private JPopupMenu tableMenu;
	private JButton re_btn;
	private JButton re_inputBtn;//输入文件/目录按钮
	private JTable re_table;
	private JScrollPane re_scrollPane;
	
	private JFileChooser musicChooser; 
	private FileNameExtensionFilter musicFilter;
	
	
	public RecogPanel(int x,int y,int w,int h){
		setBounds(x,y,w,h);
		setBackground(Cnst.BODY_COLOR);
		setBorder(null);
		setLayout(null);
		setFocusable(true);
		
		render();
	}
	
	
	
	/**
	 * 渲染识曲模块面板上的组件
	 */
	private void render() {
		//过滤音频文件
		musicFilter=new FileNameExtensionFilter("Audio(*.mp3,*.ape,*.flac,*.wma,*.aac)", "mp3", "ape", "flac", "wma", "aac");
		musicChooser=new JFileChooser();
		musicChooser.setAcceptAllFileFilterUsed(false);
		musicChooser.addChoosableFileFilter(musicFilter);
		musicChooser.setDialogTitle("选择输入文件或目录");
		musicChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// 可选文件或目录
		
		
		//识曲按钮
		re_btn = new JButton(ComUtil.icon("convert.png"));
		re_btn.setRolloverIcon(ComUtil.icon("convert_up.png"));
		re_btn.setPressedIcon(ComUtil.icon("convert_press.png"));
		re_btn.addActionListener(saveListener);
		re_btn.setText("保存");
		re_btn.setBackground(new Color(237, 237, 237));
		re_btn.setEnabled(false);
		add(re_btn);
		
		
		//选择文件或目录按钮
		re_inputBtn = new JButton(ComUtil.icon("convert.png"));
		re_inputBtn.setRolloverIcon(ComUtil.icon("convert_up.png"));
		re_inputBtn.setPressedIcon(ComUtil.icon("convert_press.png"));
		re_inputBtn.addActionListener(e->{
			new Thread(sourceReader).start();//启动一个线程进行识别防止主线程阻塞
		});
		re_inputBtn.setText("文件或目录");
		add(re_inputBtn);
		
		
		
		//表格、滚动面板
		re_scrollPane = new JScrollPane();
		re_scrollPane.setBorder(new EmptyBorder(0,0,0,0));
		re_scrollPane.setBackground(Cnst.BODY_COLOR);
		add(re_scrollPane);
		
		re_table = new JTable() {
			private static final long serialVersionUID = -6864691515344793151L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return column==1;
			}
		};
		re_table.setModel(new DefaultTableModel(re_heads,0));
		re_table.setBorder(new EmptyBorder(0,0,0,0));
//		re_table.setShowGrid(false);
//		re_table.setMinimumSize(new Dimension(re_scrollPane.getWidth()-2, re_scrollPane.getHeight()-28));//最小尺寸
		re_table.setRowHeight(28);
		re_table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		re_table.setDefaultRenderer(Object.class, tcr);
		
		JTableHeader tableHeader =re_table.getTableHeader();
		tableHeader.setReorderingAllowed(false);//表头排序不可改变
		tableHeader.setForeground(Cnst.FONT_COLOR);
		tableHeader.setFont(Cnst.HEAD_FONT);
		re_table.addMouseListener(tableRightButtonListener);
		re_scrollPane.setViewportView(re_table);
		
	}
	
	
	/**
	 * 保存按钮事件
	 */
	private ActionListener saveListener=e->{
		
		//按钮不可用
		enabled(false);
		
		//由于表格modal中改过的值并未更新到re_rows中对应的位置,所以在后面的重新渲染动作过后,需要将记录下的未完成重命名的文件的目标值重新设置到table里.
		Map<Integer,Object> uncompleteds=new HashMap<Integer,Object>();
		
		int len=re_rows.length;
		for (int i = 0; i < len; i++) {
			Object[] row=re_rows[i];
			Object status=row[re_table_status_index];
			Object source=row[re_table_source_index];
			Object target=re_table.getValueAt(i, re_table_source_index);
			//状态:未保存,修改后的存在
			if (RegStatus.UNSAVE==status && Typer.notNull(source,target)) {
				try {
					Path spath=Paths.get(source.toString());
					Path tpath=Paths.get(target.toString());
					//同一个目录下直接改名,否则剪切
					if (spath.getParent().equals(tpath.getParent())) {
						spath.toFile().renameTo(tpath.toFile());
					}else {
						Files.move(spath, tpath,StandardCopyOption.REPLACE_EXISTING);
					}
					row[re_table_source_index]=target;
					row[re_table_status_index]=RegStatus.SAVED;
				} catch (Exception e1) {
					e1.printStackTrace();
					uncompleteds.put(i, target);//未完成的记录下来
				}
			}else if(RegStatus.ILLEGAL==status) {
				uncompleteds.put(i, target);//非法的记录下来,以便在表格重新渲染后能让这些非法的行状态恢复到渲染前的非法状态
			}
		}
		
		//释放按钮
		enabled(true);
		renderTable();//重新渲染下
		
		//在表格重新渲染过后需要处理未完成的
		for(Integer i:uncompleteds.keySet()) {
			re_table.setValueAt(uncompleteds.get(i), i, re_table_source_index);
		}
		
	};
	
	
	/**
	 * 设置几个相关按钮组件是否可用
	 * @param is
	 */
	private void enabled(boolean is) {
		re_btn.setEnabled(is);
		re_inputBtn.setEnabled(is);
	}
	
	
	/**
	 * 渲染识曲表格
	 */
	private void renderTable() {
		//渲染表格
		DefaultTableModel model = new DefaultTableModel(re_rows, re_heads);
		re_table.setModel(model);
		model.addTableModelListener(event->{
			if(event.getType() == TableModelEvent.UPDATE) {
				
				int r=event.getFirstRow();
				int c=event.getColumn();
				Object oldVal=re_rows[r][c];
				Object newVal=Strs.toStr(re_table.getValueAt(r, c)).trim();
				
				//将去除前后的非法字符后的字符串设置回TableModel的数据集里,并且不触发TableModelEvent
				setTableModelValWithOutEvents((TableModel) event.getSource(), newVal, r, c);
		        
				if (Objects.equals(oldVal, newVal)) {
					re_rows[r][re_table_status_index]=RegStatus.ORIGIN;
				}else {
					//文件名所在的列需要判断值是否合法
					if (c==re_table_source_index) {
						Path spath=Paths.get(Strs.toStr(newVal));
						if (Filer.isExists(spath) || !Filer.isExists(spath.getParent())) {
							re_rows[r][re_table_status_index]=RegStatus.ILLEGAL;
						}else {
							re_rows[r][re_table_status_index]=RegStatus.UNSAVE;
						}
					}else {
						re_rows[r][re_table_status_index]=RegStatus.UNSAVE;
					}
				}
			}
			doRegBtn();//检查保存按钮是否可用
		});
		ComUtil.adaptColumn(re_table);//调整列宽
		doRegBtn();//检查保存按钮是否可用
	}
	
	
	@SuppressWarnings("unchecked")
	private void setTableModelValWithOutEvents(TableModel model,Object val,int r,int c) {
		Vector<Vector<Object>> dataVector=(Vector<Vector<Object>>) Beaner.value(model, "dataVector");
		Vector<Object> rowVector = dataVector.elementAt(r);
        rowVector.setElementAt(val, c);
	}
	
	/**
	 * 通过当前各种状态判断并设置保存按钮是否可用
	 */
	private void doRegBtn() {
		int len=Arrs.len(re_rows);
		for (int i = 0; i < len; i++) {
			if (RegStatus.UNSAVE==re_rows[i][re_table_status_index]) {
				re_btn.setEnabled(true);
				return;//有一行的enabled==true就可以保存了
			}
		}
		re_btn.setEnabled(false);
	}
	
	
	/**
	 * 读取、识别文件的Runnable实例
	 */
	private Runnable sourceReader = () -> {
		if (musicChooser.showDialog(RecogPanel.this, "选择") == JFileChooser.APPROVE_OPTION) {
			Path path = musicChooser.getSelectedFile().toPath();
			if (!Filer.isRoot(path)) {
				ProcessHint processHint=Hints.showProcess(re_scrollPane, "加载中");
				AtomicInteger ai=new AtomicInteger(0);
				re_rows = Filer.paths(path, p->Funcs.onlyFile.test(p)&&musicFilter.accept(p.toFile())).stream().map(p -> {
					//序号
					ai.set(ai.get()+1);
					//读取音频信息
					String pstr=p.toString();
					AudioInfo info=new AudioInfo(pstr);
					ID3V2Info id3v2=info.getId3v2Info();
					ID3V1Info id3v1=info.getId3v1Info();
					APEV2Info apev2=info.getApev2Info();
					//首选ID3V2信息
					TagInfo tagInfo=id3v2.isValid()?id3v2:(id3v1.isValid()?id3v1:apev2);
					Object[] row = new Object[] {
							ai.get(),
							pstr,
							tagInfo.getTitle(),
							tagInfo.getArtist(),
							tagInfo.getAlbum(),
							tagInfo.getGenre(),
							tagInfo.getYear(),
							RegStatus.ORIGIN
							};
					processHint.putText(pstr);
					return row;
				}).toArray(Object[][]::new);
				renderTable();//渲染表格
				processHint.close();
			}
		}
	};
	
	
	/**
	 * 自定义单元格渲染器
	 */
	private DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 5157864838495081192L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Object status=re_rows[row][re_table_status_index];
			JTextField textField=new JTextField(Strs.toStr(value));
			textField.setBorder(null);
			textField.setEditable(false);
			textField.addFocusListener(pathFocusListener);
			textField.addKeyListener(pathKeyListener);
			if (isSelected) {
				textField.setBackground(Cnst.TABLE_SELECTED_BACK);
				textField.setForeground(Color.WHITE);
			} else if (RegStatus.ILLEGAL == status) {
				textField.setBackground(Color.RED);
				textField.setForeground(Cnst.TABLE_FORE);
			} else if (RegStatus.UNSAVE == status) {
				textField.setBackground(Color.GREEN);
				textField.setForeground(Cnst.TABLE_FORE);
			}else if (RegStatus.SAVED == status) {
				textField.setBackground(Cnst.TABLE_BACK);
				textField.setForeground(Cnst.TABLE_FORE);
			} else {
				textField.setBackground(Cnst.TABLE_BACK);
				textField.setForeground(Cnst.TABLE_FORE);
			}
			
			return textField;
		}
	};
	
	
	
	
	/**
	 * 表格右键菜单监听器
	 */
	private MouseListener tableRightButtonListener=new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			JTable table = (JTable) e.getSource();
			if (e.getButton() == MouseEvent.BUTTON3) {
				// 通过点击位置找到点击为表格中的行
				int rowI = table.rowAtPoint(e.getPoint());
				if (rowI == -1) {
					return;
				}
				// 将表格所选项设为当前右键点击的行
				table.setRowSelectionInterval(rowI, rowI);
				// 弹出菜单
				tableMenu.show(table, e.getX(), e.getY());
			}
		}
	};
	
	
	private FocusListener pathFocusListener=new FocusAdapter() {
		public void focusLost(FocusEvent e) {
			System.out.println("focusLost:"+e);
		};
	};
	
	private KeyListener pathKeyListener=new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			System.out.println("keyTyped:"+e);
			JTextField textField=(JTextField) e.getSource();
			String text=textField.getText();
			char key=e.getKeyChar();
			if(text.length()==0 && key==' ')
			{
                e.setKeyChar('\0');
			}
		}
		public void keyPressed(KeyEvent e) {
			System.out.println("keyPressed");
		};
		
		public void keyReleased(KeyEvent e) {
			System.out.println("keyReleased");
		};
	};
	
	
	/**
	 * 重置大小
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void resize(int x,int y,int w,int h) {
		setBounds(x,y,w,h);
		re_btn.setBounds(getWidth()-Cnst.ACT_BTN_W-20, getHeight()-Cnst.ACT_BTN_H-10, Cnst.ACT_BTN_W, Cnst.ACT_BTN_H);
		re_inputBtn.setBounds(Cnst.EDGE_X2, Cnst.EDGE_X2, Cnst.ACT_BTN_W, Cnst.ACT_BTN_H);
		re_scrollPane.setBounds(0, Cnst.TOOLBAR_H, getWidth(), getHeight()-Cnst.TOOLBAR_H-Cnst.BOTTOM);
		re_table.setMinimumSize(new Dimension(re_scrollPane.getWidth()-2, re_scrollPane.getHeight()-28));
	}
	
}

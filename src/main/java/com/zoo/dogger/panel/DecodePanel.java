package com.zoo.dogger.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.zoo.base.Arrs;
import com.zoo.base.Strs;
import com.zoo.base.Typer;
import com.zoo.cons.Funcs;
import com.zoo.dogger.cons.Cnst;
import com.zoo.dogger.cons.Decoders;
import com.zoo.dogger.cons.Status;
import com.zoo.dogger.media.decoder.Decoder;
import com.zoo.dogger.util.ComUtil;
import com.zoo.mix.Dater;
import com.zoo.mix.Filer;
import com.zoo.mix.Pather;
import com.zoo.swing.component.CheckValue;
import com.zoo.swing.component.Checker;
import com.zoo.swing.component.Hints;
import com.zoo.swing.component.MultipleComboBox;
import com.zoo.swing.component.ProcessHint;

public class DecodePanel extends JPanel {
	private static final long serialVersionUID = -7102205347292432825L;
	
	private Object[] de_heads=new Object[] {"序号","源文件","日期","大小","解码到","状态"};//解码模块的表头
	private int de_table_check_index=0;
	private int de_table_source_index=1;
	private int de_table_target_index=4;
	private int de_table_status_index=5;
	private int de_table_suffix_index=6;
	private Object[][] de_rows=null;
	
	
	private JPopupMenu tableMenu;
	
	private JTable de_table;
	private JScrollPane de_scrollPane;
	private JLabel de_firmLabel;// 来源标签
	private JLabel de_suffixLabel;// 后缀过滤标签
	private JLabel de_parallelLabel;//多线程标签
	private JTextField de_statusField;//状态栏
	private JComboBox<Decoders> de_firmCombo;//源编码选择下拉框
	private MultipleComboBox<CheckValue> de_suffixCombo;//后缀过滤下拉框
	private JButton de_inputBtn;//输入文件/目录按钮
	private JButton de_outputBtn;//输出目录按钮
	private JButton de_btn;//开始解码按钮
	private JTextField de_outputFolder;//输出目录
	private JToggleButton de_parallelToggle;//多线程按钮
	
	
	private Path output;//选择的输出目录
	private boolean isComplete=true;
	private boolean isNew=true;
	private JFileChooser chooser; 
	

	
	public DecodePanel(int x,int y,int w,int h) {
		setBounds(x,y,w,h);
		setBackground(Cnst.BODY_COLOR);
		setBorder(null);
		setLayout(null);
		setFocusable(true);
		
		render();
		
	}
	
	
	/**
	 * 渲染面板的组件
	 */
	private void render() {
		chooser=new JFileChooser();
		
		tableMenu=ComUtil.getMenu();
		
		//状态栏标签
		de_statusField=new JTextField();
		de_statusField.setBorder(null);
		de_statusField.setEditable(false);
		de_statusField.setOpaque(false);
		add(de_statusField);
		
		//开始解码按钮
		de_btn = new JButton(ComUtil.icon("convert.png"));
		de_btn.setRolloverIcon(ComUtil.icon("convert_up.png"));
		de_btn.setPressedIcon(ComUtil.icon("convert_press.png"));
		de_btn.addActionListener(e->{
			new Thread(decodeRunnable).start();//开启一个线程去解码
		});
		de_btn.setText("开始解码");
		de_btn.setEnabled(false);
		add(de_btn);
		
		//来源标签
		de_firmLabel=new JLabel("类    别:");
		add(de_firmLabel);
		
		//来源下拉框
		de_firmCombo=new JComboBox<Decoders>();
		de_firmCombo.setBorder(null);
		de_firmCombo.addItem(Decoders.NE);
		de_firmCombo.addItem(Decoders.KG);
		de_firmCombo.setSelectedItem(Decoders.NE);
		de_firmCombo.setFocusable(false);
		add(de_firmCombo);
		
		//选择文件或目录按钮
		de_inputBtn = new JButton(ComUtil.icon("convert.png"));
		de_inputBtn.setRolloverIcon(ComUtil.icon("convert_up.png"));
		de_inputBtn.setPressedIcon(ComUtil.icon("convert_press.png"));
		de_inputBtn.addActionListener(e->{
			new Thread(sourceRunnable).start();//开启一个线程读取文件
		});
		de_inputBtn.setText("文件或目录");
		add(de_inputBtn);
		
		//过滤标签
		de_suffixLabel=new JLabel("过滤:");
		add(de_suffixLabel);
		
		
		//文件后缀过滤下拉框
		de_suffixCombo=new MultipleComboBox<CheckValue>();
		de_suffixCombo.setBorder(null);
		de_suffixCombo.setAfterCheckedActionListener(afterCheckedListener);//在下拉框里的CheckBox选中后触发
		add(de_suffixCombo);
		
		
		//输出目录选择组件
		de_outputBtn = new JButton(ComUtil.icon("browse.png"));
		de_outputBtn.setRolloverIcon(ComUtil.icon("browse_up.png"));
		de_outputBtn.setPressedIcon(ComUtil.icon("browse_press.png"));
		de_outputBtn.setText("输出目录");
		de_outputBtn.addActionListener(browseListener);
		add(de_outputBtn);
		
		de_outputFolder = new JTextField();
		de_outputFolder.setEditable(false);
		de_outputFolder.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		add(de_outputFolder);
		
		//来源类别选择下拉框
		de_parallelLabel=new JLabel("多线程:");
		add(de_parallelLabel);
		
		//多线程开关
		de_parallelToggle=new JToggleButton(ComUtil.icon("toggle_off.png"));
		de_parallelToggle.setRolloverIcon(ComUtil.icon("toggle_off_up.png"));
		de_parallelToggle.setSelectedIcon(ComUtil.icon("toggle_on.png"));
		de_parallelToggle.setRolloverSelectedIcon(ComUtil.icon("toggle_on_up.png"));
		de_parallelToggle.setContentAreaFilled(false);
		de_parallelToggle.setFocusPainted(false);
		de_parallelToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(de_parallelToggle);
		
		
		
		//表格、滚动面板
		de_scrollPane = new JScrollPane();
		de_scrollPane.setBorder(new EmptyBorder(0,0,0,0));
		de_scrollPane.setBackground(Cnst.BODY_COLOR);
		add(de_scrollPane);
		
		de_table = new JTable() {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) {
				return column==4;
			}
		};
		de_table.setModel(new DefaultTableModel(de_heads,0));
		de_table.setBorder(new EmptyBorder(0,0,0,0));
		de_table.setShowGrid(false);
		de_table.setRowHeight(28);
		de_table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		de_table.setDefaultRenderer(Object.class, tcr);
		
		JTableHeader tableHeader =de_table.getTableHeader();
		tableHeader.setReorderingAllowed(false);//表头排序不可改变
		tableHeader.setForeground(Cnst.FONT_COLOR);
		tableHeader.setFont(Cnst.HEAD_FONT);
		
		de_table.addMouseListener(tableRightButtonListener);
		de_scrollPane.setViewportView(de_table);
	}
	
	
	/**
	 * 解码线程
	 */
	private Runnable decodeRunnable=()->{
		
		if(!isNew && !Hints.showConfirm(DecodePanel.this, "该任务已经执行过,确定再次执行吗?")) {
			return;
		}
		//在解码完成前禁止做其他解码操作
		enabled(false);
		
		decoding();//解码操作
		
		isNew=false;//非新任务
		renderTable(de_table, de_heads, de_rows);
		//在解码后释放按钮状态
		enabled(true);
		
	};
	
	
	/**
	 * 渲染表格数据
	 * @param table
	 * @param heads
	 * @param rows
	 */
	private void renderTable(JTable table, Object[] heads, Object[][] rows) {
		DefaultTableModel model = new DefaultTableModel(rows, heads);
		table.setModel(model);
		ComUtil.adaptColumn(table);//自适应列宽
		doDecodeBtn();//检查是否可做解码操作
		statistics();//统计各种状态的数量
	}
	
	
	/**
	 * 通过当前各种状态判断并设置解码按钮是否可用
	 */
	private void doDecodeBtn() {
		if (de_table.getRowCount()>0 && isComplete && Filer.isDir(output)) {
			for (int i = 0,len=Arrs.len(de_rows); i < len; i++) {
				if (!(Status.UNDO==de_rows[i][de_table_status_index])) {
					de_btn.setEnabled(true);
					return;//有一行的enabled==true就可以进行解码了
				}
			}
		}
		de_btn.setEnabled(false);
	}
	
	/**
	 * 设置几个相关按钮组件是否可用
	 * @param is
	 */
	private void enabled(boolean is) {
		isComplete=is;
		de_btn.setEnabled(is);
		de_inputBtn.setEnabled(is);
		de_outputBtn.setEnabled(is);
		de_suffixCombo.setEnabled(is);
		de_firmCombo.setEnabled(is);
	}
	
	
	
	/**
	 * 执行解码动作
	 */
	private void decoding() {
		
		ProcessHint processHint=Hints.showProcess(de_scrollPane, "解码中", 0);
		
		//获取指定的解码器
		Decoder decoder=((Decoders) de_firmCombo.getSelectedItem()).getDecoder();
		
		String outputFolder=output.toString();
		
		Stream<Object[]> stream=Stream.of(de_rows);
		if (de_parallelToggle.isSelected()) {
			stream=stream.parallel();//转为并行流
		}
		
		stream.forEach(row->{
			//获取状态源文件全路径,解压后的文件名
			Object status = row[de_table_status_index];
			Object sval = row[de_table_source_index];
			Object tval = row[de_table_target_index];
			
			
			if (!(Status.UNDO==status) && Typer.notNull(sval, tval)) {
				
				processHint.putText(Strs.toStr(sval)+" -> "+Strs.toStr(tval));
				
				Path source = Paths.get(sval.toString());
				Path target = Paths.get(outputFolder, tval.toString());
				try (FileChannel inChannel = FileChannel.open(source, StandardOpenOption.READ);
						FileChannel outChannel = FileChannel.open(target, StandardOpenOption.WRITE, StandardOpenOption.CREATE);) {
					
					long skip = decoder.skip();
					inChannel.position(skip);
					
					ByteBuffer buffer=ByteBuffer.allocate(8192);
					while (inChannel.read(buffer) != -1) {
						buffer.flip();//读模式
						
						//将对缓冲区读取->解码->写入
						byte[] raws = new byte[buffer.limit()];
						buffer.get(raws);
						byte[] rs = decoder.decode(raws);
						
						buffer.flip();//写模式
						buffer.put(rs);
						
						buffer.flip();//读模式
						outChannel.write(buffer);
						
						buffer.clear();
					}
					
					row[de_table_status_index]=Status.DID;
				} catch (Exception ex) {
					ex.printStackTrace();
					row[de_table_status_index]=Status.FAIL;
				}
			}
		});
		processHint.close();
	}
	
	
	
	/**
	 * 统计表格数据
	 */
	private void statistics() {
		int total=0,wait=0,undo=0,did=0,fail=0,unknow=0;
		if (de_rows!=null) {
			for(Object[] row:de_rows) {
				Object state=row[de_table_status_index];
				total++;
				if (Status.WAIT==state) {
					wait++;
				}else if (Status.UNDO==state) {
					undo++;
				}else if (Status.DID==state) {
					did++;
				}else if (Status.FAIL==state) {
					fail++;
				}else {
					unknow++;
				}
			}
		}
		de_statusField.setText(String.format("总数:%s  待解码:%s  不解码:%s  已完成:%s  失败:%s", total,wait,undo,did,fail)+(unknow>0?"  未知:"+unknow:""));
		de_statusField.setToolTipText(de_statusField.getText());
	}
	
	
	
	
	/**
	 * 读取文件的线程
	 */
	private Runnable sourceRunnable = () -> {
		chooser.setDialogTitle("选择输入文件或目录");
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// 可选文件或目录
		if (chooser.showDialog(DecodePanel.this, "选择") == JFileChooser.APPROVE_OPTION) {
			Path path = chooser.getSelectedFile().toPath();
			if (!Filer.isRoot(path)) {
				
				ProcessHint processHint=Hints.showProcess(de_scrollPane, "加载中");
				
				Decoder decoder=((Decoders) de_firmCombo.getSelectedItem()).getDecoder();
				
				//获取解码器默认的后缀和下拉框中选中的后缀
				String[] defaultSuffixs=decoder.suffixs();
				String[] checkeds=de_suffixCombo.getCheckeds().stream().map(c->Strs.removeStart(c.getValue(), "*.")).toArray(String[]::new);
				AtomicInteger ai=new AtomicInteger(0);
				de_rows = Filer.paths(path, Funcs.onlyFile).stream().map(p -> {
					//序号
					ai.set(ai.get()+1);
					
					//读取文件信息
					String pstr=p.toString();
					String suffix=Pather.suffix(pstr);
					Status status=Arrs.contains(suffix, defaultSuffixs)||Arrs.contains(suffix, checkeds)?Status.WAIT:Status.UNDO;
					JCheckBox checkBox=new JCheckBox(ai.get()+"", Cnst.CHECK);
					checkBox.setRolloverIcon(Cnst.CHECK_UP);
					checkBox.setSelectedIcon(Cnst.CHECKED);
					checkBox.setSelected(Status.UNDO!=status);
					
					Object[] row = new Object[] {
							checkBox,
							pstr, 
							Dater.format(Filer.modifiedTime(p), 
							Dater.ALL_FORMAT),Filer.grace(Filer.size(p)),
							Pather.shortName(pstr) + ".mp3", 
							status,
							suffix
						};
					
					processHint.putText(pstr);
					return row;
				}).toArray(Object[][]::new);
				renderTable(de_table, de_heads, de_rows);//渲染表格
				collectSuffix(de_rows);//生成下拉框的后缀
				
				processHint.close();
			}
			isNew=true;
		}
	};
	
	
	

	
	/**
	 * 生成下拉框的后缀
	 * @param rows
	 */
	private void collectSuffix(Object[][] rows) {
		
		//已选中或是功能性的CheckBox保留
		List<CheckValue> exists=new ArrayList<>();
		List<CheckValue> existsFun=new ArrayList<>();
		for (int i = 0,len=de_suffixCombo.getItemCount(); i < len; i++) {
			CheckValue checkValue=de_suffixCombo.getItemAt(i);
			if (checkValue.all() || checkValue.turn()) {
				existsFun.add(checkValue);
			}else if (checkValue.getChecked()) {
				exists.add(checkValue);
			}
		}
		if (!existsFun.contains(Cnst.CHECK_ALL)) {
			existsFun.add(0, Cnst.CHECK_ALL);
		}
		//获取编码器,用以默认选中
		Decoder decoder=((Decoders) de_firmCombo.getSelectedItem()).getDecoder();
		String[] defaultSuffixs=decoder.suffixs();
		//获取文件列表里的后缀
		CheckValue[] suffix_checks=Stream.of(rows)
				.map(r->Strs.toStr(r[de_table_suffix_index]))
				.filter(Strs::notBlank)
				.distinct()
				.sorted()
				.map(s->{
					CheckValue cv=new CheckValue("*."+s,false,Checker.SELF,Cnst.CHECK,Cnst.CHECK_UP,Cnst.CHECKED);
					//是否设为选中按,已存在->编码器默认,的顺序来设置
					int idx=exists.indexOf(cv);
					if (idx!=-1) {
						CheckValue exis=exists.get(idx);
						cv.setChecked(exis.getChecked());
					}else {
						cv.setChecked(Arrs.contains(s, defaultSuffixs));
					}
					return cv;
				})
				.toArray(CheckValue[]::new);
		//原先存在功能性选项,则添加到数组前面
		if (suffix_checks.length>0 && existsFun.size()>0) {
			suffix_checks=Arrs.concat(existsFun.toArray(new CheckValue[existsFun.size()]),suffix_checks);
		}
		//将后缀列表存放的comboBox中
		ComboBoxModel<CheckValue> comboBoModel=new DefaultComboBoxModel<CheckValue>(suffix_checks);
		de_suffixCombo.setModel(comboBoModel);
	}
	
	
	
	/**
	 * 过滤后缀下拉框选中事件
	 */
	@SuppressWarnings("unchecked")
	private ActionListener afterCheckedListener = e -> {
		MultipleComboBox<CheckValue> mcb = (MultipleComboBox<CheckValue>) e.getSource();
		String[] suffixs = mcb.getCheckeds().stream()
				.map(c -> Strs.removeStart(c.getValue(), "*."))
				.toArray(String[]::new);
		// 将不包含在后缀内的行设置为不可用
		if (Objects.nonNull(de_rows)) {
			for (int i = 0, len = de_rows.length; i < len; i++) {
				Object[] row = de_rows[i];
				String suffix = Strs.toStr(row[de_table_suffix_index]);
				boolean isContains=Arrs.contains(suffix, suffixs);
				row[de_table_status_index] = isContains?Status.WAIT:Status.UNDO;
				JCheckBox checkBox=(JCheckBox) row[de_table_check_index];
				checkBox.setSelected(isContains);
			}
		}
		// 重新渲染
		renderTable(de_table, de_heads, de_rows);

	};
	
	
	/**
	 * 解码输出目录选择按钮点击事件监听器
	 */
	private ActionListener browseListener = e -> {
		chooser.setDialogTitle("选择输出目录");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只选目录
		if (chooser.showDialog(DecodePanel.this, "选择") == JFileChooser.APPROVE_OPTION) {
			output = chooser.getSelectedFile().toPath();
			de_outputFolder.setText(output.toString());
			de_outputFolder.setToolTipText(de_outputFolder.getText());
			doDecodeBtn();// 设置下解码按钮是否可用
		}
	};
	
	
	/**
	 * 自定义单元格渲染器
	 */
	private DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 5157864838495081192L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			
			Component comp=null;
			Object status=getStatusByRow(row);
			
			if (column==0) {
				JCheckBox checkBox=(JCheckBox) value;
				comp=checkBox;
			}else {
				JTextField textField=new JTextField(Strs.toStr(value));
				textField.setBorder(null);
				textField.setEditable(false);
				comp=textField;
			}
			JCheckBox checkBox = (JCheckBox) de_table.getValueAt(row, 0);
			if (isSelected) {
				comp.setBackground(Cnst.TABLE_SELECTED_BACK);
				comp.setForeground(Color.WHITE);
			} else if (Status.DID == status) {
				comp.setBackground(Cnst.TABLE_DID_BACK);
				comp.setForeground(Cnst.TABLE_FORE);
			} else if (Status.FAIL == status) {
				comp.setBackground(Color.RED);
				comp.setForeground(Cnst.TABLE_FORE);
			} else if (checkBox.isSelected()) {
				comp.setBackground(Cnst.TABLE_BACK);
				comp.setForeground(Cnst.TABLE_FORE);
			} else if (Status.UNDO == status) {
				comp.setBackground(new Color(240, 240, 240));
				comp.setForeground(Cnst.TABLE_FORE);
			} else {
				comp.setBackground(Cnst.TABLE_BACK);
				comp.setForeground(Cnst.TABLE_FORE);
			}

			return comp;
		}
	};
	
	
	/**
	 * 判断某一行是否被标记为不可用
	 * @param rowIndex
	 * @return
	 */
	private Object getStatusByRow(int rowIndex) {
		if (Objects.nonNull(de_rows)) {
			Object[] row=de_rows[rowIndex];
			Object status=row[de_table_status_index];
			return status;
		}
		return null;
	}
	
	
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
			}else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount()==1 && table== de_table){//解码面板表格单击事件
				int columnIndex = de_table.columnAtPoint(e.getPoint()); //获取点击的列
				if(columnIndex == 0) {//第0列为CheckBox所在的列
					int rowIndex = de_table.rowAtPoint(e.getPoint()); //获取点击的行
					JCheckBox checkBox=(JCheckBox) de_table.getValueAt(rowIndex, columnIndex);
					checkBox.setSelected(!checkBox.isSelected());
					Object status=de_rows[rowIndex][de_table_status_index];
					
					if (Status.UNDO==status && checkBox.isSelected()) {
						de_rows[rowIndex][de_table_status_index]=Status.WAIT;
					}else if(Status.WAIT==status && !checkBox.isSelected()) {
						de_rows[rowIndex][de_table_status_index]=Status.UNDO;
					}
					de_table.setValueAt(checkBox, rowIndex, columnIndex);
					statistics();//统计各种状态的数量
				}
			}
		}
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
		de_statusField.setBounds(20, getHeight()-Cnst.ACT_BTN_H-10, Cnst.STATUS_FIELD_W, Cnst.ACT_BTN_H);
		de_btn.setBounds(getWidth()-Cnst.ACT_BTN_W-20, getHeight()-Cnst.ACT_BTN_H-10, Cnst.ACT_BTN_W, Cnst.ACT_BTN_H);
		de_firmLabel.setBounds(Cnst.EDGE_X2, Cnst.EDGE_X2, Cnst.PARALLEL_LABEL_W, Cnst.ACT_BTN_H);
		de_firmCombo.setBounds(de_firmLabel.getX()+de_firmLabel.getWidth(), Cnst.EDGE_X2, Cnst.FIRM_COMBO_W, Cnst.ACT_BTN_H);
		de_inputBtn.setBounds(de_firmCombo.getX()+Cnst.FIRM_COMBO_W+Cnst.EDGE, Cnst.EDGE_X2, Cnst.ACT_BTN_W, Cnst.ACT_BTN_H);
		de_suffixLabel.setBounds(Cnst.EDGE_X2+de_inputBtn.getX()+de_inputBtn.getWidth(), Cnst.EDGE_X2, Cnst.FIRM_LABEL_W, Cnst.ACT_BTN_H);
		de_suffixCombo.setBounds(de_suffixLabel.getX()+de_suffixLabel.getWidth(), Cnst.EDGE_X2, Cnst.SUFFIX_COMBO_W, Cnst.ACT_BTN_H);
		de_outputBtn.setBounds(de_suffixCombo.getX()+de_suffixCombo.getWidth()+Cnst.EDGE, Cnst.EDGE_X2, Cnst.FOLDER_BTN_W, Cnst.ACT_BTN_H);
		de_outputFolder.setBounds(de_outputBtn.getX()+de_outputBtn.getWidth(), Cnst.EDGE_X2, Cnst.FOLDER_TF_W, Cnst.ACT_BTN_H);
		de_parallelLabel.setBounds(Cnst.EDGE_X2, de_outputFolder.getY()+de_outputFolder.getHeight()+Cnst.EDGE_X2, Cnst.PARALLEL_LABEL_W, Cnst.ACT_BTN_H);
		de_parallelToggle.setBounds(de_parallelLabel.getX()+de_parallelLabel.getWidth(), de_parallelLabel.getY(), Cnst.PARALLEL_BTN_W, Cnst.ACT_BTN_H);
		de_scrollPane.setBounds(0, Cnst.TOOLBAR_H, getWidth(), getHeight()-Cnst.TOOLBAR_H-Cnst.BOTTOM);
		de_table.setMinimumSize(new Dimension(de_scrollPane.getWidth()-2, de_scrollPane.getHeight()-28));
	}
}

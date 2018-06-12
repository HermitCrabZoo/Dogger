package com.zoo.dogger.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.zoo.base.Arrs;
import com.zoo.base.Resource;
import com.zoo.base.Strs;
import com.zoo.mix.Pather;
import com.zoo.system.Syss;

public class ComUtil {
	
	/**
	 * 加载jar/classpath下的图标资源
	 * @param filename
	 * @return
	 */
	public static ImageIcon icon(String filename) {
		return Resource.getIcon("res", filename);
	}
	

	/**
	 * 表格列宽度自适应
	 * @param table
	 */
	public static void adaptColumn(JTable table) {
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTableHeader header = table.getTableHeader();
		int tableWidth=table.getWidth();
		int rowCount = table.getRowCount();
		List<TableColumn> cols=new ArrayList<>();
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		//获取每列最大宽度的值做为该列的宽度
		int col=0,totalWidth=0;
		while (columns.hasMoreElements()) {
			TableColumn column = columns.nextElement();
			int width = (int) header.getDefaultRenderer().getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) table.getCellRenderer(row, col).getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			totalWidth+=width;
			header.setResizingColumn(column); // 此行很重要
			column.setWidth(Math.min(tableWidth,width) + table.getIntercellSpacing().width+5);
			
			col++;
			cols.add(column);
		}
		//当各列宽度和小于表格宽度时,将各列平均拉宽到各列宽的和为表格宽
		int dif=table.getMinimumSize().width-30-totalWidth;
		if (dif>0) {
			int[] quotas=Arrs.avgs(dif, col-1);//第一列"序号"的宽度无需调节
			TableColumn column=null;
			for (int i=1;i<col;i++) {
				column=cols.get(i);
				header.setResizingColumn(column);
				column.setWidth(column.getWidth()+quotas[i-1]);
			}
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			table.repaint();
		}
		
	}
	
	
	private static JPopupMenu tableMenu;
	
	/**
	 * 获取表格通用弹出菜单
	 * @return
	 */
	public static JPopupMenu getMenu() {
		if (tableMenu==null) {
			tableMenu = new JPopupMenu();
			tableMenu.setBackground(Color.WHITE);
			tableMenu.setFont(new Font("微软雅黑Light", Font.PLAIN, 16));
			JMenuItem openFolder = new JMenuItem("打开文件夹",icon("folder.png"));
			openFolder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//找到触发该按钮事件的JTable对象
					JMenuItem m=(JMenuItem) e.getSource();
					JPopupMenu pm=(JPopupMenu) m.getParent();
					JTable table=(JTable) pm.getInvoker();
					Object file=table.getValueAt(table.getSelectedRow(), 1);
					Syss.open(Pather.parent(Strs.toStr(file)));
				}
			});
			tableMenu.add(openFolder);
		}
		return tableMenu;
	}
	
	
}

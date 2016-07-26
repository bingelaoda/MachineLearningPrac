package foundation.fileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jxl.*;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;   

public class RWExcelFile {
	/**
	 * 遍历Excel某个Sheet的所有DataCell
	 * @param filePath
	 */
	 public static void readExcel(String filePath,String sheetName)   
	 	{   
		  try{   	
            InputStream is = new FileInputStream(filePath);   
            Workbook rwb = Workbook.getWorkbook(is);   
            //这里有两种方法获取sheet表:名字和下标（从0开始）   
            //Sheet st = rwb.getSheet("original");   
            //Sheet的下标是从0开始   
            //获取第一张Sheet表   
            Sheet rst = rwb.getSheet(sheetName);   
            //获取Sheet表中所包含的总列数   
            int rsColumns = rst.getColumns();   
            //获取Sheet表中所包含的总行数   
            int rsRows = rst.getRows();   
            //获取指定单元格的对象引用   
            for (int i = 0; i < rsRows; i++)   
            {   
                for (int j = 0; j < rsColumns; j++)   
                {   
                    Cell cell = rst.getCell(j, i);   
                    System.out.print(cell.getContents() + " ");   
                }   
                System.out.println();   
            }             
            //关闭   
            rwb.close();   
        }   
        catch(Exception e)   
        {   
            e.printStackTrace();   
        }   
    }
	 /**
	  * 读取某个Excel的特定Sheet的特定Column
	  * @param filePath Excel文件的绝对路径
	  * @param sheetName Sheet的名称
	  * @param ColumnIndex 具体的列数
	  * @return
	  */
	 public static List<String> readjxldefineColumn(String filePath,String sheetName,int ColumnIndex){
		 List<String> dataSet = new ArrayList<>();
		 InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			 Workbook rwb = Workbook.getWorkbook(is);
			 Sheet st = rwb.getSheet(sheetName);
			 
			  //获取Sheet表中所包含的总列数   
	            int rsColumns = st.getColumns();  
	            //获取Sheet表中所包含的总行数   
	            int rsRows = st.getRows();
	            
	            if(ColumnIndex>rsColumns||ColumnIndex<1){
	            	throw new RuntimeException ("要找的列数不存在");
	            }
	            //获取指定单元格的对象引用   
	            String text="";
	            for (int i = 0; i < rsRows; i++)   
	            {   
                    Cell cell = st.getCell(ColumnIndex-1, i);
                    if(cell.getType() == CellType.NUMBER){
                    	 NumberCell numberCell = (NumberCell) cell; 
                    	 double value =numberCell.getValue();
                    	 text = value + "";
                    	   }
                    dataSet.add(text);
	            }             
	            //关闭   
	            rwb.close();   
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} 
		 
		 return dataSet;
	 }
	 
	 /**
	  * 读取某个Excel的特定Sheet的特定Row
	  * @param filePath Excel文件的绝对路径
	  * @param sheetName Sheet的名称
	  * @param RowIndex 具体的列数
	  * @return
	  */
	 public static List<String> readjxldefineRow(String filePath,String sheetName,int RowIndex){
		 List<String> dataSet = new ArrayList<>();
		 InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			 Workbook rwb = Workbook.getWorkbook(is);
			 Sheet st = rwb.getSheet(sheetName);
			 
			  //获取Sheet表中所包含的总列数   
	            int rsColumns = st.getColumns();  
	            //获取Sheet表中所包含的总行数   
	            int rsRows = st.getRows();
	            
	            //获取指定单元格的对象引用   
	            for (int i = 0; i < rsColumns; i++)   
	            {   
                    Cell cell = st.getCell(i, RowIndex-1);
                    dataSet.add(cell.getContents());
	            }             
	            //关闭   
	            rwb.close();   
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} 
		 
		 return dataSet;
	 }
	 /**
	  * 将List<String>转化成List<Float> 更便于计算
	  * @param strTypeSet
	  * @return
	  */
	 public static List<Float> strToFloat(List<String> strTypeSet){
		 List<Float> flaTypeSet = new ArrayList<>();
		 for(String str:strTypeSet){
			 flaTypeSet.add(Float.parseFloat(str));
		 }
		 
		 return flaTypeSet;
	 }
	 

		/**
		 * 生成一个Excel文件，若原文件存在，追加新的工作表
		 * 
		 * @param fileName
		 *            ：Excel文件名
		 * @param results
		 *            ：写入的内容
		 */
		public static void write(String fileName,
				List<List<DataCell>> results) {
			WritableWorkbook wwb = null;
			Workbook wb = null;
			try {

				File excelFile = new File(fileName);
				if (excelFile.exists()) {
					// 获得Excel工作薄(Workbook)对象
					Workbook wb1 = Workbook.getWorkbook(excelFile);
					// 打开一个文件的副本，并且指定数据写回到原文件
					WritableWorkbook wwb1 = Workbook.createWorkbook(new File(
							fileName), wb1);
					wb = wb1;
					wwb = wwb1;

				} else {
					// 使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
					WritableWorkbook wwb2 = Workbook.createWorkbook(excelFile);
					wwb = wwb2;
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (BiffException e) {
				e.printStackTrace();
			}
			if (wwb != null) {
				// 创建一个可写入的工作表
				// Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
				//String fileNameWithouPath = FileNameUtil
					//	.getFileNameInfo(excelfileName)[1];
				int len = wwb.getSheets().length;
				WritableSheet ws = wwb.createSheet(String.valueOf(len),
						len);
				int rowNum = results.size();
				ws.setColumnView(0, 30); // 设置第1列的宽度
				ws.setColumnView(1, 600); // 设置2列的宽度
				ws.setColumnView(2, 600); // 设置3列的宽度
				try {
					for (int m = 0; m < 10; m++) {// 设置行的高度
						ws.setRowView(m, 400);
					}
					ws.setRowView(10, 1200);
				} catch (RowsExceededException e2) {

					e2.printStackTrace();
				}

				WritableFont wf = new WritableFont(WritableFont.TIMES, 12,
						WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
						jxl.format.Colour.CORAL); // 定义格式 字体 下划线 斜体 粗体 颜色
				WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格定义
				// wcf.setBackground(jxl.format.Colour.BLACK); // 设置单元格的背景颜色
				try {// 设置对齐方式
					wcf.setAlignment(jxl.format.Alignment.LEFT);
					wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
					wcf.setWrap(true);
				} catch (WriteException e2) {
					e2.printStackTrace();
				}

				// 下面开始添加单元格
				for (int i = 0; i < rowNum; i++) {

					List<DataCell> row = results.get(i);

					int colSize = row.size();
					for (int k = 0; k < colSize; k++) {
						DataCell cell = row.get(k);
						// // 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
						Label labelTitle = new Label(k, i, cell.getName(), wcf);
						Label labelContent = new Label(k + 1, i,
								cell.getValue(), wcf);
						try {

							// 将生成的单元格添加到工作表中
							ws.addCell(labelTitle);
							ws.addCell(labelContent);
						} catch (RowsExceededException e) {
							e.printStackTrace();
						} catch (WriteException e) {
							e.printStackTrace();
						}
					}
				}
			}
			try {
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
				if (wb != null) {
					wb.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 生成一个Excel文件,如果该文件存在，将覆盖原有文件内容
		 * 
		 * @param fileName
		 *            要生成的Excel文件名
		 */
		public static void overideWrite(String fileName,
				List<List<DataCell>> results) {
			WritableWorkbook wwb = null;
			try {
				// 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
				wwb = Workbook.createWorkbook(new File(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (wwb != null) {
				// 创建一个可写入的工作表
				// Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
				WritableSheet ws = wwb.createSheet("sheet1", 0);
				int rowNum = results.size();
				ws.setColumnView(0, 30); // 设置第1列的宽度
				ws.setColumnView(1, 600); // 设置2列的宽度
				try {
					for (int m = 0; m < 10; m++) {// 设置行的高度
						ws.setRowView(m, 400);
					}
					ws.setRowView(10, 1200);
				} catch (RowsExceededException e2) {
					e2.printStackTrace();
				}
				WritableFont wf = new WritableFont(WritableFont.TIMES, 12,
						WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
						jxl.format.Colour.CORAL); // 定义格式 字体 下划线 斜体 粗体 颜色
				WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格定义
				// wcf.setBackground(jxl.format.Colour.BLACK); // 设置单元格的背景颜色
				try {// 设置对齐方式
					wcf.setAlignment(jxl.format.Alignment.LEFT);
					wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
					wcf.setWrap(true);
				} catch (WriteException e2) {
					e2.printStackTrace();
				}

				// 下面开始添加单元格
				for (int i = 0; i < rowNum; i++) {
					List<DataCell> row = results.get(i);
					int colSize = row.size();
					for (int k = 0; k < colSize; k++) {
						DataCell cell = row.get(k);
						// // 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
						Label labelTitle = new Label(k, i, cell.getName(), wcf);
						Label labelContent = new Label(k + 1, i,
								cell.getValue(), wcf);
						try {
							// 将生成的单元格添加到工作表中
							ws.addCell(labelTitle);
							ws.addCell(labelContent);
						} catch (RowsExceededException e) {
							e.printStackTrace();
						} catch (WriteException e) {
							e.printStackTrace();
						}
					}
				}
			}
			try {
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	 
	 public static void main(String[] args){
//		 List<String> list = readjxldefineColumn("D:\\a.xls","Sheet1",1);
//		 System.out.println(list);
		 List<String> list = readjxldefineRow("D:\\a.xls","Sheet1",1);
		 
	 }
}

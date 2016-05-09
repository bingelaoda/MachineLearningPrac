package foundation.fileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.*;   
import jxl.Cell;
import jxl.read.biff.BiffException;   

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
	            for (int i = 0; i < rsRows; i++)   
	            {   
                    Cell cell = st.getCell(ColumnIndex-1, i);   
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
	            
	            if(RowIndex>rsColumns||RowIndex<1){
	            	throw new RuntimeException ("要找的行数不存在");
	            }
	            //获取指定单元格的对象引用   
	            for (int i = 0; i < rsRows; i++)   
	            {   
                    Cell cell = st.getCell(i, RowIndex-1);
                    System.out.println(cell.getContents());
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
	 
	 public static void main(String[] args){
		 readjxldefineColumn("D:\\a.xls","Sheet1",1);
//		 readjxldefineRow("D:\\a.xls","Sheet1",1);
	 }
}

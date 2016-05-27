package foundation.fileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
/**
 * @author wuxb
 */

public class RWCsvFile {
	/**
	 * 
	 * @param filePath 
	 */
	public static void readCSV(String filePath) { 
			try{
				File file = new File(filePath);  
		        FileReader fReader = new FileReader(file);  
		        CSVReader csvReader = new CSVReader(fReader);  
		        String[] strs = csvReader.readNext();  
		        if(strs != null && strs.length > 0){  
		            for(String str : strs)  
		                if(null != str && !str.equals(""))  
		                    System.out.print(str + " , ");  
		            System.out.println("\n---------------");  
		        }  
		        List<String[]> list = csvReader.readAll();  
		        for(String[] ss : list){  
		            for(String s : ss)  
		                if(null != s && !s.equals(""))  
		                    System.out.print(s + " , ");  
		            System.out.println();  
		        }  
		        csvReader.close();  
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param code	将自由度按照每一维都作为一个学习cell
	 * @param attrs 根据自由度的长度给机器学习的数据表，csv文件设置表头
	 * @param file  csv文件的存储路径
	 */
	public  static void writeCSV(List<List<StringBuffer>> code,String[] attrs,String file){
		List<String[]> sl = new ArrayList<String[]>();
		sl.add(attrs);
		for(int m=0;m<code.size();m++){
			String[] str = new String[code.get(m).size()];
			for(int n=0;n<code.get(m).size();n++){
				str[n] = code.get(m).get(n).toString();
			}
			sl.add(str);
		}
		
        Writer writer = null;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}  
        CSVWriter csvWriter = new CSVWriter(writer, ',');  
        for(int i=0;i<sl.size();i++){
        	csvWriter.writeNext(sl.get(i));
        	csvWriter.flushQuietly();
        }
        try {
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

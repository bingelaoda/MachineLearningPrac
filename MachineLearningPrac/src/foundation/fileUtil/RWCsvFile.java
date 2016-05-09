package foundation.fileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class RWCsvFile {
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
	
	public static void writeCSV(String filePath){
		File file = new File(filePath);
		try {
			Writer writer = new FileWriter(file);  
	        CSVWriter csvWriter = new CSVWriter(writer, ',');  
	        String[] strs = {"abc" , "abc" , "abc"};  
	        csvWriter.writeNext(strs);  
	        csvWriter.close();  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
        
	}
}

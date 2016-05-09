package dao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;

public class ReadCsvForClIndiceUtil {
	
	//从csv文件中得到最大最小使用率的指标
	public static List<String> readCSVForIndice(String path,String caseNm){
		List<String> indice = new ArrayList<>();
		CSVReader reader = null;  
		try {  
		  reader = new CSVReader(new FileReader(path));
		} catch (FileNotFoundException e) {  
        e.printStackTrace();  
		} 
		String[] nextLine = null;  
		try {
			for(int i=0;i<5;i++){
				reader.readNext();
			}
			while ((nextLine = reader.readNext()) != null) {  
				for(int i = 0; i < nextLine.length; i++) {  
					if(i==1&&caseNm=="max"){
						indice.add(nextLine[i]);
					}
					if(i==2&&caseNm=="min"){
						indice.add(nextLine[i]);
					}
				} 
			}
		} catch (IOException e) {  
			e.printStackTrace();  
		}
		return indice;
	}
	
	
	public static List<Integer> converge(String[] strs){
		List<Integer> conDataIntegers = new ArrayList<>();
		int lenth = strs.length;
		for(int i=0;i<lenth;i++){
			conDataIntegers.add(Integer.parseInt(strs[i]));
		}
		for(int i=0;i<conDataIntegers.size();i++){
		}
		return conDataIntegers;
	}
	public static double computeAccuracy(List<Integer> conD){
		double accuracy = 0;
		int sum =0;
		int corr = 0;
		for(int i=0;i<conD.size();i++){
			if(i==0||i==6||i==12||i==18||i==24){
				corr+=conD.get(i);
			}
			sum+=conD.get(i);
		}
		accuracy = 1.0*corr/sum;
		return accuracy;
	}
	
}

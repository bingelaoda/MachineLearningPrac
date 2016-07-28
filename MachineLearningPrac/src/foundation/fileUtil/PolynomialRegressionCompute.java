package foundation.fileUtil;

import java.util.ArrayList;
import java.util.List;

import org.netlib.util.doubleW;

import foundation.CalculateIndiceUtil.CalculatePatameter;

public class PolynomialRegressionCompute {
	public void polynomialRegressionCompute(List<List<String>> strs){
		List<List<Double>> datas = new ArrayList<List<Double>>();
		//将strs数据进行转化，编程double类型，以便进行计算
		for(int i=0;i<strs.size();i++){
			List<Double> data= new ArrayList<>();
			for(int j=0; j<strs.get(0).size();j++){
				data.add(Double.parseDouble(strs.get(i).get(j)));
			}
			datas.add(data);
		}
		
		//根据公式进行计算
		
		List<Double> realy = new ArrayList<>();
		List<Double> predy = new ArrayList<>();
		long predictStart = System.currentTimeMillis();
		for(int i=0;i<datas.size();i++){
			List<Double> data = datas.get(i);
			
			double x1 = data.get(0);
			double x2 = data.get(1);
			double x3 = data.get(2);
			double x4 = data.get(3);
			double x5 = data.get(4);
			double x6 = data.get(5);
			double x7 = data.get(6);
//			double x8 = data.get(7);
//			double x9 = data.get(8);
//			double x10 = data.get(9);
//			double x11 = data.get(10);
//			double x12 = data.get(11);
//			double x13 = data.get(12);
//			double x14 = data.get(13);
//			double x15 = data.get(14);
//			double x16 = data.get(15);
//			double x17 = data.get(16);
			
			double truey = data.get(7);
			
			//将data带入公式了
			
			double predicty =

				    -16.8762 * x2 +
				      9.926  * x3 +
				     50.9429 * x5 +
				     49.8802 * x6 +
				     -3.0496 * x1*x7 +
				      3.0347 * x2*x2 +
				     -4.0221 * x2*x4 +
				      2.3737 * x2*x5 +
				     -7.8977 * x2*x7 +
				     -0.9874 * x3*x3 +
				      3.9503 * x4*x4 +
				     -3.5043 * x4*x5 +
				      7.4828 * x4*x7 +
				    -10.7431 * x5*x5 +
				    -10.0395 * x6*x6 +
				     10.7324 * x7*x7 +
				    -59.572 ;
			
			realy.add(truey);
			predy.add(predicty);
		}
		long predictEnd = System.currentTimeMillis();
		double rp = CalculatePatameter.rp(realy, predy);
		System.out.println(predictEnd-predictStart);
		System.out.println(rp);
	}
	
	public List<List<String>> gainDatafromCSV(String filePath,String sheetName,int rowSum){
		List<List<String>> rowDataSum = new ArrayList<List<String>>();
		for(int i=1;i<rowSum+1;i++){
			List<String> oneRow= RWExcelFile.readjxldefineRow(filePath, sheetName, i);
			rowDataSum.add(oneRow);
		}
		
		return rowDataSum;
	}
	
	public static void main(String[] args){
		String filePathString = "C:\\Users\\Administrator\\Desktop\\steLatinPredict.xls";
		String sheetName="steLatinPredict";
		int rowNum = 800;
		PolynomialRegressionCompute pRegCom = new PolynomialRegressionCompute();
		List<List<String>> strs = pRegCom.gainDatafromCSV(filePathString, sheetName, rowNum);
		pRegCom.polynomialRegressionCompute(strs);
	}
}

package foundation.fileUtil;

import java.util.ArrayList;
import java.util.List;
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
		
		for(int i=0;i<datas.size();i++){
			List<Double> data = datas.get(i);
			
			double x1 = data.get(0);
			double x2 = data.get(1);
			double x3 = data.get(2);
			double x4 = data.get(3);
			double x5 = data.get(4);
			double x6 = data.get(5);
			double x7 = data.get(6);
			double x8 = data.get(7);
			double x9 = data.get(8);
			double x10 = data.get(9);
			double x11 = data.get(10);
			double x12 = data.get(11);
			double x13 = data.get(12);
			double x14 = data.get(13);
			
			double truey = data.get(14);
			
			//将data带入公式了
			double predicty = -0.0268 * x1 +
						     -0.0089 * x2 +
						     -0.0121 * x4 +
						      0.0266 * x8 +
						      0.0258 * x10 +
						      0.0269 * x11 +
						      0.0474 * x13 +
						      0.0006 * x1*x2 +
						     -0.0006 * x1*x3 +
						      0.0007 * x1*x4 +
						     -0.0268 * x1*x5 +
						      0.0024 * x1*x7 +
						     -0.0019 * x1*x13 +
						      0.002  * x1*x14 +
						      0.0011 * x2*x4 +
						     -0.0089 * x2*x5 +
						      0.0015 * x2*x6 +
						     -0.0044 * x2*x10 +
						     -0.0035 * x2*x11 +
						     -0.0012 * x3*x4 +
						     -0.0019 * x3*x7 +
						     -0.0018 * x3*x8 +
						     -0.0011 * x3*x9 +
						      0.0047 * x3*x10 +
						     -0.0022 * x3*x11 +
						     -0.002  * x3*x13 +
						     -0.0121 * x4*x5 +
						     -0.0031 * x4*x10 +
						      0.0016 * x4*x11 +
						     -0.0274 * x5*x6 +
						      0.0266 * x5*x8 +
						      0.0258 * x5*x10 +
						      0.0269 * x5*x11 +
						      0.0474 * x5*x13 +
						      0.0021 * x6*x12 +
						      0.0056 * x7*x8 +
						     -0.0051 * x7*x13 +
						      0.0043 * x9*x13 +
						     -0.0093 * x10*x14 +
						     -0.0056 * x11*x13 +
						      0.0011 * x1*x1 +
						      0.0009 * x3*x3 +
						      0.0005 * x4*x4 +
						     -0.0077 * x8*x8 +
						      0.0051 * x11*x11 +
						     -0.0041 * x13*x13 +
						      1.1686 ;
			
			realy.add(truey);
			predy.add(predicty);
		}
		
		double rp = CalculatePatameter.rp(realy, predy);
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
		String filePathString = "C:\\Users\\Administrator\\Desktop\\Latin sample\\brsLatinTest.xls";
		String sheetName="brsLatinTest";
		int rowNum = 800;
		
		PolynomialRegressionCompute pRegCom = new PolynomialRegressionCompute();
		List<List<String>> strs = pRegCom.gainDatafromCSV(filePathString, sheetName, rowNum);
		pRegCom.polynomialRegressionCompute(strs);
	}
}

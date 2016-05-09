package foundation.CalculateIndiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * @author wuxb
 *
 */
public class CalculatePatameter {
	/**
	 * 
	 * @param realValue	真实值
	 * @param predValue 预测值
	 * @return
	 */
	public static double accuracy(List<Double> realValue,List<Double> predValue){
		double mse = 0;
		double variance = 0;
		double mean = 0;
		for (int i = 0; i < realValue.size(); i++) {
			mse += (realValue.get(i) - predValue.get(i)) * (realValue.get(i) - predValue.get(i));
			mean += realValue.get(i);
		}
		mean = mean / realValue.size();
		for (int i = 0; i < realValue.size(); i++) {
			variance += (realValue.get(i) - mean) * (realValue.get(i) - mean);
		}
		double accuracy = 1 - mse / variance;
		
		return accuracy;
	}
	
	public static double rp(List<Double> realValue,List<Double> predValue){
		int num1=0;
		int num2=0;
		for(int i=0;i<realValue.size();i++){
			for(int j=i+1;j<realValue.size();j++){
			if((predValue.get(i)<predValue.get(j)&&realValue.get(i)<realValue.get(j))
					||(predValue.get(i)>predValue.get(j)&&realValue.get(i)>realValue.get(j))
					||(predValue.get(i)==predValue.get(j)&&realValue.get(i)==realValue.get(j))){
				num1++;
			}
			num2++;
			}
		}
		
		double rp = 1.0*num1/num2;
		
		return rp;
	}
	
	public  static int computeError(List<Double> realValue,List<Double> predValue,double precisionValue){
		
		List<Double> rankPredValue = new ArrayList<>();
		HashMap<Double,Double> hashMap = new HashMap<Double, Double>();
		for(int i=0;i<realValue.size();i++){			
				hashMap.put(realValue.get(i), predValue.get(i));
		}
		Collections.sort(realValue);
		for(int i=0;i<realValue.size();i++){
			rankPredValue.add(hashMap.get(realValue.get(i)));
		}
		System.out.println(rankPredValue.size());
		for(double b:rankPredValue){
			System.out.println(b);
		}
		int num=0;
		for(int i=0;i<rankPredValue.size()-1;i++){
			if(rankPredValue.get(i)-rankPredValue.get(i+1)>precisionValue){
				num++;
			}
		}
		
		return num;
	}
}
	

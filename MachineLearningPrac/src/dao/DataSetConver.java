package dao;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author wuxb
 *
 */
public class DataSetConver {

	/**
	 * 
	 * @param code 要进行转化的一个code
	 * @param startIndexs 三个自由度的区分位
	 * @return
	 */
	public static List<Float> dataSetConver(String code,List<Integer> startIndexs){
		List<Float> afData = new ArrayList<>();
		code = code.trim();
		code = code.substring(1, code.length()-1);
		String[] codes = code.split(",");
		for(int i=0;i<codes.length;i++){
			if(i<startIndexs.get(0)){
				afData.add(Float.parseFloat(codes[i]));
			}else if(i<startIndexs.get(1)){
				afData.add(Float.parseFloat(codes[i].substring(7, 8)));
			}else {
				if(codes[i]=="QuickBooking"){
					afData.add(1f);
				}else {
					afData.add(2f);
				}
			}
		}
		return afData;
	}
	
	public static void main(String[] args){
		String code = "[5.5801563, 7.73964, 3.5319653, server3, server3, server3, QuickBooking]";
		List<Integer> startIndexs = new ArrayList<>();
		startIndexs.add(3);
		startIndexs.add(6);
		List<Float> dataSourceFloats = dataSetConver(code, startIndexs);
		for(float a:dataSourceFloats){
			System.out.println(a);
		}
	}
}

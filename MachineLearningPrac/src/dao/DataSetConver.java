package dao;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author wuxb
 *
 */
public class DataSetConver {
	
	private List<Integer> degreeIndex;
	public List<Integer> getDegreeIndex() {
		return degreeIndex;
	}

	public void setDegreeIndex(List<Integer> degreeIndex) {
		this.degreeIndex = degreeIndex;
	}

	/**
	 * 将一个code和机器学习将要进行学习预测的一个字段进行转换，从而存进csv文件，便于进行机器学习
	 * @param code 要进行转化的一个code
	 * @param restValue 与code对应的解析出来的真实的响应时间
	 * @return
	 */
	public static List<StringBuffer> dataSetConver(String code,float restValue){
		List<StringBuffer> finalData = new ArrayList<>();
		//将code进行处理
		String[] modifycode = modifyCode(code);
		int lenth = modifycode.length;
		StringBuffer strb = null;
		for (int i = 0; i < lenth; i++) {
			strb = new StringBuffer();
			strb.append(modifycode[i]);
			finalData.add(strb);
		}
		strb = new StringBuffer();
		strb.append(restValue);
		finalData.add(strb);
		
		return finalData;
	}
	
	private static String[] modifyCode(String code) {
		String[] modifyCode =new String[14];
		
		code = code.trim();
		code = code.substring(1, code.length()-1);
		modifyCode = code.split(",");
		int lenth = modifyCode.length;
		
		for(int i=0;i<lenth;i++){
			String tempStr = modifyCode[i].trim();
			switch (tempStr) {
			case "server1":
				modifyCode[i] = "1";
				break;
			case "server2":
				modifyCode[i] = "2";
				break;
			case "server3":
				modifyCode[i] = "3";
				break;
			case "BookingSystem":
				modifyCode[i] = "1";
				break;
			case "QuickBooking":
				modifyCode[i] = "2";
				break;

			default:
				break;
			}
		}

		
		return modifyCode;
	}

	public static void main(String[] args){
		String code = "[5.5801563, 7.73964, 3.5319653, server3, server3, server3, QuickBooking]";
		float restValue = 0.5f;
		List<StringBuffer> dataSourceFloats = dataSetConver(code, restValue);
		for(StringBuffer a:dataSourceFloats){
			System.out.println(a);
		}
	}
}

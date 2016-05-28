package plan;

import java.util.ArrayList;
import java.util.List;

import algorithm.RandomForestAlg;

/**
 * 
 * @author wuxb
 *
 */
public class Plan {
	
	public void excute(){
		List<Double> rfs = new ArrayList<>();
		for(int i=0;i<10;i++){
//			String algNM = "jssbrs";
//			int dimension = 15;
//			String algNM = "ms";
//			int dimension = 14;
//			String algNM = "pcs";
//			int dimension = 18;
			String algNM = "ste";
//			int dimension = 8;
//			int index1 = 600*i;
			int index2 = 600*i+400;
			RandomForestAlg randomForestAlg = new RandomForestAlg();
			double  rf = randomForestAlg.trainAndTest(100, 70,index2,index2,index2+200,algNM);
			rfs.add(rf);
		}
		for(int i=0;i<rfs.size();i++){
			System.out.println(rfs.get(i));	
		}
		
	}
	
	public static void main(String[] args){
		long start  = System.currentTimeMillis();
		Plan plan = new Plan();
		plan.excute();
		
		long end = System.currentTimeMillis();
		
		System.out.println((end-start));
	}
}

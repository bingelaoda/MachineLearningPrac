package plan;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.MLRegression;

import algorithm.EncogML;
import algorithm.RamdomForestAlg;

/**
 * 
 * @author wuxb
 *
 */
public class Plan {

	public void excute(){
		List<Double> rps = new ArrayList<>();
		String filePath="";
		for(int i=0;i<10;i++){
			EncogML encogMl = new EncogML();
			MLRegression mf = encogMl.train(filePath);
		}
		System.out.println("rps.size = "+rps.size());
		for(int i=0;i<rps.size();i++){
			System.out.println(rps.get(i));
		}
	}
	
	public static void main(String[] args){
		Plan plan = new Plan();
		plan.excute();
	}
}

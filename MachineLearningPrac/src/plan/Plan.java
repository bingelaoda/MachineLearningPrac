package plan;

import algorithm.RamdomForestAlg;

/**
 * 
 * @author wuxb
 *
 */
public class Plan {

	public void excute(){
		RamdomForestAlg.done(100, 70);
	}
	
	public static void main(String[] args){
		Plan plan = new Plan();
		plan.excute();
	}
}

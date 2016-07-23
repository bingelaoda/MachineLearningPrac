package plan;

import randomForest.RamdomForestAlg;

/**
 * 
 * @author wuxb
 *
 */
public class Plan {

	public void excute(){
		RamdomForestAlg.done(100,100);
	}
	
	public static void main(String[] args){
		Plan plan = new Plan();
		plan.excute();
	}
	
}

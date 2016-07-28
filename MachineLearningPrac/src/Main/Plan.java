package Main;

import excute.RBFNExcute;
import excute.RFExcute;
import excute.SVMregExcute;

public class Plan {
	public static void main(String[] args){
		String caseNM = "";
		
		RFExcute rfExcute = new RFExcute();
		rfExcute.run(caseNM);
//		RBFNExcute rbfnExcute = new RBFNExcute();
//		rbfnExcute.run();
//		SVMregExcute svMregExcute = new SVMregExcute();
//		svMregExcute.run();
	}
}

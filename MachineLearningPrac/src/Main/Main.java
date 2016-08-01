package Main;

import excute.RBFNExcute;
import excute.RFExcute;
import excute.SVMregExcute;

public class Main {
	public static void main(String[] args){
		String caseNM = "brs";
//		RFExcute rfExcute = new RFExcute();
//		rfExcute.run(caseNM);
//		RBFNExcute rbfnExcute = new RBFNExcute();
//		rbfnExcute.run(caseNM);
		SVMregExcute svMregExcute = new SVMregExcute();
		svMregExcute.run(caseNM);
	}
}

package excute;

import java.util.ArrayList;
import java.util.List;

import algorithm.SVMREG;

/**
 * 
 * @author wuxb
 *
 */
public class SVMregExcute extends Excute {
	
	private List<Long> trainTime;
	private List<Long> predTime;
	private List<Double> rp;
	public List<List<Double>> getParams() {
		return params;
	}

	public void setParams(List<List<Double>> params) {
		this.params = params;
	}

	private List<List<Double>> params;
	
	public List<Long> getTrainTime() {
		return trainTime;
	}

	public void setTrainTime(List<Long> trainTime) {
		this.trainTime = trainTime;
	}

	public List<Long> getPredTime() {
		return predTime;
	}

	public void setPredTime(List<Long> predTime) {
		this.predTime = predTime;
	}

	public List<Double> getRp() {
		return rp;
	}

	public void setRp(List<Double> rp) {
		this.rp = rp;
	}

	public void excute(List<Double> C_Param,List<Double> Gamma_Param,List<Double> toler_Param,String algNM){
		List<Long> trainTime = new ArrayList<>();
		List<Long> predTime = new ArrayList<>();
		List<Double> rp = new ArrayList<>();
		List<List<Double>> params = new ArrayList<>();
		for (int i = 0; i < C_Param.size(); i++) {
			for(int j=0;j<Gamma_Param.size();j++){
				for (int k = 0; k < toler_Param.size(); k++) {
					SVMREG svmreg = new SVMREG(C_Param.get(i),Gamma_Param.get(j),toler_Param.get(k),algNM);
					svmreg.trainAndPredict();
					trainTime.add(svmreg.getTrainTimeConsuming());
					predTime.add(svmreg.getPredTimeConsuming());
					rp.add(svmreg.getRp());
					List<Double> param = new ArrayList<>();
					param.add(C_Param.get(i));
					param.add(Gamma_Param.get(j));
					param.add(toler_Param.get(k));
					params.add(param);
				}
			}
			System.out.println(i);
		}
		setTrainTime(trainTime);
		setPredTime(predTime);
		setRp(rp);
		setParams(params);
	}
	
	public void run(){
		SVMregExcute plan = new SVMregExcute();
		List<Double> C_Param = new ArrayList<>();
		List<Double> Gamma_Param = new ArrayList<>();
		List<Double> toler_Param = new ArrayList<>();
		for(int i=-5;i<-4;i++){
			C_Param.add(Math.pow(2, i));
		}
		for (int i = 1; i < 2; i++) {
			Gamma_Param.add(i*0.1);
		}
		for (int i = -10; i < -9; i++) {
			toler_Param.add(Math.pow(2, i));
		}
		String algNM="ste";
		plan.excute(C_Param,Gamma_Param,toler_Param,algNM);
		double bestRP = 0d;
		int cursor = 0;
		for (int i = 0; i < plan.getRp().size(); i++) {
			double rp = plan.getRp().get(i);
			if (rp<bestRP) {
				bestRP = rp;
				cursor = i;
			}
		}
		System.out.println("BestRp"+plan.getRp().get(cursor));
		System.out.println(plan.getTrainTime().get(cursor));
		System.out.println(plan.getPredTime().get(cursor));
		System.out.println(plan.getParams().get(cursor));
	}
}

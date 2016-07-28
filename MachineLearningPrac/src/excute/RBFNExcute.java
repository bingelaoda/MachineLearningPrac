package excute;

import java.util.ArrayList;
import java.util.List;
import algorithm.RBFN;
/**
 * 
 * @author wuxb
 *
 */
public class RBFNExcute extends Excute {
	private List<Long> trainTime;
	private List<Long> predTime;
	private List<Double> rp;
	private List<Integer> params;

	public List<Integer> getParams() {
		return params;
	}

	public void setParams(List<Integer> params) {
		this.params = params;
	}

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

	public void excute(List<Integer> K,String algNM){
		List<Long> trainTime = new ArrayList<>();
		List<Long> predTime = new ArrayList<>();
		List<Double> rp = new ArrayList<>();
		List<Integer> params = new ArrayList<>();
		RBFN rbfn = new RBFN(algNM);
		for (int i = 0; i < K.size(); i++) {
			rbfn.setCenterNum(K.get(i));
			rbfn.trainAndPredict();
			trainTime.add(rbfn.getTrainTimeConsuming());
			predTime.add(rbfn.getPredTimeConsuming());
			rp.add(rbfn.getRp());
			params.add(K.get(i));
		}
		
	
		setTrainTime(trainTime);
		setPredTime(predTime);
		setRp(rp);
		setParams(params);
	}
	
	public void run(String caseNM){
		RBFNExcute rfExcute = new RBFNExcute();
		List<Integer> K = new ArrayList<>();
		for(int i=1;i<20;i++){
			K.add(i+1);
		}
		rfExcute.excute(K,caseNM);
		double bestRP = 0d;
		int cursor = 0;
		for (int i = 0; i < rfExcute.getRp().size(); i++) {
			double rp = rfExcute.getRp().get(i);
			if (rp<bestRP) {
				bestRP = rp;
				cursor = i;
			}
		}
		System.out.println("BestRp"+rfExcute.getRp().get(cursor));
		System.out.println(rfExcute.getTrainTime().get(cursor));
		System.out.println(rfExcute.getPredTime().get(cursor));
		System.out.println(rfExcute.getParams().get(cursor));
	}
}

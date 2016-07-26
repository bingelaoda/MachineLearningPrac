package excute;

import java.util.ArrayList;
import java.util.List;

import algorithm.RBFN;
/**
 * 
 * @author wuxb
 *
 */
public class RFExcute extends Excute {
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

	public void excute(String algNM){
		List<Long> trainTime = new ArrayList<>();
		List<Long> predTime = new ArrayList<>();
		List<Double> rp = new ArrayList<>();
		List<List<Double>> params = new ArrayList<>();
		RBFN rbfn = new RBFN(algNM);
		rbfn.trainAndPredict();
		trainTime.add(rbfn.getTrainTimeConsuming());
		predTime.add(rbfn.getPredTimeConsuming());
		rp.add(rbfn.getRp());
//		List<Double> param = new ArrayList<>();
//		params.add(param);
		setTrainTime(trainTime);
		setPredTime(predTime);
		setRp(rp);
		setParams(params);
	}
	
	public void run(){
		RFExcute rfExcute = new RFExcute();
		String algNM="brs";
		rfExcute.excute(algNM);
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
//		System.out.println(rfExcute.getParams().get(cursor));
	}
}

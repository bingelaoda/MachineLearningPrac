package excute;

import java.util.ArrayList;
import java.util.List;

import algorithm.RBFN;
import algorithm.RF;
/**
 * 
 * @author wuxb
 *
 */
public class RFExcute extends Excute {
	private List<Long> trainTime;
	private List<Long> predTime;
	private List<Double> rp;
	public List<List<Integer>> getParams() {
		return params;
	}

	public void setParams(List<List<Integer>> params) {
		this.params = params;
	}

	private List<List<Integer>> params;
	
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

	public void excute(List<Integer> newNumTrees,List<Integer> newNumFeatures,String algNM){
		List<Long> trainTime = new ArrayList<>();
		List<Long> predTime = new ArrayList<>();
		List<Double> rp = new ArrayList<>();
		List<List<Integer>> params = new ArrayList<>();
		for (int i = 0; i < newNumTrees.size(); i++) {
			for (int j = 0; j < newNumFeatures.size(); j++) {
				RF rbfn = new RF(algNM,newNumTrees.get(i),newNumFeatures.get(j));
				rbfn.trainAndPredict();
				trainTime.add(rbfn.getTrainTimeConsuming());
				predTime.add(rbfn.getPredTimeConsuming());
				rp.add(rbfn.getRp());
				List<Integer> param = new ArrayList<>();
				param.add(newNumTrees.get(i));
				param.add(newNumFeatures.get(j));
				params.add(param);
			}
		}
		
		setTrainTime(trainTime);
		setPredTime(predTime);
		setRp(rp);
		setParams(params);
	}
	
	public void run(){
		RFExcute rfExcute = new RFExcute();
		String algNM="ste";
		List<Integer> newNumTrees = new ArrayList<>();
		List<Integer> newNumFeatures = new ArrayList<>();
		for (int i = 1; i < 20; i++) {
			newNumTrees.add(i*5);
		}
		for (int i = 2; i < 10; i++) {
			newNumFeatures.add(i);
		}
		rfExcute.excute(newNumTrees,newNumFeatures,algNM);
		for (int i = 0; i < rfExcute.getRp().size(); i++) {
			System.out.println(rfExcute.getRp().get(i));
			System.out.println(rfExcute.getTrainTime().get(i));
			System.out.println(rfExcute.getPredTime().get(i));
			System.out.println(rfExcute.getParams().get(i));
		}
		
	}
}

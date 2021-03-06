package algorithm;

import java.util.ArrayList;
import java.util.List;
import foundation.CalculateIndiceUtil.CalculateIndice;
import foundation.fileUtil.FileNameUtil;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.RegOptimizer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class SVMREG {
	/*根据给定的样本进行训练，根据给的样本进行预测，并且返回
	 * rp值
	 * 训练时间  trainTimeConsume
	 * 预测时间  PredictTimeConsume
	 * 三个算法参数 C Gamma tolerance
	*/
	
	private double C;
	private double Gamma;
	private double toler;
	private long trainTimeConsuming;
	private long predTimeConsuming;
	private double rp;
	private String algNM;
	
	public SVMREG(double C,double Gamma,double toler,String algNM){
		this.C = C;
		this.Gamma = Gamma;
		this.toler = toler;
		this.algNM = algNM;
	}
	public long getTrainTimeConsuming() {
		return trainTimeConsuming;
	}

	public void setTrainTimeConsuming(long trainTimeConsuming) {
		this.trainTimeConsuming = trainTimeConsuming;
	}

	public String getAlgNM() {
		return algNM;
	}

	public void setAlgNM(String algNM) {
		this.algNM = algNM;
	}

	public double getC() {
		return C;
	}

	public void setC(double c) {
		C = c;
	}

	public double getGamma() {
		return Gamma;
	}

	public void setGamma(double gamma) {
		Gamma = gamma;
	}

	public double getToler() {
		return toler;
	}

	public void setToler(double toler) {
		this.toler = toler;
	}

	public long getPredTimeConsuming() {
		return predTimeConsuming;
	}

	public void setPredTimeConsuming(long predTimeConsuming) {
		this.predTimeConsuming = predTimeConsuming;
	}

	public double getRp() {
		return rp;
	}

	public void setRp(double rp) {
		this.rp = rp;
	}

	public void trainAndPredict(){
		String trainSetPath = FileNameUtil.getPrjPath()+"dataSource\\"+algNM+"LatinTrain.csv";
		String predSetPath = FileNameUtil.getPrjPath()+"dataSource\\"+algNM+"LatinPredict.csv";
		/*
		 * 输入数据集
		 */
		Instances trainData = null;
		try {
			trainData = DataSource.read(trainSetPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 
		//设置目标属性位
		trainData.setClassIndex(trainData.numAttributes() - 1);
		/*
		 * 准备测试数据
		 */
		Instances testData = null;
		try {
			testData = DataSource.read(predSetPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testData.setClassIndex(testData.numAttributes() - 1);
		/*
		 * 修改参数C的值
		 */
		
		//实例化选用的机器学习算法
		SMOreg svm = new SMOreg();
		
		/*
		 * 参数设定
		 */
		String[] options = new String[8];
		options[0] ="-C";
		options[1] = ""+C;
		options[2] = "-N";
		options[3] = "0";
		options[4] = "-I";
		options[5] = "weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1";
		options[6] = "-K";
		options[7] = "weka.classifiers.functions.supportVector.RBFKernel -G 0.01 -C 250007";
		try {
			svm.setOptions(options);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
//		String[] option1 = svm.getOptions();
//		for (int i = 0; i < option1.length; i++) {
//			System.out.println(option1[i]);
//		}
		
		/*
		 * 修改Gamma参数
		 */
		Kernel kernel = svm.getKernel();
		String[] modifyKernelOpt = new String[4];
		modifyKernelOpt[0] = "-G";
		modifyKernelOpt[1] = ""+Gamma;
		modifyKernelOpt[2] = "-C";
		modifyKernelOpt[3]= "250007";
		try {
			kernel.setOptions(modifyKernelOpt);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/*
		 * 修改Tolerance参数
		 */
		RegOptimizer regOptimizer = svm.getRegOptimizer();
		String[] modifyregOption = new String[9];
		modifyregOption[0] = "-T";
		modifyregOption[1] = ""+toler;
		modifyregOption[2] = "-V";
		modifyregOption[3] = "-P";
		modifyregOption[4] = "1.0E-12";
		modifyregOption[5] = "-L";
		modifyregOption[6] = "0.001";
		modifyregOption[7] = "-W";
		modifyregOption[8] = "1";
	
		try {
			regOptimizer.setOptions(modifyregOption);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/*
		 * 开始进行训练，并且记录训练消耗时间
		 */
	    try {
	    	long trainStart = System.currentTimeMillis();
			//训练进行建模
	    	svm.buildClassifier(trainData);
			long trainEnd = System.currentTimeMillis();
			long trainTimeConsuming = trainEnd-trainStart;
			setTrainTimeConsuming(trainTimeConsuming);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    List<Double> realv = new ArrayList<>();
	    List<Double> predv = new ArrayList<>();
	    long predTime = 0;
	    int testDataSize = testData.size();
	    
	    
	    //测试并且记录测试所用时间
	    for (int i =0 ; i < testDataSize; i++) {
	    	double realValue = Double.parseDouble(testData.instance(i).toString(testData.classIndex()));
	    	double predValue=0;
			try {
				long predOnceStartTime = System.currentTimeMillis();
				//进行测试
				predValue = svm.classifyInstance(testData.instance(i));
				long predOneceEndTime = System.currentTimeMillis();
				predTime += predOneceEndTime-predOnceStartTime; 
			} catch (Exception e) {
				e.printStackTrace();
			}
    	realv.add(realValue);
    	predv.add(predValue);
	    }
	    /*
	     * 存储预测耗时
	     */
	    setPredTimeConsuming(predTime);
	    /*
	     * 存储RP值
	     */
	    double rp = CalculateIndice.rp(realv, predv);
	    setRp(rp);
	}
}
	    
  

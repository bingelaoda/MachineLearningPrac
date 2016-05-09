package randomForest;

import java.util.ArrayList;
import java.util.List;
import foundation.CalculateIndiceUtil.CalculatePatameter;
import foundation.fileUtil.FileNameUtil;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * This example class trains a Random Forest classifier on a dataset and outputs for 
 * a second dataset.	
 */
public class RamdomForestAlg {
	
  /**
   * Expects two parameters: training file and test file.
   * 
   * @throws Exception	if something goes wrong
   */
	
  public static void main(String[] args) throws Exception {
//		  for(int j=0;j<20;j++){
//			int batchsize = 10*j;
//			
//			if(done(400, batchsize)>0){
//				System.out.println(""+done(400, batchsize));
//			}
//		}
	  done(200, 400);
		  
  }
  public static double done(int treesize,int batchsize){
	  // load data
	  String path = FileNameUtil.getPrjPath();
//    BufferedReader br = null;
//    int numFolds = 10;
//    br = new BufferedReader(new FileReader(path+"doc\\maxUtilSvr.csv"));
//    Instances trainData =DataSource.read(path+"doc\\maxUtilSvrTrain.csv");
//    Instances trainData =DataSource.read(path+"doc\\respTimeTrain.csv");
//	  Instances trainData = new Instances(br);
      Instances trainData = null;
	try {
		trainData = DataSource.read(path+"dataSource\\train.csv");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      trainData.setClassIndex(trainData.numAttributes() - 1);
      
//      br.close();
//    Instances train = DataSource.read(args[0]);
//    train.setClassIndex(train.numAttributes() - 1);
//  	BufferedReader br = null;
//    int numFolds = 10;k
//    br = new BufferedReader(new FileReader(path+"doc\\maxUtilSvr.csv"));
//    Instances test = new Instances(br);
//    Instances test = DataSource.read(path+"doc\\maxUtilSvrTest.csv");
//    Instances test = DataSource.read(path+"doc\\respTimeTest.csv");
      
    Instances test = null;
	try {
		test = DataSource.read(path+"dataSource\\predict.csv");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    test.setClassIndex(test.numAttributes() - 1);
    
    if (!trainData.equalHeaders(test))
      throw new IllegalArgumentException(
	  "Train and test set are not compatible: " + trainData.equalHeadersMsg(test));
    
    // train classifier
    RandomForest rf = new RandomForest();
    rf.setNumTrees(treesize);
    rf.setBatchSize(""+batchsize);
    rf.setNumDecimalPlaces(10);
    try {
		rf.buildClassifier(trainData);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    // output predictions
//    System.out.println("# - actual - predicted - error - distribution");
//  int num=0;
    List<Double> realv = new ArrayList<>();
    List<Double> predv = new ArrayList<>();
    for (int i = 0; i < test.numInstances(); i++) {
    	double real = Double.parseDouble(test.instance(i).toString(test.classIndex()));
    	double pred=0;
		try {
			pred = rf.classifyInstance(test.instance(i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	realv.add(real);
    	predv.add(pred);
      
      System.out.println("pred "+pred+"actual "+real);
//      double[] dist = rf.distributionForInstance(test.instance(i));

//      System.out.print((i+1));
//      System.out.print(" - ");
//      System.out.println(pred);
//      int real = Integer.parseInt(test.instance(i).toString(test.classIndex()));
//      System.out.print(Integer.parseInt(test.instance(i).toString(test.classIndex())));
//      int pred1 = (int) Math.round(pred);
//      System.out.println("    "+real+"   "+pred1);
      
//      if(real!=pred1){
////    	  throw new Exception("����һ�γ��ִ���"+i);
//    	 num++;
//      }
//      System.out.print(" - ");
//      System.out.print(test.classAttribute().value((int) pred));
//      System.out.println(pred);
//      System.out.print(" - ");
//      if (pred != test.instance(i).classValue())
//	System.out.print("yes");
//      else
//	System.out.print("no");
//      System.out.print(" - ");
//      System.out.print(Utils.arrayToString(dist));
      System.out.println();
    }
    double accuracy = CalculatePatameter.accuracy(realv, predv);
    double rp = CalculatePatameter.rp(realv, predv);
    
    System.out.println("Accuracy"+accuracy);
    System.out.println("rp"+rp);
//    
    return accuracy;
  }
  
}

package plan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.MLRegression;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import algorithm.EncogML;
import foundation.fileUtil.FileNameUtil;
import foundation.fileUtil.RWCsvFileUtil;

/**
 * 
 * @author wuxb
 *
 */
public class Plan {

	public void excute(){
		
		List<Double> rfs = new ArrayList<>();
		for(int i=0;i<10;i++){
//			String algNM = "jssbrs";
//			int dimension = 15;
//			String algNM = "ms";
//			int dimension = 14;
//			String algNM = "pcs";
//			int dimension = 18;
			String algNM = "ste";
			int dimension = 8;
			int index1 = 600*i;
			int index2 = 600*i+400;
			
			RWCsvFileUtil.preparedForEncogMLCSV(algNM, index1, index2, dimension);
			String path = FileNameUtil.getPrjPath();
			File testCSV = new File(path+"dataSource\\"+algNM+"PCMData.csv");
			ReadCSV csv = new ReadCSV(testCSV, false, CSVFormat.DECIMAL_POINT);
			EncogML encogMl = new EncogML();
			MLRegression bestMethod = encogMl.train(path+"dataSource\\"+algNM+"PCMEncogTrainData.csv",dimension);
			double  rf = encogMl.trainAndTest(csv, bestMethod, index2, index2+200, dimension);
			rfs.add(rf);
		}
		
		for(double rf:rfs){
			System.out.println(rf);
		}
	}
	
	public static void main(String[] args){
		Plan plan = new Plan();
		plan.excute();
	}
}

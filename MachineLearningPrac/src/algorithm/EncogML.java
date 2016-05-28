package algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.encog.ConsoleStatusReportable;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import foundation.CalculateIndiceUtil.CalculatePatameter;

public class EncogML {
	
	private NormalizationHelper helper = null;
	
	public NormalizationHelper getHelper() {
		return helper;
	}
	public void setHelper(NormalizationHelper helper) {
		this.helper = helper;
	}
	public EncogML(){
		NormalizationHelper helper = new NormalizationHelper();
		this.helper = helper;
	}

	public MLRegression  train(String filePath,int dimension) {
			MLRegression bestMethod = null;
			File irisFile = new File(filePath);
			// Define the format of the data file.
			// This area will change, depending on the columns and 
			// format of the file that you are trying to model.
			VersatileDataSource source = new CSVDataSource(irisFile, false,
					CSVFormat.DECIMAL_POINT);
			VersatileMLDataSet data = new VersatileMLDataSet(source);
			
			for(int i=0;i<dimension-1;i++){
				data.defineSourceColumn(""+(i+1), i, ColumnType.continuous);
			}
		/*	data.defineSourceColumn("1", 0, ColumnType.continuous);
			data.defineSourceColumn("2", 1, ColumnType.continuous);
			data.defineSourceColumn("3", 2, ColumnType.continuous);
			data.defineSourceColumn("4", 3, ColumnType.continuous);
			data.defineSourceColumn("5", 4, ColumnType.continuous);
			data.defineSourceColumn("6", 5, ColumnType.continuous);
			data.defineSourceColumn("7", 6, ColumnType.continuous);
			data.defineSourceColumn("8", 7, ColumnType.continuous);
			data.defineSourceColumn("9", 8, ColumnType.continuous);
			data.defineSourceColumn("10", 9, ColumnType.continuous);
			data.defineSourceColumn("11", 10, ColumnType.continuous);
			data.defineSourceColumn("12", 11, ColumnType.continuous);
			data.defineSourceColumn("13", 12, ColumnType.continuous);
			data.defineSourceColumn("14", 13, ColumnType.continuous);*/
			// Define the column that we are trying to predict.
			ColumnDefinition outputColumn = data.defineSourceColumn("species", dimension-1,
					ColumnType.continuous);
			
			// Analyze the data, determine the min/max/mean/sd of every column.
			data.analyze();
			
			// Map the prediction column to the output of the model, and all
			// other columns to the input.
			data.defineSingleOutputOthersInput(outputColumn);
			
			// Create feedforward neural network as the model type. MLMethodFactory.TYPE_FEEDFORWARD.
			// You could also other model types, such as:
			// MLMethodFactory.SVM:  Support Vector Machine (SVM)
			// MLMethodFactory.TYPE_RBFNETWORK: RBF Neural Network
			// MLMethodFactor.TYPE_NEAT: NEAT Neural Network
			// MLMethodFactor.TYPE_PNN: Probabilistic Neural Network
			EncogModel model = new EncogModel(data);
			model.selectMethod(data, MLMethodFactory.TYPE_NEAT);
			
			// Send any output to the console.
			model.setReport(new ConsoleStatusReportable());
			
			// Now normalize the data.  Encog will automatically determine the correct normalization
			// type based on the model you chose in the last step.
			data.normalize();
			
			// Hold back some data for a final validation.
			// Shuffle the data into a random ordering.
			// Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
			model.holdBackValidation(0.3, true, 1001);
			
			// Choose whatever is the default training type for this model.
			model.selectTrainingType(data);
			
			// Use a 5-fold cross-validated train.  Return the best method found.
			bestMethod = (MLRegression)model.crossvalidate(5, true);
			helper = data.getNormHelper();
			return bestMethod;
		}
	
	public double trainAndTest(ReadCSV csv,MLRegression bestMethod,int index1,int index2, int dimension){
		String[] line = new String[dimension-1];
		MLData input = helper.allocateInputVector();
		String irisChosen = "";
		List<Double> realv = new ArrayList<>();
		List<Double> predv = new ArrayList<>();
		int cursor = 0;
		csv.next();
		while (csv.next()) {
			cursor++;
			if(cursor<index1||cursor>index2){
				continue;
			}
				
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < dimension-1; i++) {
				line[i] = csv.get(i);
			}
			String correct = csv.get(dimension-1);
			realv.add(Double.parseDouble(correct));
			helper.normalizeInputVector(line, input.getData(), false);
			MLData output = bestMethod.compute(input);
			irisChosen = helper.denormalizeOutputVectorToString(output)[0];
			predv.add(Double.parseDouble(irisChosen));
			result.append(Arrays.toString(line));
			result.append(" -> predicted: ");
			result.append(irisChosen);
			result.append(")");
			System.out.println("correct ="+correct+"predict = "+irisChosen);
		}
	
		double rp = CalculatePatameter.accuracy(realv, predv);
		    
	    return rp;
		
	}
	
	public static void main(String[] args) {
		
//		EncogML prg = new EncogML();
//		File irisFile1 = new File("D:", "test1.csv");
//		ReadCSV csv = new ReadCSV(irisFile1, false, CSVFormat.DECIMAL_POINT);
	}
}

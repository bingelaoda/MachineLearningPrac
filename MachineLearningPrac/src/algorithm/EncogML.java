package algorithm;

import java.io.File;
import org.encog.ConsoleStatusReportable;
import org.encog.ml.MLRegression;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;


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

	public MLRegression  train(String filePath) {
			MLRegression bestMethod = null;
			File irisFile = new File(filePath);
			// Define the format of the data file.
			// This area will change, depending on the columns and 
			// format of the file that you are trying to model.
			VersatileDataSource source = new CSVDataSource(irisFile, false,
					CSVFormat.DECIMAL_POINT);
			VersatileMLDataSet data = new VersatileMLDataSet(source);
			data.defineSourceColumn("1", 0, ColumnType.continuous);
			data.defineSourceColumn("2", 1, ColumnType.continuous);
			data.defineSourceColumn("3", 2, ColumnType.continuous);
			data.defineSourceColumn("4", 3, ColumnType.continuous);
			data.defineSourceColumn("5", 4, ColumnType.continuous);
			data.defineSourceColumn("6", 5, ColumnType.continuous);
			data.defineSourceColumn("7", 6, ColumnType.continuous);
			data.defineSourceColumn("8", 7, ColumnType.continuous);
//			data.defineSourceColumn("9", 8, ColumnType.continuous);
//			data.defineSourceColumn("10", 9, ColumnType.continuous);
//			data.defineSourceColumn("11", 10, ColumnType.continuous);
//			data.defineSourceColumn("12", 11, ColumnType.continuous);
//			data.defineSourceColumn("13", 12, ColumnType.continuous);
//			data.defineSourceColumn("14", 13, ColumnType.continuous);
			// Define the column that we are trying to predict.
			ColumnDefinition outputColumn = data.defineSourceColumn("species", 8,
					ColumnType.nominal);
			
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
			model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);
			
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
			return bestMethod;
		} 
}

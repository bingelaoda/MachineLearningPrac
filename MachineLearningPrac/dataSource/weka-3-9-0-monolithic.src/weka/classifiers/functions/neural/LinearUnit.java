/*   1:    */ package weka.classifiers.functions.neural;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionHandler;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class LinearUnit
/*   7:    */   implements NeuralMethod, RevisionHandler
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 8572152807755673630L;
/*  10:    */   
/*  11:    */   public double outputValue(NeuralNode node)
/*  12:    */   {
/*  13: 45 */     double[] weights = node.getWeights();
/*  14: 46 */     NeuralConnection[] inputs = node.getInputs();
/*  15: 47 */     double value = weights[0];
/*  16: 48 */     for (int noa = 0; noa < node.getNumInputs(); noa++) {
/*  17: 50 */       value += inputs[noa].outputValue(true) * weights[(noa + 1)];
/*  18:    */     }
/*  19: 54 */     return value;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public double errorValue(NeuralNode node)
/*  23:    */   {
/*  24: 65 */     NeuralConnection[] outputs = node.getOutputs();
/*  25: 66 */     int[] oNums = node.getOutputNums();
/*  26: 67 */     double error = 0.0D;
/*  27: 69 */     for (int noa = 0; noa < node.getNumOutputs(); noa++) {
/*  28: 70 */       error += outputs[noa].errorValue(true) * outputs[noa].weightValue(oNums[noa]);
/*  29:    */     }
/*  30: 73 */     return error;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void updateWeights(NeuralNode node, double learn, double momentum)
/*  34:    */   {
/*  35: 85 */     NeuralConnection[] inputs = node.getInputs();
/*  36: 86 */     double[] cWeights = node.getChangeInWeights();
/*  37: 87 */     double[] weights = node.getWeights();
/*  38:    */     
/*  39: 89 */     double learnTimesError = 0.0D;
/*  40: 90 */     learnTimesError = learn * node.errorValue(false);
/*  41:    */     
/*  42: 92 */     double c = learnTimesError + momentum * cWeights[0];
/*  43: 93 */     weights[0] += c;
/*  44: 94 */     cWeights[0] = c;
/*  45:    */     
/*  46: 96 */     int stopValue = node.getNumInputs() + 1;
/*  47: 97 */     for (int noa = 1; noa < stopValue; noa++)
/*  48:    */     {
/*  49: 99 */       c = learnTimesError * inputs[(noa - 1)].outputValue(false);
/*  50:100 */       c += momentum * cWeights[noa];
/*  51:    */       
/*  52:102 */       weights[noa] += c;
/*  53:103 */       cWeights[noa] = c;
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String getRevision()
/*  58:    */   {
/*  59:113 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.neural.LinearUnit
 * JD-Core Version:    0.7.0.1
 */
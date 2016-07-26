/*   1:    */ package weka.classifiers.functions.neural;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionHandler;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class SigmoidUnit
/*   7:    */   implements NeuralMethod, RevisionHandler
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -5162958458177475652L;
/*  10:    */   
/*  11:    */   public double outputValue(NeuralNode node)
/*  12:    */   {
/*  13: 45 */     double[] weights = node.getWeights();
/*  14: 46 */     NeuralConnection[] inputs = node.getInputs();
/*  15: 47 */     double value = weights[0];
/*  16: 48 */     for (int noa = 0; noa < node.getNumInputs(); noa++) {
/*  17: 50 */       value += inputs[noa].outputValue(true) * weights[(noa + 1)];
/*  18:    */     }
/*  19: 56 */     if (value < -45.0D) {
/*  20: 57 */       value = 0.0D;
/*  21: 59 */     } else if (value > 45.0D) {
/*  22: 60 */       value = 1.0D;
/*  23:    */     } else {
/*  24: 63 */       value = 1.0D / (1.0D + Math.exp(-value));
/*  25:    */     }
/*  26: 65 */     return value;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double errorValue(NeuralNode node)
/*  30:    */   {
/*  31: 76 */     NeuralConnection[] outputs = node.getOutputs();
/*  32: 77 */     int[] oNums = node.getOutputNums();
/*  33: 78 */     double error = 0.0D;
/*  34: 80 */     for (int noa = 0; noa < node.getNumOutputs(); noa++) {
/*  35: 81 */       error += outputs[noa].errorValue(true) * outputs[noa].weightValue(oNums[noa]);
/*  36:    */     }
/*  37: 84 */     double value = node.outputValue(false);
/*  38: 85 */     error *= value * (1.0D - value);
/*  39:    */     
/*  40: 87 */     return error;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void updateWeights(NeuralNode node, double learn, double momentum)
/*  44:    */   {
/*  45: 99 */     NeuralConnection[] inputs = node.getInputs();
/*  46:100 */     double[] cWeights = node.getChangeInWeights();
/*  47:101 */     double[] weights = node.getWeights();
/*  48:102 */     double learnTimesError = 0.0D;
/*  49:103 */     learnTimesError = learn * node.errorValue(false);
/*  50:104 */     double c = learnTimesError + momentum * cWeights[0];
/*  51:105 */     weights[0] += c;
/*  52:106 */     cWeights[0] = c;
/*  53:    */     
/*  54:108 */     int stopValue = node.getNumInputs() + 1;
/*  55:109 */     for (int noa = 1; noa < stopValue; noa++)
/*  56:    */     {
/*  57:111 */       c = learnTimesError * inputs[(noa - 1)].outputValue(false);
/*  58:112 */       c += momentum * cWeights[noa];
/*  59:    */       
/*  60:114 */       weights[noa] += c;
/*  61:115 */       cWeights[noa] = c;
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String getRevision()
/*  66:    */   {
/*  67:125 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.neural.SigmoidUnit
 * JD-Core Version:    0.7.0.1
 */
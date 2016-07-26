/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.DenseInstance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.filters.SimpleBatchFilter;
/*  12:    */ import weka.filters.UnsupervisedFilter;
/*  13:    */ 
/*  14:    */ public class Transpose
/*  15:    */   extends SimpleBatchFilter
/*  16:    */   implements UnsupervisedFilter
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 213999899640387499L;
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 64 */     return "Transposes the data: instances become attributes and attributes become instances. If the first attribute in the original data is a nominal or string identifier attribute, this identifier attribute will be used to create attribute names in the transposed data. All attributes other than the identifier attribute must be numeric. The attribute names in the original data are used to create an identifier attribute of type string in the transposed data.\n\nThis filter can only process one batch of data, e.g. it cannot be used in the the FilteredClassifier.\n\nThis filter can only be applied when no class attribute has been set.\n\n Date values will be turned into simple numeric values.\n\n";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Capabilities getCapabilities()
/*  26:    */   {
/*  27: 85 */     Capabilities result = super.getCapabilities();
/*  28: 86 */     result.disableAll();
/*  29:    */     
/*  30: 88 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  31: 89 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  32: 90 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  33: 91 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  34: 92 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  35:    */     
/*  36: 94 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  37:    */     
/*  38: 96 */     return result;
/*  39:    */   }
/*  40:    */   
/*  41:    */   protected Instances determineOutputFormat(Instances inputFormat)
/*  42:    */     throws Exception
/*  43:    */   {
/*  44:115 */     ArrayList<Attribute> newAtts = new ArrayList(inputFormat.numInstances());
/*  45:    */     
/*  46:    */ 
/*  47:118 */     newAtts.add(new Attribute("Identifier", (ArrayList)null));
/*  48:119 */     for (int i = 0; i < inputFormat.numInstances(); i++)
/*  49:    */     {
/*  50:120 */       if ((inputFormat.attribute(0).isNominal()) || (inputFormat.attribute(0).isString())) {
/*  51:124 */         newAtts.add(new Attribute(inputFormat.instance(i).stringValue(0)));
/*  52:    */       } else {
/*  53:128 */         newAtts.add(new Attribute("" + (i + 1)));
/*  54:    */       }
/*  55:130 */       ((Attribute)newAtts.get(i)).setWeight(inputFormat.instance(i).weight());
/*  56:    */     }
/*  57:133 */     return new Instances(inputFormat.relationName(), newAtts, inputFormat.numAttributes());
/*  58:    */   }
/*  59:    */   
/*  60:    */   protected Instances process(Instances instances)
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:149 */     if (isFirstBatchDone()) {
/*  64:150 */       throw new Exception("The Transpose filter can only process one batch of instances.");
/*  65:    */     }
/*  66:154 */     setOutputFormat(determineOutputFormat(instances));
/*  67:    */     
/*  68:    */ 
/*  69:157 */     int offset = (instances.attribute(0).isNominal()) || (instances.attribute(0).isString()) ? 1 : 0;
/*  70:    */     
/*  71:    */ 
/*  72:    */ 
/*  73:161 */     double[][] newData = new double[instances.numAttributes() - offset][instances.numInstances() + 1];
/*  74:163 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  75:164 */       for (int j = offset; j < instances.numAttributes(); j++)
/*  76:    */       {
/*  77:165 */         newData[(j - offset)][0] = getOutputFormat().attribute(0).addStringValue(instances.attribute(j).name());
/*  78:167 */         if (!instances.attribute(j).isNumeric()) {
/*  79:168 */           throw new Exception("Only numeric attributes can be transposed: " + instances.attribute(j).name() + " is not numeric.");
/*  80:    */         }
/*  81:171 */         newData[(j - offset)][(i + 1)] = instances.instance(i).value(j);
/*  82:    */       }
/*  83:    */     }
/*  84:176 */     Instances result = getOutputFormat();
/*  85:177 */     for (int i = 0; i < newData.length; i++) {
/*  86:178 */       result.add(new DenseInstance(instances.attribute(i + offset).weight(), newData[i]));
/*  87:    */     }
/*  88:182 */     return result;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getRevision()
/*  92:    */   {
/*  93:192 */     return RevisionUtils.extract("$Revision: 10215 $");
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static void main(String[] args)
/*  97:    */   {
/*  98:201 */     runFilter(new Transpose(), args);
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Transpose
 * JD-Core Version:    0.7.0.1
 */
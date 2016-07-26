/*   1:    */ package weka.classifiers.pmml.producer;
/*   2:    */ 
/*   3:    */ import weka.core.Attribute;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.core.Version;
/*   6:    */ import weka.core.pmml.jaxbbindings.Application;
/*   7:    */ import weka.core.pmml.jaxbbindings.DataDictionary;
/*   8:    */ import weka.core.pmml.jaxbbindings.DataField;
/*   9:    */ import weka.core.pmml.jaxbbindings.Header;
/*  10:    */ import weka.core.pmml.jaxbbindings.OPTYPE;
/*  11:    */ import weka.core.pmml.jaxbbindings.PMML;
/*  12:    */ import weka.core.pmml.jaxbbindings.Value;
/*  13:    */ 
/*  14:    */ public abstract class AbstractPMMLProducerHelper
/*  15:    */ {
/*  16:    */   public static final String PMML_VERSION = "4.1";
/*  17:    */   
/*  18:    */   public static PMML initPMML()
/*  19:    */   {
/*  20: 53 */     PMML pmml = new PMML();
/*  21: 54 */     pmml.setVersion("4.1");
/*  22: 55 */     Header header = new Header();
/*  23: 56 */     header.setCopyright("WEKA");
/*  24: 57 */     header.setApplication(new Application("WEKA", Version.VERSION));
/*  25: 58 */     pmml.setHeader(header);
/*  26:    */     
/*  27: 60 */     return pmml;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static void addDataDictionary(Instances trainHeader, PMML pmml)
/*  31:    */   {
/*  32: 71 */     DataDictionary dictionary = new DataDictionary();
/*  33: 73 */     for (int i = 0; i < trainHeader.numAttributes(); i++)
/*  34:    */     {
/*  35: 74 */       String name = trainHeader.attribute(i).name();
/*  36: 75 */       OPTYPE optype = getOPTYPE(trainHeader.attribute(i).type());
/*  37: 76 */       DataField field = new DataField(name, optype);
/*  38: 77 */       if (trainHeader.attribute(i).isNominal()) {
/*  39: 78 */         for (int j = 0; j < trainHeader.attribute(i).numValues(); j++)
/*  40:    */         {
/*  41: 79 */           Value val = new Value(trainHeader.attribute(i).value(j));
/*  42: 80 */           field.addValue(val);
/*  43:    */         }
/*  44:    */       }
/*  45: 83 */       dictionary.addDataField(field);
/*  46:    */     }
/*  47: 86 */     pmml.setDataDictionary(dictionary);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static OPTYPE getOPTYPE(int wekaType)
/*  51:    */   {
/*  52: 97 */     switch (wekaType)
/*  53:    */     {
/*  54:    */     case 0: 
/*  55:    */     case 3: 
/*  56:100 */       return OPTYPE.CONTINUOUS;
/*  57:    */     }
/*  58:102 */     return OPTYPE.CATEGORICAL;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static String[] getNameAndValueFromUnsupervisedNominalToBinaryDerivedAttribute(Instances train, Attribute derived)
/*  62:    */   {
/*  63:119 */     String[] nameAndVal = new String[2];
/*  64:    */     
/*  65:    */ 
/*  66:    */ 
/*  67:123 */     boolean success = false;
/*  68:124 */     String derivedName = derived.name();
/*  69:125 */     int currentEqualsIndex = derivedName.indexOf('=');
/*  70:126 */     String leftSide = derivedName.substring(0, currentEqualsIndex);
/*  71:127 */     String rightSide = derivedName.substring(currentEqualsIndex + 1, derivedName.length());
/*  72:129 */     while (!success) {
/*  73:130 */       if (train.attribute(leftSide) != null)
/*  74:    */       {
/*  75:131 */         nameAndVal[0] = leftSide;
/*  76:132 */         nameAndVal[1] = rightSide;
/*  77:133 */         success = true;
/*  78:    */       }
/*  79:    */       else
/*  80:    */       {
/*  81:136 */         leftSide = leftSide + "=" + rightSide.substring(0, rightSide.indexOf('='));
/*  82:137 */         rightSide = rightSide.substring(rightSide.indexOf('=') + 1, rightSide.length());
/*  83:    */       }
/*  84:    */     }
/*  85:142 */     return nameAndVal;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.producer.AbstractPMMLProducerHelper
 * JD-Core Version:    0.7.0.1
 */
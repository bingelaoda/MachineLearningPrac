/*   1:    */ package weka.classifiers.pmml.producer;
/*   2:    */ 
/*   3:    */ import java.io.StringWriter;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import javax.xml.bind.JAXBContext;
/*   6:    */ import javax.xml.bind.JAXBException;
/*   7:    */ import javax.xml.bind.Marshaller;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.pmml.jaxbbindings.DATATYPE;
/*  11:    */ import weka.core.pmml.jaxbbindings.DerivedField;
/*  12:    */ import weka.core.pmml.jaxbbindings.FIELDUSAGETYPE;
/*  13:    */ import weka.core.pmml.jaxbbindings.LocalTransformations;
/*  14:    */ import weka.core.pmml.jaxbbindings.MININGFUNCTION;
/*  15:    */ import weka.core.pmml.jaxbbindings.MISSINGVALUETREATMENTMETHOD;
/*  16:    */ import weka.core.pmml.jaxbbindings.MiningField;
/*  17:    */ import weka.core.pmml.jaxbbindings.MiningSchema;
/*  18:    */ import weka.core.pmml.jaxbbindings.NormDiscrete;
/*  19:    */ import weka.core.pmml.jaxbbindings.NumericPredictor;
/*  20:    */ import weka.core.pmml.jaxbbindings.OPTYPE;
/*  21:    */ import weka.core.pmml.jaxbbindings.Output;
/*  22:    */ import weka.core.pmml.jaxbbindings.OutputField;
/*  23:    */ import weka.core.pmml.jaxbbindings.PMML;
/*  24:    */ import weka.core.pmml.jaxbbindings.REGRESSIONNORMALIZATIONMETHOD;
/*  25:    */ import weka.core.pmml.jaxbbindings.RegressionModel;
/*  26:    */ import weka.core.pmml.jaxbbindings.RegressionTable;
/*  27:    */ import weka.core.pmml.jaxbbindings.TransformationDictionary;
/*  28:    */ 
/*  29:    */ public class LogisticProducerHelper
/*  30:    */   extends AbstractPMMLProducerHelper
/*  31:    */ {
/*  32:    */   public static String toPMML(Instances train, Instances structureAfterFiltering, double[][] par, int numClasses)
/*  33:    */   {
/*  34: 75 */     PMML pmml = initPMML();
/*  35: 76 */     addDataDictionary(train, pmml);
/*  36:    */     
/*  37: 78 */     String currentAttrName = null;
/*  38: 79 */     TransformationDictionary transformDict = null;
/*  39: 80 */     LocalTransformations localTransforms = null;
/*  40: 81 */     MiningSchema schema = new MiningSchema();
/*  41: 82 */     for (int i = 0; i < structureAfterFiltering.numAttributes(); i++)
/*  42:    */     {
/*  43: 83 */       Attribute attr = structureAfterFiltering.attribute(i);
/*  44: 84 */       Attribute originalAttr = train.attribute(attr.name());
/*  45: 85 */       if (i == structureAfterFiltering.classIndex()) {
/*  46: 86 */         schema.addMiningFields(new MiningField(attr.name(), FIELDUSAGETYPE.PREDICTED));
/*  47:    */       }
/*  48: 90 */       if (originalAttr == null)
/*  49:    */       {
/*  50: 92 */         if (localTransforms == null) {
/*  51: 93 */           localTransforms = new LocalTransformations();
/*  52:    */         }
/*  53: 95 */         if (transformDict == null) {
/*  54: 96 */           transformDict = new TransformationDictionary();
/*  55:    */         }
/*  56: 98 */         String[] nameAndValue = getNameAndValueFromUnsupervisedNominalToBinaryDerivedAttribute(train, attr);
/*  57:101 */         if (!nameAndValue[0].equals(currentAttrName))
/*  58:    */         {
/*  59:102 */           currentAttrName = nameAndValue[0];
/*  60:103 */           if (i != structureAfterFiltering.classIndex())
/*  61:    */           {
/*  62:105 */             int mode = (int)train.meanOrMode(train.attribute(nameAndValue[0]));
/*  63:106 */             schema.addMiningFields(new MiningField(nameAndValue[0], FIELDUSAGETYPE.ACTIVE, MISSINGVALUETREATMENTMETHOD.AS_MODE, train.attribute(nameAndValue[0]).value(mode)));
/*  64:    */           }
/*  65:    */         }
/*  66:111 */         DerivedField derivedfield = new DerivedField(attr.name(), DATATYPE.DOUBLE, OPTYPE.CONTINUOUS);
/*  67:    */         
/*  68:113 */         NormDiscrete normDiscrete = new NormDiscrete(nameAndValue[0], nameAndValue[1]);
/*  69:    */         
/*  70:115 */         derivedfield.setNormDiscrete(normDiscrete);
/*  71:116 */         transformDict.addDerivedField(derivedfield);
/*  72:    */       }
/*  73:119 */       else if (i != structureAfterFiltering.classIndex())
/*  74:    */       {
/*  75:120 */         if (originalAttr.isNumeric())
/*  76:    */         {
/*  77:121 */           String mean = "" + train.meanOrMode(originalAttr);
/*  78:122 */           schema.addMiningFields(new MiningField(originalAttr.name(), FIELDUSAGETYPE.ACTIVE, MISSINGVALUETREATMENTMETHOD.AS_MEAN, mean));
/*  79:    */         }
/*  80:    */         else
/*  81:    */         {
/*  82:127 */           int mode = (int)train.meanOrMode(originalAttr);
/*  83:128 */           schema.addMiningFields(new MiningField(originalAttr.name(), FIELDUSAGETYPE.ACTIVE, MISSINGVALUETREATMENTMETHOD.AS_MODE, originalAttr.value(mode)));
/*  84:    */         }
/*  85:    */       }
/*  86:    */     }
/*  87:136 */     RegressionModel model = new RegressionModel();
/*  88:137 */     if (transformDict != null) {
/*  89:138 */       pmml.setTransformationDictionary(transformDict);
/*  90:    */     }
/*  91:140 */     model.addContent(schema);
/*  92:    */     
/*  93:142 */     model.setFunctionName(MININGFUNCTION.CLASSIFICATION);
/*  94:143 */     model.setAlgorithmName("logisticRegression");
/*  95:144 */     model.setModelType("logisticRegression");
/*  96:145 */     model.setNormalizationMethod(REGRESSIONNORMALIZATIONMETHOD.SOFTMAX);
/*  97:    */     
/*  98:147 */     Output output = new Output();
/*  99:148 */     Attribute classAttribute = structureAfterFiltering.classAttribute();
/* 100:149 */     for (int i = 0; i < classAttribute.numValues(); i++)
/* 101:    */     {
/* 102:150 */       OutputField outputField = new OutputField();
/* 103:151 */       outputField.setName(classAttribute.name());
/* 104:152 */       outputField.setValue(classAttribute.value(i));
/* 105:153 */       output.addOutputField(outputField);
/* 106:    */     }
/* 107:155 */     model.addContent(output);
/* 108:157 */     for (int i = 0; i < numClasses - 1; i++)
/* 109:    */     {
/* 110:158 */       RegressionTable table = new RegressionTable(structureAfterFiltering.classAttribute().value(i));
/* 111:    */       
/* 112:    */ 
/* 113:161 */       int j = 1;
/* 114:162 */       for (int k = 0; k < structureAfterFiltering.numAttributes(); k++) {
/* 115:163 */         if (k != structureAfterFiltering.classIndex())
/* 116:    */         {
/* 117:164 */           Attribute attr = structureAfterFiltering.attribute(k);
/* 118:165 */           table.addNumericPredictor(new NumericPredictor(attr.name(), BigInteger.valueOf(1L), par[j][i]));
/* 119:    */           
/* 120:167 */           j++;
/* 121:    */         }
/* 122:    */       }
/* 123:170 */       table.setIntercept(par[0][i]);
/* 124:171 */       model.addContent(table);
/* 125:    */     }
/* 126:174 */     pmml.addAssociationModelOrBaselineModelOrClusteringModes(model);
/* 127:    */     try
/* 128:    */     {
/* 129:177 */       StringWriter sw = new StringWriter();
/* 130:178 */       JAXBContext jc = JAXBContext.newInstance(new Class[] { PMML.class });
/* 131:179 */       Marshaller marshaller = jc.createMarshaller();
/* 132:180 */       marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
/* 133:181 */       marshaller.marshal(pmml, sw);
/* 134:182 */       return sw.toString();
/* 135:    */     }
/* 136:    */     catch (JAXBException e)
/* 137:    */     {
/* 138:184 */       e.printStackTrace();
/* 139:    */     }
/* 140:186 */     return "";
/* 141:    */   }
/* 142:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.producer.LogisticProducerHelper
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "output", "modelStats", "targets", "localTransformations", "resultField", "regressionTable"})
/*  14:    */ @XmlRootElement(name="Regression")
/*  15:    */ public class Regression
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Output", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Output output;
/*  21:    */   @XmlElement(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected ModelStats modelStats;
/*  23:    */   @XmlElement(name="Targets", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected Targets targets;
/*  25:    */   @XmlElement(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected LocalTransformations localTransformations;
/*  27:    */   @XmlElement(name="ResultField", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  28:    */   protected List<ResultField> resultField;
/*  29:    */   @XmlElement(name="RegressionTable", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  30:    */   protected List<RegressionTable> regressionTable;
/*  31:    */   @XmlAttribute
/*  32:    */   protected String algorithmName;
/*  33:    */   @XmlAttribute(required=true)
/*  34:    */   protected MININGFUNCTION functionName;
/*  35:    */   @XmlAttribute
/*  36:    */   protected String modelName;
/*  37:    */   @XmlAttribute
/*  38:    */   protected REGRESSIONNORMALIZATIONMETHOD normalizationMethod;
/*  39:    */   
/*  40:    */   public List<Extension> getExtension()
/*  41:    */   {
/*  42:111 */     if (this.extension == null) {
/*  43:112 */       this.extension = new ArrayList();
/*  44:    */     }
/*  45:114 */     return this.extension;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Output getOutput()
/*  49:    */   {
/*  50:126 */     return this.output;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setOutput(Output value)
/*  54:    */   {
/*  55:138 */     this.output = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public ModelStats getModelStats()
/*  59:    */   {
/*  60:150 */     return this.modelStats;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setModelStats(ModelStats value)
/*  64:    */   {
/*  65:162 */     this.modelStats = value;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Targets getTargets()
/*  69:    */   {
/*  70:174 */     return this.targets;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setTargets(Targets value)
/*  74:    */   {
/*  75:186 */     this.targets = value;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public LocalTransformations getLocalTransformations()
/*  79:    */   {
/*  80:198 */     return this.localTransformations;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setLocalTransformations(LocalTransformations value)
/*  84:    */   {
/*  85:210 */     this.localTransformations = value;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public List<ResultField> getResultField()
/*  89:    */   {
/*  90:236 */     if (this.resultField == null) {
/*  91:237 */       this.resultField = new ArrayList();
/*  92:    */     }
/*  93:239 */     return this.resultField;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public List<RegressionTable> getRegressionTable()
/*  97:    */   {
/*  98:265 */     if (this.regressionTable == null) {
/*  99:266 */       this.regressionTable = new ArrayList();
/* 100:    */     }
/* 101:268 */     return this.regressionTable;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String getAlgorithmName()
/* 105:    */   {
/* 106:280 */     return this.algorithmName;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setAlgorithmName(String value)
/* 110:    */   {
/* 111:292 */     this.algorithmName = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public MININGFUNCTION getFunctionName()
/* 115:    */   {
/* 116:304 */     return this.functionName;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setFunctionName(MININGFUNCTION value)
/* 120:    */   {
/* 121:316 */     this.functionName = value;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String getModelName()
/* 125:    */   {
/* 126:328 */     return this.modelName;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setModelName(String value)
/* 130:    */   {
/* 131:340 */     this.modelName = value;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public REGRESSIONNORMALIZATIONMETHOD getNormalizationMethod()
/* 135:    */   {
/* 136:352 */     if (this.normalizationMethod == null) {
/* 137:353 */       return REGRESSIONNORMALIZATIONMETHOD.NONE;
/* 138:    */     }
/* 139:355 */     return this.normalizationMethod;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setNormalizationMethod(REGRESSIONNORMALIZATIONMETHOD value)
/* 143:    */   {
/* 144:368 */     this.normalizationMethod = value;
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Regression
 * JD-Core Version:    0.7.0.1
 */
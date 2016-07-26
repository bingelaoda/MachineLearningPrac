/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension", "output", "modelStats", "targets", "localTransformations", "resultField", "node"})
/*  15:    */ @XmlRootElement(name="DecisionTree")
/*  16:    */ public class DecisionTree
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="Output", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected Output output;
/*  22:    */   @XmlElement(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected ModelStats modelStats;
/*  24:    */   @XmlElement(name="Targets", namespace="http://www.dmg.org/PMML-4_1")
/*  25:    */   protected Targets targets;
/*  26:    */   @XmlElement(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1")
/*  27:    */   protected LocalTransformations localTransformations;
/*  28:    */   @XmlElement(name="ResultField", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  29:    */   protected List<ResultField> resultField;
/*  30:    */   @XmlElement(name="Node", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  31:    */   protected Node node;
/*  32:    */   @XmlAttribute
/*  33:    */   protected String algorithmName;
/*  34:    */   @XmlAttribute(required=true)
/*  35:    */   protected MININGFUNCTION functionName;
/*  36:    */   @XmlAttribute
/*  37:    */   protected BigDecimal missingValuePenalty;
/*  38:    */   @XmlAttribute
/*  39:    */   protected MISSINGVALUESTRATEGY missingValueStrategy;
/*  40:    */   @XmlAttribute
/*  41:    */   protected String modelName;
/*  42:    */   @XmlAttribute
/*  43:    */   protected NOTRUECHILDSTRATEGY noTrueChildStrategy;
/*  44:    */   @XmlAttribute
/*  45:    */   protected String splitCharacteristic;
/*  46:    */   
/*  47:    */   public List<Extension> getExtension()
/*  48:    */   {
/*  49:128 */     if (this.extension == null) {
/*  50:129 */       this.extension = new ArrayList();
/*  51:    */     }
/*  52:131 */     return this.extension;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public Output getOutput()
/*  56:    */   {
/*  57:143 */     return this.output;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setOutput(Output value)
/*  61:    */   {
/*  62:155 */     this.output = value;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public ModelStats getModelStats()
/*  66:    */   {
/*  67:167 */     return this.modelStats;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setModelStats(ModelStats value)
/*  71:    */   {
/*  72:179 */     this.modelStats = value;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Targets getTargets()
/*  76:    */   {
/*  77:191 */     return this.targets;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setTargets(Targets value)
/*  81:    */   {
/*  82:203 */     this.targets = value;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public LocalTransformations getLocalTransformations()
/*  86:    */   {
/*  87:215 */     return this.localTransformations;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setLocalTransformations(LocalTransformations value)
/*  91:    */   {
/*  92:227 */     this.localTransformations = value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public List<ResultField> getResultField()
/*  96:    */   {
/*  97:253 */     if (this.resultField == null) {
/*  98:254 */       this.resultField = new ArrayList();
/*  99:    */     }
/* 100:256 */     return this.resultField;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Node getNode()
/* 104:    */   {
/* 105:268 */     return this.node;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setNode(Node value)
/* 109:    */   {
/* 110:280 */     this.node = value;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public String getAlgorithmName()
/* 114:    */   {
/* 115:292 */     return this.algorithmName;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setAlgorithmName(String value)
/* 119:    */   {
/* 120:304 */     this.algorithmName = value;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public MININGFUNCTION getFunctionName()
/* 124:    */   {
/* 125:316 */     return this.functionName;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setFunctionName(MININGFUNCTION value)
/* 129:    */   {
/* 130:328 */     this.functionName = value;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public BigDecimal getMissingValuePenalty()
/* 134:    */   {
/* 135:340 */     if (this.missingValuePenalty == null) {
/* 136:341 */       return new BigDecimal("1.0");
/* 137:    */     }
/* 138:343 */     return this.missingValuePenalty;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void setMissingValuePenalty(BigDecimal value)
/* 142:    */   {
/* 143:356 */     this.missingValuePenalty = value;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public MISSINGVALUESTRATEGY getMissingValueStrategy()
/* 147:    */   {
/* 148:368 */     if (this.missingValueStrategy == null) {
/* 149:369 */       return MISSINGVALUESTRATEGY.NONE;
/* 150:    */     }
/* 151:371 */     return this.missingValueStrategy;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void setMissingValueStrategy(MISSINGVALUESTRATEGY value)
/* 155:    */   {
/* 156:384 */     this.missingValueStrategy = value;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String getModelName()
/* 160:    */   {
/* 161:396 */     return this.modelName;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setModelName(String value)
/* 165:    */   {
/* 166:408 */     this.modelName = value;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public NOTRUECHILDSTRATEGY getNoTrueChildStrategy()
/* 170:    */   {
/* 171:420 */     if (this.noTrueChildStrategy == null) {
/* 172:421 */       return NOTRUECHILDSTRATEGY.RETURN_NULL_PREDICTION;
/* 173:    */     }
/* 174:423 */     return this.noTrueChildStrategy;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void setNoTrueChildStrategy(NOTRUECHILDSTRATEGY value)
/* 178:    */   {
/* 179:436 */     this.noTrueChildStrategy = value;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String getSplitCharacteristic()
/* 183:    */   {
/* 184:448 */     if (this.splitCharacteristic == null) {
/* 185:449 */       return "multiSplit";
/* 186:    */     }
/* 187:451 */     return this.splitCharacteristic;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setSplitCharacteristic(String value)
/* 191:    */   {
/* 192:464 */     this.splitCharacteristic = value;
/* 193:    */   }
/* 194:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.DecisionTree
 * JD-Core Version:    0.7.0.1
 */
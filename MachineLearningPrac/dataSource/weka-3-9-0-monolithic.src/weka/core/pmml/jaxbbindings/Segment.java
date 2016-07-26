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
/*  13:    */ @XmlType(name="", propOrder={"extension", "simplePredicate", "compoundPredicate", "simpleSetPredicate", "_true", "_false", "associationModel", "baselineModel", "clusteringModel", "generalRegressionModel", "miningModel", "naiveBayesModel", "nearestNeighborModel", "neuralNetwork", "regressionModel", "ruleSetModel", "sequenceModel", "scorecard", "supportVectorMachineModel", "textModel", "timeSeriesModel", "treeModel"})
/*  14:    */ @XmlRootElement(name="Segment")
/*  15:    */ public class Segment
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="SimplePredicate", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected SimplePredicate simplePredicate;
/*  21:    */   @XmlElement(name="CompoundPredicate", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected CompoundPredicate compoundPredicate;
/*  23:    */   @XmlElement(name="SimpleSetPredicate", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected SimpleSetPredicate simpleSetPredicate;
/*  25:    */   @XmlElement(name="True", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected True _true;
/*  27:    */   @XmlElement(name="False", namespace="http://www.dmg.org/PMML-4_1")
/*  28:    */   protected False _false;
/*  29:    */   @XmlElement(name="AssociationModel", namespace="http://www.dmg.org/PMML-4_1")
/*  30:    */   protected AssociationModel associationModel;
/*  31:    */   @XmlElement(name="BaselineModel", namespace="http://www.dmg.org/PMML-4_1")
/*  32:    */   protected BaselineModel baselineModel;
/*  33:    */   @XmlElement(name="ClusteringModel", namespace="http://www.dmg.org/PMML-4_1")
/*  34:    */   protected ClusteringModel clusteringModel;
/*  35:    */   @XmlElement(name="GeneralRegressionModel", namespace="http://www.dmg.org/PMML-4_1")
/*  36:    */   protected GeneralRegressionModel generalRegressionModel;
/*  37:    */   @XmlElement(name="MiningModel", namespace="http://www.dmg.org/PMML-4_1")
/*  38:    */   protected MiningModel miningModel;
/*  39:    */   @XmlElement(name="NaiveBayesModel", namespace="http://www.dmg.org/PMML-4_1")
/*  40:    */   protected NaiveBayesModel naiveBayesModel;
/*  41:    */   @XmlElement(name="NearestNeighborModel", namespace="http://www.dmg.org/PMML-4_1")
/*  42:    */   protected NearestNeighborModel nearestNeighborModel;
/*  43:    */   @XmlElement(name="NeuralNetwork", namespace="http://www.dmg.org/PMML-4_1")
/*  44:    */   protected NeuralNetwork neuralNetwork;
/*  45:    */   @XmlElement(name="RegressionModel", namespace="http://www.dmg.org/PMML-4_1")
/*  46:    */   protected RegressionModel regressionModel;
/*  47:    */   @XmlElement(name="RuleSetModel", namespace="http://www.dmg.org/PMML-4_1")
/*  48:    */   protected RuleSetModel ruleSetModel;
/*  49:    */   @XmlElement(name="SequenceModel", namespace="http://www.dmg.org/PMML-4_1")
/*  50:    */   protected SequenceModel sequenceModel;
/*  51:    */   @XmlElement(name="Scorecard", namespace="http://www.dmg.org/PMML-4_1")
/*  52:    */   protected Scorecard scorecard;
/*  53:    */   @XmlElement(name="SupportVectorMachineModel", namespace="http://www.dmg.org/PMML-4_1")
/*  54:    */   protected SupportVectorMachineModel supportVectorMachineModel;
/*  55:    */   @XmlElement(name="TextModel", namespace="http://www.dmg.org/PMML-4_1")
/*  56:    */   protected TextModel textModel;
/*  57:    */   @XmlElement(name="TimeSeriesModel", namespace="http://www.dmg.org/PMML-4_1")
/*  58:    */   protected TimeSeriesModel timeSeriesModel;
/*  59:    */   @XmlElement(name="TreeModel", namespace="http://www.dmg.org/PMML-4_1")
/*  60:    */   protected TreeModel treeModel;
/*  61:    */   @XmlAttribute
/*  62:    */   protected String id;
/*  63:    */   @XmlAttribute
/*  64:    */   protected Double weight;
/*  65:    */   
/*  66:    */   public List<Extension> getExtension()
/*  67:    */   {
/*  68:146 */     if (this.extension == null) {
/*  69:147 */       this.extension = new ArrayList();
/*  70:    */     }
/*  71:149 */     return this.extension;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public SimplePredicate getSimplePredicate()
/*  75:    */   {
/*  76:161 */     return this.simplePredicate;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setSimplePredicate(SimplePredicate value)
/*  80:    */   {
/*  81:173 */     this.simplePredicate = value;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public CompoundPredicate getCompoundPredicate()
/*  85:    */   {
/*  86:185 */     return this.compoundPredicate;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setCompoundPredicate(CompoundPredicate value)
/*  90:    */   {
/*  91:197 */     this.compoundPredicate = value;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public SimpleSetPredicate getSimpleSetPredicate()
/*  95:    */   {
/*  96:209 */     return this.simpleSetPredicate;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setSimpleSetPredicate(SimpleSetPredicate value)
/* 100:    */   {
/* 101:221 */     this.simpleSetPredicate = value;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public True getTrue()
/* 105:    */   {
/* 106:233 */     return this._true;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setTrue(True value)
/* 110:    */   {
/* 111:245 */     this._true = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public False getFalse()
/* 115:    */   {
/* 116:257 */     return this._false;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setFalse(False value)
/* 120:    */   {
/* 121:269 */     this._false = value;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public AssociationModel getAssociationModel()
/* 125:    */   {
/* 126:281 */     return this.associationModel;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setAssociationModel(AssociationModel value)
/* 130:    */   {
/* 131:293 */     this.associationModel = value;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public BaselineModel getBaselineModel()
/* 135:    */   {
/* 136:305 */     return this.baselineModel;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setBaselineModel(BaselineModel value)
/* 140:    */   {
/* 141:317 */     this.baselineModel = value;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public ClusteringModel getClusteringModel()
/* 145:    */   {
/* 146:329 */     return this.clusteringModel;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setClusteringModel(ClusteringModel value)
/* 150:    */   {
/* 151:341 */     this.clusteringModel = value;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public GeneralRegressionModel getGeneralRegressionModel()
/* 155:    */   {
/* 156:353 */     return this.generalRegressionModel;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setGeneralRegressionModel(GeneralRegressionModel value)
/* 160:    */   {
/* 161:365 */     this.generalRegressionModel = value;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public MiningModel getMiningModel()
/* 165:    */   {
/* 166:377 */     return this.miningModel;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setMiningModel(MiningModel value)
/* 170:    */   {
/* 171:389 */     this.miningModel = value;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public NaiveBayesModel getNaiveBayesModel()
/* 175:    */   {
/* 176:401 */     return this.naiveBayesModel;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setNaiveBayesModel(NaiveBayesModel value)
/* 180:    */   {
/* 181:413 */     this.naiveBayesModel = value;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public NearestNeighborModel getNearestNeighborModel()
/* 185:    */   {
/* 186:425 */     return this.nearestNeighborModel;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setNearestNeighborModel(NearestNeighborModel value)
/* 190:    */   {
/* 191:437 */     this.nearestNeighborModel = value;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public NeuralNetwork getNeuralNetwork()
/* 195:    */   {
/* 196:449 */     return this.neuralNetwork;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setNeuralNetwork(NeuralNetwork value)
/* 200:    */   {
/* 201:461 */     this.neuralNetwork = value;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public RegressionModel getRegressionModel()
/* 205:    */   {
/* 206:473 */     return this.regressionModel;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public void setRegressionModel(RegressionModel value)
/* 210:    */   {
/* 211:485 */     this.regressionModel = value;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public RuleSetModel getRuleSetModel()
/* 215:    */   {
/* 216:497 */     return this.ruleSetModel;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public void setRuleSetModel(RuleSetModel value)
/* 220:    */   {
/* 221:509 */     this.ruleSetModel = value;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public SequenceModel getSequenceModel()
/* 225:    */   {
/* 226:521 */     return this.sequenceModel;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setSequenceModel(SequenceModel value)
/* 230:    */   {
/* 231:533 */     this.sequenceModel = value;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public Scorecard getScorecard()
/* 235:    */   {
/* 236:545 */     return this.scorecard;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void setScorecard(Scorecard value)
/* 240:    */   {
/* 241:557 */     this.scorecard = value;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public SupportVectorMachineModel getSupportVectorMachineModel()
/* 245:    */   {
/* 246:569 */     return this.supportVectorMachineModel;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public void setSupportVectorMachineModel(SupportVectorMachineModel value)
/* 250:    */   {
/* 251:581 */     this.supportVectorMachineModel = value;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public TextModel getTextModel()
/* 255:    */   {
/* 256:593 */     return this.textModel;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void setTextModel(TextModel value)
/* 260:    */   {
/* 261:605 */     this.textModel = value;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public TimeSeriesModel getTimeSeriesModel()
/* 265:    */   {
/* 266:617 */     return this.timeSeriesModel;
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void setTimeSeriesModel(TimeSeriesModel value)
/* 270:    */   {
/* 271:629 */     this.timeSeriesModel = value;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public TreeModel getTreeModel()
/* 275:    */   {
/* 276:641 */     return this.treeModel;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void setTreeModel(TreeModel value)
/* 280:    */   {
/* 281:653 */     this.treeModel = value;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public String getId()
/* 285:    */   {
/* 286:665 */     return this.id;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void setId(String value)
/* 290:    */   {
/* 291:677 */     this.id = value;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public double getWeight()
/* 295:    */   {
/* 296:689 */     if (this.weight == null) {
/* 297:690 */       return 1.0D;
/* 298:    */     }
/* 299:692 */     return this.weight.doubleValue();
/* 300:    */   }
/* 301:    */   
/* 302:    */   public void setWeight(Double value)
/* 303:    */   {
/* 304:705 */     this.weight = value;
/* 305:    */   }
/* 306:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Segment
 * JD-Core Version:    0.7.0.1
 */
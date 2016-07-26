/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.associations.AbstractAssociator;
/*   7:    */ import weka.associations.AssociationRules;
/*   8:    */ import weka.associations.AssociationRulesProducer;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Drawable;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.gui.ProgrammaticProperty;
/*  14:    */ import weka.knowledgeflow.Data;
/*  15:    */ import weka.knowledgeflow.StepManager;
/*  16:    */ 
/*  17:    */ @KFStep(name="Associator", category="Associations", toolTipText="Weka associator wrapper", iconPath="")
/*  18:    */ public class Associator
/*  19:    */   extends WekaAlgorithmWrapper
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -589410455393151511L;
/*  22:    */   protected weka.associations.Associator m_associatorTemplate;
/*  23:    */   
/*  24:    */   public Class getWrappedAlgorithmClass()
/*  25:    */   {
/*  26: 63 */     return weka.associations.Associator.class;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void setWrappedAlgorithm(Object algo)
/*  30:    */   {
/*  31: 73 */     super.setWrappedAlgorithm(algo);
/*  32: 74 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultAssociator.gif";
/*  33:    */   }
/*  34:    */   
/*  35:    */   @ProgrammaticProperty
/*  36:    */   public void setAssociator(weka.associations.Associator associator)
/*  37:    */   {
/*  38: 85 */     setWrappedAlgorithm(associator);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public weka.associations.Associator getAssociator()
/*  42:    */   {
/*  43: 95 */     return (weka.associations.Associator)getWrappedAlgorithm();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void stepInit()
/*  47:    */     throws WekaException
/*  48:    */   {
/*  49:105 */     if (!(getWrappedAlgorithm() instanceof weka.associations.Associator)) {
/*  50:106 */       throw new WekaException("Wrapped algorithm is not an instance of a weka.associations.Associator!");
/*  51:    */     }
/*  52:    */     try
/*  53:    */     {
/*  54:111 */       this.m_associatorTemplate = AbstractAssociator.makeCopy(getAssociator());
/*  55:    */     }
/*  56:    */     catch (Exception ex)
/*  57:    */     {
/*  58:114 */       throw new WekaException(ex);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void processIncoming(Data data)
/*  63:    */     throws WekaException
/*  64:    */   {
/*  65:127 */     Instances insts = (Instances)data.getPrimaryPayload();
/*  66:128 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num");
/*  67:129 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num");
/*  68:    */     try
/*  69:    */     {
/*  70:133 */       if (!isStopRequested())
/*  71:    */       {
/*  72:134 */         getStepManager().processing();
/*  73:135 */         weka.associations.Associator associator = AbstractAssociator.makeCopy(this.m_associatorTemplate);
/*  74:    */         
/*  75:    */ 
/*  76:138 */         associator.buildAssociations(insts);
/*  77:139 */         outputAssociatorData(associator, setNum, maxSetNum);
/*  78:140 */         outputTextData(associator, insts, setNum);
/*  79:141 */         outputGraphData(associator, insts, setNum);
/*  80:143 */         if (!isStopRequested()) {
/*  81:144 */           getStepManager().finished();
/*  82:    */         } else {
/*  83:146 */           getStepManager().interrupted();
/*  84:    */         }
/*  85:    */       }
/*  86:    */     }
/*  87:    */     catch (Exception ex)
/*  88:    */     {
/*  89:150 */       throw new WekaException(ex);
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   protected void outputAssociatorData(weka.associations.Associator associator, Integer setNum, Integer maxSetNum)
/*  94:    */     throws WekaException
/*  95:    */   {
/*  96:164 */     if (getStepManager().numOutgoingConnectionsOfType("batchAssociator") == 0) {
/*  97:166 */       return;
/*  98:    */     }
/*  99:169 */     Data out = new Data("batchAssociator", associator);
/* 100:170 */     if ((setNum != null) && (maxSetNum != null))
/* 101:    */     {
/* 102:171 */       out.setPayloadElement("aux_set_num", setNum);
/* 103:172 */       out.setPayloadElement("aux_max_set_num", maxSetNum);
/* 104:    */     }
/* 105:175 */     if ((associator instanceof AssociationRulesProducer))
/* 106:    */     {
/* 107:176 */       AssociationRules rules = ((AssociationRulesProducer)associator).getAssociationRules();
/* 108:    */       
/* 109:178 */       out.setPayloadElement("batch_association_rules", rules);
/* 110:    */     }
/* 111:182 */     getStepManager().outputData(new Data[] { out });
/* 112:    */   }
/* 113:    */   
/* 114:    */   protected void outputTextData(weka.associations.Associator associator, Instances train, Integer setNum)
/* 115:    */     throws WekaException
/* 116:    */   {
/* 117:195 */     if (getStepManager().numOutgoingConnectionsOfType("text") == 0) {
/* 118:197 */       return;
/* 119:    */     }
/* 120:200 */     String modelString = associator.toString();
/* 121:201 */     String titleString = associator.getClass().getName();
/* 122:    */     
/* 123:203 */     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 124:    */     
/* 125:205 */     modelString = "=== Associator model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + train.relationName() + "\n\n" + modelString;
/* 126:    */     
/* 127:207 */     titleString = "Model: " + titleString;
/* 128:    */     
/* 129:209 */     Data textData = new Data("text", modelString);
/* 130:210 */     textData.setPayloadElement("aux_textTitle", titleString);
/* 131:213 */     if (setNum != null) {
/* 132:214 */       textData.setPayloadElement("aux_set_num", setNum);
/* 133:    */     }
/* 134:217 */     getStepManager().outputData(new Data[] { textData });
/* 135:    */   }
/* 136:    */   
/* 137:    */   protected void outputGraphData(weka.associations.Associator associator, Instances insts, Integer setNum)
/* 138:    */     throws WekaException
/* 139:    */   {
/* 140:223 */     if ((!(associator instanceof Drawable)) || (getStepManager().numOutgoingConnectionsOfType("graph") == 0)) {
/* 141:225 */       return;
/* 142:    */     }
/* 143:    */     try
/* 144:    */     {
/* 145:229 */       String graphString = ((Drawable)associator).graph();
/* 146:230 */       int graphType = ((Drawable)associator).graphType();
/* 147:231 */       String grphTitle = associator.getClass().getCanonicalName();
/* 148:232 */       grphTitle = grphTitle.substring(grphTitle.lastIndexOf('.') + 1, grphTitle.length());
/* 149:    */       
/* 150:234 */       String set = setNum != null ? "Set " + setNum : "";
/* 151:235 */       grphTitle = set + " (" + insts.relationName() + ") " + grphTitle;
/* 152:236 */       Data graphData = new Data("graph");
/* 153:237 */       graphData.setPayloadElement("graph", graphString);
/* 154:238 */       graphData.setPayloadElement("graph_title", grphTitle);
/* 155:    */       
/* 156:240 */       graphData.setPayloadElement("graph_type", Integer.valueOf(graphType));
/* 157:    */       
/* 158:242 */       getStepManager().outputData(new Data[] { graphData });
/* 159:    */     }
/* 160:    */     catch (Exception ex)
/* 161:    */     {
/* 162:244 */       throw new WekaException(ex);
/* 163:    */     }
/* 164:    */   }
/* 165:    */   
/* 166:    */   public List<String> getIncomingConnectionTypes()
/* 167:    */   {
/* 168:256 */     List<String> result = new ArrayList();
/* 169:258 */     if (getStepManager().numIncomingConnections() == 0) {
/* 170:259 */       result.addAll(Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" }));
/* 171:    */     }
/* 172:262 */     return result;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public List<String> getOutgoingConnectionTypes()
/* 176:    */   {
/* 177:272 */     List<String> result = new ArrayList();
/* 178:274 */     if (getStepManager().numIncomingConnections() > 0)
/* 179:    */     {
/* 180:275 */       result.add("batchAssociator");
/* 181:276 */       result.add("text");
/* 182:    */     }
/* 183:279 */     result.add("info");
/* 184:    */     
/* 185:281 */     return result;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 189:    */     throws WekaException
/* 190:    */   {
/* 191:298 */     if (connectionName.equals("text"))
/* 192:    */     {
/* 193:299 */       ArrayList<Attribute> attInfo = new ArrayList();
/* 194:300 */       attInfo.add(new Attribute("Title", (ArrayList)null));
/* 195:301 */       attInfo.add(new Attribute("Text", (ArrayList)null));
/* 196:302 */       return new Instances("TextEvent", attInfo, 0);
/* 197:    */     }
/* 198:303 */     if ((connectionName.equals("batchAssociator")) && 
/* 199:304 */       ((this.m_associatorTemplate instanceof AssociationRulesProducer)))
/* 200:    */     {
/* 201:311 */       String[] metricNames = ((AssociationRulesProducer)this.m_associatorTemplate).getRuleMetricNames();
/* 202:    */       
/* 203:313 */       ArrayList<Attribute> attInfo = new ArrayList();
/* 204:314 */       attInfo.add(new Attribute("LHS", (ArrayList)null));
/* 205:315 */       attInfo.add(new Attribute("RHS", (ArrayList)null));
/* 206:316 */       attInfo.add(new Attribute("Support"));
/* 207:317 */       for (String metricName : metricNames) {
/* 208:318 */         attInfo.add(new Attribute(metricName));
/* 209:    */       }
/* 210:320 */       return new Instances("batch_association_rules", attInfo, 0);
/* 211:    */     }
/* 212:325 */     return null;
/* 213:    */   }
/* 214:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Associator
 * JD-Core Version:    0.7.0.1
 */
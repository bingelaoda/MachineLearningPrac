/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import weka.core.Environment;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.OptionMetadata;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.filters.unsupervised.attribute.Add;
/*  14:    */ import weka.gui.ProgrammaticProperty;
/*  15:    */ import weka.gui.beans.SubstringLabelerRules;
/*  16:    */ import weka.knowledgeflow.Data;
/*  17:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  18:    */ import weka.knowledgeflow.StepManager;
/*  19:    */ import weka.knowledgeflow.StepManagerImpl;
/*  20:    */ 
/*  21:    */ @KFStep(name="SubstringLabeler", category="Tools", toolTipText="Label instances according to substring matches in String attributes The user can specify the attributes to match against and associated label to create by defining 'match' rules. A new attribute is appended to the data to contain the label. Rules are applied in order when processing instances, and the label associated with the first matching rule is applied. Non-matching instances can either receive a missing value for the label attribute or be 'consumed' (i.e. they are not output).", iconPath="weka/gui/knowledgeflow/icons/DefaultFilter.gif")
/*  22:    */ public class SubstringLabeler
/*  23:    */   extends BaseStep
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 1409175779108600014L;
/*  26: 68 */   protected String m_matchDetails = "";
/*  27:    */   protected transient SubstringLabelerRules m_matches;
/*  28:    */   protected boolean m_nominalBinary;
/*  29:    */   protected boolean m_consumeNonMatchingInstances;
/*  30:    */   protected Add m_addFilter;
/*  31: 89 */   protected String m_attName = "Match";
/*  32:    */   protected boolean m_isReset;
/*  33:    */   protected Data m_streamingData;
/*  34:    */   protected boolean m_streaming;
/*  35:    */   
/*  36:    */   @ProgrammaticProperty
/*  37:    */   public void setMatchDetails(String details)
/*  38:    */   {
/*  39:107 */     this.m_matchDetails = details;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getMatchDetails()
/*  43:    */   {
/*  44:116 */     return this.m_matchDetails;
/*  45:    */   }
/*  46:    */   
/*  47:    */   @OptionMetadata(displayName="Make a nominal binary attribute", description="Whether to encode the new attribute as nominal when it is binary (as opposed to numeric)", displayOrder=1)
/*  48:    */   public void setNominalBinary(boolean nom)
/*  49:    */   {
/*  50:129 */     this.m_nominalBinary = nom;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean getNominalBinary()
/*  54:    */   {
/*  55:139 */     return this.m_nominalBinary;
/*  56:    */   }
/*  57:    */   
/*  58:    */   @OptionMetadata(displayName="Consume non matching instances", description="Instances that do not match any rules will be consumed, rather than being output with a missing value for the new attribute", displayOrder=2)
/*  59:    */   public void setConsumeNonMatching(boolean consume)
/*  60:    */   {
/*  61:155 */     this.m_consumeNonMatchingInstances = consume;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean getConsumeNonMatching()
/*  65:    */   {
/*  66:166 */     return this.m_consumeNonMatchingInstances;
/*  67:    */   }
/*  68:    */   
/*  69:    */   @OptionMetadata(displayName="Name of the new attribute", description="Name to give the new attribute", displayOrder=0)
/*  70:    */   public void setMatchAttributeName(String name)
/*  71:    */   {
/*  72:177 */     this.m_attName = name;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String getMatchAttributeName()
/*  76:    */   {
/*  77:186 */     return this.m_attName;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void stepInit()
/*  81:    */     throws WekaException
/*  82:    */   {
/*  83:196 */     this.m_isReset = true;
/*  84:197 */     this.m_streamingData = new Data("instance");
/*  85:198 */     this.m_streaming = false;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public List<String> getIncomingConnectionTypes()
/*  89:    */   {
/*  90:212 */     return getStepManager().numIncomingConnections() == 0 ? Arrays.asList(new String[] { "instance", "dataSet", "trainingSet", "testSet" }) : null;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public List<String> getOutgoingConnectionTypes()
/*  94:    */   {
/*  95:228 */     List<String> result = new ArrayList();
/*  96:230 */     for (Map.Entry<String, List<StepManager>> e : getStepManager().getIncomingConnections().entrySet()) {
/*  97:232 */       if (((List)e.getValue()).size() > 0) {
/*  98:233 */         result.add(e.getKey());
/*  99:    */       }
/* 100:    */     }
/* 101:237 */     return result;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void processIncoming(Data data)
/* 105:    */     throws WekaException
/* 106:    */   {
/* 107:250 */     if (this.m_isReset)
/* 108:    */     {
/* 109:    */       Instances structure;
/* 110:251 */       if (getStepManager().numIncomingConnectionsOfType("instance") > 0)
/* 111:    */       {
/* 112:253 */         Instance inst = (Instance)data.getPrimaryPayload();
/* 113:254 */         Instances structure = inst.dataset();
/* 114:255 */         this.m_streaming = true;
/* 115:    */       }
/* 116:    */       else
/* 117:    */       {
/* 118:257 */         structure = (Instances)data.getPrimaryPayload();
/* 119:258 */         structure = new Instances(structure, 0);
/* 120:    */       }
/* 121:    */       try
/* 122:    */       {
/* 123:261 */         this.m_matches = new SubstringLabelerRules(this.m_matchDetails, this.m_attName, getConsumeNonMatching(), getNominalBinary(), structure, ((StepManagerImpl)getStepManager()).stepStatusMessagePrefix(), getStepManager().getLog(), getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 124:    */       }
/* 125:    */       catch (Exception ex)
/* 126:    */       {
/* 127:268 */         throw new WekaException(ex);
/* 128:    */       }
/* 129:271 */       this.m_isReset = false;
/* 130:    */     }
/* 131:274 */     if (this.m_streaming)
/* 132:    */     {
/* 133:275 */       if (getStepManager().isStreamFinished(data))
/* 134:    */       {
/* 135:276 */         this.m_streamingData.clearPayload();
/* 136:277 */         getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/* 137:278 */         return;
/* 138:    */       }
/* 139:280 */       processStreaming(data);
/* 140:    */     }
/* 141:    */     else
/* 142:    */     {
/* 143:283 */       processBatch(data);
/* 144:    */     }
/* 145:286 */     if (isStopRequested()) {
/* 146:287 */       getStepManager().interrupted();
/* 147:288 */     } else if (!this.m_streaming) {
/* 148:289 */       getStepManager().finished();
/* 149:    */     }
/* 150:    */   }
/* 151:    */   
/* 152:    */   protected void processStreaming(Data data)
/* 153:    */     throws WekaException
/* 154:    */   {
/* 155:300 */     getStepManager().throughputUpdateStart();
/* 156:301 */     Instance toProcess = (Instance)data.getPrimaryPayload();
/* 157:    */     try
/* 158:    */     {
/* 159:303 */       Instance result = this.m_matches.makeOutputInstance(toProcess, false);
/* 160:304 */       if (result != null)
/* 161:    */       {
/* 162:305 */         this.m_streamingData.setPayloadElement("instance", result);
/* 163:306 */         getStepManager().outputData(new Data[] { this.m_streamingData });
/* 164:307 */         getStepManager().throughputUpdateEnd();
/* 165:    */       }
/* 166:    */     }
/* 167:    */     catch (Exception ex)
/* 168:    */     {
/* 169:310 */       throw new WekaException(ex);
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   protected void processBatch(Data data)
/* 174:    */     throws WekaException
/* 175:    */   {
/* 176:321 */     if (isStopRequested()) {
/* 177:322 */       return;
/* 178:    */     }
/* 179:325 */     Instances batch = (Instances)data.getPrimaryPayload();
/* 180:326 */     for (int i = 0; i < batch.numInstances(); i++)
/* 181:    */     {
/* 182:327 */       Instance current = batch.instance(i);
/* 183:328 */       Instance result = null;
/* 184:    */       try
/* 185:    */       {
/* 186:330 */         result = this.m_matches.makeOutputInstance(current, true);
/* 187:    */       }
/* 188:    */       catch (Exception ex)
/* 189:    */       {
/* 190:332 */         ex.printStackTrace();
/* 191:    */       }
/* 192:335 */       if (isStopRequested()) {
/* 193:336 */         return;
/* 194:    */       }
/* 195:339 */       if (result != null) {
/* 196:340 */         this.m_matches.getOutputStructure().add(result);
/* 197:    */       }
/* 198:    */     }
/* 199:344 */     Data outputD = new Data(data.getConnectionName(), this.m_matches.getOutputStructure());
/* 200:    */     
/* 201:346 */     outputD.setPayloadElement("aux_set_num", data.getPayloadElement("aux_set_num"));
/* 202:    */     
/* 203:348 */     outputD.setPayloadElement("aux_max_set_num", data.getPayloadElement("aux_max_set_num"));
/* 204:    */     
/* 205:350 */     getStepManager().outputData(new Data[] { outputD });
/* 206:    */   }
/* 207:    */   
/* 208:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 209:    */     throws WekaException
/* 210:    */   {
/* 211:367 */     if (getStepManager().numIncomingConnections() > 0) {
/* 212:368 */       for (Map.Entry<String, List<StepManager>> e : getStepManager().getIncomingConnections().entrySet()) {
/* 213:370 */         if (((List)e.getValue()).size() > 0)
/* 214:    */         {
/* 215:371 */           StepManager incoming = (StepManager)((List)e.getValue()).get(0);
/* 216:372 */           String incomingConnType = (String)e.getKey();
/* 217:373 */           Instances incomingStruc = getStepManager().getIncomingStructureFromStep(incoming, incomingConnType);
/* 218:376 */           if (incomingStruc == null) {
/* 219:377 */             return null;
/* 220:    */           }
/* 221:    */           try
/* 222:    */           {
/* 223:381 */             SubstringLabelerRules rules = new SubstringLabelerRules(this.m_matchDetails, this.m_attName, getConsumeNonMatching(), getNominalBinary(), incomingStruc, ((StepManagerImpl)getStepManager()).stepStatusMessagePrefix(), null, Environment.getSystemWide());
/* 224:    */             
/* 225:    */ 
/* 226:    */ 
/* 227:    */ 
/* 228:    */ 
/* 229:387 */             return rules.getOutputStructure();
/* 230:    */           }
/* 231:    */           catch (Exception ex)
/* 232:    */           {
/* 233:389 */             throw new WekaException(ex);
/* 234:    */           }
/* 235:    */         }
/* 236:    */       }
/* 237:    */     }
/* 238:395 */     return null;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public String getCustomEditorForStep()
/* 242:    */   {
/* 243:408 */     return "weka.gui.knowledgeflow.steps.SubstringLabelerStepEditorDialog";
/* 244:    */   }
/* 245:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.SubstringLabeler
 * JD-Core Version:    0.7.0.1
 */
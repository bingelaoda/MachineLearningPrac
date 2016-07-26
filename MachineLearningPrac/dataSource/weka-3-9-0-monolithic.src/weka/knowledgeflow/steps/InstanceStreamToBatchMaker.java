/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.WekaException;
/*  11:    */ import weka.knowledgeflow.Data;
/*  12:    */ import weka.knowledgeflow.StepManager;
/*  13:    */ 
/*  14:    */ @KFStep(name="InstanceStreamToBatchMaker", category="Flow", toolTipText="Converts an incoming instance stream into a batch dataset", iconPath="weka/gui/knowledgeflow/icons/InstanceStreamToBatchMaker.gif")
/*  15:    */ public class InstanceStreamToBatchMaker
/*  16:    */   extends BaseStep
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 5461324282251111320L;
/*  19:    */   protected boolean m_isReset;
/*  20:    */   protected Instances m_structure;
/*  21:    */   protected boolean m_hasStringAtts;
/*  22:    */   
/*  23:    */   public void stepInit()
/*  24:    */     throws WekaException
/*  25:    */   {
/*  26: 65 */     this.m_isReset = true;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public List<String> getIncomingConnectionTypes()
/*  30:    */   {
/*  31: 79 */     if (getStepManager().numIncomingConnections() == 0) {
/*  32: 80 */       return Arrays.asList(new String[] { "instance" });
/*  33:    */     }
/*  34: 82 */     return null;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public List<String> getOutgoingConnectionTypes()
/*  38:    */   {
/*  39: 96 */     if (getStepManager().numIncomingConnections() > 0) {
/*  40: 97 */       return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/*  41:    */     }
/*  42:101 */     return null;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void processIncoming(Data data)
/*  46:    */     throws WekaException
/*  47:    */   {
/*  48:112 */     if (this.m_isReset)
/*  49:    */     {
/*  50:113 */       this.m_isReset = false;
/*  51:114 */       if (data.getPrimaryPayload() == null) {
/*  52:115 */         throw new WekaException("We didn't receive any instances!");
/*  53:    */       }
/*  54:117 */       getStepManager().logDetailed("Collecting instances...");
/*  55:118 */       Instance temp = (Instance)data.getPrimaryPayload();
/*  56:119 */       this.m_structure = new Instances(temp.dataset(), 0).stringFreeStructure();
/*  57:120 */       this.m_hasStringAtts = temp.dataset().checkForStringAttributes();
/*  58:    */     }
/*  59:123 */     if (isStopRequested())
/*  60:    */     {
/*  61:124 */       getStepManager().interrupted();
/*  62:125 */       return;
/*  63:    */     }
/*  64:128 */     if (!getStepManager().isStreamFinished(data))
/*  65:    */     {
/*  66:129 */       getStepManager().throughputUpdateStart();
/*  67:130 */       Instance inst = (Instance)data.getPrimaryPayload();
/*  68:131 */       if (this.m_hasStringAtts) {
/*  69:132 */         for (int i = 0; i < this.m_structure.numAttributes(); i++) {
/*  70:133 */           if ((this.m_structure.attribute(i).isString()) && (!inst.isMissing(i)))
/*  71:    */           {
/*  72:134 */             int index = this.m_structure.attribute(i).addStringValue(inst.stringValue(i));
/*  73:    */             
/*  74:136 */             inst.setValue(i, index);
/*  75:    */           }
/*  76:    */         }
/*  77:    */       }
/*  78:140 */       this.m_structure.add(inst);
/*  79:141 */       getStepManager().throughputUpdateEnd();
/*  80:    */     }
/*  81:    */     else
/*  82:    */     {
/*  83:144 */       this.m_structure.compactify();
/*  84:145 */       getStepManager().logBasic("Emitting a batch of " + this.m_structure.numInstances() + " instances.");
/*  85:    */       
/*  86:147 */       List<String> outCons = new ArrayList(getStepManager().getOutgoingConnections().keySet());
/*  87:    */       
/*  88:    */ 
/*  89:150 */       Data out = new Data((String)outCons.get(0), this.m_structure);
/*  90:151 */       out.setPayloadElement("aux_set_num", Integer.valueOf(1));
/*  91:152 */       out.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  92:153 */       if (!isStopRequested())
/*  93:    */       {
/*  94:154 */         getStepManager().outputData(new Data[] { out });
/*  95:155 */         getStepManager().finished();
/*  96:    */       }
/*  97:    */       else
/*  98:    */       {
/*  99:157 */         getStepManager().interrupted();
/* 100:    */       }
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 105:    */     throws WekaException
/* 106:    */   {
/* 107:176 */     if (getStepManager().numIncomingConnections() > 0) {
/* 108:178 */       return getStepManager().getIncomingStructureForConnectionType("instance");
/* 109:    */     }
/* 110:182 */     return null;
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.InstanceStreamToBatchMaker
 * JD-Core Version:    0.7.0.1
 */
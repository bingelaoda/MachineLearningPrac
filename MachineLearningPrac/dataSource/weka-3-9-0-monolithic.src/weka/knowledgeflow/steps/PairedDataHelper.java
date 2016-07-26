/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.concurrent.ConcurrentHashMap;
/*   6:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   7:    */ import weka.core.WekaException;
/*   8:    */ import weka.knowledgeflow.Data;
/*   9:    */ import weka.knowledgeflow.StepManager;
/*  10:    */ 
/*  11:    */ public class PairedDataHelper<P>
/*  12:    */   implements Serializable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -7813465607881227514L;
/*  15:113 */   protected Map<String, Map<Integer, Object>> m_namedIndexedStore = new ConcurrentHashMap();
/*  16:117 */   protected Map<Integer, P> m_primaryResultMap = new ConcurrentHashMap();
/*  17:124 */   protected Map<Integer, Data> m_secondaryDataMap = new ConcurrentHashMap();
/*  18:    */   protected String m_primaryConType;
/*  19:    */   protected String m_secondaryConType;
/*  20:    */   protected transient PairedProcessor m_processor;
/*  21:    */   protected transient Step m_ownerStep;
/*  22:    */   protected transient AtomicInteger m_setCount;
/*  23:    */   
/*  24:    */   public PairedDataHelper(Step owner, PairedProcessor processor, String primaryConType, String secondaryConType)
/*  25:    */   {
/*  26:152 */     this.m_primaryConType = primaryConType;
/*  27:153 */     this.m_secondaryConType = secondaryConType;
/*  28:154 */     this.m_ownerStep = owner;
/*  29:155 */     this.m_processor = processor;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void process(Data data)
/*  33:    */     throws WekaException
/*  34:    */   {
/*  35:165 */     if (this.m_ownerStep.getStepManager().isStopRequested())
/*  36:    */     {
/*  37:166 */       this.m_ownerStep.getStepManager().interrupted();
/*  38:167 */       return;
/*  39:    */     }
/*  40:169 */     String connType = data.getConnectionName();
/*  41:170 */     if (connType.equals(this.m_primaryConType)) {
/*  42:171 */       processPrimary(data);
/*  43:172 */     } else if ((this.m_secondaryConType != null) && (connType.equals(this.m_secondaryConType))) {
/*  44:174 */       processSecondary(data);
/*  45:    */     } else {
/*  46:176 */       throw new WekaException("Illegal connection/data type: " + connType);
/*  47:    */     }
/*  48:179 */     if (!this.m_ownerStep.getStepManager().isStopRequested())
/*  49:    */     {
/*  50:180 */       if ((this.m_setCount != null) && (this.m_setCount.get() == 0))
/*  51:    */       {
/*  52:181 */         this.m_ownerStep.getStepManager().finished();
/*  53:    */         
/*  54:183 */         this.m_primaryResultMap.clear();
/*  55:184 */         this.m_secondaryDataMap.clear();
/*  56:185 */         this.m_namedIndexedStore.clear();
/*  57:    */       }
/*  58:    */     }
/*  59:    */     else {
/*  60:188 */       this.m_ownerStep.getStepManager().interrupted();
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   private void processPrimary(Data data)
/*  65:    */     throws WekaException
/*  66:    */   {
/*  67:202 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num", Integer.valueOf(1));
/*  68:    */     
/*  69:204 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  70:206 */     if (this.m_setCount == null) {
/*  71:207 */       this.m_setCount = new AtomicInteger(maxSetNum.intValue());
/*  72:    */     }
/*  73:210 */     if (setNum.intValue() == 1)
/*  74:    */     {
/*  75:211 */       this.m_ownerStep.getStepManager().processing();
/*  76:212 */       this.m_ownerStep.getStepManager().statusMessage("Processing set/fold " + setNum + " out of " + maxSetNum);
/*  77:    */     }
/*  78:216 */     if (!this.m_ownerStep.getStepManager().isStopRequested())
/*  79:    */     {
/*  80:217 */       P result = this.m_processor.processPrimary(setNum, maxSetNum, data, this);
/*  81:218 */       if (result != null) {
/*  82:219 */         this.m_primaryResultMap.put(setNum, result);
/*  83:    */       }
/*  84:    */     }
/*  85:    */     else
/*  86:    */     {
/*  87:222 */       this.m_ownerStep.getStepManager().interrupted();
/*  88:223 */       return;
/*  89:    */     }
/*  90:226 */     Data waitingSecondary = (Data)this.m_secondaryDataMap.get(setNum);
/*  91:227 */     if (waitingSecondary != null) {
/*  92:228 */       processSecondary(waitingSecondary);
/*  93:229 */     } else if (this.m_secondaryConType == null) {
/*  94:231 */       this.m_setCount.decrementAndGet();
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   private synchronized void processSecondary(Data data)
/*  99:    */     throws WekaException
/* 100:    */   {
/* 101:245 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num", Integer.valueOf(1));
/* 102:    */     
/* 103:247 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num", Integer.valueOf(1));
/* 104:    */     
/* 105:    */ 
/* 106:250 */     P primaryData = this.m_primaryResultMap.get(setNum);
/* 107:251 */     if (primaryData == null)
/* 108:    */     {
/* 109:253 */       this.m_secondaryDataMap.put(setNum, data);
/* 110:254 */       return;
/* 111:    */     }
/* 112:257 */     if (!this.m_ownerStep.getStepManager().isStopRequested())
/* 113:    */     {
/* 114:258 */       this.m_processor.processSecondary(setNum, maxSetNum, data, this);
/* 115:    */     }
/* 116:    */     else
/* 117:    */     {
/* 118:260 */       this.m_ownerStep.getStepManager().interrupted();
/* 119:261 */       return;
/* 120:    */     }
/* 121:264 */     this.m_setCount.decrementAndGet();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public P getIndexedPrimaryResult(int index)
/* 125:    */   {
/* 126:274 */     return this.m_primaryResultMap.get(Integer.valueOf(index));
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void reset()
/* 130:    */   {
/* 131:283 */     if ((this.m_setCount != null) && (this.m_setCount.get() > 0) && (!this.m_ownerStep.getStepManager().isStopRequested())) {
/* 132:285 */       return;
/* 133:    */     }
/* 134:287 */     this.m_setCount = null;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public boolean isFinished()
/* 138:    */   {
/* 139:296 */     return this.m_setCount.get() == 0;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void createNamedIndexedStore(String name)
/* 143:    */   {
/* 144:305 */     this.m_namedIndexedStore.put(name, new ConcurrentHashMap());
/* 145:    */   }
/* 146:    */   
/* 147:    */   public <T> T getIndexedValueFromNamedStore(String storeName, Integer index)
/* 148:    */   {
/* 149:319 */     Map<Integer, Object> store = (Map)this.m_namedIndexedStore.get(storeName);
/* 150:320 */     if (store != null) {
/* 151:321 */       return store.get(index);
/* 152:    */     }
/* 153:324 */     return null;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public synchronized void addIndexedValueToNamedStore(String storeName, Integer index, Object value)
/* 157:    */   {
/* 158:337 */     Map<Integer, Object> store = (Map)this.m_namedIndexedStore.get(storeName);
/* 159:338 */     if (store == null)
/* 160:    */     {
/* 161:339 */       createNamedIndexedStore(storeName);
/* 162:340 */       store = (Map)this.m_namedIndexedStore.get(storeName);
/* 163:    */     }
/* 164:342 */     store.put(index, value);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static abstract interface PairedProcessor<P>
/* 168:    */   {
/* 169:    */     public abstract P processPrimary(Integer paramInteger1, Integer paramInteger2, Data paramData, PairedDataHelper<P> paramPairedDataHelper)
/* 170:    */       throws WekaException;
/* 171:    */     
/* 172:    */     public abstract void processSecondary(Integer paramInteger1, Integer paramInteger2, Data paramData, PairedDataHelper<P> paramPairedDataHelper)
/* 173:    */       throws WekaException;
/* 174:    */   }
/* 175:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.PairedDataHelper
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package weka.gui.streams;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ 
/*   9:    */ public class InstanceJoiner
/*  10:    */   implements Serializable, InstanceProducer, SerialInstanceListener
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -6529972700291329656L;
/*  13:    */   private final Vector<InstanceListener> listeners;
/*  14:    */   private boolean b_Debug;
/*  15:    */   protected Instances m_InputFormat;
/*  16:    */   private Instance m_OutputInstance;
/*  17:    */   private boolean b_FirstInputFinished;
/*  18:    */   
/*  19:    */   public InstanceJoiner()
/*  20:    */   {
/*  21: 62 */     this.listeners = new Vector();
/*  22: 63 */     this.m_InputFormat = null;
/*  23: 64 */     this.m_OutputInstance = null;
/*  24: 65 */     this.b_Debug = false;
/*  25: 66 */     this.b_FirstInputFinished = false;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public boolean inputFormat(Instances instanceInfo)
/*  29:    */   {
/*  30: 83 */     this.m_InputFormat = new Instances(instanceInfo, 0);
/*  31: 84 */     notifyInstanceProduced(new InstanceEvent(this, 1));
/*  32:    */     
/*  33: 86 */     this.b_FirstInputFinished = false;
/*  34:    */     
/*  35: 88 */     return true;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Instances outputFormat()
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:102 */     if (this.m_InputFormat == null) {
/*  42:103 */       throw new Exception("No output format defined.");
/*  43:    */     }
/*  44:105 */     return new Instances(this.m_InputFormat, 0);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public boolean input(Instance instance)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:110 */     if (this.m_InputFormat == null) {
/*  51:111 */       throw new Exception("No input instance format defined");
/*  52:    */     }
/*  53:113 */     if (instance != null)
/*  54:    */     {
/*  55:114 */       this.m_OutputInstance = ((Instance)instance.copy());
/*  56:115 */       notifyInstanceProduced(new InstanceEvent(this, 2));
/*  57:    */       
/*  58:117 */       return true;
/*  59:    */     }
/*  60:119 */     return false;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void batchFinished()
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:135 */     if (this.m_InputFormat == null) {
/*  67:136 */       throw new Exception("No input instance format defined");
/*  68:    */     }
/*  69:138 */     notifyInstanceProduced(new InstanceEvent(this, 3));
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Instance outputPeek()
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:151 */     if (this.m_InputFormat == null) {
/*  76:152 */       throw new Exception("No output instance format defined");
/*  77:    */     }
/*  78:154 */     if (this.m_OutputInstance == null) {
/*  79:155 */       return null;
/*  80:    */     }
/*  81:157 */     return (Instance)this.m_OutputInstance.copy();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setDebug(boolean debug)
/*  85:    */   {
/*  86:162 */     this.b_Debug = debug;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean getDebug()
/*  90:    */   {
/*  91:167 */     return this.b_Debug;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public synchronized void addInstanceListener(InstanceListener ipl)
/*  95:    */   {
/*  96:173 */     this.listeners.addElement(ipl);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public synchronized void removeInstanceListener(InstanceListener ipl)
/* 100:    */   {
/* 101:179 */     this.listeners.removeElement(ipl);
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected void notifyInstanceProduced(InstanceEvent e)
/* 105:    */   {
/* 106:185 */     if (this.listeners.size() > 0)
/* 107:    */     {
/* 108:186 */       if (this.b_Debug) {
/* 109:187 */         System.err.println(getClass().getName() + "::notifyInstanceProduced()");
/* 110:    */       }
/* 111:    */       Vector<InstanceListener> l;
/* 112:191 */       synchronized (this)
/* 113:    */       {
/* 114:192 */         l = (Vector)this.listeners.clone();
/* 115:    */       }
/* 116:194 */       for (int i = 0; i < l.size(); i++) {
/* 117:195 */         ((InstanceListener)l.elementAt(i)).instanceProduced(e);
/* 118:    */       }
/* 119:    */       try
/* 120:    */       {
/* 121:200 */         if (e.getID() == 2) {
/* 122:201 */           this.m_OutputInstance = null;
/* 123:    */         }
/* 124:    */       }
/* 125:    */       catch (Exception ex)
/* 126:    */       {
/* 127:204 */         System.err.println("Problem: notifyInstanceProduced() was\ncalled with INSTANCE_AVAILABLE, but output()\nthrew an exception: " + ex.getMessage());
/* 128:    */       }
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void instanceProduced(InstanceEvent e)
/* 133:    */   {
/* 134:213 */     Object source = e.getSource();
/* 135:214 */     if ((source instanceof InstanceProducer)) {
/* 136:    */       try
/* 137:    */       {
/* 138:216 */         InstanceProducer a = (InstanceProducer)source;
/* 139:217 */         switch (e.getID())
/* 140:    */         {
/* 141:    */         case 1: 
/* 142:219 */           if (this.b_Debug) {
/* 143:220 */             System.err.println(getClass().getName() + "::firstInstanceProduced() - Format available");
/* 144:    */           }
/* 145:223 */           inputFormat(a.outputFormat());
/* 146:224 */           break;
/* 147:    */         case 2: 
/* 148:226 */           if (this.b_Debug) {
/* 149:227 */             System.err.println(getClass().getName() + "::firstInstanceProduced() - Instance available");
/* 150:    */           }
/* 151:230 */           input(a.outputPeek());
/* 152:231 */           break;
/* 153:    */         case 3: 
/* 154:233 */           if (this.b_Debug) {
/* 155:234 */             System.err.println(getClass().getName() + "::firstInstanceProduced() - End of instance batch");
/* 156:    */           }
/* 157:237 */           batchFinished();
/* 158:238 */           this.b_FirstInputFinished = true;
/* 159:239 */           break;
/* 160:    */         default: 
/* 161:241 */           System.err.println(getClass().getName() + "::firstInstanceProduced() - unknown event type");
/* 162:    */         }
/* 163:    */       }
/* 164:    */       catch (Exception ex)
/* 165:    */       {
/* 166:246 */         System.err.println(ex.getMessage());
/* 167:    */       }
/* 168:    */     } else {
/* 169:249 */       System.err.println(getClass().getName() + "::firstInstanceProduced() - Unknown source object type");
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void secondInstanceProduced(InstanceEvent e)
/* 174:    */   {
/* 175:257 */     Object source = e.getSource();
/* 176:258 */     if ((source instanceof InstanceProducer)) {
/* 177:    */       try
/* 178:    */       {
/* 179:260 */         if (!this.b_FirstInputFinished) {
/* 180:261 */           throw new Exception(getClass().getName() + "::secondInstanceProduced() - Input received from" + " second stream before first stream finished");
/* 181:    */         }
/* 182:265 */         InstanceProducer a = (InstanceProducer)source;
/* 183:266 */         switch (e.getID())
/* 184:    */         {
/* 185:    */         case 1: 
/* 186:268 */           if (this.b_Debug) {
/* 187:269 */             System.err.println(getClass().getName() + "::secondInstanceProduced() - Format available");
/* 188:    */           }
/* 189:273 */           if (!a.outputFormat().equalHeaders(outputFormat())) {
/* 190:274 */             throw new Exception(getClass().getName() + "::secondInstanceProduced() - incompatible instance streams\n" + a.outputFormat().equalHeadersMsg(outputFormat()));
/* 191:    */           }
/* 192:    */           break;
/* 193:    */         case 2: 
/* 194:280 */           if (this.b_Debug) {
/* 195:281 */             System.err.println(getClass().getName() + "::secondInstanceProduced() - Instance available");
/* 196:    */           }
/* 197:284 */           input(a.outputPeek());
/* 198:285 */           break;
/* 199:    */         case 3: 
/* 200:287 */           if (this.b_Debug) {
/* 201:288 */             System.err.println(getClass().getName() + "::secondInstanceProduced() - End of instance batch");
/* 202:    */           }
/* 203:291 */           batchFinished();
/* 204:292 */           break;
/* 205:    */         default: 
/* 206:294 */           System.err.println(getClass().getName() + "::secondInstanceProduced() - unknown event type");
/* 207:    */         }
/* 208:    */       }
/* 209:    */       catch (Exception ex)
/* 210:    */       {
/* 211:299 */         System.err.println(ex.getMessage());
/* 212:    */       }
/* 213:    */     } else {
/* 214:302 */       System.err.println(getClass().getName() + "::secondInstanceProduced() - Unknown source object type");
/* 215:    */     }
/* 216:    */   }
/* 217:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceJoiner
 * JD-Core Version:    0.7.0.1
 */
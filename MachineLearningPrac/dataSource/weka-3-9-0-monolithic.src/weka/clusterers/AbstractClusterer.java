/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.CapabilitiesHandler;
/*   9:    */ import weka.core.CapabilitiesIgnorer;
/*  10:    */ import weka.core.CommandlineRunnable;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SerializedObject;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public abstract class AbstractClusterer
/*  21:    */   implements Clusterer, Cloneable, Serializable, CapabilitiesHandler, RevisionHandler, OptionHandler, CapabilitiesIgnorer, CommandlineRunnable
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -6099962589663877632L;
/*  24: 55 */   protected boolean m_Debug = false;
/*  25: 58 */   protected boolean m_DoNotCheckCapabilities = false;
/*  26:    */   
/*  27:    */   public abstract void buildClusterer(Instances paramInstances)
/*  28:    */     throws Exception;
/*  29:    */   
/*  30:    */   public int clusterInstance(Instance instance)
/*  31:    */     throws Exception
/*  32:    */   {
/*  33: 85 */     double[] dist = distributionForInstance(instance);
/*  34: 87 */     if (dist == null) {
/*  35: 88 */       throw new Exception("Null distribution predicted");
/*  36:    */     }
/*  37: 91 */     if (Utils.sum(dist) <= 0.0D) {
/*  38: 92 */       throw new Exception("Unable to cluster instance");
/*  39:    */     }
/*  40: 94 */     return Utils.maxIndex(dist);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double[] distributionForInstance(Instance instance)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:109 */     double[] d = new double[numberOfClusters()];
/*  47:    */     
/*  48:111 */     d[clusterInstance(instance)] = 1.0D;
/*  49:    */     
/*  50:113 */     return d;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public abstract int numberOfClusters()
/*  54:    */     throws Exception;
/*  55:    */   
/*  56:    */   public Enumeration<Option> listOptions()
/*  57:    */   {
/*  58:134 */     Vector<Option> newVector = Option.listOptionsForClassHierarchy(getClass(), AbstractClusterer.class);
/*  59:    */     
/*  60:    */ 
/*  61:137 */     newVector.addElement(new Option("\tIf set, clusterer is run in debug mode and\n\tmay output additional info to the console", "output-debug-info", 0, "-output-debug-info"));
/*  62:    */     
/*  63:    */ 
/*  64:    */ 
/*  65:141 */     newVector.addElement(new Option("\tIf set, clusterer capabilities are not checked before clusterer is built\n\t(use with caution).", "-do-not-check-capabilities", 0, "-do-not-check-capabilities"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:146 */     return newVector.elements();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setOptions(String[] options)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:169 */     Option.setOptionsForHierarchy(options, this, AbstractClusterer.class);
/*  77:170 */     setDebug(Utils.getFlag("output-debug-info", options));
/*  78:171 */     setDoNotCheckCapabilities(Utils.getFlag("do-not-check-capabilities", options));
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setDebug(boolean debug)
/*  82:    */   {
/*  83:182 */     this.m_Debug = debug;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean getDebug()
/*  87:    */   {
/*  88:192 */     return this.m_Debug;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String debugTipText()
/*  92:    */   {
/*  93:202 */     return "If set to true, clusterer may output additional info to the console.";
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*  97:    */   {
/*  98:213 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean getDoNotCheckCapabilities()
/* 102:    */   {
/* 103:223 */     return this.m_DoNotCheckCapabilities;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String doNotCheckCapabilitiesTipText()
/* 107:    */   {
/* 108:233 */     return "If set, clusterer capabilities are not checked before clusterer is built (Use with caution to reduce runtime).";
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String[] getOptions()
/* 112:    */   {
/* 113:245 */     Vector<String> options = new Vector();
/* 114:246 */     for (String s : Option.getOptionsForHierarchy(this, AbstractClusterer.class)) {
/* 115:248 */       options.add(s);
/* 116:    */     }
/* 117:251 */     if (getDebug()) {
/* 118:252 */       options.add("-output-debug-info");
/* 119:    */     }
/* 120:254 */     if (getDoNotCheckCapabilities()) {
/* 121:255 */       options.add("-do-not-check-capabilities");
/* 122:    */     }
/* 123:258 */     return (String[])options.toArray(new String[0]);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public static Clusterer forName(String clustererName, String[] options)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:276 */     return (Clusterer)Utils.forName(Clusterer.class, clustererName, options);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public static Clusterer makeCopy(Clusterer model)
/* 133:    */     throws Exception
/* 134:    */   {
/* 135:287 */     return (Clusterer)new SerializedObject(model).getObject();
/* 136:    */   }
/* 137:    */   
/* 138:    */   public static Clusterer[] makeCopies(Clusterer model, int num)
/* 139:    */     throws Exception
/* 140:    */   {
/* 141:302 */     if (model == null) {
/* 142:303 */       throw new Exception("No model clusterer set");
/* 143:    */     }
/* 144:305 */     Clusterer[] clusterers = new Clusterer[num];
/* 145:306 */     SerializedObject so = new SerializedObject(model);
/* 146:307 */     for (int i = 0; i < clusterers.length; i++) {
/* 147:308 */       clusterers[i] = ((Clusterer)so.getObject());
/* 148:    */     }
/* 149:310 */     return clusterers;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public Capabilities getCapabilities()
/* 153:    */   {
/* 154:324 */     Capabilities result = new Capabilities(this);
/* 155:325 */     result.enableAll();
/* 156:    */     
/* 157:327 */     return result;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public String getRevision()
/* 161:    */   {
/* 162:337 */     return RevisionUtils.extract("$Revision: 12201 $");
/* 163:    */   }
/* 164:    */   
/* 165:    */   public static void runClusterer(Clusterer clusterer, String[] options)
/* 166:    */   {
/* 167:    */     try
/* 168:    */     {
/* 169:348 */       if ((clusterer instanceof CommandlineRunnable)) {
/* 170:349 */         ((CommandlineRunnable)clusterer).preExecution();
/* 171:    */       }
/* 172:351 */       System.out.println(ClusterEvaluation.evaluateClusterer(clusterer, options));
/* 173:    */     }
/* 174:    */     catch (Exception e)
/* 175:    */     {
/* 176:354 */       if ((e.getMessage() == null) || ((e.getMessage() != null) && (e.getMessage().indexOf("General options") == -1))) {
/* 177:356 */         e.printStackTrace();
/* 178:    */       } else {
/* 179:358 */         System.err.println(e.getMessage());
/* 180:    */       }
/* 181:    */     }
/* 182:    */     try
/* 183:    */     {
/* 184:362 */       if ((clusterer instanceof CommandlineRunnable)) {
/* 185:363 */         ((CommandlineRunnable)clusterer).postExecution();
/* 186:    */       }
/* 187:    */     }
/* 188:    */     catch (Exception ex)
/* 189:    */     {
/* 190:366 */       ex.printStackTrace();
/* 191:    */     }
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void preExecution()
/* 195:    */     throws Exception
/* 196:    */   {}
/* 197:    */   
/* 198:    */   public void run(Object toRun, String[] options)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:389 */     if (!(toRun instanceof Clusterer)) {
/* 202:390 */       throw new IllegalArgumentException("Object to execute is not a Clusterer!");
/* 203:    */     }
/* 204:394 */     runClusterer((Clusterer)toRun, options);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void postExecution()
/* 208:    */     throws Exception
/* 209:    */   {}
/* 210:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.AbstractClusterer
 * JD-Core Version:    0.7.0.1
 */
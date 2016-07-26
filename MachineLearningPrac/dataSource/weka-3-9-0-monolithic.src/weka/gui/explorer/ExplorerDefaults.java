/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Properties;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.associations.Apriori;
/*  10:    */ import weka.associations.Associator;
/*  11:    */ import weka.attributeSelection.ASEvaluation;
/*  12:    */ import weka.attributeSelection.ASSearch;
/*  13:    */ import weka.attributeSelection.BestFirst;
/*  14:    */ import weka.attributeSelection.CfsSubsetEval;
/*  15:    */ import weka.classifiers.Classifier;
/*  16:    */ import weka.classifiers.rules.ZeroR;
/*  17:    */ import weka.clusterers.Clusterer;
/*  18:    */ import weka.clusterers.EM;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ 
/*  22:    */ public class ExplorerDefaults
/*  23:    */   implements Serializable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 4954795757927524225L;
/*  26:    */   public static final String PROPERTY_FILE = "weka/gui/explorer/Explorer.props";
/*  27:    */   protected static Properties PROPERTIES;
/*  28:    */   
/*  29:    */   static
/*  30:    */   {
/*  31:    */     try
/*  32:    */     {
/*  33: 51 */       PROPERTIES = Utils.readProperties("weka/gui/explorer/Explorer.props");
/*  34:    */     }
/*  35:    */     catch (Exception e)
/*  36:    */     {
/*  37: 53 */       System.err.println("Problem reading properties. Fix before continuing.");
/*  38: 54 */       e.printStackTrace();
/*  39: 55 */       PROPERTIES = new Properties();
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static String get(String property, String defaultValue)
/*  44:    */   {
/*  45: 68 */     return PROPERTIES.getProperty(property, defaultValue);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static void set(String property, String value)
/*  49:    */   {
/*  50: 72 */     PROPERTIES.setProperty(property, value);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static final Properties getProperties()
/*  54:    */   {
/*  55: 81 */     return PROPERTIES;
/*  56:    */   }
/*  57:    */   
/*  58:    */   protected static Object getObject(String property, String defaultValue)
/*  59:    */   {
/*  60: 93 */     return getObject(property, defaultValue, Object.class);
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected static Object getObject(String property, String defaultValue, Class<?> cls)
/*  64:    */   {
/*  65:111 */     Object result = null;
/*  66:    */     try
/*  67:    */     {
/*  68:114 */       String tmpStr = get(property, defaultValue);
/*  69:115 */       String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  70:116 */       if (tmpOptions.length != 0)
/*  71:    */       {
/*  72:117 */         tmpStr = tmpOptions[0];
/*  73:118 */         tmpOptions[0] = "";
/*  74:119 */         result = Utils.forName(cls, tmpStr, tmpOptions);
/*  75:    */       }
/*  76:    */     }
/*  77:    */     catch (Exception e)
/*  78:    */     {
/*  79:122 */       e.printStackTrace();
/*  80:123 */       result = null;
/*  81:    */     }
/*  82:126 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static boolean getInitGenericObjectEditorFilter()
/*  86:    */   {
/*  87:136 */     return Boolean.parseBoolean(get("InitGenericObjectEditorFilter", "false"));
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static String[] getTabs()
/*  91:    */   {
/*  92:150 */     String tabs = get("Tabs", "weka.gui.explorer.ClassifierPanel,weka.gui.explorer.ClustererPanel,weka.gui.explorer.AssociationsPanel,weka.gui.explorer.AttributeSelectionPanel,weka.gui.explorer.VisualizePanel");
/*  93:    */     
/*  94:    */ 
/*  95:153 */     String[] result = tabs.split(",");
/*  96:    */     
/*  97:155 */     return result;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static String getInitialDirectory()
/* 101:    */   {
/* 102:176 */     String result = get("InitialDirectory", "%c");
/* 103:177 */     result = result.replaceAll("%t", System.getProperty("java.io.tmpdir"));
/* 104:178 */     result = result.replaceAll("%h", System.getProperty("user.home"));
/* 105:179 */     result = result.replaceAll("%c", System.getProperty("user.dir"));
/* 106:180 */     result = result.replaceAll("%%", System.getProperty("%"));
/* 107:    */     
/* 108:182 */     return result;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public static Object getFilter()
/* 112:    */   {
/* 113:191 */     return getObject("Filter", "", Filter.class);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static Object getClassifier()
/* 117:    */   {
/* 118:202 */     Object result = getObject("Classifier", ZeroR.class.getName(), Classifier.class);
/* 119:205 */     if (result == null) {
/* 120:206 */       result = new ZeroR();
/* 121:    */     }
/* 122:209 */     return result;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static int getClassifierTestMode()
/* 126:    */   {
/* 127:218 */     return Integer.parseInt(get("ClassifierTestMode", "1"));
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static int getClassifierCrossvalidationFolds()
/* 131:    */   {
/* 132:227 */     return Integer.parseInt(get("ClassifierCrossvalidationFolds", "10"));
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static int getClassifierPercentageSplit()
/* 136:    */   {
/* 137:236 */     return Integer.parseInt(get("ClassifierPercentageSplit", "66"));
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static boolean getClassifierOutputModel()
/* 141:    */   {
/* 142:245 */     return Boolean.parseBoolean(get("ClassifierOutputModel", "true"));
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static boolean getClassifierOutputPerClassStats()
/* 146:    */   {
/* 147:254 */     return Boolean.parseBoolean(get("ClassifierOutputPerClassStats", "true"));
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static boolean getClassifierOutputEntropyEvalMeasures()
/* 151:    */   {
/* 152:264 */     return Boolean.parseBoolean(get("ClassifierOutputEntropyEvalMeasures", "false"));
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static boolean getClassifierOutputConfusionMatrix()
/* 156:    */   {
/* 157:274 */     return Boolean.parseBoolean(get("ClassifierOutputConfusionMatrix", "true"));
/* 158:    */   }
/* 159:    */   
/* 160:    */   public static boolean getClassifierOutputPredictions()
/* 161:    */   {
/* 162:283 */     return Boolean.parseBoolean(get("ClassifierOutputPredictions", "false"));
/* 163:    */   }
/* 164:    */   
/* 165:    */   public static String getClassifierOutputAdditionalAttributes()
/* 166:    */   {
/* 167:293 */     return get("ClassifierOutputAdditionalAttributes", "");
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static boolean getClassifierStorePredictionsForVis()
/* 171:    */   {
/* 172:303 */     return Boolean.parseBoolean(get("ClassifierStorePredictionsForVis", "true"));
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static boolean getClassifierCostSensitiveEval()
/* 176:    */   {
/* 177:313 */     return Boolean.parseBoolean(get("ClassifierCostSensitiveEval", "false"));
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static int getClassifierRandomSeed()
/* 181:    */   {
/* 182:323 */     return Integer.parseInt(get("ClassifierRandomSeed", "1"));
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static boolean getClassifierPreserveOrder()
/* 186:    */   {
/* 187:333 */     return Boolean.parseBoolean(get("ClassifierPreserveOrder", "false"));
/* 188:    */   }
/* 189:    */   
/* 190:    */   public static boolean getClassifierOutputSourceCode()
/* 191:    */   {
/* 192:343 */     return Boolean.parseBoolean(get("ClassifierOutputSourceCode", "false"));
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static String getClassifierSourceCodeClass()
/* 196:    */   {
/* 197:353 */     return get("ClassifierSourceCodeClass", "Foobar");
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static ClassifierErrorsPlotInstances getClassifierErrorsPlotInstances()
/* 201:    */   {
/* 202:    */     ClassifierErrorsPlotInstances result;
/* 203:    */     try
/* 204:    */     {
/* 205:368 */       String[] options = Utils.splitOptions(get("ClassifierErrorsPlotInstances", "weka.gui.explorer.ClassifierErrorsPlotInstances"));
/* 206:    */       
/* 207:370 */       String classname = options[0];
/* 208:371 */       options[0] = "";
/* 209:372 */       result = (ClassifierErrorsPlotInstances)Utils.forName(ClassifierErrorsPlotInstances.class, classname, options);
/* 210:    */     }
/* 211:    */     catch (Exception e)
/* 212:    */     {
/* 213:375 */       e.printStackTrace();
/* 214:376 */       result = new ClassifierErrorsPlotInstances();
/* 215:    */     }
/* 216:379 */     return result;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public static int getClassifierErrorsMinimumPlotSizeNumeric()
/* 220:    */   {
/* 221:389 */     return Integer.parseInt(get("ClassifierErrorsMinimumPlotSizeNumeric", "1"));
/* 222:    */   }
/* 223:    */   
/* 224:    */   public static int getClassifierErrorsMaximumPlotSizeNumeric()
/* 225:    */   {
/* 226:399 */     return Integer.parseInt(get("ClassifierErrorsMaximumPlotSizeNumeric", "20"));
/* 227:    */   }
/* 228:    */   
/* 229:    */   public static Object getClusterer()
/* 230:    */   {
/* 231:411 */     Object result = getObject("Clusterer", EM.class.getName(), Clusterer.class);
/* 232:413 */     if (result == null) {
/* 233:414 */       result = new EM();
/* 234:    */     }
/* 235:417 */     return result;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public static int getClustererTestMode()
/* 239:    */   {
/* 240:426 */     return Integer.parseInt(get("ClustererTestMode", "3"));
/* 241:    */   }
/* 242:    */   
/* 243:    */   public static boolean getClustererStoreClustersForVis()
/* 244:    */   {
/* 245:436 */     return Boolean.parseBoolean(get("ClustererStoreClustersForVis", "true"));
/* 246:    */   }
/* 247:    */   
/* 248:    */   public static ClustererAssignmentsPlotInstances getClustererAssignmentsPlotInstances()
/* 249:    */   {
/* 250:    */     ClustererAssignmentsPlotInstances result;
/* 251:    */     try
/* 252:    */     {
/* 253:451 */       String[] options = Utils.splitOptions(get("ClustererAssignmentsPlotInstances", "weka.gui.explorer.ClustererAssignmentsPlotInstances"));
/* 254:    */       
/* 255:453 */       String classname = options[0];
/* 256:454 */       options[0] = "";
/* 257:455 */       result = (ClustererAssignmentsPlotInstances)Utils.forName(ClustererAssignmentsPlotInstances.class, classname, options);
/* 258:    */     }
/* 259:    */     catch (Exception e)
/* 260:    */     {
/* 261:458 */       e.printStackTrace();
/* 262:459 */       result = new ClustererAssignmentsPlotInstances();
/* 263:    */     }
/* 264:462 */     return result;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public static Object getAssociator()
/* 268:    */   {
/* 269:474 */     Object result = getObject("Associator", Apriori.class.getName(), Associator.class);
/* 270:476 */     if (result == null) {
/* 271:477 */       result = new Apriori();
/* 272:    */     }
/* 273:480 */     return result;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public static Object getASEvaluator()
/* 277:    */   {
/* 278:492 */     Object result = getObject("ASEvaluation", CfsSubsetEval.class.getName(), ASEvaluation.class);
/* 279:495 */     if (result == null) {
/* 280:496 */       result = new CfsSubsetEval();
/* 281:    */     }
/* 282:499 */     return result;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public static Object getASSearch()
/* 286:    */   {
/* 287:511 */     Object result = getObject("ASSearch", BestFirst.class.getName(), ASSearch.class);
/* 288:514 */     if (result == null) {
/* 289:515 */       result = new BestFirst();
/* 290:    */     }
/* 291:518 */     return result;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public static int getASTestMode()
/* 295:    */   {
/* 296:528 */     return Integer.parseInt(get("ASTestMode", "0"));
/* 297:    */   }
/* 298:    */   
/* 299:    */   public static int getASCrossvalidationFolds()
/* 300:    */   {
/* 301:538 */     return Integer.parseInt(get("ASCrossvalidationFolds", "10"));
/* 302:    */   }
/* 303:    */   
/* 304:    */   public static int getASRandomSeed()
/* 305:    */   {
/* 306:547 */     return Integer.parseInt(get("ASRandomSeed", "1"));
/* 307:    */   }
/* 308:    */   
/* 309:    */   public static void main(String[] args)
/* 310:    */   {
/* 311:560 */     System.out.println("\nExplorer defaults:");
/* 312:561 */     Enumeration<?> names = PROPERTIES.propertyNames();
/* 313:    */     
/* 314:    */ 
/* 315:564 */     Vector<String> sorted = new Vector();
/* 316:565 */     while (names.hasMoreElements()) {
/* 317:566 */       sorted.add(names.nextElement().toString());
/* 318:    */     }
/* 319:568 */     Collections.sort(sorted);
/* 320:569 */     names = sorted.elements();
/* 321:572 */     while (names.hasMoreElements())
/* 322:    */     {
/* 323:573 */       String name = names.nextElement().toString();
/* 324:574 */       System.out.println("- " + name + ": " + PROPERTIES.getProperty(name, ""));
/* 325:    */     }
/* 326:576 */     System.out.println();
/* 327:    */   }
/* 328:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ExplorerDefaults
 * JD-Core Version:    0.7.0.1
 */
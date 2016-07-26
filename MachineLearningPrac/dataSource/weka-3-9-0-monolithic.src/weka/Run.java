/*   1:    */ package weka;
/*   2:    */ 
/*   3:    */ import java.beans.Beans;
/*   4:    */ import java.io.BufferedReader;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStreamReader;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.List;
/*  10:    */ import weka.associations.AbstractAssociator;
/*  11:    */ import weka.associations.Associator;
/*  12:    */ import weka.attributeSelection.ASEvaluation;
/*  13:    */ import weka.classifiers.AbstractClassifier;
/*  14:    */ import weka.classifiers.Classifier;
/*  15:    */ import weka.clusterers.AbstractClusterer;
/*  16:    */ import weka.clusterers.Clusterer;
/*  17:    */ import weka.core.ClassDiscovery;
/*  18:    */ import weka.core.CommandlineRunnable;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.core.WekaPackageManager;
/*  21:    */ import weka.core.converters.AbstractFileLoader;
/*  22:    */ import weka.core.converters.AbstractFileSaver;
/*  23:    */ import weka.datagenerators.DataGenerator;
/*  24:    */ import weka.filters.Filter;
/*  25:    */ 
/*  26:    */ public class Run
/*  27:    */ {
/*  28:    */   public static enum SchemeType
/*  29:    */   {
/*  30: 52 */     CLASSIFIER("classifier"),  CLUSTERER("clusterer"),  ASSOCIATOR("association rules"),  ATTRIBUTE_SELECTION("attribute selection"),  FILTER("filter"),  LOADER("loader"),  SAVER("saver"),  DATAGENERATOR("data generator"),  COMMANDLINE("general commandline runnable");
/*  31:    */     
/*  32:    */     private final String m_stringVal;
/*  33:    */     
/*  34:    */     private SchemeType(String name)
/*  35:    */     {
/*  36: 60 */       this.m_stringVal = name;
/*  37:    */     }
/*  38:    */     
/*  39:    */     public String toString()
/*  40:    */     {
/*  41: 65 */       return this.m_stringVal;
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static List<String> findSchemeMatch(Class<?> classType, String schemeToFind, boolean matchAnywhere, boolean notJustRunnables)
/*  46:    */   {
/*  47: 80 */     ClassDiscovery.clearCache();
/*  48: 81 */     ArrayList<String> matches = ClassDiscovery.find(schemeToFind);
/*  49: 82 */     ArrayList<String> prunedMatches = new ArrayList();
/*  50: 84 */     for (int i = 0; i < matches.size(); i++) {
/*  51: 85 */       if ((((String)matches.get(i)).endsWith(schemeToFind)) || (matchAnywhere)) {
/*  52:    */         try
/*  53:    */         {
/*  54: 87 */           Object scheme = Beans.instantiate(new Run().getClass().getClassLoader(), (String)matches.get(i));
/*  55: 89 */           if ((classType == null) || (classType.isAssignableFrom(scheme.getClass()))) {
/*  56: 91 */             if ((notJustRunnables) || ((scheme instanceof Classifier)) || ((scheme instanceof Clusterer)) || ((scheme instanceof Associator)) || ((scheme instanceof ASEvaluation)) || ((scheme instanceof Filter)) || ((scheme instanceof AbstractFileLoader)) || ((scheme instanceof AbstractFileSaver)) || ((scheme instanceof DataGenerator)) || ((scheme instanceof CommandlineRunnable))) {
/*  57:101 */               prunedMatches.add(matches.get(i));
/*  58:    */             }
/*  59:    */           }
/*  60:    */         }
/*  61:    */         catch (Exception ex) {}
/*  62:    */       }
/*  63:    */     }
/*  64:111 */     return prunedMatches;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static List<String> findSchemeMatch(String schemeToFind, boolean matchAnywhere)
/*  68:    */   {
/*  69:124 */     return findSchemeMatch(null, schemeToFind, matchAnywhere, false);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static void main(String[] args)
/*  73:    */   {
/*  74:133 */     System.setProperty("apple.awt.UIElement", "true");
/*  75:    */     try
/*  76:    */     {
/*  77:135 */       if ((args.length == 0) || (args[0].equalsIgnoreCase("-h")) || (args[0].equalsIgnoreCase("-help")))
/*  78:    */       {
/*  79:137 */         System.err.println("Usage:\n\tweka.Run [-no-scan] [-no-load] [-match-anywhere] <scheme name [scheme options]>");
/*  80:    */         
/*  81:139 */         return;
/*  82:    */       }
/*  83:141 */       boolean noScan = false;
/*  84:142 */       boolean noLoad = false;
/*  85:143 */       boolean matchAnywhere = false;
/*  86:144 */       boolean dontPromptIfMultipleMatches = false;
/*  87:146 */       if (Utils.getFlag("list-packages", args))
/*  88:    */       {
/*  89:147 */         WekaPackageManager.loadPackages(true, true, false);
/*  90:148 */         return;
/*  91:    */       }
/*  92:151 */       int schemeIndex = 0;
/*  93:152 */       if (Utils.getFlag("no-load", args))
/*  94:    */       {
/*  95:153 */         noLoad = true;
/*  96:154 */         schemeIndex++;
/*  97:    */       }
/*  98:157 */       if (Utils.getFlag("no-scan", args))
/*  99:    */       {
/* 100:158 */         noScan = true;
/* 101:159 */         schemeIndex++;
/* 102:    */       }
/* 103:162 */       if (Utils.getFlag("match-anywhere", args))
/* 104:    */       {
/* 105:163 */         matchAnywhere = true;
/* 106:164 */         schemeIndex++;
/* 107:    */       }
/* 108:167 */       if (Utils.getFlag("do-not-prompt-if-multiple-matches", args))
/* 109:    */       {
/* 110:168 */         dontPromptIfMultipleMatches = true;
/* 111:169 */         schemeIndex++;
/* 112:    */       }
/* 113:172 */       if (!noLoad) {
/* 114:173 */         WekaPackageManager.loadPackages(false, true, false);
/* 115:    */       }
/* 116:176 */       String schemeToRun = null;
/* 117:177 */       String[] options = null;
/* 118:179 */       if (schemeIndex >= args.length)
/* 119:    */       {
/* 120:180 */         System.err.println("No scheme name given.");
/* 121:181 */         return;
/* 122:    */       }
/* 123:183 */       schemeToRun = args[schemeIndex];
/* 124:184 */       options = new String[args.length - schemeIndex - 1];
/* 125:185 */       if (options.length > 0) {
/* 126:186 */         System.arraycopy(args, schemeIndex + 1, options, 0, options.length);
/* 127:    */       }
/* 128:189 */       if (!noScan)
/* 129:    */       {
/* 130:190 */         List<String> prunedMatches = findSchemeMatch(schemeToRun, matchAnywhere);
/* 131:192 */         if (prunedMatches.size() == 0)
/* 132:    */         {
/* 133:193 */           System.err.println("Can't find scheme " + schemeToRun + ", or it is not runnable.");
/* 134:    */           
/* 135:    */ 
/* 136:196 */           return;
/* 137:    */         }
/* 138:197 */         if (prunedMatches.size() > 1)
/* 139:    */         {
/* 140:198 */           if (dontPromptIfMultipleMatches)
/* 141:    */           {
/* 142:199 */             System.out.println("There are multiple matches:");
/* 143:200 */             for (int i = 0; i < prunedMatches.size(); i++) {
/* 144:201 */               System.out.println("\t" + (i + 1) + ") " + (String)prunedMatches.get(i));
/* 145:    */             }
/* 146:203 */             System.out.println("\nPlease make your scheme name more specific (i.e. qualify it with more of the package name).");
/* 147:    */             
/* 148:205 */             return;
/* 149:    */           }
/* 150:207 */           BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
/* 151:    */           
/* 152:209 */           boolean done = false;
/* 153:210 */           while (!done)
/* 154:    */           {
/* 155:211 */             System.out.println("Select a scheme to run, or <return> to exit:");
/* 156:212 */             for (int i = 0; i < prunedMatches.size(); i++) {
/* 157:213 */               System.out.println("\t" + (i + 1) + ") " + (String)prunedMatches.get(i));
/* 158:    */             }
/* 159:215 */             System.out.print("\nEnter a number > ");
/* 160:216 */             String choice = null;
/* 161:217 */             int schemeNumber = 0;
/* 162:    */             try
/* 163:    */             {
/* 164:219 */               choice = br.readLine();
/* 165:220 */               if (choice.equals("")) {
/* 166:222 */                 return;
/* 167:    */               }
/* 168:224 */               schemeNumber = Integer.parseInt(choice);
/* 169:225 */               schemeNumber--;
/* 170:226 */               if ((schemeNumber >= 0) && (schemeNumber < prunedMatches.size()))
/* 171:    */               {
/* 172:227 */                 schemeToRun = (String)prunedMatches.get(schemeNumber);
/* 173:228 */                 done = true;
/* 174:    */               }
/* 175:    */             }
/* 176:    */             catch (IOException ex) {}
/* 177:    */           }
/* 178:    */         }
/* 179:    */         else
/* 180:    */         {
/* 181:236 */           schemeToRun = (String)prunedMatches.get(0);
/* 182:    */         }
/* 183:    */       }
/* 184:240 */       Object scheme = null;
/* 185:    */       try
/* 186:    */       {
/* 187:242 */         scheme = Beans.instantiate(new Run().getClass().getClassLoader(), schemeToRun);
/* 188:    */       }
/* 189:    */       catch (Exception ex)
/* 190:    */       {
/* 191:245 */         System.err.println(schemeToRun + " is not runnable!");
/* 192:    */         
/* 193:247 */         return;
/* 194:    */       }
/* 195:250 */       ArrayList<SchemeType> types = new ArrayList();
/* 196:251 */       if ((scheme instanceof CommandlineRunnable))
/* 197:    */       {
/* 198:252 */         types.add(SchemeType.COMMANDLINE);
/* 199:    */       }
/* 200:    */       else
/* 201:    */       {
/* 202:254 */         if ((scheme instanceof Classifier)) {
/* 203:255 */           types.add(SchemeType.CLASSIFIER);
/* 204:    */         }
/* 205:257 */         if ((scheme instanceof Clusterer)) {
/* 206:258 */           types.add(SchemeType.CLUSTERER);
/* 207:    */         }
/* 208:260 */         if ((scheme instanceof Associator)) {
/* 209:261 */           types.add(SchemeType.ASSOCIATOR);
/* 210:    */         }
/* 211:263 */         if ((scheme instanceof ASEvaluation)) {
/* 212:264 */           types.add(SchemeType.ATTRIBUTE_SELECTION);
/* 213:    */         }
/* 214:266 */         if ((scheme instanceof Filter)) {
/* 215:267 */           types.add(SchemeType.FILTER);
/* 216:    */         }
/* 217:269 */         if ((scheme instanceof AbstractFileLoader)) {
/* 218:270 */           types.add(SchemeType.LOADER);
/* 219:    */         }
/* 220:272 */         if ((scheme instanceof AbstractFileSaver)) {
/* 221:273 */           types.add(SchemeType.SAVER);
/* 222:    */         }
/* 223:275 */         if ((scheme instanceof DataGenerator)) {
/* 224:276 */           types.add(SchemeType.DATAGENERATOR);
/* 225:    */         }
/* 226:    */       }
/* 227:280 */       SchemeType selectedType = null;
/* 228:281 */       if (types.size() == 0)
/* 229:    */       {
/* 230:282 */         System.err.println("" + schemeToRun + " is not runnable!");
/* 231:    */         
/* 232:284 */         return;
/* 233:    */       }
/* 234:286 */       if (types.size() == 1)
/* 235:    */       {
/* 236:287 */         selectedType = (SchemeType)types.get(0);
/* 237:    */       }
/* 238:    */       else
/* 239:    */       {
/* 240:289 */         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
/* 241:    */         
/* 242:291 */         boolean done = false;
/* 243:292 */         while (!done)
/* 244:    */         {
/* 245:293 */           System.out.println("" + schemeToRun + " can be executed as any of the following:");
/* 246:295 */           for (int i = 0; i < types.size(); i++) {
/* 247:296 */             System.out.println("\t" + (i + 1) + ") " + types.get(i));
/* 248:    */           }
/* 249:298 */           System.out.print("\nEnter a number > ");
/* 250:299 */           String choice = null;
/* 251:300 */           int typeNumber = 0;
/* 252:    */           try
/* 253:    */           {
/* 254:302 */             choice = br.readLine();
/* 255:303 */             if (choice.equals("")) {
/* 256:305 */               return;
/* 257:    */             }
/* 258:307 */             typeNumber = Integer.parseInt(choice);
/* 259:308 */             typeNumber--;
/* 260:309 */             if ((typeNumber >= 0) && (typeNumber < types.size()))
/* 261:    */             {
/* 262:310 */               selectedType = (SchemeType)types.get(typeNumber);
/* 263:311 */               done = true;
/* 264:    */             }
/* 265:    */           }
/* 266:    */           catch (IOException ex) {}
/* 267:    */         }
/* 268:    */       }
/* 269:320 */       if (selectedType == SchemeType.CLASSIFIER) {
/* 270:321 */         AbstractClassifier.runClassifier((Classifier)scheme, options);
/* 271:323 */       } else if (selectedType == SchemeType.CLUSTERER) {
/* 272:324 */         AbstractClusterer.runClusterer((Clusterer)scheme, options);
/* 273:326 */       } else if (selectedType == SchemeType.ATTRIBUTE_SELECTION) {
/* 274:327 */         ASEvaluation.runEvaluator((ASEvaluation)scheme, options);
/* 275:329 */       } else if (selectedType == SchemeType.ASSOCIATOR) {
/* 276:330 */         AbstractAssociator.runAssociator((Associator)scheme, options);
/* 277:332 */       } else if (selectedType == SchemeType.FILTER) {
/* 278:333 */         Filter.runFilter((Filter)scheme, options);
/* 279:334 */       } else if (selectedType == SchemeType.LOADER) {
/* 280:335 */         AbstractFileLoader.runFileLoader((AbstractFileLoader)scheme, options);
/* 281:337 */       } else if (selectedType == SchemeType.SAVER) {
/* 282:338 */         AbstractFileSaver.runFileSaver((AbstractFileSaver)scheme, options);
/* 283:340 */       } else if (selectedType == SchemeType.DATAGENERATOR) {
/* 284:341 */         DataGenerator.runDataGenerator((DataGenerator)scheme, options);
/* 285:343 */       } else if (selectedType == SchemeType.COMMANDLINE) {
/* 286:344 */         ((CommandlineRunnable)scheme).run(scheme, options);
/* 287:    */       }
/* 288:    */     }
/* 289:    */     catch (Exception e)
/* 290:    */     {
/* 291:347 */       if (((e.getMessage() != null) && (e.getMessage().indexOf("General options") == -1)) || (e.getMessage() == null)) {
/* 292:350 */         e.printStackTrace();
/* 293:    */       } else {
/* 294:352 */         System.err.println(e.getMessage());
/* 295:    */       }
/* 296:    */     }
/* 297:    */   }
/* 298:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.Run
 * JD-Core Version:    0.7.0.1
 */
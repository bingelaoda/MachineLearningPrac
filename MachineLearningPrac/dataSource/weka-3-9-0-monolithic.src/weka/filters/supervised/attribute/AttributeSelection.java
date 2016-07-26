/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.attributeSelection.ASEvaluation;
/*   8:    */ import weka.attributeSelection.ASSearch;
/*   9:    */ import weka.attributeSelection.AttributeEvaluator;
/*  10:    */ import weka.attributeSelection.AttributeTransformer;
/*  11:    */ import weka.attributeSelection.BestFirst;
/*  12:    */ import weka.attributeSelection.CfsSubsetEval;
/*  13:    */ import weka.attributeSelection.Ranker;
/*  14:    */ import weka.core.Attribute;
/*  15:    */ import weka.core.Capabilities;
/*  16:    */ import weka.core.Capabilities.Capability;
/*  17:    */ import weka.core.DenseInstance;
/*  18:    */ import weka.core.Instance;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.Option;
/*  21:    */ import weka.core.OptionHandler;
/*  22:    */ import weka.core.RevisionUtils;
/*  23:    */ import weka.core.SparseInstance;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ import weka.filters.SupervisedFilter;
/*  27:    */ 
/*  28:    */ public class AttributeSelection
/*  29:    */   extends Filter
/*  30:    */   implements SupervisedFilter, OptionHandler
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = -296211247688169716L;
/*  33:    */   private weka.attributeSelection.AttributeSelection m_trainSelector;
/*  34:    */   private ASEvaluation m_ASEvaluator;
/*  35:    */   private ASSearch m_ASSearch;
/*  36:    */   private int[] m_SelectedAttributes;
/*  37:    */   protected boolean m_hasClass;
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:150 */     return "A supervised attribute filter that can be used to select attributes. It is very flexible and allows various search and evaluation methods to be combined.";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public AttributeSelection()
/*  45:    */   {
/*  46:160 */     resetOptions();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Enumeration<Option> listOptions()
/*  50:    */   {
/*  51:171 */     Vector<Option> newVector = new Vector(6);
/*  52:    */     
/*  53:173 */     newVector.addElement(new Option("\tSets search method for subset evaluators.\n\teg. -S \"weka.attributeSelection.BestFirst -S 8\"", "S", 1, "-S <\"Name of search class [search options]\">"));
/*  54:    */     
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:178 */     newVector.addElement(new Option("\tSets attribute/subset evaluator.\n\teg. -E \"weka.attributeSelection.CfsSubsetEval -L\"", "E", 1, "-E <\"Name of attribute/subset evaluation class [evaluator options]\">"));
/*  59:183 */     if ((this.m_ASEvaluator != null) && ((this.m_ASEvaluator instanceof OptionHandler)))
/*  60:    */     {
/*  61:185 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_ASEvaluator.getClass().getName() + ":"));
/*  62:    */       
/*  63:    */ 
/*  64:188 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ASEvaluator).listOptions()));
/*  65:    */     }
/*  66:192 */     if ((this.m_ASSearch != null) && ((this.m_ASSearch instanceof OptionHandler)))
/*  67:    */     {
/*  68:194 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to search " + this.m_ASSearch.getClass().getName() + ":"));
/*  69:    */       
/*  70:    */ 
/*  71:197 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ASSearch).listOptions()));
/*  72:    */     }
/*  73:200 */     return newVector.elements();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setOptions(String[] options)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:274 */     resetOptions();
/*  80:276 */     if (Utils.getFlag('X', options)) {
/*  81:277 */       throw new Exception("Cross validation is not a valid option when using attribute selection as a Filter.");
/*  82:    */     }
/*  83:281 */     String optionString = Utils.getOption('E', options);
/*  84:282 */     if (optionString.length() != 0)
/*  85:    */     {
/*  86:283 */       optionString = optionString.trim();
/*  87:    */       
/*  88:285 */       int breakLoc = optionString.indexOf(' ');
/*  89:286 */       String evalClassName = optionString;
/*  90:287 */       String evalOptionsString = "";
/*  91:288 */       String[] evalOptions = null;
/*  92:289 */       if (breakLoc != -1)
/*  93:    */       {
/*  94:290 */         evalClassName = optionString.substring(0, breakLoc);
/*  95:291 */         evalOptionsString = optionString.substring(breakLoc).trim();
/*  96:292 */         evalOptions = Utils.splitOptions(evalOptionsString);
/*  97:    */       }
/*  98:294 */       setEvaluator(ASEvaluation.forName(evalClassName, evalOptions));
/*  99:    */     }
/* 100:297 */     if ((this.m_ASEvaluator instanceof AttributeEvaluator)) {
/* 101:298 */       setSearch(new Ranker());
/* 102:    */     }
/* 103:301 */     optionString = Utils.getOption('S', options);
/* 104:302 */     if (optionString.length() != 0)
/* 105:    */     {
/* 106:303 */       optionString = optionString.trim();
/* 107:304 */       int breakLoc = optionString.indexOf(' ');
/* 108:305 */       String SearchClassName = optionString;
/* 109:306 */       String SearchOptionsString = "";
/* 110:307 */       String[] SearchOptions = null;
/* 111:308 */       if (breakLoc != -1)
/* 112:    */       {
/* 113:309 */         SearchClassName = optionString.substring(0, breakLoc);
/* 114:310 */         SearchOptionsString = optionString.substring(breakLoc).trim();
/* 115:311 */         SearchOptions = Utils.splitOptions(SearchOptionsString);
/* 116:    */       }
/* 117:313 */       setSearch(ASSearch.forName(SearchClassName, SearchOptions));
/* 118:    */     }
/* 119:316 */     Utils.checkForRemainingOptions(options);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String[] getOptions()
/* 123:    */   {
/* 124:327 */     String[] EvaluatorOptions = new String[0];
/* 125:328 */     String[] SearchOptions = new String[0];
/* 126:329 */     int current = 0;
/* 127:331 */     if ((this.m_ASEvaluator instanceof OptionHandler)) {
/* 128:332 */       EvaluatorOptions = ((OptionHandler)this.m_ASEvaluator).getOptions();
/* 129:    */     }
/* 130:335 */     if ((this.m_ASSearch instanceof OptionHandler)) {
/* 131:336 */       SearchOptions = ((OptionHandler)this.m_ASSearch).getOptions();
/* 132:    */     }
/* 133:339 */     String[] setOptions = new String[10];
/* 134:340 */     setOptions[(current++)] = "-E";
/* 135:341 */     setOptions[(current++)] = (getEvaluator().getClass().getName() + " " + Utils.joinOptions(EvaluatorOptions));
/* 136:    */     
/* 137:    */ 
/* 138:    */ 
/* 139:345 */     setOptions[(current++)] = "-S";
/* 140:346 */     setOptions[(current++)] = (getSearch().getClass().getName() + " " + Utils.joinOptions(SearchOptions));
/* 141:349 */     while (current < setOptions.length) {
/* 142:350 */       setOptions[(current++)] = "";
/* 143:    */     }
/* 144:353 */     return setOptions;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String evaluatorTipText()
/* 148:    */   {
/* 149:364 */     return "Determines how attributes/attribute subsets are evaluated.";
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setEvaluator(ASEvaluation evaluator)
/* 153:    */   {
/* 154:373 */     this.m_ASEvaluator = evaluator;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String searchTipText()
/* 158:    */   {
/* 159:384 */     return "Determines the search method.";
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setSearch(ASSearch search)
/* 163:    */   {
/* 164:393 */     this.m_ASSearch = search;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public ASEvaluation getEvaluator()
/* 168:    */   {
/* 169:403 */     return this.m_ASEvaluator;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public ASSearch getSearch()
/* 173:    */   {
/* 174:413 */     return this.m_ASSearch;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public Capabilities getCapabilities()
/* 178:    */   {
/* 179:    */     Capabilities result;
/* 180:426 */     if (this.m_ASEvaluator == null)
/* 181:    */     {
/* 182:427 */       Capabilities result = super.getCapabilities();
/* 183:428 */       result.disableAll();
/* 184:    */     }
/* 185:    */     else
/* 186:    */     {
/* 187:430 */       result = this.m_ASEvaluator.getCapabilities();
/* 188:    */       
/* 189:    */ 
/* 190:    */ 
/* 191:434 */       result.enable(Capabilities.Capability.NO_CLASS);
/* 192:    */     }
/* 193:437 */     result.setMinimumNumberInstances(0);
/* 194:    */     
/* 195:439 */     return result;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public boolean input(Instance instance)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:456 */     if (getInputFormat() == null) {
/* 202:457 */       throw new IllegalStateException("No input instance format defined");
/* 203:    */     }
/* 204:460 */     if (this.m_NewBatch)
/* 205:    */     {
/* 206:461 */       resetQueue();
/* 207:462 */       this.m_NewBatch = false;
/* 208:    */     }
/* 209:465 */     if (isOutputFormatDefined())
/* 210:    */     {
/* 211:466 */       convertInstance(instance);
/* 212:467 */       return true;
/* 213:    */     }
/* 214:470 */     bufferInput(instance);
/* 215:471 */     return false;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public boolean batchFinished()
/* 219:    */     throws Exception
/* 220:    */   {
/* 221:486 */     if (getInputFormat() == null) {
/* 222:487 */       throw new IllegalStateException("No input instance format defined");
/* 223:    */     }
/* 224:490 */     if (!isOutputFormatDefined())
/* 225:    */     {
/* 226:491 */       this.m_hasClass = (getInputFormat().classIndex() >= 0);
/* 227:    */       
/* 228:493 */       this.m_trainSelector.setEvaluator(this.m_ASEvaluator);
/* 229:494 */       this.m_trainSelector.setSearch(this.m_ASSearch);
/* 230:495 */       this.m_trainSelector.SelectAttributes(getInputFormat());
/* 231:    */       
/* 232:    */ 
/* 233:498 */       this.m_SelectedAttributes = this.m_trainSelector.selectedAttributes();
/* 234:499 */       if (this.m_SelectedAttributes == null) {
/* 235:500 */         throw new Exception("No selected attributes\n");
/* 236:    */       }
/* 237:503 */       setOutputFormat();
/* 238:506 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 239:507 */         convertInstance(getInputFormat().instance(i));
/* 240:    */       }
/* 241:509 */       flushInput();
/* 242:    */     }
/* 243:512 */     this.m_NewBatch = true;
/* 244:513 */     return numPendingOutput() != 0;
/* 245:    */   }
/* 246:    */   
/* 247:    */   protected void setOutputFormat()
/* 248:    */     throws Exception
/* 249:    */   {
/* 250:525 */     if (this.m_SelectedAttributes == null)
/* 251:    */     {
/* 252:526 */       setOutputFormat(null);
/* 253:527 */       return;
/* 254:    */     }
/* 255:530 */     ArrayList<Attribute> attributes = new ArrayList(this.m_SelectedAttributes.length);
/* 256:    */     Instances informat;
/* 257:    */     Instances informat;
/* 258:534 */     if ((this.m_ASEvaluator instanceof AttributeTransformer)) {
/* 259:535 */       informat = ((AttributeTransformer)this.m_ASEvaluator).transformedHeader();
/* 260:    */     } else {
/* 261:537 */       informat = getInputFormat();
/* 262:    */     }
/* 263:540 */     for (int i = 0; i < this.m_SelectedAttributes.length; i++) {
/* 264:541 */       attributes.add((Attribute)informat.attribute(this.m_SelectedAttributes[i]).copy());
/* 265:    */     }
/* 266:545 */     Instances outputFormat = new Instances(getInputFormat().relationName(), attributes, 0);
/* 267:550 */     if (this.m_hasClass) {
/* 268:551 */       outputFormat.setClassIndex(this.m_SelectedAttributes.length - 1);
/* 269:    */     }
/* 270:554 */     setOutputFormat(outputFormat);
/* 271:    */   }
/* 272:    */   
/* 273:    */   protected void convertInstance(Instance instance)
/* 274:    */     throws Exception
/* 275:    */   {
/* 276:565 */     double[] newVals = new double[getOutputFormat().numAttributes()];
/* 277:567 */     if ((this.m_ASEvaluator instanceof AttributeTransformer))
/* 278:    */     {
/* 279:568 */       Instance tempInstance = ((AttributeTransformer)this.m_ASEvaluator).convertInstance(instance);
/* 280:570 */       for (int i = 0; i < this.m_SelectedAttributes.length; i++)
/* 281:    */       {
/* 282:571 */         int current = this.m_SelectedAttributes[i];
/* 283:572 */         newVals[i] = tempInstance.value(current);
/* 284:    */       }
/* 285:    */     }
/* 286:    */     else
/* 287:    */     {
/* 288:575 */       for (int i = 0; i < this.m_SelectedAttributes.length; i++)
/* 289:    */       {
/* 290:576 */         int current = this.m_SelectedAttributes[i];
/* 291:577 */         newVals[i] = instance.value(current);
/* 292:    */       }
/* 293:    */     }
/* 294:580 */     if ((instance instanceof SparseInstance)) {
/* 295:581 */       push(new SparseInstance(instance.weight(), newVals));
/* 296:    */     } else {
/* 297:583 */       push(new DenseInstance(instance.weight(), newVals));
/* 298:    */     }
/* 299:    */   }
/* 300:    */   
/* 301:    */   protected void resetOptions()
/* 302:    */   {
/* 303:592 */     this.m_trainSelector = new weka.attributeSelection.AttributeSelection();
/* 304:593 */     setEvaluator(new CfsSubsetEval());
/* 305:594 */     setSearch(new BestFirst());
/* 306:595 */     this.m_SelectedAttributes = null;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public String getRevision()
/* 310:    */   {
/* 311:605 */     return RevisionUtils.extract("$Revision: 10866 $");
/* 312:    */   }
/* 313:    */   
/* 314:    */   public static void main(String[] argv)
/* 315:    */   {
/* 316:614 */     runFilter(new AttributeSelection(), argv);
/* 317:    */   }
/* 318:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.AttributeSelection
 * JD-Core Version:    0.7.0.1
 */
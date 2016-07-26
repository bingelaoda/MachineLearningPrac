/*    1:     */ package weka.knowledgeflow.steps;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Arrays;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Map;
/*    9:     */ import java.util.Map.Entry;
/*   10:     */ import java.util.Set;
/*   11:     */ import java.util.concurrent.atomic.AtomicInteger;
/*   12:     */ import java.util.regex.Matcher;
/*   13:     */ import java.util.regex.Pattern;
/*   14:     */ import javax.swing.tree.DefaultMutableTreeNode;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.Environment;
/*   17:     */ import weka.core.Instance;
/*   18:     */ import weka.core.Instances;
/*   19:     */ import weka.core.Utils;
/*   20:     */ import weka.core.WekaException;
/*   21:     */ import weka.knowledgeflow.Data;
/*   22:     */ import weka.knowledgeflow.ExecutionEnvironment;
/*   23:     */ import weka.knowledgeflow.StepManager;
/*   24:     */ 
/*   25:     */ @KFStep(name="FlowByExpression", category="Flow", toolTipText="Route instances according to the evaluation of a logical expression. The expression can test the values of one or more incoming attributes. The test can involve constants or comparing one attribute's values to another. Inequalities along with string operations such as contains, starts-with, ends-with and regular expressions may be used as operators. \"True\" instances can be sent to one downstream step and \"False\" instances sent to another.", iconPath="weka/gui/knowledgeflow/icons/FlowByExpression.png")
/*   26:     */ public class FlowByExpression
/*   27:     */   extends BaseStep
/*   28:     */ {
/*   29:     */   private static final long serialVersionUID = 7006511778677802572L;
/*   30:     */   protected boolean m_isReset;
/*   31:     */   protected ExpressionNode m_root;
/*   32:     */   protected String m_expressionString;
/*   33:     */   protected String m_customNameOfTrueStep;
/*   34:     */   protected String m_customNameOfFalseStep;
/*   35:     */   protected Instances m_incomingStructure;
/*   36:     */   protected AtomicInteger m_batchCount;
/*   37:     */   protected boolean m_validTrueStep;
/*   38:     */   protected boolean m_validFalseStep;
/*   39:     */   protected Data m_streamingData;
/*   40:     */   
/*   41:     */   public FlowByExpression()
/*   42:     */   {
/*   43:  75 */     this.m_expressionString = "";
/*   44:     */     
/*   45:     */ 
/*   46:     */ 
/*   47:     */ 
/*   48:     */ 
/*   49:  81 */     this.m_customNameOfTrueStep = "";
/*   50:     */     
/*   51:     */ 
/*   52:     */ 
/*   53:     */ 
/*   54:     */ 
/*   55:  87 */     this.m_customNameOfFalseStep = "";
/*   56:     */   }
/*   57:     */   
/*   58:     */   public void setExpressionString(String expressionString)
/*   59:     */   {
/*   60: 110 */     this.m_expressionString = expressionString;
/*   61:     */   }
/*   62:     */   
/*   63:     */   public String getExpressionString()
/*   64:     */   {
/*   65: 119 */     return this.m_expressionString;
/*   66:     */   }
/*   67:     */   
/*   68:     */   public void setTrueStepName(String trueStep)
/*   69:     */   {
/*   70: 128 */     this.m_customNameOfTrueStep = trueStep;
/*   71:     */   }
/*   72:     */   
/*   73:     */   public String getTrueStepName()
/*   74:     */   {
/*   75: 137 */     return this.m_customNameOfTrueStep;
/*   76:     */   }
/*   77:     */   
/*   78:     */   public void setFalseStepName(String falseStep)
/*   79:     */   {
/*   80: 146 */     this.m_customNameOfFalseStep = falseStep;
/*   81:     */   }
/*   82:     */   
/*   83:     */   public String getFalseStepName()
/*   84:     */   {
/*   85: 155 */     return this.m_customNameOfFalseStep;
/*   86:     */   }
/*   87:     */   
/*   88:     */   public List<String> getDownstreamStepNames()
/*   89:     */   {
/*   90: 164 */     List<String> result = new ArrayList();
/*   91: 166 */     for (List<StepManager> o : getStepManager().getOutgoingConnections().values()) {
/*   92: 168 */       for (StepManager m : o) {
/*   93: 169 */         result.add(m.getName());
/*   94:     */       }
/*   95:     */     }
/*   96: 173 */     return result;
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void stepInit()
/*  100:     */     throws WekaException
/*  101:     */   {
/*  102: 183 */     this.m_isReset = true;
/*  103:     */     
/*  104:     */ 
/*  105: 186 */     this.m_validTrueStep = (getStepManager().getOutgoingConnectedStepWithName(environmentSubstitute(this.m_customNameOfTrueStep)) != null);
/*  106:     */     
/*  107:     */ 
/*  108: 189 */     this.m_validFalseStep = (getStepManager().getOutgoingConnectedStepWithName(environmentSubstitute(this.m_customNameOfFalseStep)) != null);
/*  109:     */     
/*  110:     */ 
/*  111:     */ 
/*  112: 193 */     this.m_incomingStructure = null;
/*  113: 195 */     if ((this.m_expressionString == null) || (this.m_expressionString.length() == 0)) {
/*  114: 196 */       throw new WekaException("No expression defined!");
/*  115:     */     }
/*  116:     */   }
/*  117:     */   
/*  118:     */   public List<String> getIncomingConnectionTypes()
/*  119:     */   {
/*  120: 212 */     if (getStepManager().numIncomingConnections() == 0) {
/*  121: 213 */       return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet", "instance" });
/*  122:     */     }
/*  123: 218 */     if (getStepManager().numIncomingConnectionsOfType("instance") == 0) {
/*  124: 219 */       return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/*  125:     */     }
/*  126: 222 */     return null;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public List<String> getOutgoingConnectionTypes()
/*  130:     */   {
/*  131: 236 */     List<String> result = new ArrayList();
/*  132: 237 */     if (getStepManager().numIncomingConnectionsOfType("instance") > 0)
/*  133:     */     {
/*  134: 238 */       result.add("instance");
/*  135:     */     }
/*  136: 239 */     else if (getStepManager().numIncomingConnections() > 0)
/*  137:     */     {
/*  138: 240 */       if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0) {
/*  139: 242 */         result.add("dataSet");
/*  140:     */       }
/*  141: 245 */       if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/*  142: 247 */         result.add("trainingSet");
/*  143:     */       }
/*  144: 250 */       if (getStepManager().numIncomingConnectionsOfType("testSet") > 0) {
/*  145: 252 */         result.add("testSet");
/*  146:     */       }
/*  147:     */     }
/*  148: 256 */     return result;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public Instances outputStructureForConnectionType(String connectionName)
/*  152:     */     throws WekaException
/*  153:     */   {
/*  154: 273 */     if (getStepManager().numIncomingConnections() > 0)
/*  155:     */     {
/*  156: 274 */       Iterator i$ = getStepManager().getIncomingConnections().entrySet().iterator();
/*  157: 274 */       if (i$.hasNext())
/*  158:     */       {
/*  159: 274 */         Map.Entry<String, List<StepManager>> e = (Map.Entry)i$.next();
/*  160:     */         
/*  161:     */ 
/*  162:     */ 
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166: 281 */         String incomingConnType = (String)e.getKey();
/*  167: 282 */         Instances incomingStruc = getStepManager().getIncomingStructureFromStep((StepManager)((List)e.getValue()).get(0), incomingConnType);
/*  168:     */         
/*  169:     */ 
/*  170: 285 */         return incomingStruc;
/*  171:     */       }
/*  172:     */     }
/*  173: 289 */     return null;
/*  174:     */   }
/*  175:     */   
/*  176:     */   public void processIncoming(Data data)
/*  177:     */     throws WekaException
/*  178:     */   {
/*  179: 300 */     if (this.m_isReset)
/*  180:     */     {
/*  181: 301 */       this.m_isReset = false;
/*  182: 303 */       if (getStepManager().numIncomingConnectionsOfType("instance") > 0)
/*  183:     */       {
/*  184: 305 */         this.m_streamingData = new Data("instance");
/*  185: 306 */         Instance inst = (Instance)data.getPrimaryPayload();
/*  186: 307 */         this.m_incomingStructure = new Instances(inst.dataset(), 0);
/*  187:     */       }
/*  188:     */       else
/*  189:     */       {
/*  190: 309 */         this.m_incomingStructure = ((Instances)data.getPrimaryPayload());
/*  191: 310 */         this.m_incomingStructure = new Instances(this.m_incomingStructure, 0);
/*  192: 311 */         this.m_batchCount = new AtomicInteger(getStepManager().numIncomingConnections());
/*  193:     */         
/*  194: 313 */         getStepManager().processing();
/*  195:     */       }
/*  196: 315 */       this.m_root = new BracketNode();
/*  197: 316 */       this.m_root.parseFromInternal(this.m_expressionString);
/*  198: 317 */       this.m_root.init(this.m_incomingStructure, getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  199:     */     }
/*  200: 321 */     if (this.m_streamingData == null)
/*  201:     */     {
/*  202: 323 */       Instances batch = (Instances)data.getPrimaryPayload();
/*  203: 324 */       if (!this.m_incomingStructure.equalHeaders(batch)) {
/*  204: 325 */         throw new WekaException("Incoming batches with different structure: " + this.m_incomingStructure.equalHeadersMsg(batch));
/*  205:     */       }
/*  206: 328 */       processBatch(data);
/*  207: 329 */       if (isStopRequested()) {
/*  208: 330 */         getStepManager().interrupted();
/*  209: 331 */       } else if (this.m_batchCount.get() == 0) {
/*  210: 332 */         getStepManager().finished();
/*  211:     */       }
/*  212:     */     }
/*  213:     */     else
/*  214:     */     {
/*  215: 336 */       processStreaming(data);
/*  216: 338 */       if (isStopRequested()) {
/*  217: 339 */         getStepManager().interrupted();
/*  218:     */       }
/*  219:     */     }
/*  220:     */   }
/*  221:     */   
/*  222:     */   protected void processStreaming(Data data)
/*  223:     */     throws WekaException
/*  224:     */   {
/*  225: 351 */     if (getStepManager().isStreamFinished(data))
/*  226:     */     {
/*  227: 352 */       this.m_streamingData.clearPayload();
/*  228: 353 */       getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/*  229: 354 */       return;
/*  230:     */     }
/*  231: 356 */     getStepManager().throughputUpdateStart();
/*  232: 357 */     Instance toProcess = (Instance)data.getPrimaryPayload();
/*  233:     */     
/*  234: 359 */     boolean result = this.m_root.evaluate(toProcess, true);
/*  235: 360 */     this.m_streamingData.setPayloadElement("instance", toProcess);
/*  236: 361 */     if (result)
/*  237:     */     {
/*  238: 362 */       if (this.m_validTrueStep) {
/*  239: 363 */         getStepManager().outputData("instance", this.m_customNameOfTrueStep, this.m_streamingData);
/*  240:     */       }
/*  241:     */     }
/*  242: 367 */     else if (this.m_validFalseStep) {
/*  243: 368 */       getStepManager().outputData("instance", this.m_customNameOfFalseStep, this.m_streamingData);
/*  244:     */     }
/*  245: 373 */     getStepManager().throughputUpdateEnd();
/*  246:     */   }
/*  247:     */   
/*  248:     */   protected void processBatch(Data data)
/*  249:     */     throws WekaException
/*  250:     */   {
/*  251: 383 */     Instances incoming = (Instances)data.getPrimaryPayload();
/*  252: 384 */     Instances trueBatch = new Instances(incoming, 0);
/*  253: 385 */     Instances falseBatch = new Instances(incoming, 0);
/*  254: 387 */     for (int i = 0; i < incoming.numInstances(); i++)
/*  255:     */     {
/*  256: 388 */       if (isStopRequested()) {
/*  257: 389 */         return;
/*  258:     */       }
/*  259: 392 */       Instance current = incoming.instance(i);
/*  260: 393 */       boolean result = this.m_root.evaluate(current, true);
/*  261: 395 */       if (result)
/*  262:     */       {
/*  263: 396 */         if (this.m_validTrueStep) {
/*  264: 397 */           trueBatch.add(current);
/*  265:     */         }
/*  266:     */       }
/*  267: 400 */       else if (this.m_validFalseStep) {
/*  268: 401 */         falseBatch.add(current);
/*  269:     */       }
/*  270:     */     }
/*  271: 406 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num", Integer.valueOf(1));
/*  272:     */     
/*  273: 408 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  274: 410 */     if (this.m_validTrueStep)
/*  275:     */     {
/*  276: 411 */       getStepManager().logDetailed("Routing " + trueBatch.numInstances() + " instances to step " + this.m_customNameOfTrueStep);
/*  277:     */       
/*  278:     */ 
/*  279: 414 */       Data outputData = new Data(data.getConnectionName(), trueBatch);
/*  280: 415 */       outputData.setPayloadElement("aux_set_num", setNum);
/*  281: 416 */       outputData.setPayloadElement("aux_max_set_num", maxSetNum);
/*  282:     */       
/*  283: 418 */       getStepManager().outputData(data.getConnectionName(), this.m_customNameOfTrueStep, outputData);
/*  284:     */     }
/*  285: 422 */     if (this.m_validFalseStep)
/*  286:     */     {
/*  287: 423 */       getStepManager().logDetailed("Routing " + falseBatch.numInstances() + " instances to step " + this.m_customNameOfFalseStep);
/*  288:     */       
/*  289:     */ 
/*  290: 426 */       Data outputData = new Data(data.getConnectionName(), falseBatch);
/*  291: 427 */       outputData.setPayloadElement("aux_set_num", setNum);
/*  292: 428 */       outputData.setPayloadElement("aux_max_set_num", maxSetNum);
/*  293:     */       
/*  294: 430 */       getStepManager().outputData(data.getConnectionName(), this.m_customNameOfFalseStep, outputData);
/*  295:     */     }
/*  296: 434 */     if (setNum == maxSetNum) {
/*  297: 435 */       this.m_batchCount.decrementAndGet();
/*  298:     */     }
/*  299:     */   }
/*  300:     */   
/*  301:     */   public String getCustomEditorForStep()
/*  302:     */   {
/*  303: 449 */     return "weka.gui.knowledgeflow.steps.FlowByExpressionStepEditorDialog";
/*  304:     */   }
/*  305:     */   
/*  306:     */   public static abstract class ExpressionNode
/*  307:     */     implements Serializable
/*  308:     */   {
/*  309:     */     private static final long serialVersionUID = -8427857202322768762L;
/*  310:     */     protected boolean m_isAnOr;
/*  311:     */     protected boolean m_isNegated;
/*  312:     */     protected transient Environment m_env;
/*  313: 472 */     protected boolean m_showAndOr = true;
/*  314:     */     
/*  315:     */     public void setIsOr(boolean isOr)
/*  316:     */     {
/*  317: 480 */       this.m_isAnOr = isOr;
/*  318:     */     }
/*  319:     */     
/*  320:     */     public boolean isOr()
/*  321:     */     {
/*  322: 489 */       return this.m_isAnOr;
/*  323:     */     }
/*  324:     */     
/*  325:     */     public boolean isNegated()
/*  326:     */     {
/*  327: 498 */       return this.m_isNegated;
/*  328:     */     }
/*  329:     */     
/*  330:     */     public void setNegated(boolean negated)
/*  331:     */     {
/*  332: 507 */       this.m_isNegated = negated;
/*  333:     */     }
/*  334:     */     
/*  335:     */     public void setShowAndOr(boolean show)
/*  336:     */     {
/*  337: 516 */       this.m_showAndOr = show;
/*  338:     */     }
/*  339:     */     
/*  340:     */     public void init(Instances structure, Environment env)
/*  341:     */     {
/*  342: 526 */       this.m_env = env;
/*  343:     */     }
/*  344:     */     
/*  345:     */     public abstract boolean evaluate(Instance paramInstance, boolean paramBoolean);
/*  346:     */     
/*  347:     */     public abstract void toStringInternal(StringBuffer paramStringBuffer);
/*  348:     */     
/*  349:     */     public abstract void toStringDisplay(StringBuffer paramStringBuffer);
/*  350:     */     
/*  351:     */     protected abstract String parseFromInternal(String paramString);
/*  352:     */     
/*  353:     */     public abstract DefaultMutableTreeNode toJTree(DefaultMutableTreeNode paramDefaultMutableTreeNode);
/*  354:     */   }
/*  355:     */   
/*  356:     */   public static class BracketNode
/*  357:     */     extends FlowByExpression.ExpressionNode
/*  358:     */     implements Serializable
/*  359:     */   {
/*  360:     */     private static final long serialVersionUID = 8732159083173001115L;
/*  361: 582 */     protected List<FlowByExpression.ExpressionNode> m_children = new ArrayList();
/*  362:     */     
/*  363:     */     public void init(Instances structure, Environment env)
/*  364:     */     {
/*  365: 586 */       super.init(structure, env);
/*  366: 588 */       for (FlowByExpression.ExpressionNode n : this.m_children) {
/*  367: 589 */         n.init(structure, env);
/*  368:     */       }
/*  369:     */     }
/*  370:     */     
/*  371:     */     public boolean evaluate(Instance inst, boolean result)
/*  372:     */     {
/*  373: 596 */       boolean thisNode = true;
/*  374: 597 */       if (this.m_children.size() > 0)
/*  375:     */       {
/*  376: 598 */         for (FlowByExpression.ExpressionNode n : this.m_children) {
/*  377: 599 */           thisNode = n.evaluate(inst, thisNode);
/*  378:     */         }
/*  379: 601 */         if (isNegated()) {
/*  380: 602 */           thisNode = !thisNode;
/*  381:     */         }
/*  382:     */       }
/*  383: 606 */       return (result) || (thisNode);
/*  384:     */     }
/*  385:     */     
/*  386:     */     public void addChild(FlowByExpression.ExpressionNode child)
/*  387:     */     {
/*  388: 615 */       this.m_children.add(child);
/*  389: 617 */       if (this.m_children.size() > 0) {
/*  390: 618 */         ((FlowByExpression.ExpressionNode)this.m_children.get(0)).setShowAndOr(false);
/*  391:     */       }
/*  392:     */     }
/*  393:     */     
/*  394:     */     public void removeChild(FlowByExpression.ExpressionNode child)
/*  395:     */     {
/*  396: 628 */       this.m_children.remove(child);
/*  397: 630 */       if (this.m_children.size() > 0) {
/*  398: 631 */         ((FlowByExpression.ExpressionNode)this.m_children.get(0)).setShowAndOr(false);
/*  399:     */       }
/*  400:     */     }
/*  401:     */     
/*  402:     */     public String toString()
/*  403:     */     {
/*  404: 640 */       String result = "( )";
/*  405: 641 */       if (isNegated()) {
/*  406: 642 */         result = "!" + result;
/*  407:     */       }
/*  408: 645 */       if (this.m_showAndOr) {
/*  409: 646 */         if (this.m_isAnOr) {
/*  410: 647 */           result = "|| " + result;
/*  411:     */         } else {
/*  412: 649 */           result = "&& " + result;
/*  413:     */         }
/*  414:     */       }
/*  415: 653 */       return result;
/*  416:     */     }
/*  417:     */     
/*  418:     */     public DefaultMutableTreeNode toJTree(DefaultMutableTreeNode parent)
/*  419:     */     {
/*  420: 659 */       DefaultMutableTreeNode current = new DefaultMutableTreeNode(this);
/*  421: 660 */       if (parent != null) {
/*  422: 661 */         parent.add(current);
/*  423:     */       }
/*  424: 664 */       for (FlowByExpression.ExpressionNode child : this.m_children) {
/*  425: 665 */         child.toJTree(current);
/*  426:     */       }
/*  427: 668 */       return current;
/*  428:     */     }
/*  429:     */     
/*  430:     */     private void toString(StringBuffer buff, boolean internal)
/*  431:     */     {
/*  432: 672 */       if (this.m_children.size() >= 0)
/*  433:     */       {
/*  434: 673 */         if ((internal) || (this.m_showAndOr)) {
/*  435: 674 */           if (this.m_isAnOr) {
/*  436: 675 */             buff.append("|| ");
/*  437:     */           } else {
/*  438: 677 */             buff.append("&& ");
/*  439:     */           }
/*  440:     */         }
/*  441: 681 */         if (isNegated()) {
/*  442: 682 */           buff.append("!");
/*  443:     */         }
/*  444: 684 */         buff.append("(");
/*  445:     */         
/*  446: 686 */         int count = 0;
/*  447: 687 */         for (FlowByExpression.ExpressionNode child : this.m_children)
/*  448:     */         {
/*  449: 688 */           if (internal) {
/*  450: 689 */             child.toStringInternal(buff);
/*  451:     */           } else {
/*  452: 691 */             child.toStringDisplay(buff);
/*  453:     */           }
/*  454: 693 */           count++;
/*  455: 694 */           if (count != this.m_children.size()) {
/*  456: 695 */             buff.append(" ");
/*  457:     */           }
/*  458:     */         }
/*  459: 698 */         buff.append(")");
/*  460:     */       }
/*  461:     */     }
/*  462:     */     
/*  463:     */     public void toStringDisplay(StringBuffer buff)
/*  464:     */     {
/*  465: 704 */       toString(buff, false);
/*  466:     */     }
/*  467:     */     
/*  468:     */     public void toStringInternal(StringBuffer buff)
/*  469:     */     {
/*  470: 709 */       toString(buff, true);
/*  471:     */     }
/*  472:     */     
/*  473:     */     public String parseFromInternal(String expression)
/*  474:     */     {
/*  475: 714 */       if (expression.startsWith("|| ")) {
/*  476: 715 */         this.m_isAnOr = true;
/*  477:     */       }
/*  478: 718 */       if ((expression.startsWith("|| ")) || (expression.startsWith("&& "))) {
/*  479: 719 */         expression = expression.substring(3, expression.length());
/*  480:     */       }
/*  481: 722 */       if (expression.charAt(0) == '!')
/*  482:     */       {
/*  483: 723 */         setNegated(true);
/*  484: 724 */         expression = expression.substring(1, expression.length());
/*  485:     */       }
/*  486: 727 */       if (expression.charAt(0) != '(') {
/*  487: 728 */         throw new IllegalArgumentException("Malformed expression! Was expecting a \"(\"");
/*  488:     */       }
/*  489: 732 */       expression = expression.substring(1, expression.length());
/*  490: 734 */       while (expression.charAt(0) != ')')
/*  491:     */       {
/*  492: 735 */         int offset = 3;
/*  493: 737 */         if (expression.charAt(offset) == '(')
/*  494:     */         {
/*  495: 738 */           FlowByExpression.ExpressionNode child = new BracketNode();
/*  496: 739 */           expression = child.parseFromInternal(expression);
/*  497: 740 */           this.m_children.add(child);
/*  498:     */         }
/*  499:     */         else
/*  500:     */         {
/*  501: 743 */           FlowByExpression.ExpressionNode child = new FlowByExpression.ExpressionClause();
/*  502: 744 */           expression = child.parseFromInternal(expression);
/*  503: 745 */           this.m_children.add(child);
/*  504:     */         }
/*  505:     */       }
/*  506: 749 */       if (this.m_children.size() > 0) {
/*  507: 750 */         ((FlowByExpression.ExpressionNode)this.m_children.get(0)).setShowAndOr(false);
/*  508:     */       }
/*  509: 753 */       return expression;
/*  510:     */     }
/*  511:     */   }
/*  512:     */   
/*  513:     */   public static class ExpressionClause
/*  514:     */     extends FlowByExpression.ExpressionNode
/*  515:     */     implements Serializable
/*  516:     */   {
/*  517:     */     private static final long serialVersionUID = 2754006654981248325L;
/*  518:     */     protected ExpressionType m_operator;
/*  519:     */     protected String m_lhsAttributeName;
/*  520: 775 */     protected int m_lhsAttIndex = -1;
/*  521:     */     protected String m_rhsOperand;
/*  522:     */     protected boolean m_rhsIsAttribute;
/*  523: 784 */     protected int m_rhsAttIndex = -1;
/*  524:     */     protected String m_resolvedLhsName;
/*  525:     */     protected String m_resolvedRhsOperand;
/*  526:     */     protected Pattern m_regexPattern;
/*  527:     */     protected double m_numericOperand;
/*  528:     */     public ExpressionClause() {}
/*  529:     */     
/*  530:     */     public static abstract enum ExpressionType
/*  531:     */     {
/*  532: 799 */       EQUALS(" = "),  NOTEQUAL(" != "),  LESSTHAN(" < "),  LESSTHANEQUAL(" <= "),  GREATERTHAN(" > "),  GREATERTHANEQUAL(" >= "),  ISMISSING(" isMissing "),  CONTAINS(" contains "),  STARTSWITH(" startsWith "),  ENDSWITH(" endsWith "),  REGEX(" regex ");
/*  533:     */       
/*  534:     */       private final String m_stringVal;
/*  535:     */       
/*  536:     */       abstract boolean evaluate(Instance paramInstance, int paramInt1, String paramString, double paramDouble, Pattern paramPattern, boolean paramBoolean, int paramInt2);
/*  537:     */       
/*  538:     */       private ExpressionType(String name)
/*  539:     */       {
/*  540:1032 */         this.m_stringVal = name;
/*  541:     */       }
/*  542:     */       
/*  543:     */       public String toString()
/*  544:     */       {
/*  545:1037 */         return this.m_stringVal;
/*  546:     */       }
/*  547:     */     }
/*  548:     */     
/*  549:     */     public ExpressionClause(ExpressionType operator, String lhsAttributeName, String rhsOperand, boolean rhsIsAttribute, boolean isAnOr)
/*  550:     */     {
/*  551:1056 */       this.m_operator = operator;
/*  552:1057 */       this.m_lhsAttributeName = lhsAttributeName;
/*  553:1058 */       this.m_rhsOperand = rhsOperand;
/*  554:1059 */       this.m_rhsIsAttribute = rhsIsAttribute;
/*  555:1060 */       this.m_isAnOr = isAnOr;
/*  556:     */     }
/*  557:     */     
/*  558:     */     public String getLHSAttName()
/*  559:     */     {
/*  560:1069 */       return this.m_lhsAttributeName;
/*  561:     */     }
/*  562:     */     
/*  563:     */     public void setLHSAttName(String attName)
/*  564:     */     {
/*  565:1078 */       this.m_lhsAttributeName = attName;
/*  566:     */     }
/*  567:     */     
/*  568:     */     public String getRHSOperand()
/*  569:     */     {
/*  570:1087 */       return this.m_rhsOperand;
/*  571:     */     }
/*  572:     */     
/*  573:     */     public void setRHSOperand(String opp)
/*  574:     */     {
/*  575:1096 */       this.m_rhsOperand = opp;
/*  576:     */     }
/*  577:     */     
/*  578:     */     public boolean isRHSAnAttribute()
/*  579:     */     {
/*  580:1105 */       return this.m_rhsIsAttribute;
/*  581:     */     }
/*  582:     */     
/*  583:     */     public void setRHSIsAnAttribute(boolean rhs)
/*  584:     */     {
/*  585:1114 */       this.m_rhsIsAttribute = rhs;
/*  586:     */     }
/*  587:     */     
/*  588:     */     public ExpressionType getOperator()
/*  589:     */     {
/*  590:1123 */       return this.m_operator;
/*  591:     */     }
/*  592:     */     
/*  593:     */     public void setOperator(ExpressionType opp)
/*  594:     */     {
/*  595:1132 */       this.m_operator = opp;
/*  596:     */     }
/*  597:     */     
/*  598:     */     public void init(Instances structure, Environment env)
/*  599:     */     {
/*  600:1137 */       super.init(structure, env);
/*  601:     */       
/*  602:1139 */       this.m_resolvedLhsName = this.m_lhsAttributeName;
/*  603:1140 */       this.m_resolvedRhsOperand = this.m_rhsOperand;
/*  604:     */       try
/*  605:     */       {
/*  606:1142 */         this.m_resolvedLhsName = this.m_env.substitute(this.m_resolvedLhsName);
/*  607:1143 */         this.m_resolvedRhsOperand = this.m_env.substitute(this.m_resolvedRhsOperand);
/*  608:     */       }
/*  609:     */       catch (Exception ex) {}
/*  610:1147 */       Attribute lhs = null;
/*  611:1149 */       if (this.m_resolvedLhsName.toLowerCase().startsWith("/first")) {
/*  612:1150 */         lhs = structure.attribute(0);
/*  613:1151 */       } else if (this.m_resolvedLhsName.toLowerCase().startsWith("/last")) {
/*  614:1152 */         lhs = structure.attribute(structure.numAttributes() - 1);
/*  615:     */       } else {
/*  616:     */         try
/*  617:     */         {
/*  618:1156 */           int indx = Integer.parseInt(this.m_resolvedLhsName);
/*  619:1157 */           indx--;
/*  620:1158 */           lhs = structure.attribute(indx);
/*  621:     */         }
/*  622:     */         catch (NumberFormatException ex) {}
/*  623:     */       }
/*  624:1164 */       if (lhs == null) {
/*  625:1165 */         lhs = structure.attribute(this.m_resolvedLhsName);
/*  626:     */       }
/*  627:1167 */       if (lhs == null) {
/*  628:1168 */         throw new IllegalArgumentException("Data does not contain attribute \"" + this.m_resolvedLhsName + "\"");
/*  629:     */       }
/*  630:1171 */       this.m_lhsAttIndex = lhs.index();
/*  631:1173 */       if (this.m_rhsIsAttribute)
/*  632:     */       {
/*  633:1174 */         Attribute rhs = null;
/*  634:1177 */         if (this.m_resolvedRhsOperand.toLowerCase().equals("/first")) {
/*  635:1178 */           rhs = structure.attribute(0);
/*  636:1179 */         } else if (this.m_resolvedRhsOperand.toLowerCase().equals("/last")) {
/*  637:1180 */           rhs = structure.attribute(structure.numAttributes() - 1);
/*  638:     */         } else {
/*  639:     */           try
/*  640:     */           {
/*  641:1184 */             int indx = Integer.parseInt(this.m_resolvedRhsOperand);
/*  642:1185 */             indx--;
/*  643:1186 */             rhs = structure.attribute(indx);
/*  644:     */           }
/*  645:     */           catch (NumberFormatException ex) {}
/*  646:     */         }
/*  647:1192 */         if (rhs == null) {
/*  648:1193 */           rhs = structure.attribute(this.m_resolvedRhsOperand);
/*  649:     */         }
/*  650:1195 */         if (rhs == null) {
/*  651:1196 */           throw new IllegalArgumentException("Data does not contain attribute \"" + this.m_resolvedRhsOperand + "\"");
/*  652:     */         }
/*  653:1199 */         this.m_rhsAttIndex = rhs.index();
/*  654:     */       }
/*  655:1200 */       else if ((this.m_operator != ExpressionType.CONTAINS) && (this.m_operator != ExpressionType.STARTSWITH) && (this.m_operator != ExpressionType.ENDSWITH) && (this.m_operator != ExpressionType.REGEX) && (this.m_operator != ExpressionType.ISMISSING))
/*  656:     */       {
/*  657:1207 */         if (lhs.isNominal())
/*  658:     */         {
/*  659:1208 */           this.m_numericOperand = lhs.indexOfValue(this.m_resolvedRhsOperand);
/*  660:1210 */           if (this.m_numericOperand < 0.0D) {
/*  661:1211 */             throw new IllegalArgumentException("Unknown nominal value '" + this.m_resolvedRhsOperand + "' for attribute '" + lhs.name() + "'");
/*  662:     */           }
/*  663:     */         }
/*  664:     */         else
/*  665:     */         {
/*  666:     */           try
/*  667:     */           {
/*  668:1216 */             this.m_numericOperand = Double.parseDouble(this.m_resolvedRhsOperand);
/*  669:     */           }
/*  670:     */           catch (NumberFormatException e)
/*  671:     */           {
/*  672:1218 */             throw new IllegalArgumentException("\"" + this.m_resolvedRhsOperand + "\" is not parseable as a number!");
/*  673:     */           }
/*  674:     */         }
/*  675:     */       }
/*  676:1224 */       if (this.m_operator == ExpressionType.REGEX) {
/*  677:1225 */         this.m_regexPattern = Pattern.compile(this.m_resolvedRhsOperand);
/*  678:     */       }
/*  679:     */     }
/*  680:     */     
/*  681:     */     public boolean evaluate(Instance inst, boolean result)
/*  682:     */     {
/*  683:1232 */       boolean thisNode = this.m_operator.evaluate(inst, this.m_lhsAttIndex, this.m_rhsOperand, this.m_numericOperand, this.m_regexPattern, this.m_rhsIsAttribute, this.m_rhsAttIndex);
/*  684:1236 */       if (isNegated()) {
/*  685:1237 */         thisNode = !thisNode;
/*  686:     */       }
/*  687:1240 */       return (result) || (thisNode);
/*  688:     */     }
/*  689:     */     
/*  690:     */     public String toString()
/*  691:     */     {
/*  692:1245 */       StringBuffer buff = new StringBuffer();
/*  693:1246 */       toStringDisplay(buff);
/*  694:     */       
/*  695:1248 */       return buff.toString();
/*  696:     */     }
/*  697:     */     
/*  698:     */     public void toStringDisplay(StringBuffer buff)
/*  699:     */     {
/*  700:1253 */       toString(buff, false);
/*  701:     */     }
/*  702:     */     
/*  703:     */     public void toStringInternal(StringBuffer buff)
/*  704:     */     {
/*  705:1258 */       toString(buff, true);
/*  706:     */     }
/*  707:     */     
/*  708:     */     public DefaultMutableTreeNode toJTree(DefaultMutableTreeNode parent)
/*  709:     */     {
/*  710:1263 */       parent.add(new DefaultMutableTreeNode(this));
/*  711:     */       
/*  712:1265 */       return parent;
/*  713:     */     }
/*  714:     */     
/*  715:     */     private void toString(StringBuffer buff, boolean internal)
/*  716:     */     {
/*  717:1269 */       if ((internal) || (this.m_showAndOr)) {
/*  718:1270 */         if (this.m_isAnOr) {
/*  719:1271 */           buff.append("|| ");
/*  720:     */         } else {
/*  721:1273 */           buff.append("&& ");
/*  722:     */         }
/*  723:     */       }
/*  724:1276 */       if (isNegated()) {
/*  725:1277 */         buff.append("!");
/*  726:     */       }
/*  727:1280 */       buff.append("[");
/*  728:     */       
/*  729:1282 */       buff.append(this.m_lhsAttributeName);
/*  730:1283 */       if (internal) {
/*  731:1284 */         buff.append("@EC@" + this.m_operator.toString());
/*  732:     */       } else {
/*  733:1286 */         buff.append(" " + this.m_operator.toString());
/*  734:     */       }
/*  735:1289 */       if (this.m_operator != ExpressionType.ISMISSING)
/*  736:     */       {
/*  737:1291 */         if (internal) {
/*  738:1292 */           buff.append("@EC@" + (this.m_rhsIsAttribute ? "@@" : "") + this.m_rhsOperand);
/*  739:     */         } else {
/*  740:1294 */           buff.append(" " + (this.m_rhsIsAttribute ? "ATT: " : "") + this.m_rhsOperand);
/*  741:     */         }
/*  742:     */       }
/*  743:1297 */       else if (internal) {
/*  744:1298 */         buff.append("@EC@");
/*  745:     */       } else {
/*  746:1300 */         buff.append(" ");
/*  747:     */       }
/*  748:1304 */       buff.append("]");
/*  749:     */     }
/*  750:     */     
/*  751:     */     protected String parseFromInternal(String expression)
/*  752:     */     {
/*  753:1311 */       if (expression.startsWith("|| ")) {
/*  754:1312 */         this.m_isAnOr = true;
/*  755:     */       }
/*  756:1315 */       if ((expression.startsWith("|| ")) || (expression.startsWith("&& "))) {
/*  757:1317 */         expression = expression.substring(3, expression.length());
/*  758:     */       }
/*  759:1320 */       if (expression.charAt(0) == '!')
/*  760:     */       {
/*  761:1321 */         setNegated(true);
/*  762:1322 */         expression = expression.substring(1, expression.length());
/*  763:     */       }
/*  764:1325 */       if (expression.charAt(0) != '[') {
/*  765:1326 */         throw new IllegalArgumentException("Was expecting a \"[\" to start this ExpressionClause!");
/*  766:     */       }
/*  767:1329 */       expression = expression.substring(1, expression.length());
/*  768:1330 */       this.m_lhsAttributeName = expression.substring(0, expression.indexOf("@EC@"));
/*  769:1331 */       expression = expression.substring(expression.indexOf("@EC@") + 4, expression.length());
/*  770:     */       
/*  771:     */ 
/*  772:1334 */       String oppName = expression.substring(0, expression.indexOf("@EC@"));
/*  773:1335 */       expression = expression.substring(expression.indexOf("@EC@") + 4, expression.length());
/*  774:1338 */       for (ExpressionType n : ExpressionType.values()) {
/*  775:1339 */         if (n.toString().equals(oppName))
/*  776:     */         {
/*  777:1340 */           this.m_operator = n;
/*  778:1341 */           break;
/*  779:     */         }
/*  780:     */       }
/*  781:1345 */       if (expression.startsWith("@@"))
/*  782:     */       {
/*  783:1347 */         expression = expression.substring(2, expression.length());
/*  784:     */         
/*  785:1349 */         this.m_rhsIsAttribute = true;
/*  786:     */       }
/*  787:1351 */       this.m_rhsOperand = expression.substring(0, expression.indexOf(']'));
/*  788:     */       
/*  789:1353 */       expression = expression.substring(expression.indexOf(']') + 1, expression.length());
/*  790:1356 */       if (expression.charAt(0) == ' ') {
/*  791:1357 */         expression = expression.substring(1, expression.length());
/*  792:     */       }
/*  793:1360 */       return expression;
/*  794:     */     }
/*  795:     */   }
/*  796:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.FlowByExpression
 * JD-Core Version:    0.7.0.1
 */
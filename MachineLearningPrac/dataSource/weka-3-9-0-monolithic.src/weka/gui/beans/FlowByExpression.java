/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.beans.EventSetDescriptor;
/*    5:     */ import java.io.Serializable;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.regex.Matcher;
/*    9:     */ import java.util.regex.Pattern;
/*   10:     */ import javax.swing.JPanel;
/*   11:     */ import javax.swing.tree.DefaultMutableTreeNode;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Environment;
/*   14:     */ import weka.core.EnvironmentHandler;
/*   15:     */ import weka.core.Instance;
/*   16:     */ import weka.core.Instances;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.gui.Logger;
/*   19:     */ 
/*   20:     */ @KFStep(category="Flow", toolTipText="Route instances according to a boolean expression")
/*   21:     */ public class FlowByExpression
/*   22:     */   extends JPanel
/*   23:     */   implements BeanCommon, Visible, Serializable, InstanceListener, TrainingSetListener, TestSetListener, DataSourceListener, EventConstraints, EnvironmentHandler, DataSource, StructureProducer
/*   24:     */ {
/*   25:     */   private static final long serialVersionUID = 2492050246494259885L;
/*   26:     */   protected ExpressionNode m_root;
/*   27:     */   
/*   28:     */   protected static abstract class ExpressionNode
/*   29:     */     implements Serializable
/*   30:     */   {
/*   31:     */     private static final long serialVersionUID = -8427857202322768762L;
/*   32:     */     protected boolean m_isAnOr;
/*   33:     */     protected boolean m_isNegated;
/*   34:     */     protected transient Environment m_env;
/*   35:  82 */     protected boolean m_showAndOr = true;
/*   36:     */     
/*   37:     */     public void setIsOr(boolean isOr)
/*   38:     */     {
/*   39:  90 */       this.m_isAnOr = isOr;
/*   40:     */     }
/*   41:     */     
/*   42:     */     public boolean isOr()
/*   43:     */     {
/*   44:  99 */       return this.m_isAnOr;
/*   45:     */     }
/*   46:     */     
/*   47:     */     public boolean isNegated()
/*   48:     */     {
/*   49: 108 */       return this.m_isNegated;
/*   50:     */     }
/*   51:     */     
/*   52:     */     public void setNegated(boolean negated)
/*   53:     */     {
/*   54: 117 */       this.m_isNegated = negated;
/*   55:     */     }
/*   56:     */     
/*   57:     */     public void setShowAndOr(boolean show)
/*   58:     */     {
/*   59: 126 */       this.m_showAndOr = show;
/*   60:     */     }
/*   61:     */     
/*   62:     */     public void init(Instances structure, Environment env)
/*   63:     */     {
/*   64: 136 */       this.m_env = env;
/*   65:     */     }
/*   66:     */     
/*   67:     */     public abstract boolean evaluate(Instance paramInstance, boolean paramBoolean);
/*   68:     */     
/*   69:     */     protected abstract void toStringInternal(StringBuffer paramStringBuffer);
/*   70:     */     
/*   71:     */     public abstract void toStringDisplay(StringBuffer paramStringBuffer);
/*   72:     */     
/*   73:     */     protected abstract String parseFromInternal(String paramString);
/*   74:     */     
/*   75:     */     public abstract DefaultMutableTreeNode toJTree(DefaultMutableTreeNode paramDefaultMutableTreeNode);
/*   76:     */   }
/*   77:     */   
/*   78:     */   protected static class BracketNode
/*   79:     */     extends FlowByExpression.ExpressionNode
/*   80:     */     implements Serializable
/*   81:     */   {
/*   82:     */     private static final long serialVersionUID = 8732159083173001115L;
/*   83: 191 */     protected List<FlowByExpression.ExpressionNode> m_children = new ArrayList();
/*   84:     */     
/*   85:     */     public void init(Instances structure, Environment env)
/*   86:     */     {
/*   87: 195 */       super.init(structure, env);
/*   88: 197 */       for (FlowByExpression.ExpressionNode n : this.m_children) {
/*   89: 198 */         n.init(structure, env);
/*   90:     */       }
/*   91:     */     }
/*   92:     */     
/*   93:     */     public boolean evaluate(Instance inst, boolean result)
/*   94:     */     {
/*   95: 205 */       boolean thisNode = true;
/*   96: 206 */       if (this.m_children.size() > 0)
/*   97:     */       {
/*   98: 207 */         for (FlowByExpression.ExpressionNode n : this.m_children) {
/*   99: 208 */           thisNode = n.evaluate(inst, thisNode);
/*  100:     */         }
/*  101: 210 */         if (isNegated()) {
/*  102: 211 */           thisNode = !thisNode;
/*  103:     */         }
/*  104:     */       }
/*  105: 215 */       return (result) || (thisNode);
/*  106:     */     }
/*  107:     */     
/*  108:     */     public void addChild(FlowByExpression.ExpressionNode child)
/*  109:     */     {
/*  110: 224 */       this.m_children.add(child);
/*  111: 226 */       if (this.m_children.size() > 0) {
/*  112: 227 */         ((FlowByExpression.ExpressionNode)this.m_children.get(0)).setShowAndOr(false);
/*  113:     */       }
/*  114:     */     }
/*  115:     */     
/*  116:     */     public void removeChild(FlowByExpression.ExpressionNode child)
/*  117:     */     {
/*  118: 237 */       this.m_children.remove(child);
/*  119: 239 */       if (this.m_children.size() > 0) {
/*  120: 240 */         ((FlowByExpression.ExpressionNode)this.m_children.get(0)).setShowAndOr(false);
/*  121:     */       }
/*  122:     */     }
/*  123:     */     
/*  124:     */     public String toString()
/*  125:     */     {
/*  126: 249 */       String result = "( )";
/*  127: 250 */       if (isNegated()) {
/*  128: 251 */         result = "!" + result;
/*  129:     */       }
/*  130: 254 */       if (this.m_showAndOr) {
/*  131: 255 */         if (this.m_isAnOr) {
/*  132: 256 */           result = "|| " + result;
/*  133:     */         } else {
/*  134: 258 */           result = "&& " + result;
/*  135:     */         }
/*  136:     */       }
/*  137: 262 */       return result;
/*  138:     */     }
/*  139:     */     
/*  140:     */     public DefaultMutableTreeNode toJTree(DefaultMutableTreeNode parent)
/*  141:     */     {
/*  142: 268 */       DefaultMutableTreeNode current = new DefaultMutableTreeNode(this);
/*  143: 269 */       if (parent != null) {
/*  144: 270 */         parent.add(current);
/*  145:     */       }
/*  146: 273 */       for (FlowByExpression.ExpressionNode child : this.m_children) {
/*  147: 274 */         child.toJTree(current);
/*  148:     */       }
/*  149: 277 */       return current;
/*  150:     */     }
/*  151:     */     
/*  152:     */     private void toString(StringBuffer buff, boolean internal)
/*  153:     */     {
/*  154: 281 */       if (this.m_children.size() >= 0)
/*  155:     */       {
/*  156: 282 */         if ((internal) || (this.m_showAndOr)) {
/*  157: 283 */           if (this.m_isAnOr) {
/*  158: 284 */             buff.append("|| ");
/*  159:     */           } else {
/*  160: 286 */             buff.append("&& ");
/*  161:     */           }
/*  162:     */         }
/*  163: 290 */         if (isNegated()) {
/*  164: 291 */           buff.append("!");
/*  165:     */         }
/*  166: 293 */         buff.append("(");
/*  167:     */         
/*  168: 295 */         int count = 0;
/*  169: 296 */         for (FlowByExpression.ExpressionNode child : this.m_children)
/*  170:     */         {
/*  171: 297 */           if (internal) {
/*  172: 298 */             child.toStringInternal(buff);
/*  173:     */           } else {
/*  174: 300 */             child.toStringDisplay(buff);
/*  175:     */           }
/*  176: 302 */           count++;
/*  177: 303 */           if (count != this.m_children.size()) {
/*  178: 304 */             buff.append(" ");
/*  179:     */           }
/*  180:     */         }
/*  181: 307 */         buff.append(")");
/*  182:     */       }
/*  183:     */     }
/*  184:     */     
/*  185:     */     public void toStringDisplay(StringBuffer buff)
/*  186:     */     {
/*  187: 313 */       toString(buff, false);
/*  188:     */     }
/*  189:     */     
/*  190:     */     protected void toStringInternal(StringBuffer buff)
/*  191:     */     {
/*  192: 318 */       toString(buff, true);
/*  193:     */     }
/*  194:     */     
/*  195:     */     protected String parseFromInternal(String expression)
/*  196:     */     {
/*  197: 323 */       if (expression.startsWith("|| ")) {
/*  198: 324 */         this.m_isAnOr = true;
/*  199:     */       }
/*  200: 327 */       if ((expression.startsWith("|| ")) || (expression.startsWith("&& "))) {
/*  201: 328 */         expression = expression.substring(3, expression.length());
/*  202:     */       }
/*  203: 331 */       if (expression.charAt(0) == '!')
/*  204:     */       {
/*  205: 332 */         setNegated(true);
/*  206: 333 */         expression = expression.substring(1, expression.length());
/*  207:     */       }
/*  208: 336 */       if (expression.charAt(0) != '(') {
/*  209: 337 */         throw new IllegalArgumentException("Malformed expression! Was expecting a \"(\"");
/*  210:     */       }
/*  211: 341 */       expression = expression.substring(1, expression.length());
/*  212: 343 */       while (expression.charAt(0) != ')')
/*  213:     */       {
/*  214: 344 */         int offset = 3;
/*  215: 346 */         if (expression.charAt(offset) == '(')
/*  216:     */         {
/*  217: 347 */           FlowByExpression.ExpressionNode child = new BracketNode();
/*  218: 348 */           expression = child.parseFromInternal(expression);
/*  219: 349 */           this.m_children.add(child);
/*  220:     */         }
/*  221:     */         else
/*  222:     */         {
/*  223: 352 */           FlowByExpression.ExpressionNode child = new FlowByExpression.ExpressionClause();
/*  224: 353 */           expression = child.parseFromInternal(expression);
/*  225: 354 */           this.m_children.add(child);
/*  226:     */         }
/*  227:     */       }
/*  228: 358 */       if (this.m_children.size() > 0) {
/*  229: 359 */         ((FlowByExpression.ExpressionNode)this.m_children.get(0)).setShowAndOr(false);
/*  230:     */       }
/*  231: 362 */       return expression;
/*  232:     */     }
/*  233:     */   }
/*  234:     */   
/*  235:     */   protected static class ExpressionClause
/*  236:     */     extends FlowByExpression.ExpressionNode
/*  237:     */     implements Serializable
/*  238:     */   {
/*  239:     */     private static final long serialVersionUID = 2754006654981248325L;
/*  240:     */     protected ExpressionType m_operator;
/*  241:     */     protected String m_lhsAttributeName;
/*  242: 379 */     protected int m_lhsAttIndex = -1;
/*  243:     */     protected String m_rhsOperand;
/*  244:     */     protected boolean m_rhsIsAttribute;
/*  245: 388 */     protected int m_rhsAttIndex = -1;
/*  246:     */     protected String m_resolvedLhsName;
/*  247:     */     protected String m_resolvedRhsOperand;
/*  248:     */     protected Pattern m_regexPattern;
/*  249:     */     protected double m_numericOperand;
/*  250:     */     public ExpressionClause() {}
/*  251:     */     
/*  252:     */     public static abstract enum ExpressionType
/*  253:     */     {
/*  254: 403 */       EQUALS(" = "),  NOTEQUAL(" != "),  LESSTHAN(" < "),  LESSTHANEQUAL(" <= "),  GREATERTHAN(" > "),  GREATERTHANEQUAL(" >= "),  ISMISSING(" isMissing "),  CONTAINS(" contains "),  STARTSWITH(" startsWith "),  ENDSWITH(" endsWith "),  REGEX(" regex ");
/*  255:     */       
/*  256:     */       private final String m_stringVal;
/*  257:     */       
/*  258:     */       abstract boolean evaluate(Instance paramInstance, int paramInt1, String paramString, double paramDouble, Pattern paramPattern, boolean paramBoolean, int paramInt2);
/*  259:     */       
/*  260:     */       private ExpressionType(String name)
/*  261:     */       {
/*  262: 636 */         this.m_stringVal = name;
/*  263:     */       }
/*  264:     */       
/*  265:     */       public String toString()
/*  266:     */       {
/*  267: 641 */         return this.m_stringVal;
/*  268:     */       }
/*  269:     */     }
/*  270:     */     
/*  271:     */     public ExpressionClause(ExpressionType operator, String lhsAttributeName, String rhsOperand, boolean rhsIsAttribute, boolean isAnOr)
/*  272:     */     {
/*  273: 660 */       this.m_operator = operator;
/*  274: 661 */       this.m_lhsAttributeName = lhsAttributeName;
/*  275: 662 */       this.m_rhsOperand = rhsOperand;
/*  276: 663 */       this.m_rhsIsAttribute = rhsIsAttribute;
/*  277: 664 */       this.m_isAnOr = isAnOr;
/*  278:     */     }
/*  279:     */     
/*  280:     */     public void init(Instances structure, Environment env)
/*  281:     */     {
/*  282: 669 */       super.init(structure, env);
/*  283:     */       
/*  284: 671 */       this.m_resolvedLhsName = this.m_lhsAttributeName;
/*  285: 672 */       this.m_resolvedRhsOperand = this.m_rhsOperand;
/*  286:     */       try
/*  287:     */       {
/*  288: 674 */         this.m_resolvedLhsName = this.m_env.substitute(this.m_resolvedLhsName);
/*  289: 675 */         this.m_resolvedRhsOperand = this.m_env.substitute(this.m_resolvedRhsOperand);
/*  290:     */       }
/*  291:     */       catch (Exception ex) {}
/*  292: 679 */       Attribute lhs = null;
/*  293: 681 */       if (this.m_resolvedLhsName.toLowerCase().startsWith("/first")) {
/*  294: 682 */         lhs = structure.attribute(0);
/*  295: 683 */       } else if (this.m_resolvedLhsName.toLowerCase().startsWith("/last")) {
/*  296: 684 */         lhs = structure.attribute(structure.numAttributes() - 1);
/*  297:     */       } else {
/*  298:     */         try
/*  299:     */         {
/*  300: 688 */           int indx = Integer.parseInt(this.m_resolvedLhsName);
/*  301: 689 */           indx--;
/*  302: 690 */           lhs = structure.attribute(indx);
/*  303:     */         }
/*  304:     */         catch (NumberFormatException ex) {}
/*  305:     */       }
/*  306: 695 */       if (lhs == null) {
/*  307: 696 */         lhs = structure.attribute(this.m_resolvedLhsName);
/*  308:     */       }
/*  309: 698 */       if (lhs == null) {
/*  310: 699 */         throw new IllegalArgumentException("Data does not contain attribute \"" + this.m_resolvedLhsName + "\"");
/*  311:     */       }
/*  312: 702 */       this.m_lhsAttIndex = lhs.index();
/*  313: 704 */       if (this.m_rhsIsAttribute)
/*  314:     */       {
/*  315: 705 */         Attribute rhs = null;
/*  316: 708 */         if (this.m_resolvedRhsOperand.toLowerCase().equals("/first")) {
/*  317: 709 */           rhs = structure.attribute(0);
/*  318: 710 */         } else if (this.m_resolvedRhsOperand.toLowerCase().equals("/last")) {
/*  319: 711 */           rhs = structure.attribute(structure.numAttributes() - 1);
/*  320:     */         } else {
/*  321:     */           try
/*  322:     */           {
/*  323: 715 */             int indx = Integer.parseInt(this.m_resolvedRhsOperand);
/*  324: 716 */             indx--;
/*  325: 717 */             rhs = structure.attribute(indx);
/*  326:     */           }
/*  327:     */           catch (NumberFormatException ex) {}
/*  328:     */         }
/*  329: 722 */         if (rhs == null) {
/*  330: 723 */           rhs = structure.attribute(this.m_resolvedRhsOperand);
/*  331:     */         }
/*  332: 725 */         if (rhs == null) {
/*  333: 726 */           throw new IllegalArgumentException("Data does not contain attribute \"" + this.m_resolvedRhsOperand + "\"");
/*  334:     */         }
/*  335: 729 */         this.m_rhsAttIndex = rhs.index();
/*  336:     */       }
/*  337: 730 */       else if ((this.m_operator != ExpressionType.CONTAINS) && (this.m_operator != ExpressionType.STARTSWITH) && (this.m_operator != ExpressionType.ENDSWITH) && (this.m_operator != ExpressionType.REGEX) && (this.m_operator != ExpressionType.ISMISSING))
/*  338:     */       {
/*  339: 737 */         if (lhs.isNominal())
/*  340:     */         {
/*  341: 738 */           this.m_numericOperand = lhs.indexOfValue(this.m_resolvedRhsOperand);
/*  342: 740 */           if (this.m_numericOperand < 0.0D) {
/*  343: 741 */             throw new IllegalArgumentException("Unknown nominal value '" + this.m_resolvedRhsOperand + "' for attribute '" + lhs.name() + "'");
/*  344:     */           }
/*  345:     */         }
/*  346:     */         else
/*  347:     */         {
/*  348:     */           try
/*  349:     */           {
/*  350: 746 */             this.m_numericOperand = Double.parseDouble(this.m_resolvedRhsOperand);
/*  351:     */           }
/*  352:     */           catch (NumberFormatException e)
/*  353:     */           {
/*  354: 748 */             throw new IllegalArgumentException("\"" + this.m_resolvedRhsOperand + "\" is not parseable as a number!");
/*  355:     */           }
/*  356:     */         }
/*  357:     */       }
/*  358: 754 */       if (this.m_operator == ExpressionType.REGEX) {
/*  359: 755 */         this.m_regexPattern = Pattern.compile(this.m_resolvedRhsOperand);
/*  360:     */       }
/*  361:     */     }
/*  362:     */     
/*  363:     */     public boolean evaluate(Instance inst, boolean result)
/*  364:     */     {
/*  365: 762 */       boolean thisNode = this.m_operator.evaluate(inst, this.m_lhsAttIndex, this.m_rhsOperand, this.m_numericOperand, this.m_regexPattern, this.m_rhsIsAttribute, this.m_rhsAttIndex);
/*  366: 765 */       if (isNegated()) {
/*  367: 766 */         thisNode = !thisNode;
/*  368:     */       }
/*  369: 769 */       return (result) || (thisNode);
/*  370:     */     }
/*  371:     */     
/*  372:     */     public String toString()
/*  373:     */     {
/*  374: 774 */       StringBuffer buff = new StringBuffer();
/*  375: 775 */       toStringDisplay(buff);
/*  376:     */       
/*  377: 777 */       return buff.toString();
/*  378:     */     }
/*  379:     */     
/*  380:     */     public void toStringDisplay(StringBuffer buff)
/*  381:     */     {
/*  382: 782 */       toString(buff, false);
/*  383:     */     }
/*  384:     */     
/*  385:     */     protected void toStringInternal(StringBuffer buff)
/*  386:     */     {
/*  387: 787 */       toString(buff, true);
/*  388:     */     }
/*  389:     */     
/*  390:     */     public DefaultMutableTreeNode toJTree(DefaultMutableTreeNode parent)
/*  391:     */     {
/*  392: 792 */       parent.add(new DefaultMutableTreeNode(this));
/*  393:     */       
/*  394: 794 */       return parent;
/*  395:     */     }
/*  396:     */     
/*  397:     */     private void toString(StringBuffer buff, boolean internal)
/*  398:     */     {
/*  399: 798 */       if ((internal) || (this.m_showAndOr)) {
/*  400: 799 */         if (this.m_isAnOr) {
/*  401: 800 */           buff.append("|| ");
/*  402:     */         } else {
/*  403: 802 */           buff.append("&& ");
/*  404:     */         }
/*  405:     */       }
/*  406: 805 */       if (isNegated()) {
/*  407: 806 */         buff.append("!");
/*  408:     */       }
/*  409: 809 */       buff.append("[");
/*  410:     */       
/*  411: 811 */       buff.append(this.m_lhsAttributeName);
/*  412: 812 */       if (internal) {
/*  413: 813 */         buff.append("@EC@" + this.m_operator.toString());
/*  414:     */       } else {
/*  415: 815 */         buff.append(" " + this.m_operator.toString());
/*  416:     */       }
/*  417: 818 */       if (this.m_operator != ExpressionType.ISMISSING)
/*  418:     */       {
/*  419: 820 */         if (internal) {
/*  420: 821 */           buff.append("@EC@" + (this.m_rhsIsAttribute ? "@@" : "") + this.m_rhsOperand);
/*  421:     */         } else {
/*  422: 823 */           buff.append(" " + (this.m_rhsIsAttribute ? "ATT: " : "") + this.m_rhsOperand);
/*  423:     */         }
/*  424:     */       }
/*  425: 826 */       else if (internal) {
/*  426: 827 */         buff.append("@EC@");
/*  427:     */       } else {
/*  428: 829 */         buff.append(" ");
/*  429:     */       }
/*  430: 833 */       buff.append("]");
/*  431:     */     }
/*  432:     */     
/*  433:     */     protected String parseFromInternal(String expression)
/*  434:     */     {
/*  435: 840 */       if (expression.startsWith("|| ")) {
/*  436: 841 */         this.m_isAnOr = true;
/*  437:     */       }
/*  438: 844 */       if ((expression.startsWith("|| ")) || (expression.startsWith("&& "))) {
/*  439: 846 */         expression = expression.substring(3, expression.length());
/*  440:     */       }
/*  441: 849 */       if (expression.charAt(0) == '!')
/*  442:     */       {
/*  443: 850 */         setNegated(true);
/*  444: 851 */         expression = expression.substring(1, expression.length());
/*  445:     */       }
/*  446: 854 */       if (expression.charAt(0) != '[') {
/*  447: 855 */         throw new IllegalArgumentException("Was expecting a \"[\" to start this ExpressionClause!");
/*  448:     */       }
/*  449: 858 */       expression = expression.substring(1, expression.length());
/*  450: 859 */       this.m_lhsAttributeName = expression.substring(0, expression.indexOf("@EC@"));
/*  451: 860 */       expression = expression.substring(expression.indexOf("@EC@") + 4, expression.length());
/*  452:     */       
/*  453: 862 */       String oppName = expression.substring(0, expression.indexOf("@EC@"));
/*  454: 863 */       expression = expression.substring(expression.indexOf("@EC@") + 4, expression.length());
/*  455: 865 */       for (ExpressionType n : ExpressionType.values()) {
/*  456: 866 */         if (n.toString().equals(oppName))
/*  457:     */         {
/*  458: 867 */           this.m_operator = n;
/*  459: 868 */           break;
/*  460:     */         }
/*  461:     */       }
/*  462: 872 */       if (expression.startsWith("@@"))
/*  463:     */       {
/*  464: 874 */         expression = expression.substring(2, expression.length());
/*  465:     */         
/*  466: 876 */         this.m_rhsIsAttribute = true;
/*  467:     */       }
/*  468: 878 */       this.m_rhsOperand = expression.substring(0, expression.indexOf(']'));
/*  469:     */       
/*  470: 880 */       expression = expression.substring(expression.indexOf(']') + 1, expression.length());
/*  471: 882 */       if (expression.charAt(0) == ' ') {
/*  472: 883 */         expression = expression.substring(1, expression.length());
/*  473:     */       }
/*  474: 886 */       return expression;
/*  475:     */     }
/*  476:     */   }
/*  477:     */   
/*  478: 894 */   protected String m_expressionString = "";
/*  479:     */   protected Object[] m_downstream;
/*  480: 906 */   protected String m_customNameOfTrueStep = "";
/*  481: 912 */   protected String m_customNameOfFalseStep = "";
/*  482:     */   protected int m_indexOfTrueStep;
/*  483:     */   protected int m_indexOfFalseStep;
/*  484:     */   protected transient Logger m_log;
/*  485:     */   protected transient boolean m_busy;
/*  486:     */   protected Object m_listenee;
/*  487:     */   protected String m_connectionType;
/*  488:     */   private Instances m_connectedFormat;
/*  489:     */   protected transient Environment m_env;
/*  490: 935 */   protected InstanceEvent m_ie = new InstanceEvent(this);
/*  491: 940 */   protected BeanVisual m_visual = new BeanVisual("FlowByExpression", "weka/gui/beans/icons/FlowByExpression.png", "weka/gui/beans/icons/FlowByExpression.png");
/*  492:     */   
/*  493:     */   public FlowByExpression()
/*  494:     */   {
/*  495: 948 */     setLayout(new BorderLayout());
/*  496: 949 */     add(this.m_visual, "Center");
/*  497:     */     
/*  498: 951 */     this.m_env = Environment.getSystemWide();
/*  499:     */   }
/*  500:     */   
/*  501:     */   public String globalInfo()
/*  502:     */   {
/*  503: 955 */     return "Splits incoming instances (or instance stream) according to the evaluation of a logical expression. The expression can test the values of one or more incoming attributes. The test can involve constants or comparing one attribute's values to another. Inequalities along with string operations such as contains, starts-with, ends-with and regular expressions may be used as operators. \"True\" instances can be sent to one downstream step and \"False\" instances sent to another.";
/*  504:     */   }
/*  505:     */   
/*  506:     */   public void setExpressionString(String expressionString)
/*  507:     */   {
/*  508: 970 */     this.m_expressionString = expressionString;
/*  509:     */   }
/*  510:     */   
/*  511:     */   public String getExpressionString()
/*  512:     */   {
/*  513: 979 */     return this.m_expressionString;
/*  514:     */   }
/*  515:     */   
/*  516:     */   public void setTrueStepName(String trueStep)
/*  517:     */   {
/*  518: 988 */     this.m_customNameOfTrueStep = trueStep;
/*  519:     */   }
/*  520:     */   
/*  521:     */   public String getTrueStepName()
/*  522:     */   {
/*  523: 997 */     return this.m_customNameOfTrueStep;
/*  524:     */   }
/*  525:     */   
/*  526:     */   public void setFalseStepName(String falseStep)
/*  527:     */   {
/*  528:1006 */     this.m_customNameOfFalseStep = falseStep;
/*  529:     */   }
/*  530:     */   
/*  531:     */   public String getFalseStepName()
/*  532:     */   {
/*  533:1015 */     return this.m_customNameOfFalseStep;
/*  534:     */   }
/*  535:     */   
/*  536:     */   public void addDataSourceListener(DataSourceListener dsl)
/*  537:     */   {
/*  538:1020 */     if (this.m_downstream == null) {
/*  539:1021 */       this.m_downstream = new Object[2];
/*  540:     */     }
/*  541:1024 */     if ((this.m_downstream[0] == null) && (this.m_downstream[1] == null))
/*  542:     */     {
/*  543:1025 */       this.m_downstream[0] = dsl;
/*  544:1026 */       return;
/*  545:     */     }
/*  546:1029 */     if ((this.m_downstream[0] == null) || (this.m_downstream[1] == null))
/*  547:     */     {
/*  548:1030 */       if ((this.m_downstream[0] == null) && ((this.m_downstream[1] instanceof DataSourceListener)))
/*  549:     */       {
/*  550:1032 */         this.m_downstream[0] = dsl;
/*  551:1033 */         return;
/*  552:     */       }
/*  553:1034 */       if ((this.m_downstream[1] == null) && ((this.m_downstream[0] instanceof DataSourceListener)))
/*  554:     */       {
/*  555:1036 */         this.m_downstream[1] = dsl;
/*  556:1037 */         return;
/*  557:     */       }
/*  558:     */     }
/*  559:     */   }
/*  560:     */   
/*  561:     */   protected void remove(Object dsl)
/*  562:     */   {
/*  563:1043 */     if (this.m_downstream[0] == dsl)
/*  564:     */     {
/*  565:1044 */       this.m_downstream[0] = null;
/*  566:1045 */       return;
/*  567:     */     }
/*  568:1048 */     if (this.m_downstream[1] == dsl) {
/*  569:1049 */       this.m_downstream[1] = null;
/*  570:     */     }
/*  571:     */   }
/*  572:     */   
/*  573:     */   public void removeDataSourceListener(DataSourceListener dsl)
/*  574:     */   {
/*  575:1055 */     if (this.m_downstream == null) {
/*  576:1056 */       this.m_downstream = new Object[2];
/*  577:     */     }
/*  578:1059 */     remove(dsl);
/*  579:     */   }
/*  580:     */   
/*  581:     */   public void addInstanceListener(InstanceListener dsl)
/*  582:     */   {
/*  583:1064 */     if (this.m_downstream == null) {
/*  584:1065 */       this.m_downstream = new Object[2];
/*  585:     */     }
/*  586:1068 */     if ((this.m_downstream[0] == null) && (this.m_downstream[1] == null))
/*  587:     */     {
/*  588:1069 */       this.m_downstream[0] = dsl;
/*  589:1070 */       return;
/*  590:     */     }
/*  591:1073 */     if ((this.m_downstream[0] == null) || (this.m_downstream[1] == null))
/*  592:     */     {
/*  593:1074 */       if ((this.m_downstream[0] == null) && ((this.m_downstream[1] instanceof InstanceListener)))
/*  594:     */       {
/*  595:1076 */         this.m_downstream[0] = dsl;
/*  596:1077 */         return;
/*  597:     */       }
/*  598:1078 */       if ((this.m_downstream[1] == null) && ((this.m_downstream[0] instanceof InstanceListener)))
/*  599:     */       {
/*  600:1080 */         this.m_downstream[1] = dsl;
/*  601:1081 */         return;
/*  602:     */       }
/*  603:     */     }
/*  604:     */   }
/*  605:     */   
/*  606:     */   public void removeInstanceListener(InstanceListener dsl)
/*  607:     */   {
/*  608:1088 */     if (this.m_downstream == null) {
/*  609:1089 */       this.m_downstream = new Object[2];
/*  610:     */     }
/*  611:1092 */     remove(dsl);
/*  612:     */   }
/*  613:     */   
/*  614:     */   public void setEnvironment(Environment env)
/*  615:     */   {
/*  616:1098 */     this.m_env = env;
/*  617:     */   }
/*  618:     */   
/*  619:     */   public boolean eventGeneratable(String eventName)
/*  620:     */   {
/*  621:1103 */     if (this.m_listenee == null) {
/*  622:1104 */       return false;
/*  623:     */     }
/*  624:1107 */     if ((this.m_listenee instanceof EventConstraints))
/*  625:     */     {
/*  626:1109 */       if (eventName.equals("dataSet")) {
/*  627:1110 */         return (((EventConstraints)this.m_listenee).eventGeneratable(eventName)) || (((EventConstraints)this.m_listenee).eventGeneratable("trainingSet")) || (((EventConstraints)this.m_listenee).eventGeneratable("testSet"));
/*  628:     */       }
/*  629:1115 */       return ((EventConstraints)this.m_listenee).eventGeneratable(eventName);
/*  630:     */     }
/*  631:1118 */     return true;
/*  632:     */   }
/*  633:     */   
/*  634:     */   protected void init(Instances data)
/*  635:     */   {
/*  636:1127 */     this.m_indexOfTrueStep = -1;
/*  637:1128 */     this.m_indexOfFalseStep = -1;
/*  638:1129 */     this.m_connectedFormat = data;
/*  639:1131 */     if (this.m_downstream == null) {
/*  640:1132 */       return;
/*  641:     */     }
/*  642:1135 */     if ((this.m_downstream[0] != null) && (((BeanCommon)this.m_downstream[0]).getCustomName().equals(this.m_customNameOfTrueStep))) {
/*  643:1138 */       this.m_indexOfTrueStep = 0;
/*  644:     */     }
/*  645:1140 */     if ((this.m_downstream[0] != null) && (((BeanCommon)this.m_downstream[0]).getCustomName().equals(this.m_customNameOfFalseStep))) {
/*  646:1143 */       this.m_indexOfFalseStep = 0;
/*  647:     */     }
/*  648:1146 */     if ((this.m_downstream[1] != null) && (((BeanCommon)this.m_downstream[1]).getCustomName().equals(this.m_customNameOfTrueStep))) {
/*  649:1149 */       this.m_indexOfTrueStep = 1;
/*  650:     */     }
/*  651:1151 */     if ((this.m_downstream[1] != null) && (((BeanCommon)this.m_downstream[1]).getCustomName().equals(this.m_customNameOfFalseStep))) {
/*  652:1154 */       this.m_indexOfFalseStep = 1;
/*  653:     */     }
/*  654:1157 */     if (this.m_env == null) {
/*  655:1158 */       this.m_env = Environment.getSystemWide();
/*  656:     */     }
/*  657:     */     try
/*  658:     */     {
/*  659:1162 */       if ((this.m_expressionString != null) && (this.m_expressionString.length() > 0))
/*  660:     */       {
/*  661:1163 */         this.m_root = new BracketNode();
/*  662:1164 */         this.m_root.parseFromInternal(this.m_expressionString);
/*  663:     */       }
/*  664:1166 */       if (this.m_root != null) {
/*  665:1167 */         this.m_root.init(data, this.m_env);
/*  666:     */       }
/*  667:     */     }
/*  668:     */     catch (Exception ex)
/*  669:     */     {
/*  670:1170 */       ex.printStackTrace();
/*  671:1171 */       stop();
/*  672:1172 */       this.m_busy = false;
/*  673:     */     }
/*  674:     */   }
/*  675:     */   
/*  676:     */   public void acceptDataSet(DataSetEvent e)
/*  677:     */   {
/*  678:1179 */     this.m_busy = true;
/*  679:1180 */     if ((this.m_log != null) && (!e.isStructureOnly())) {
/*  680:1181 */       this.m_log.statusMessage(statusMessagePrefix() + "Processing batch...");
/*  681:     */     }
/*  682:1184 */     init(new Instances(e.getDataSet(), 0));
/*  683:1186 */     if (this.m_root != null)
/*  684:     */     {
/*  685:1187 */       Instances trueBatch = new Instances(e.getDataSet(), 0);
/*  686:1188 */       Instances falseBatch = new Instances(e.getDataSet(), 0);
/*  687:1190 */       for (int i = 0; i < e.getDataSet().numInstances(); i++)
/*  688:     */       {
/*  689:1191 */         Instance current = e.getDataSet().instance(i);
/*  690:     */         
/*  691:1193 */         boolean result = this.m_root.evaluate(current, true);
/*  692:1195 */         if (result)
/*  693:     */         {
/*  694:1196 */           if (this.m_indexOfTrueStep >= 0) {
/*  695:1197 */             trueBatch.add(current);
/*  696:     */           }
/*  697:     */         }
/*  698:1200 */         else if (this.m_indexOfFalseStep >= 0) {
/*  699:1201 */           falseBatch.add(current);
/*  700:     */         }
/*  701:     */       }
/*  702:1206 */       if (this.m_indexOfTrueStep >= 0)
/*  703:     */       {
/*  704:1207 */         DataSetEvent d = new DataSetEvent(this, trueBatch);
/*  705:1208 */         ((DataSourceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptDataSet(d);
/*  706:     */       }
/*  707:1211 */       if (this.m_indexOfFalseStep >= 0)
/*  708:     */       {
/*  709:1212 */         DataSetEvent d = new DataSetEvent(this, falseBatch);
/*  710:1213 */         ((DataSourceListener)this.m_downstream[this.m_indexOfFalseStep]).acceptDataSet(d);
/*  711:     */       }
/*  712:     */     }
/*  713:1217 */     else if (this.m_indexOfTrueStep >= 0)
/*  714:     */     {
/*  715:1218 */       DataSetEvent d = new DataSetEvent(this, e.getDataSet());
/*  716:1219 */       ((DataSourceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptDataSet(d);
/*  717:     */     }
/*  718:1223 */     if ((this.m_log != null) && (!e.isStructureOnly())) {
/*  719:1224 */       this.m_log.statusMessage(statusMessagePrefix() + "Finished");
/*  720:     */     }
/*  721:1227 */     this.m_busy = false;
/*  722:     */   }
/*  723:     */   
/*  724:     */   public void acceptTestSet(TestSetEvent e)
/*  725:     */   {
/*  726:1232 */     Instances test = e.getTestSet();
/*  727:1233 */     DataSetEvent d = new DataSetEvent(this, test);
/*  728:1234 */     acceptDataSet(d);
/*  729:     */   }
/*  730:     */   
/*  731:     */   public void acceptTrainingSet(TrainingSetEvent e)
/*  732:     */   {
/*  733:1239 */     Instances train = e.getTrainingSet();
/*  734:1240 */     DataSetEvent d = new DataSetEvent(this, train);
/*  735:1241 */     acceptDataSet(d);
/*  736:     */   }
/*  737:     */   
/*  738:     */   public void acceptInstance(InstanceEvent e)
/*  739:     */   {
/*  740:1246 */     this.m_busy = true;
/*  741:1248 */     if (e.getStatus() == 0)
/*  742:     */     {
/*  743:1249 */       Instances structure = e.getStructure();
/*  744:1250 */       init(structure);
/*  745:1252 */       if (this.m_log != null) {
/*  746:1253 */         this.m_log.statusMessage(statusMessagePrefix() + "Processing stream...");
/*  747:     */       }
/*  748:1257 */       this.m_ie.setStructure(structure);
/*  749:1258 */       if (this.m_indexOfTrueStep >= 0) {
/*  750:1259 */         ((InstanceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptInstance(this.m_ie);
/*  751:     */       }
/*  752:1262 */       if (this.m_indexOfFalseStep >= 0) {
/*  753:1263 */         ((InstanceListener)this.m_downstream[this.m_indexOfFalseStep]).acceptInstance(this.m_ie);
/*  754:     */       }
/*  755:     */     }
/*  756:     */     else
/*  757:     */     {
/*  758:1267 */       Instance inst = e.getInstance();
/*  759:1268 */       this.m_ie.setStatus(e.getStatus());
/*  760:1270 */       if ((inst == null) || (e.getStatus() == 2))
/*  761:     */       {
/*  762:1271 */         if (inst != null)
/*  763:     */         {
/*  764:1273 */           boolean result = true;
/*  765:1274 */           if (this.m_root != null) {
/*  766:1275 */             result = this.m_root.evaluate(inst, true);
/*  767:     */           }
/*  768:1278 */           if (result)
/*  769:     */           {
/*  770:1279 */             if (this.m_indexOfTrueStep >= 0)
/*  771:     */             {
/*  772:1280 */               this.m_ie.setInstance(inst);
/*  773:1281 */               ((InstanceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptInstance(this.m_ie);
/*  774:     */             }
/*  775:1284 */             if (this.m_indexOfFalseStep >= 0)
/*  776:     */             {
/*  777:1285 */               this.m_ie.setInstance(null);
/*  778:1286 */               ((InstanceListener)this.m_downstream[this.m_indexOfFalseStep]).acceptInstance(this.m_ie);
/*  779:     */             }
/*  780:     */           }
/*  781:     */           else
/*  782:     */           {
/*  783:1290 */             if (this.m_indexOfFalseStep >= 0)
/*  784:     */             {
/*  785:1291 */               this.m_ie.setInstance(inst);
/*  786:1292 */               ((InstanceListener)this.m_downstream[this.m_indexOfFalseStep]).acceptInstance(this.m_ie);
/*  787:     */             }
/*  788:1295 */             if (this.m_indexOfTrueStep >= 0)
/*  789:     */             {
/*  790:1296 */               this.m_ie.setInstance(null);
/*  791:1297 */               ((InstanceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptInstance(this.m_ie);
/*  792:     */             }
/*  793:     */           }
/*  794:     */         }
/*  795:     */         else
/*  796:     */         {
/*  797:1303 */           this.m_ie.setInstance(null);
/*  798:1304 */           if (this.m_indexOfTrueStep >= 0) {
/*  799:1305 */             ((InstanceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptInstance(this.m_ie);
/*  800:     */           }
/*  801:1308 */           if (this.m_indexOfFalseStep >= 0) {
/*  802:1309 */             ((InstanceListener)this.m_downstream[this.m_indexOfFalseStep]).acceptInstance(this.m_ie);
/*  803:     */           }
/*  804:     */         }
/*  805:1314 */         if (this.m_log != null) {
/*  806:1315 */           this.m_log.statusMessage(statusMessagePrefix() + "Finished");
/*  807:     */         }
/*  808:     */       }
/*  809:     */       else
/*  810:     */       {
/*  811:1318 */         boolean result = true;
/*  812:1319 */         if (this.m_root != null) {
/*  813:1320 */           result = this.m_root.evaluate(inst, true);
/*  814:     */         }
/*  815:1322 */         this.m_ie.setInstance(inst);
/*  816:1323 */         if (result)
/*  817:     */         {
/*  818:1324 */           if (this.m_indexOfTrueStep >= 0) {
/*  819:1325 */             ((InstanceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptInstance(this.m_ie);
/*  820:     */           }
/*  821:     */         }
/*  822:1329 */         else if (this.m_indexOfFalseStep >= 0) {
/*  823:1330 */           ((InstanceListener)this.m_downstream[this.m_indexOfFalseStep]).acceptInstance(this.m_ie);
/*  824:     */         }
/*  825:     */       }
/*  826:     */     }
/*  827:1337 */     this.m_busy = false;
/*  828:     */   }
/*  829:     */   
/*  830:     */   public void useDefaultVisual()
/*  831:     */   {
/*  832:1342 */     this.m_visual.loadIcons("weka/gui/beans/icons/FlowByExpression.png", "weka/gui/beans/icons/FlowByExpression.png");
/*  833:     */     
/*  834:1344 */     this.m_visual.setText("FlowByExpression");
/*  835:     */   }
/*  836:     */   
/*  837:     */   public void setVisual(BeanVisual newVisual)
/*  838:     */   {
/*  839:1349 */     this.m_visual = newVisual;
/*  840:     */   }
/*  841:     */   
/*  842:     */   public BeanVisual getVisual()
/*  843:     */   {
/*  844:1354 */     return this.m_visual;
/*  845:     */   }
/*  846:     */   
/*  847:     */   public void setCustomName(String name)
/*  848:     */   {
/*  849:1359 */     this.m_visual.setText(name);
/*  850:     */   }
/*  851:     */   
/*  852:     */   public String getCustomName()
/*  853:     */   {
/*  854:1364 */     return this.m_visual.getText();
/*  855:     */   }
/*  856:     */   
/*  857:     */   public void stop()
/*  858:     */   {
/*  859:1369 */     if ((this.m_listenee != null) && 
/*  860:1370 */       ((this.m_listenee instanceof BeanCommon))) {
/*  861:1371 */       ((BeanCommon)this.m_listenee).stop();
/*  862:     */     }
/*  863:1375 */     if (this.m_log != null) {
/*  864:1376 */       this.m_log.statusMessage(statusMessagePrefix() + "Stopped");
/*  865:     */     }
/*  866:1379 */     this.m_busy = false;
/*  867:     */   }
/*  868:     */   
/*  869:     */   public boolean isBusy()
/*  870:     */   {
/*  871:1384 */     return this.m_busy;
/*  872:     */   }
/*  873:     */   
/*  874:     */   public void setLog(Logger logger)
/*  875:     */   {
/*  876:1389 */     this.m_log = logger;
/*  877:     */   }
/*  878:     */   
/*  879:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  880:     */   {
/*  881:1394 */     return connectionAllowed(esd.getName());
/*  882:     */   }
/*  883:     */   
/*  884:     */   public boolean connectionAllowed(String eventName)
/*  885:     */   {
/*  886:1399 */     if (this.m_listenee != null) {
/*  887:1400 */       return false;
/*  888:     */     }
/*  889:1403 */     return true;
/*  890:     */   }
/*  891:     */   
/*  892:     */   public void connectionNotification(String eventName, Object source)
/*  893:     */   {
/*  894:1408 */     if (connectionAllowed(eventName))
/*  895:     */     {
/*  896:1409 */       this.m_listenee = source;
/*  897:1410 */       this.m_connectionType = eventName;
/*  898:     */     }
/*  899:     */   }
/*  900:     */   
/*  901:     */   public void disconnectionNotification(String eventName, Object source)
/*  902:     */   {
/*  903:1416 */     if (source == this.m_listenee) {
/*  904:1417 */       this.m_listenee = null;
/*  905:     */     }
/*  906:     */   }
/*  907:     */   
/*  908:     */   protected String statusMessagePrefix()
/*  909:     */   {
/*  910:1422 */     return getCustomName() + "$" + hashCode() + "|";
/*  911:     */   }
/*  912:     */   
/*  913:     */   private Instances getUpstreamStructure()
/*  914:     */   {
/*  915:1426 */     if ((this.m_listenee != null) && ((this.m_listenee instanceof StructureProducer))) {
/*  916:1427 */       return ((StructureProducer)this.m_listenee).getStructure(this.m_connectionType);
/*  917:     */     }
/*  918:1429 */     return null;
/*  919:     */   }
/*  920:     */   
/*  921:     */   public Instances getStructure(String eventName)
/*  922:     */   {
/*  923:1447 */     if ((!eventName.equals("dataSet")) && (!eventName.equals("instance"))) {
/*  924:1448 */       return null;
/*  925:     */     }
/*  926:1451 */     if ((eventName.equals("dataSet")) && ((this.m_downstream == null) || (this.m_downstream.length == 0))) {
/*  927:1453 */       return null;
/*  928:     */     }
/*  929:1456 */     if ((eventName.equals("instance")) && ((this.m_downstream == null) || (this.m_downstream.length == 0))) {
/*  930:1458 */       return null;
/*  931:     */     }
/*  932:1461 */     if (this.m_connectedFormat == null) {
/*  933:1462 */       this.m_connectedFormat = getUpstreamStructure();
/*  934:     */     }
/*  935:1465 */     return this.m_connectedFormat;
/*  936:     */   }
/*  937:     */   
/*  938:     */   public Instances getConnectedFormat()
/*  939:     */   {
/*  940:1474 */     if (this.m_connectedFormat == null) {
/*  941:1475 */       this.m_connectedFormat = getUpstreamStructure();
/*  942:     */     }
/*  943:1478 */     return this.m_connectedFormat;
/*  944:     */   }
/*  945:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.FlowByExpression
 * JD-Core Version:    0.7.0.1
 */
/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.awt.Point;
/*    4:     */ import java.awt.geom.Point2D.Double;
/*    5:     */ import java.beans.PropertyDescriptor;
/*    6:     */ import java.io.File;
/*    7:     */ import java.io.PrintStream;
/*    8:     */ import java.io.Serializable;
/*    9:     */ import java.util.Collections;
/*   10:     */ import java.util.Comparator;
/*   11:     */ import java.util.Enumeration;
/*   12:     */ import java.util.Hashtable;
/*   13:     */ import java.util.Iterator;
/*   14:     */ import java.util.Properties;
/*   15:     */ import java.util.Random;
/*   16:     */ import java.util.Vector;
/*   17:     */ import java.util.concurrent.LinkedBlockingQueue;
/*   18:     */ import java.util.concurrent.ThreadPoolExecutor;
/*   19:     */ import java.util.concurrent.TimeUnit;
/*   20:     */ import weka.classifiers.AbstractClassifier;
/*   21:     */ import weka.classifiers.Classifier;
/*   22:     */ import weka.classifiers.Evaluation;
/*   23:     */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   24:     */ import weka.classifiers.functions.GaussianProcesses;
/*   25:     */ import weka.core.AdditionalMeasureProducer;
/*   26:     */ import weka.core.Capabilities;
/*   27:     */ import weka.core.Capabilities.Capability;
/*   28:     */ import weka.core.Debug;
/*   29:     */ import weka.core.Instance;
/*   30:     */ import weka.core.Instances;
/*   31:     */ import weka.core.Option;
/*   32:     */ import weka.core.OptionHandler;
/*   33:     */ import weka.core.PropertyPath;
/*   34:     */ import weka.core.RevisionHandler;
/*   35:     */ import weka.core.RevisionUtils;
/*   36:     */ import weka.core.SelectedTag;
/*   37:     */ import weka.core.SerializedObject;
/*   38:     */ import weka.core.Summarizable;
/*   39:     */ import weka.core.Tag;
/*   40:     */ import weka.core.Utils;
/*   41:     */ import weka.core.WekaException;
/*   42:     */ import weka.core.expressionlanguage.common.IfElseMacro;
/*   43:     */ import weka.core.expressionlanguage.common.JavaMacro;
/*   44:     */ import weka.core.expressionlanguage.common.MacroDeclarationsCompositor;
/*   45:     */ import weka.core.expressionlanguage.common.MathFunctions;
/*   46:     */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*   47:     */ import weka.core.expressionlanguage.common.SimpleVariableDeclarations;
/*   48:     */ import weka.core.expressionlanguage.common.SimpleVariableDeclarations.VariableInitializer;
/*   49:     */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*   50:     */ import weka.core.expressionlanguage.parser.Parser;
/*   51:     */ import weka.filters.Filter;
/*   52:     */ import weka.filters.unsupervised.instance.Resample;
/*   53:     */ 
/*   54:     */ public class GridSearch
/*   55:     */   extends RandomizableSingleClassifierEnhancer
/*   56:     */   implements AdditionalMeasureProducer, Summarizable
/*   57:     */ {
/*   58:     */   private static final long serialVersionUID = -3034773968581595348L;
/*   59:     */   public static final int EVALUATION_CC = 0;
/*   60:     */   public static final int EVALUATION_RMSE = 1;
/*   61:     */   public static final int EVALUATION_RRSE = 2;
/*   62:     */   public static final int EVALUATION_MAE = 3;
/*   63:     */   public static final int EVALUATION_RAE = 4;
/*   64:     */   public static final int EVALUATION_COMBINED = 5;
/*   65:     */   public static final int EVALUATION_ACC = 6;
/*   66:     */   public static final int EVALUATION_KAPPA = 7;
/*   67:     */   public static final int EVALUATION_WAUC = 8;
/*   68:     */   
/*   69:     */   protected static class PointDouble
/*   70:     */     extends Point2D.Double
/*   71:     */     implements Serializable, RevisionHandler
/*   72:     */   {
/*   73:     */     private static final long serialVersionUID = 7151661776161898119L;
/*   74:     */     
/*   75:     */     public PointDouble(double x, double y)
/*   76:     */     {
/*   77: 442 */       super(y);
/*   78:     */     }
/*   79:     */     
/*   80:     */     public boolean equals(Object obj)
/*   81:     */     {
/*   82: 456 */       PointDouble pd = (PointDouble)obj;
/*   83:     */       
/*   84: 458 */       return (Utils.eq(getX(), pd.getX())) && (Utils.eq(getY(), pd.getY()));
/*   85:     */     }
/*   86:     */     
/*   87:     */     public String toString()
/*   88:     */     {
/*   89: 469 */       return super.toString().replaceAll(".*\\[", "[");
/*   90:     */     }
/*   91:     */     
/*   92:     */     public String getRevision()
/*   93:     */     {
/*   94: 479 */       return RevisionUtils.extract("$Revision: 11501 $");
/*   95:     */     }
/*   96:     */   }
/*   97:     */   
/*   98:     */   protected static class PointInt
/*   99:     */     extends Point
/*  100:     */     implements Serializable, RevisionHandler
/*  101:     */   {
/*  102:     */     private static final long serialVersionUID = -5900415163698021618L;
/*  103:     */     
/*  104:     */     public PointInt(int x, int y)
/*  105:     */     {
/*  106: 501 */       super(y);
/*  107:     */     }
/*  108:     */     
/*  109:     */     public String toString()
/*  110:     */     {
/*  111: 511 */       return super.toString().replaceAll(".*\\[", "[");
/*  112:     */     }
/*  113:     */     
/*  114:     */     public String getRevision()
/*  115:     */     {
/*  116: 521 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  117:     */     }
/*  118:     */   }
/*  119:     */   
/*  120:     */   protected static class Grid
/*  121:     */     implements Serializable, RevisionHandler
/*  122:     */   {
/*  123:     */     private static final long serialVersionUID = 7290732613611243139L;
/*  124:     */     protected double m_MinX;
/*  125:     */     protected double m_MaxX;
/*  126:     */     protected double m_StepX;
/*  127:     */     protected String m_LabelX;
/*  128:     */     protected double m_MinY;
/*  129:     */     protected double m_MaxY;
/*  130:     */     protected double m_StepY;
/*  131:     */     protected String m_LabelY;
/*  132:     */     protected int m_Width;
/*  133:     */     protected int m_Height;
/*  134:     */     
/*  135:     */     public Grid(double minX, double maxX, double stepX, double minY, double maxY, double stepY)
/*  136:     */     {
/*  137: 575 */       this(minX, maxX, stepX, "", minY, maxY, stepY, "");
/*  138:     */     }
/*  139:     */     
/*  140:     */     public Grid(double minX, double maxX, double stepX, String labelX, double minY, double maxY, double stepY, String labelY)
/*  141:     */     {
/*  142: 595 */       this.m_MinX = minX;
/*  143: 596 */       this.m_MaxX = maxX;
/*  144: 597 */       this.m_StepX = stepX;
/*  145: 598 */       this.m_LabelX = labelX;
/*  146: 599 */       this.m_MinY = minY;
/*  147: 600 */       this.m_MaxY = maxY;
/*  148: 601 */       this.m_StepY = stepY;
/*  149: 602 */       this.m_LabelY = labelY;
/*  150: 603 */       this.m_Height = ((int)StrictMath.round((this.m_MaxY - this.m_MinY) / this.m_StepY) + 1);
/*  151: 604 */       this.m_Width = ((int)StrictMath.round((this.m_MaxX - this.m_MinX) / this.m_StepX) + 1);
/*  152: 607 */       if (this.m_MinX >= this.m_MaxX) {
/*  153: 608 */         throw new IllegalArgumentException("XMin must be smaller than XMax!");
/*  154:     */       }
/*  155: 610 */       if (this.m_MinY >= this.m_MaxY) {
/*  156: 611 */         throw new IllegalArgumentException("YMin must be smaller than YMax!");
/*  157:     */       }
/*  158: 615 */       if (this.m_StepX <= 0.0D) {
/*  159: 616 */         throw new IllegalArgumentException("XStep must be a positive number!");
/*  160:     */       }
/*  161: 618 */       if (this.m_StepY <= 0.0D) {
/*  162: 619 */         throw new IllegalArgumentException("YStep must be a positive number!");
/*  163:     */       }
/*  164: 623 */       if (!Utils.eq(this.m_MinX + (this.m_Width - 1) * this.m_StepX, this.m_MaxX)) {
/*  165: 624 */         throw new IllegalArgumentException("X axis doesn't match! Provided max: " + this.m_MaxX + ", calculated max via min and step size: " + (this.m_MinX + (this.m_Width - 1) * this.m_StepX));
/*  166:     */       }
/*  167: 629 */       if (!Utils.eq(this.m_MinY + (this.m_Height - 1) * this.m_StepY, this.m_MaxY)) {
/*  168: 630 */         throw new IllegalArgumentException("Y axis doesn't match! Provided max: " + this.m_MaxY + ", calculated max via min and step size: " + (this.m_MinY + (this.m_Height - 1) * this.m_StepY));
/*  169:     */       }
/*  170:     */     }
/*  171:     */     
/*  172:     */     public boolean equals(Object o)
/*  173:     */     {
/*  174: 648 */       Grid g = (Grid)o;
/*  175:     */       
/*  176: 650 */       boolean result = (width() == g.width()) && (height() == g.height()) && (getMinX() == g.getMinX()) && (getMinY() == g.getMinY()) && (getStepX() == g.getStepX()) && (getStepY() == g.getStepY()) && (getLabelX().equals(g.getLabelX())) && (getLabelY().equals(g.getLabelY()));
/*  177:     */       
/*  178:     */ 
/*  179:     */ 
/*  180:     */ 
/*  181:     */ 
/*  182: 656 */       return result;
/*  183:     */     }
/*  184:     */     
/*  185:     */     public double getMinX()
/*  186:     */     {
/*  187: 665 */       return this.m_MinX;
/*  188:     */     }
/*  189:     */     
/*  190:     */     public double getMaxX()
/*  191:     */     {
/*  192: 674 */       return this.m_MaxX;
/*  193:     */     }
/*  194:     */     
/*  195:     */     public double getStepX()
/*  196:     */     {
/*  197: 683 */       return this.m_StepX;
/*  198:     */     }
/*  199:     */     
/*  200:     */     public String getLabelX()
/*  201:     */     {
/*  202: 692 */       return this.m_LabelX;
/*  203:     */     }
/*  204:     */     
/*  205:     */     public double getMinY()
/*  206:     */     {
/*  207: 701 */       return this.m_MinY;
/*  208:     */     }
/*  209:     */     
/*  210:     */     public double getMaxY()
/*  211:     */     {
/*  212: 710 */       return this.m_MaxY;
/*  213:     */     }
/*  214:     */     
/*  215:     */     public double getStepY()
/*  216:     */     {
/*  217: 719 */       return this.m_StepY;
/*  218:     */     }
/*  219:     */     
/*  220:     */     public String getLabelY()
/*  221:     */     {
/*  222: 728 */       return this.m_LabelY;
/*  223:     */     }
/*  224:     */     
/*  225:     */     public int height()
/*  226:     */     {
/*  227: 737 */       return this.m_Height;
/*  228:     */     }
/*  229:     */     
/*  230:     */     public int width()
/*  231:     */     {
/*  232: 746 */       return this.m_Width;
/*  233:     */     }
/*  234:     */     
/*  235:     */     public GridSearch.PointDouble getValues(int x, int y)
/*  236:     */     {
/*  237: 757 */       if (x >= width()) {
/*  238: 758 */         throw new IllegalArgumentException("Index out of scope on X axis (" + x + " >= " + width() + ")!");
/*  239:     */       }
/*  240: 761 */       if (y >= height()) {
/*  241: 762 */         throw new IllegalArgumentException("Index out of scope on Y axis (" + y + " >= " + height() + ")!");
/*  242:     */       }
/*  243: 766 */       return new GridSearch.PointDouble(this.m_MinX + this.m_StepX * x, this.m_MinY + this.m_StepY * y);
/*  244:     */     }
/*  245:     */     
/*  246:     */     public GridSearch.PointInt getLocation(GridSearch.PointDouble values)
/*  247:     */     {
/*  248: 784 */       int x = 0;
/*  249: 785 */       double distance = this.m_StepX;
/*  250: 786 */       for (int i = 0; i < width(); i++)
/*  251:     */       {
/*  252: 787 */         double currDistance = StrictMath.abs(values.getX() - getValues(i, 0).getX());
/*  253: 788 */         if (Utils.sm(currDistance, distance))
/*  254:     */         {
/*  255: 789 */           distance = currDistance;
/*  256: 790 */           x = i;
/*  257:     */         }
/*  258:     */       }
/*  259: 795 */       int y = 0;
/*  260: 796 */       distance = this.m_StepY;
/*  261: 797 */       for (i = 0; i < height(); i++)
/*  262:     */       {
/*  263: 798 */         double currDistance = StrictMath.abs(values.getY() - getValues(0, i).getY());
/*  264: 799 */         if (Utils.sm(currDistance, distance))
/*  265:     */         {
/*  266: 800 */           distance = currDistance;
/*  267: 801 */           y = i;
/*  268:     */         }
/*  269:     */       }
/*  270: 805 */       GridSearch.PointInt result = new GridSearch.PointInt(x, y);
/*  271: 806 */       return result;
/*  272:     */     }
/*  273:     */     
/*  274:     */     public boolean isOnBorder(GridSearch.PointDouble values)
/*  275:     */     {
/*  276: 816 */       return isOnBorder(getLocation(values));
/*  277:     */     }
/*  278:     */     
/*  279:     */     public boolean isOnBorder(GridSearch.PointInt location)
/*  280:     */     {
/*  281: 826 */       if (location.getX() == 0.0D) {
/*  282: 827 */         return true;
/*  283:     */       }
/*  284: 828 */       if (location.getX() == width() - 1) {
/*  285: 829 */         return true;
/*  286:     */       }
/*  287: 831 */       if (location.getY() == 0.0D) {
/*  288: 832 */         return true;
/*  289:     */       }
/*  290: 833 */       if (location.getY() == height() - 1) {
/*  291: 834 */         return true;
/*  292:     */       }
/*  293: 836 */       return false;
/*  294:     */     }
/*  295:     */     
/*  296:     */     public Grid subgrid(int top, int left, int bottom, int right)
/*  297:     */     {
/*  298: 850 */       return new Grid(getValues(left, top).getX(), getValues(right, top).getX(), getStepX(), getLabelX(), getValues(left, bottom).getY(), getValues(left, top).getY(), getStepY(), getLabelY());
/*  299:     */     }
/*  300:     */     
/*  301:     */     public Grid extend(GridSearch.PointDouble values)
/*  302:     */     {
/*  303:     */       double minX;
/*  304:     */       double minX;
/*  305: 871 */       if (Utils.smOrEq(values.getX(), getMinX()))
/*  306:     */       {
/*  307: 872 */         double distance = getMinX() - values.getX();
/*  308:     */         double minX;
/*  309: 874 */         if (Utils.eq(distance, 0.0D)) {
/*  310: 875 */           minX = getMinX() - getStepX() * (StrictMath.round(distance / getStepX()) + 1L);
/*  311:     */         } else {
/*  312: 878 */           minX = getMinX() - getStepX() * StrictMath.round(distance / getStepX());
/*  313:     */         }
/*  314:     */       }
/*  315:     */       else
/*  316:     */       {
/*  317: 882 */         minX = getMinX();
/*  318:     */       }
/*  319:     */       double maxX;
/*  320:     */       double maxX;
/*  321: 886 */       if (Utils.grOrEq(values.getX(), getMaxX()))
/*  322:     */       {
/*  323: 887 */         double distance = values.getX() - getMaxX();
/*  324:     */         double maxX;
/*  325: 889 */         if (Utils.eq(distance, 0.0D)) {
/*  326: 890 */           maxX = getMaxX() + getStepX() * (StrictMath.round(distance / getStepX()) + 1L);
/*  327:     */         } else {
/*  328: 893 */           maxX = getMaxX() + getStepX() * StrictMath.round(distance / getStepX());
/*  329:     */         }
/*  330:     */       }
/*  331:     */       else
/*  332:     */       {
/*  333: 897 */         maxX = getMaxX();
/*  334:     */       }
/*  335:     */       double minY;
/*  336:     */       double minY;
/*  337: 901 */       if (Utils.smOrEq(values.getY(), getMinY()))
/*  338:     */       {
/*  339: 902 */         double distance = getMinY() - values.getY();
/*  340:     */         double minY;
/*  341: 904 */         if (Utils.eq(distance, 0.0D)) {
/*  342: 905 */           minY = getMinY() - getStepY() * (StrictMath.round(distance / getStepY()) + 1L);
/*  343:     */         } else {
/*  344: 908 */           minY = getMinY() - getStepY() * StrictMath.round(distance / getStepY());
/*  345:     */         }
/*  346:     */       }
/*  347:     */       else
/*  348:     */       {
/*  349: 912 */         minY = getMinY();
/*  350:     */       }
/*  351:     */       double maxY;
/*  352:     */       double maxY;
/*  353: 916 */       if (Utils.grOrEq(values.getY(), getMaxY()))
/*  354:     */       {
/*  355: 917 */         double distance = values.getY() - getMaxY();
/*  356:     */         double maxY;
/*  357: 919 */         if (Utils.eq(distance, 0.0D)) {
/*  358: 920 */           maxY = getMaxY() + getStepY() * (StrictMath.round(distance / getStepY()) + 1L);
/*  359:     */         } else {
/*  360: 923 */           maxY = getMaxY() + getStepY() * StrictMath.round(distance / getStepY());
/*  361:     */         }
/*  362:     */       }
/*  363:     */       else
/*  364:     */       {
/*  365: 927 */         maxY = getMaxY();
/*  366:     */       }
/*  367: 930 */       Grid result = new Grid(minX, maxX, getStepX(), getLabelX(), minY, maxY, getStepY(), getLabelY());
/*  368: 934 */       if (equals(result)) {
/*  369: 935 */         throw new IllegalStateException("Grid extension failed!");
/*  370:     */       }
/*  371: 938 */       return result;
/*  372:     */     }
/*  373:     */     
/*  374:     */     public Enumeration<GridSearch.PointDouble> row(int y)
/*  375:     */     {
/*  376: 952 */       Vector<GridSearch.PointDouble> result = new Vector();
/*  377: 954 */       for (int i = 0; i < width(); i++) {
/*  378: 955 */         result.add(getValues(i, y));
/*  379:     */       }
/*  380: 958 */       return result.elements();
/*  381:     */     }
/*  382:     */     
/*  383:     */     public Enumeration<GridSearch.PointDouble> column(int x)
/*  384:     */     {
/*  385: 972 */       Vector<GridSearch.PointDouble> result = new Vector();
/*  386: 974 */       for (int i = 0; i < height(); i++) {
/*  387: 975 */         result.add(getValues(x, i));
/*  388:     */       }
/*  389: 978 */       return result.elements();
/*  390:     */     }
/*  391:     */     
/*  392:     */     public String toString()
/*  393:     */     {
/*  394: 990 */       String result = "X: " + this.m_MinX + " - " + this.m_MaxX + ", Step " + this.m_StepX;
/*  395: 991 */       if (this.m_LabelX.length() != 0) {
/*  396: 992 */         result = result + " (" + this.m_LabelX + ")";
/*  397:     */       }
/*  398: 994 */       result = result + "\n";
/*  399:     */       
/*  400: 996 */       result = result + "Y: " + this.m_MinY + " - " + this.m_MaxY + ", Step " + this.m_StepY;
/*  401: 997 */       if (this.m_LabelY.length() != 0) {
/*  402: 998 */         result = result + " (" + this.m_LabelY + ")";
/*  403:     */       }
/*  404:1000 */       result = result + "\n";
/*  405:     */       
/*  406:1002 */       result = result + "Dimensions (Rows x Columns): " + height() + " x " + width();
/*  407:     */       
/*  408:1004 */       return result;
/*  409:     */     }
/*  410:     */     
/*  411:     */     public String getRevision()
/*  412:     */     {
/*  413:1014 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  414:     */     }
/*  415:     */   }
/*  416:     */   
/*  417:     */   protected static class Performance
/*  418:     */     implements Serializable, RevisionHandler
/*  419:     */   {
/*  420:     */     private static final long serialVersionUID = -4374706475277588755L;
/*  421:     */     protected GridSearch.PointDouble m_Values;
/*  422:     */     protected double m_CC;
/*  423:     */     protected double m_RMSE;
/*  424:     */     protected double m_RRSE;
/*  425:     */     protected double m_MAE;
/*  426:     */     protected double m_RAE;
/*  427:     */     protected double m_ACC;
/*  428:     */     protected double m_wAUC;
/*  429:     */     protected double m_Kappa;
/*  430:     */     
/*  431:     */     public Performance(GridSearch.PointDouble values, Evaluation evaluation)
/*  432:     */       throws Exception
/*  433:     */     {
/*  434:1067 */       this.m_Values = values;
/*  435:     */       
/*  436:1069 */       this.m_RMSE = evaluation.rootMeanSquaredError();
/*  437:1070 */       this.m_RRSE = evaluation.rootRelativeSquaredError();
/*  438:1071 */       this.m_MAE = evaluation.meanAbsoluteError();
/*  439:1072 */       this.m_RAE = evaluation.relativeAbsoluteError();
/*  440:     */       try
/*  441:     */       {
/*  442:1075 */         this.m_wAUC = evaluation.weightedAreaUnderROC();
/*  443:     */       }
/*  444:     */       catch (Exception e)
/*  445:     */       {
/*  446:1077 */         this.m_wAUC = (0.0D / 0.0D);
/*  447:     */       }
/*  448:     */       try
/*  449:     */       {
/*  450:1080 */         this.m_CC = evaluation.correlationCoefficient();
/*  451:     */       }
/*  452:     */       catch (Exception e)
/*  453:     */       {
/*  454:1082 */         this.m_CC = (0.0D / 0.0D);
/*  455:     */       }
/*  456:     */       try
/*  457:     */       {
/*  458:1085 */         this.m_ACC = evaluation.pctCorrect();
/*  459:     */       }
/*  460:     */       catch (Exception e)
/*  461:     */       {
/*  462:1087 */         this.m_ACC = (0.0D / 0.0D);
/*  463:     */       }
/*  464:     */       try
/*  465:     */       {
/*  466:1090 */         this.m_Kappa = evaluation.kappa();
/*  467:     */       }
/*  468:     */       catch (Exception e)
/*  469:     */       {
/*  470:1092 */         this.m_Kappa = (0.0D / 0.0D);
/*  471:     */       }
/*  472:     */     }
/*  473:     */     
/*  474:     */     public double getPerformance(int evaluation)
/*  475:     */     {
/*  476:1105 */       double result = (0.0D / 0.0D);
/*  477:1107 */       switch (evaluation)
/*  478:     */       {
/*  479:     */       case 0: 
/*  480:1109 */         result = this.m_CC;
/*  481:1110 */         break;
/*  482:     */       case 1: 
/*  483:1112 */         result = this.m_RMSE;
/*  484:1113 */         break;
/*  485:     */       case 2: 
/*  486:1115 */         result = this.m_RRSE;
/*  487:1116 */         break;
/*  488:     */       case 3: 
/*  489:1118 */         result = this.m_MAE;
/*  490:1119 */         break;
/*  491:     */       case 4: 
/*  492:1121 */         result = this.m_RAE;
/*  493:1122 */         break;
/*  494:     */       case 5: 
/*  495:1124 */         result = 1.0D - StrictMath.abs(this.m_CC) + this.m_RRSE + this.m_RAE;
/*  496:1125 */         break;
/*  497:     */       case 6: 
/*  498:1127 */         result = this.m_ACC;
/*  499:1128 */         break;
/*  500:     */       case 7: 
/*  501:1130 */         result = this.m_Kappa;
/*  502:1131 */         break;
/*  503:     */       case 8: 
/*  504:1133 */         result = this.m_wAUC;
/*  505:1134 */         break;
/*  506:     */       default: 
/*  507:1136 */         throw new IllegalArgumentException("Evaluation type '" + evaluation + "' not supported!");
/*  508:     */       }
/*  509:1140 */       return result;
/*  510:     */     }
/*  511:     */     
/*  512:     */     public GridSearch.PointDouble getValues()
/*  513:     */     {
/*  514:1149 */       return this.m_Values;
/*  515:     */     }
/*  516:     */     
/*  517:     */     public String toString(int evaluation)
/*  518:     */     {
/*  519:1161 */       String result = "Performance (" + getValues() + "): " + getPerformance(evaluation) + " (" + new SelectedTag(evaluation, GridSearch.TAGS_EVALUATION) + ")";
/*  520:     */       
/*  521:     */ 
/*  522:     */ 
/*  523:1165 */       return result;
/*  524:     */     }
/*  525:     */     
/*  526:     */     public String toGnuplot(int evaluation)
/*  527:     */     {
/*  528:1177 */       String result = getValues().getX() + "\t" + getValues().getY() + "\t" + getPerformance(evaluation);
/*  529:     */       
/*  530:     */ 
/*  531:1180 */       return result;
/*  532:     */     }
/*  533:     */     
/*  534:     */     public String toString()
/*  535:     */     {
/*  536:1193 */       String result = "Performance (" + getValues() + "): ";
/*  537:1195 */       for (int i = 0; i < GridSearch.TAGS_EVALUATION.length; i++)
/*  538:     */       {
/*  539:1196 */         if (i > 0) {
/*  540:1197 */           result = result + ", ";
/*  541:     */         }
/*  542:1199 */         result = result + getPerformance(GridSearch.TAGS_EVALUATION[i].getID()) + " (" + new SelectedTag(GridSearch.TAGS_EVALUATION[i].getID(), GridSearch.TAGS_EVALUATION) + ")";
/*  543:     */       }
/*  544:1203 */       return result;
/*  545:     */     }
/*  546:     */     
/*  547:     */     public String getRevision()
/*  548:     */     {
/*  549:1213 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  550:     */     }
/*  551:     */   }
/*  552:     */   
/*  553:     */   protected static class PerformanceComparator
/*  554:     */     implements Comparator<GridSearch.Performance>, Serializable, RevisionHandler
/*  555:     */   {
/*  556:     */     private static final long serialVersionUID = 6507592831825393847L;
/*  557:     */     protected int m_Evaluation;
/*  558:     */     
/*  559:     */     public PerformanceComparator(int evaluation)
/*  560:     */     {
/*  561:1244 */       this.m_Evaluation = evaluation;
/*  562:     */     }
/*  563:     */     
/*  564:     */     public int getEvaluation()
/*  565:     */     {
/*  566:1254 */       return this.m_Evaluation;
/*  567:     */     }
/*  568:     */     
/*  569:     */     public int compare(GridSearch.Performance o1, GridSearch.Performance o2)
/*  570:     */     {
/*  571:1272 */       double p1 = o1.getPerformance(getEvaluation());
/*  572:1273 */       double p2 = o2.getPerformance(getEvaluation());
/*  573:     */       int result;
/*  574:     */       int result;
/*  575:1275 */       if (Utils.sm(p1, p2))
/*  576:     */       {
/*  577:1276 */         result = -1;
/*  578:     */       }
/*  579:     */       else
/*  580:     */       {
/*  581:     */         int result;
/*  582:1277 */         if (Utils.gr(p1, p2)) {
/*  583:1278 */           result = 1;
/*  584:     */         } else {
/*  585:1280 */           result = 0;
/*  586:     */         }
/*  587:     */       }
/*  588:1286 */       if ((getEvaluation() != 0) && (getEvaluation() != 6) && (getEvaluation() != 8) && (getEvaluation() != 7)) {
/*  589:1290 */         result = -result;
/*  590:     */       }
/*  591:1293 */       return result;
/*  592:     */     }
/*  593:     */     
/*  594:     */     public boolean equals(Object obj)
/*  595:     */     {
/*  596:1304 */       if (!(obj instanceof PerformanceComparator)) {
/*  597:1305 */         throw new IllegalArgumentException("Must be PerformanceComparator!");
/*  598:     */       }
/*  599:1308 */       return this.m_Evaluation == ((PerformanceComparator)obj).m_Evaluation;
/*  600:     */     }
/*  601:     */     
/*  602:     */     public String getRevision()
/*  603:     */     {
/*  604:1318 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  605:     */     }
/*  606:     */   }
/*  607:     */   
/*  608:     */   protected static class PerformanceTable
/*  609:     */     implements Serializable, RevisionHandler
/*  610:     */   {
/*  611:     */     private static final long serialVersionUID = 5486491313460338379L;
/*  612:     */     protected GridSearch m_Owner;
/*  613:     */     protected GridSearch.Grid m_Grid;
/*  614:     */     protected Vector<GridSearch.Performance> m_Performances;
/*  615:     */     protected int m_Type;
/*  616:     */     protected double[][] m_Table;
/*  617:     */     protected double m_Min;
/*  618:     */     protected double m_Max;
/*  619:     */     
/*  620:     */     public PerformanceTable(GridSearch owner, GridSearch.Grid grid, Vector<GridSearch.Performance> performances, int type)
/*  621:     */     {
/*  622:1375 */       this.m_Owner = owner;
/*  623:1376 */       this.m_Grid = grid;
/*  624:1377 */       this.m_Type = type;
/*  625:1378 */       this.m_Performances = performances;
/*  626:     */       
/*  627:1380 */       generate();
/*  628:     */     }
/*  629:     */     
/*  630:     */     protected void generate()
/*  631:     */     {
/*  632:1391 */       this.m_Table = new double[getGrid().height()][getGrid().width()];
/*  633:1392 */       this.m_Min = 0.0D;
/*  634:1393 */       this.m_Max = 0.0D;
/*  635:1395 */       for (int i = 0; i < getPerformances().size(); i++)
/*  636:     */       {
/*  637:1396 */         GridSearch.Performance perf = (GridSearch.Performance)getPerformances().get(i);
/*  638:1397 */         GridSearch.PointInt location = getGrid().getLocation(perf.getValues());
/*  639:1398 */         this.m_Table[(getGrid().height() - (int)location.getY() - 1)][((int)location.getX())] = perf.getPerformance(getType());
/*  640:1402 */         if (i == 0)
/*  641:     */         {
/*  642:1403 */           this.m_Min = perf.getPerformance(this.m_Type);
/*  643:1404 */           this.m_Max = this.m_Min;
/*  644:     */         }
/*  645:     */         else
/*  646:     */         {
/*  647:1406 */           if (perf.getPerformance(this.m_Type) < this.m_Min) {
/*  648:1407 */             this.m_Min = perf.getPerformance(this.m_Type);
/*  649:     */           }
/*  650:1409 */           if (perf.getPerformance(this.m_Type) > this.m_Max) {
/*  651:1410 */             this.m_Max = perf.getPerformance(this.m_Type);
/*  652:     */           }
/*  653:     */         }
/*  654:     */       }
/*  655:     */     }
/*  656:     */     
/*  657:     */     public GridSearch.Grid getGrid()
/*  658:     */     {
/*  659:1422 */       return this.m_Grid;
/*  660:     */     }
/*  661:     */     
/*  662:     */     public Vector<GridSearch.Performance> getPerformances()
/*  663:     */     {
/*  664:1431 */       return this.m_Performances;
/*  665:     */     }
/*  666:     */     
/*  667:     */     public int getType()
/*  668:     */     {
/*  669:1440 */       return this.m_Type;
/*  670:     */     }
/*  671:     */     
/*  672:     */     public double[][] getTable()
/*  673:     */     {
/*  674:1451 */       return this.m_Table;
/*  675:     */     }
/*  676:     */     
/*  677:     */     public double getMin()
/*  678:     */     {
/*  679:1460 */       return this.m_Min;
/*  680:     */     }
/*  681:     */     
/*  682:     */     public double getMax()
/*  683:     */     {
/*  684:1469 */       return this.m_Max;
/*  685:     */     }
/*  686:     */     
/*  687:     */     public String toString()
/*  688:     */     {
/*  689:1483 */       String result = "Table (" + new SelectedTag(getType(), GridSearch.TAGS_EVALUATION).getSelectedTag().getReadable() + ") - " + "X: " + getGrid().getLabelX() + ", Y: " + getGrid().getLabelY() + ":\n";
/*  690:1488 */       for (int i = 0; i < getTable().length; i++)
/*  691:     */       {
/*  692:1489 */         if (i > 0) {
/*  693:1490 */           result = result + "\n";
/*  694:     */         }
/*  695:1493 */         for (int n = 0; n < getTable()[i].length; n++)
/*  696:     */         {
/*  697:1494 */           if (n > 0) {
/*  698:1495 */             result = result + ",";
/*  699:     */           }
/*  700:1497 */           result = result + getTable()[i][n];
/*  701:     */         }
/*  702:     */       }
/*  703:1501 */       return result;
/*  704:     */     }
/*  705:     */     
/*  706:     */     public String toGnuplot()
/*  707:     */     {
/*  708:1514 */       StringBuffer result = new StringBuffer();
/*  709:1515 */       Tag type = new SelectedTag(getType(), GridSearch.TAGS_EVALUATION).getSelectedTag();
/*  710:     */       
/*  711:1517 */       result.append("Gnuplot (" + type.getReadable() + "):\n");
/*  712:1518 */       result.append("# begin 'gridsearch.data'\n");
/*  713:1519 */       result.append("# " + type.getReadable() + "\n");
/*  714:1520 */       for (int i = 0; i < getPerformances().size(); i++) {
/*  715:1521 */         result.append(((GridSearch.Performance)getPerformances().get(i)).toGnuplot(type.getID()) + "\n");
/*  716:     */       }
/*  717:1523 */       result.append("# end 'gridsearch.data'\n\n");
/*  718:     */       
/*  719:1525 */       result.append("# begin 'gridsearch.plot'\n");
/*  720:1526 */       result.append("# " + type.getReadable() + "\n");
/*  721:1527 */       result.append("set data style lines\n");
/*  722:1528 */       result.append("set contour base\n");
/*  723:1529 */       result.append("set surface\n");
/*  724:1530 */       result.append("set title '" + this.m_Owner.getData().relationName() + "'\n");
/*  725:1531 */       result.append("set xrange [" + getGrid().getMinX() + ":" + getGrid().getMaxX() + "]\n");
/*  726:     */       
/*  727:1533 */       result.append("set xlabel 'x (" + this.m_Owner.getClassifier().getClass().getName() + ": " + this.m_Owner.getXProperty() + ")'\n");
/*  728:     */       
/*  729:     */ 
/*  730:1536 */       result.append("set yrange [" + getGrid().getMinY() + ":" + getGrid().getMaxY() + "]\n");
/*  731:     */       
/*  732:1538 */       result.append("set ylabel 'y - (" + this.m_Owner.getClassifier().getClass().getName() + ": " + this.m_Owner.getYProperty() + ")'\n");
/*  733:     */       
/*  734:     */ 
/*  735:1541 */       result.append("set zrange [" + (getMin() - (getMax() - getMin()) * 0.1D) + ":" + (getMax() + (getMax() - getMin()) * 0.1D) + "]\n");
/*  736:     */       
/*  737:1543 */       result.append("set zlabel 'z - " + type.getReadable() + "'\n");
/*  738:1544 */       result.append("set dgrid3d " + getGrid().height() + "," + getGrid().width() + ",1\n");
/*  739:     */       
/*  740:1546 */       result.append("show contour\n");
/*  741:1547 */       result.append("splot 'gridsearch.data'\n");
/*  742:1548 */       result.append("pause -1\n");
/*  743:1549 */       result.append("# end 'gridsearch.plot'");
/*  744:     */       
/*  745:1551 */       return result.toString();
/*  746:     */     }
/*  747:     */     
/*  748:     */     public String getRevision()
/*  749:     */     {
/*  750:1561 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  751:     */     }
/*  752:     */   }
/*  753:     */   
/*  754:     */   protected static class PerformanceCache
/*  755:     */     implements Serializable, RevisionHandler
/*  756:     */   {
/*  757:     */     private static final long serialVersionUID = 5838863230451530252L;
/*  758:1575 */     protected Hashtable<String, GridSearch.Performance> m_Cache = new Hashtable();
/*  759:     */     
/*  760:     */     protected String getID(int cv, GridSearch.PointDouble values)
/*  761:     */     {
/*  762:1586 */       return cv + "\t" + values.getX() + "\t" + values.getY();
/*  763:     */     }
/*  764:     */     
/*  765:     */     public boolean isCached(int cv, GridSearch.PointDouble values)
/*  766:     */     {
/*  767:1597 */       return get(cv, values) != null;
/*  768:     */     }
/*  769:     */     
/*  770:     */     public GridSearch.Performance get(int cv, GridSearch.PointDouble values)
/*  771:     */     {
/*  772:1608 */       return (GridSearch.Performance)this.m_Cache.get(getID(cv, values));
/*  773:     */     }
/*  774:     */     
/*  775:     */     public void add(int cv, GridSearch.Performance p)
/*  776:     */     {
/*  777:1618 */       this.m_Cache.put(getID(cv, p.getValues()), p);
/*  778:     */     }
/*  779:     */     
/*  780:     */     public String toString()
/*  781:     */     {
/*  782:1628 */       return this.m_Cache.toString();
/*  783:     */     }
/*  784:     */     
/*  785:     */     public String getRevision()
/*  786:     */     {
/*  787:1638 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  788:     */     }
/*  789:     */   }
/*  790:     */   
/*  791:     */   protected static class SetupGenerator
/*  792:     */     implements Serializable, RevisionHandler
/*  793:     */   {
/*  794:     */     private static final long serialVersionUID = -2517395033342543417L;
/*  795:1652 */     private static final SimpleVariableDeclarations variables = new SimpleVariableDeclarations();
/*  796:     */     protected GridSearch m_Owner;
/*  797:     */     protected String m_Y_Property;
/*  798:     */     protected double m_Y_Min;
/*  799:     */     protected double m_Y_Max;
/*  800:     */     protected double m_Y_Step;
/*  801:     */     protected double m_Y_Base;
/*  802:     */     protected String m_Y_Expression;
/*  803:     */     private Primitives.DoubleExpression m_Y_Node;
/*  804:     */     protected String m_X_Property;
/*  805:     */     protected double m_X_Min;
/*  806:     */     protected double m_X_Max;
/*  807:     */     protected double m_X_Step;
/*  808:     */     protected double m_X_Base;
/*  809:     */     private Primitives.DoubleExpression m_X_Node;
/*  810:     */     protected String m_X_Expression;
/*  811:     */     
/*  812:     */     static
/*  813:     */     {
/*  814:1656 */       variables.addDouble("BASE");
/*  815:1657 */       variables.addDouble("FROM");
/*  816:1658 */       variables.addDouble("TO");
/*  817:1659 */       variables.addDouble("STEP");
/*  818:1660 */       variables.addDouble("I");
/*  819:     */     }
/*  820:     */     
/*  821:     */     public SetupGenerator(GridSearch owner)
/*  822:     */     {
/*  823:1716 */       this.m_Owner = owner;
/*  824:     */       
/*  825:1718 */       this.m_Y_Expression = this.m_Owner.getYExpression();
/*  826:1719 */       this.m_Y_Property = this.m_Owner.getYProperty();
/*  827:1720 */       this.m_Y_Min = this.m_Owner.getYMin();
/*  828:1721 */       this.m_Y_Max = this.m_Owner.getYMax();
/*  829:1722 */       this.m_Y_Step = this.m_Owner.getYStep();
/*  830:1723 */       this.m_Y_Base = this.m_Owner.getYBase();
/*  831:     */       try
/*  832:     */       {
/*  833:1727 */         this.m_Y_Node = ((Primitives.DoubleExpression)Parser.parse(this.m_Y_Expression, variables, new MacroDeclarationsCompositor(new MacroDeclarations[] { new MathFunctions(), new IfElseMacro(), new JavaMacro() })));
/*  834:     */       }
/*  835:     */       catch (Exception e)
/*  836:     */       {
/*  837:1740 */         this.m_Y_Node = null;
/*  838:1741 */         System.err.println("Failed to compile Y expression '" + this.m_Y_Expression + "'");
/*  839:     */         
/*  840:1743 */         e.printStackTrace();
/*  841:     */       }
/*  842:1746 */       this.m_X_Expression = this.m_Owner.getXExpression();
/*  843:1747 */       this.m_X_Property = this.m_Owner.getXProperty();
/*  844:1748 */       this.m_X_Min = this.m_Owner.getXMin();
/*  845:1749 */       this.m_X_Max = this.m_Owner.getXMax();
/*  846:1750 */       this.m_X_Step = this.m_Owner.getXStep();
/*  847:1751 */       this.m_X_Base = this.m_Owner.getXBase();
/*  848:     */       try
/*  849:     */       {
/*  850:1755 */         this.m_X_Node = ((Primitives.DoubleExpression)Parser.parse(this.m_X_Expression, variables, new MacroDeclarationsCompositor(new MacroDeclarations[] { new MathFunctions(), new IfElseMacro(), new JavaMacro() })));
/*  851:     */       }
/*  852:     */       catch (Exception e)
/*  853:     */       {
/*  854:1768 */         this.m_X_Node = null;
/*  855:1769 */         System.err.println("Failed to compile X expression '" + this.m_X_Expression + "'");
/*  856:     */         
/*  857:1771 */         e.printStackTrace();
/*  858:     */       }
/*  859:     */     }
/*  860:     */     
/*  861:     */     public double evaluate(double value, boolean isX)
/*  862:     */     {
/*  863:     */       Primitives.DoubleExpression expr;
/*  864:     */       Primitives.DoubleExpression expr;
/*  865:1788 */       if (isX)
/*  866:     */       {
/*  867:1789 */         if (variables.getInitializer().hasVariable("BASE")) {
/*  868:1790 */           variables.getInitializer().setDouble("BASE", this.m_X_Base);
/*  869:     */         }
/*  870:1791 */         if (variables.getInitializer().hasVariable("FROM")) {
/*  871:1792 */           variables.getInitializer().setDouble("FROM", this.m_X_Min);
/*  872:     */         }
/*  873:1793 */         if (variables.getInitializer().hasVariable("TO")) {
/*  874:1794 */           variables.getInitializer().setDouble("TO", this.m_X_Max);
/*  875:     */         }
/*  876:1795 */         if (variables.getInitializer().hasVariable("STEP")) {
/*  877:1796 */           variables.getInitializer().setDouble("STEP", this.m_X_Step);
/*  878:     */         }
/*  879:1797 */         expr = this.m_X_Node;
/*  880:     */       }
/*  881:     */       else
/*  882:     */       {
/*  883:1799 */         if (variables.getInitializer().hasVariable("BASE")) {
/*  884:1800 */           variables.getInitializer().setDouble("BASE", this.m_Y_Base);
/*  885:     */         }
/*  886:1801 */         if (variables.getInitializer().hasVariable("FROM")) {
/*  887:1802 */           variables.getInitializer().setDouble("FROM", this.m_Y_Min);
/*  888:     */         }
/*  889:1803 */         if (variables.getInitializer().hasVariable("TO")) {
/*  890:1804 */           variables.getInitializer().setDouble("TO", this.m_Y_Max);
/*  891:     */         }
/*  892:1805 */         if (variables.getInitializer().hasVariable("STEP")) {
/*  893:1806 */           variables.getInitializer().setDouble("STEP", this.m_Y_Step);
/*  894:     */         }
/*  895:1807 */         expr = this.m_Y_Node;
/*  896:     */       }
/*  897:1809 */       if (variables.getInitializer().hasVariable("I")) {
/*  898:1810 */         variables.getInitializer().setDouble("I", value);
/*  899:     */       }
/*  900:     */       try
/*  901:     */       {
/*  902:1813 */         return expr.evaluate();
/*  903:     */       }
/*  904:     */       catch (Exception e)
/*  905:     */       {
/*  906:1815 */         e.printStackTrace();
/*  907:     */       }
/*  908:1816 */       return (0.0D / 0.0D);
/*  909:     */     }
/*  910:     */     
/*  911:     */     public Object setValue(Object o, String path, double value)
/*  912:     */       throws Exception
/*  913:     */     {
/*  914:1836 */       PropertyDescriptor desc = PropertyPath.getPropertyDescriptor(o, path);
/*  915:1837 */       Class<?> c = desc.getPropertyType();
/*  916:1840 */       if ((c == Float.class) || (c == Float.TYPE)) {
/*  917:1841 */         PropertyPath.setValue(o, path, new Float((float)value));
/*  918:1842 */       } else if ((c == Double.class) || (c == Double.TYPE)) {
/*  919:1843 */         PropertyPath.setValue(o, path, new Double(value));
/*  920:1844 */       } else if ((c == Character.class) || (c == Character.TYPE)) {
/*  921:1845 */         PropertyPath.setValue(o, path, new Integer((char)(int)value));
/*  922:1846 */       } else if ((c == Integer.class) || (c == Integer.TYPE)) {
/*  923:1847 */         PropertyPath.setValue(o, path, new Integer((int)value));
/*  924:1848 */       } else if ((c == Long.class) || (c == Long.TYPE)) {
/*  925:1849 */         PropertyPath.setValue(o, path, new Long(value));
/*  926:1850 */       } else if ((c == Boolean.class) || (c == Boolean.TYPE)) {
/*  927:1851 */         PropertyPath.setValue(o, path, value == 0.0D ? new Boolean(false) : new Boolean(true));
/*  928:     */       } else {
/*  929:1854 */         throw new Exception("Could neither set double nor integer nor boolean value for '" + path + "'!");
/*  930:     */       }
/*  931:1859 */       return o;
/*  932:     */     }
/*  933:     */     
/*  934:     */     public Object setup(Object original, double valueX, double valueY)
/*  935:     */       throws Exception
/*  936:     */     {
/*  937:1875 */       Object result = new SerializedObject(original).getObject();
/*  938:1877 */       if ((original instanceof Classifier))
/*  939:     */       {
/*  940:1878 */         setValue(result, this.m_X_Property, valueX);
/*  941:     */         
/*  942:1880 */         setValue(result, this.m_Y_Property, valueY);
/*  943:     */       }
/*  944:     */       else
/*  945:     */       {
/*  946:1882 */         throw new IllegalArgumentException("Object must be a classifier!");
/*  947:     */       }
/*  948:1886 */       return result;
/*  949:     */     }
/*  950:     */     
/*  951:     */     public String getRevision()
/*  952:     */     {
/*  953:1896 */       return RevisionUtils.extract("$Revision: 11501 $");
/*  954:     */     }
/*  955:     */   }
/*  956:     */   
/*  957:     */   protected static class EvaluationTask
/*  958:     */     implements Runnable, RevisionHandler
/*  959:     */   {
/*  960:     */     protected GridSearch m_Owner;
/*  961:     */     protected GridSearch.SetupGenerator m_Generator;
/*  962:     */     protected Classifier m_Classifier;
/*  963:     */     protected Instances m_Data;
/*  964:     */     protected GridSearch.PointDouble m_Values;
/*  965:     */     protected int m_Folds;
/*  966:     */     protected int m_Evaluation;
/*  967:     */     
/*  968:     */     public EvaluationTask(GridSearch owner, GridSearch.SetupGenerator generator, Instances inst, GridSearch.PointDouble values, int folds, int eval)
/*  969:     */     {
/*  970:1941 */       this.m_Owner = owner;
/*  971:1942 */       this.m_Generator = generator;
/*  972:1943 */       this.m_Classifier = this.m_Owner.getClassifier();
/*  973:1944 */       this.m_Data = inst;
/*  974:1945 */       this.m_Values = values;
/*  975:1946 */       this.m_Folds = folds;
/*  976:1947 */       this.m_Evaluation = eval;
/*  977:     */     }
/*  978:     */     
/*  979:     */     public void run()
/*  980:     */     {
/*  981:1962 */       Classifier classifier = null;
/*  982:1963 */       double x = this.m_Generator.evaluate(this.m_Values.getX(), true);
/*  983:1964 */       double y = this.m_Generator.evaluate(this.m_Values.getY(), false);
/*  984:     */       try
/*  985:     */       {
/*  986:1967 */         Instances data = this.m_Data;
/*  987:     */         
/*  988:     */ 
/*  989:1970 */         classifier = (Classifier)this.m_Generator.setup(this.m_Classifier, x, y);
/*  990:     */         
/*  991:     */ 
/*  992:1973 */         Evaluation eval = new Evaluation(data);
/*  993:1974 */         eval.crossValidateModel(classifier, data, this.m_Folds, new Random(this.m_Owner.getSeed()), new Object[0]);
/*  994:     */         
/*  995:     */ 
/*  996:     */ 
/*  997:1978 */         GridSearch.Performance performance = new GridSearch.Performance(this.m_Values, eval);
/*  998:1979 */         this.m_Owner.addPerformance(performance, this.m_Folds);
/*  999:     */         
/* 1000:     */ 
/* 1001:1982 */         this.m_Owner.log(performance + ": cached=false");
/* 1002:     */         
/* 1003:     */ 
/* 1004:1985 */         this.m_Owner.completedEvaluation(classifier, null);
/* 1005:     */       }
/* 1006:     */       catch (Exception e)
/* 1007:     */       {
/* 1008:1987 */         if (this.m_Owner.getDebug())
/* 1009:     */         {
/* 1010:1988 */           System.err.println("Encountered exception while evaluating classifier, skipping!");
/* 1011:     */           
/* 1012:1990 */           System.err.println("- Values....: " + this.m_Values);
/* 1013:1991 */           System.err.println("- Classifier: " + (classifier != null ? Utils.toCommandLine(classifier) : "-no setup-"));
/* 1014:     */           
/* 1015:     */ 
/* 1016:1994 */           e.printStackTrace();
/* 1017:     */         }
/* 1018:1996 */         this.m_Owner.completedEvaluation(this.m_Values, e);
/* 1019:     */       }
/* 1020:2000 */       this.m_Owner = null;
/* 1021:2001 */       this.m_Data = null;
/* 1022:     */     }
/* 1023:     */     
/* 1024:     */     public String getRevision()
/* 1025:     */     {
/* 1026:2011 */       return RevisionUtils.extract("$Revision: 11501 $");
/* 1027:     */     }
/* 1028:     */   }
/* 1029:     */   
/* 1030:2037 */   public static final Tag[] TAGS_EVALUATION = { new Tag(0, "CC", "Correlation coefficient"), new Tag(1, "RMSE", "Root mean squared error"), new Tag(2, "RRSE", "Root relative squared error"), new Tag(3, "MAE", "Mean absolute error"), new Tag(4, "RAE", "Root absolute error"), new Tag(5, "COMB", "Combined = (1-abs(CC)) + RRSE + RAE"), new Tag(6, "ACC", "Accuracy"), new Tag(8, "WAUC", "Weighted AUC"), new Tag(7, "KAP", "Kappa") };
/* 1031:     */   public static final int TRAVERSAL_BY_ROW = 0;
/* 1032:     */   public static final int TRAVERSAL_BY_COLUMN = 1;
/* 1033:2055 */   public static final Tag[] TAGS_TRAVERSAL = { new Tag(0, "row-wise", "row-wise"), new Tag(1, "column-wise", "column-wise") };
/* 1034:     */   protected Classifier m_BestClassifier;
/* 1035:2063 */   protected PointDouble m_Values = null;
/* 1036:2066 */   protected int m_Evaluation = 0;
/* 1037:2071 */   protected String m_Y_Property = "kernel.gamma";
/* 1038:2074 */   protected double m_Y_Min = -3.0D;
/* 1039:2077 */   protected double m_Y_Max = 3.0D;
/* 1040:2080 */   protected double m_Y_Step = 1.0D;
/* 1041:2083 */   protected double m_Y_Base = 10.0D;
/* 1042:2097 */   protected String m_Y_Expression = "pow(BASE,I)";
/* 1043:2102 */   protected String m_X_Property = "C";
/* 1044:2105 */   protected double m_X_Min = -3.0D;
/* 1045:2108 */   protected double m_X_Max = 3.0D;
/* 1046:2111 */   protected double m_X_Step = 1.0D;
/* 1047:2114 */   protected double m_X_Base = 10.0D;
/* 1048:2128 */   protected String m_X_Expression = "pow(BASE,I)";
/* 1049:2131 */   protected boolean m_GridIsExtendable = false;
/* 1050:2134 */   protected int m_MaxGridExtensions = 3;
/* 1051:2137 */   protected int m_GridExtensionsPerformed = 0;
/* 1052:2140 */   protected double m_SampleSize = 100.0D;
/* 1053:2143 */   protected int m_Traversal = 1;
/* 1054:2146 */   protected File m_LogFile = new File(System.getProperty("user.dir"));
/* 1055:     */   protected Grid m_Grid;
/* 1056:     */   protected Instances m_Data;
/* 1057:     */   protected PerformanceCache m_Cache;
/* 1058:     */   protected Vector<Performance> m_Performances;
/* 1059:2161 */   protected boolean m_UniformPerformance = false;
/* 1060:2164 */   protected int m_NumExecutionSlots = 1;
/* 1061:     */   protected transient ThreadPoolExecutor m_ExecutorPool;
/* 1062:     */   protected int m_Completed;
/* 1063:     */   protected int m_Failed;
/* 1064:     */   protected int m_NumSetups;
/* 1065:     */   protected SetupGenerator m_Generator;
/* 1066:     */   protected transient Exception m_Exception;
/* 1067:     */   protected static final String PROPERTY_FILE = "weka/classifiers/meta/GridSearch.props";
/* 1068:     */   protected static Properties GRID_SEARCH_PROPS;
/* 1069:     */   
/* 1070:     */   static
/* 1071:     */   {
/* 1072:     */     try
/* 1073:     */     {
/* 1074:2196 */       GRID_SEARCH_PROPS = Utils.readProperties("weka/classifiers/meta/GridSearch.props");
/* 1075:     */     }
/* 1076:     */     catch (Exception ex)
/* 1077:     */     {
/* 1078:2199 */       ex.printStackTrace();
/* 1079:     */     }
/* 1080:     */   }
/* 1081:     */   
/* 1082:     */   public GridSearch()
/* 1083:     */   {
/* 1084:2209 */     defaultsFromProps();
/* 1085:     */     try
/* 1086:     */     {
/* 1087:2212 */       this.m_BestClassifier = AbstractClassifier.makeCopy(this.m_Classifier);
/* 1088:     */     }
/* 1089:     */     catch (Exception e)
/* 1090:     */     {
/* 1091:2214 */       e.printStackTrace();
/* 1092:     */     }
/* 1093:     */   }
/* 1094:     */   
/* 1095:     */   protected void defaultsFromProps()
/* 1096:     */   {
/* 1097:     */     try
/* 1098:     */     {
/* 1099:2226 */       if (GRID_SEARCH_PROPS != null)
/* 1100:     */       {
/* 1101:2227 */         String classifierSpec = GRID_SEARCH_PROPS.getProperty("classifier");
/* 1102:2228 */         if ((classifierSpec != null) && (classifierSpec.length() > 0))
/* 1103:     */         {
/* 1104:2229 */           String[] spec = Utils.splitOptions(classifierSpec);
/* 1105:2230 */           String classifier = spec[0];
/* 1106:2231 */           spec[0] = "";
/* 1107:2232 */           boolean ok = true;
/* 1108:     */           try
/* 1109:     */           {
/* 1110:2234 */             Classifier result = AbstractClassifier.forName(classifier, spec);
/* 1111:2235 */             setClassifier(result);
/* 1112:     */             
/* 1113:     */ 
/* 1114:2238 */             String yProp = GRID_SEARCH_PROPS.getProperty("yProperty", "");
/* 1115:2239 */             String yMin = GRID_SEARCH_PROPS.getProperty("yMin", "");
/* 1116:2240 */             String yMax = GRID_SEARCH_PROPS.getProperty("yMax", "");
/* 1117:2241 */             String yStep = GRID_SEARCH_PROPS.getProperty("yStep", "");
/* 1118:2242 */             String yBase = GRID_SEARCH_PROPS.getProperty("yBase", "");
/* 1119:2243 */             String yExpression = GRID_SEARCH_PROPS.getProperty("yExpression", "");
/* 1120:2245 */             if ((yProp.length() > 0) && (yMin.length() > 0) && (yMax.length() > 0) && (yStep.length() > 0) && (yBase.length() > 0) && (yExpression.length() > 0))
/* 1121:     */             {
/* 1122:2248 */               setYProperty(yProp);
/* 1123:2249 */               setYMin(Double.parseDouble(yMin));
/* 1124:2250 */               setYMax(Double.parseDouble(yMax));
/* 1125:2251 */               setYStep(Double.parseDouble(yStep));
/* 1126:2252 */               setYBase(Double.parseDouble(yBase));
/* 1127:2253 */               setYExpression(yExpression);
/* 1128:     */             }
/* 1129:     */             else
/* 1130:     */             {
/* 1131:2255 */               ok = false;
/* 1132:     */             }
/* 1133:2258 */             String xProp = GRID_SEARCH_PROPS.getProperty("xProperty", "");
/* 1134:2259 */             String xMin = GRID_SEARCH_PROPS.getProperty("xMin", "");
/* 1135:2260 */             String xMax = GRID_SEARCH_PROPS.getProperty("xMax", "");
/* 1136:2261 */             String xStep = GRID_SEARCH_PROPS.getProperty("xStep", "");
/* 1137:2262 */             String xBase = GRID_SEARCH_PROPS.getProperty("xBase", "");
/* 1138:2263 */             String xExpression = GRID_SEARCH_PROPS.getProperty("xExpression", "");
/* 1139:2265 */             if ((xProp.length() > 0) && (xMin.length() > 0) && (xMax.length() > 0) && (xStep.length() > 0) && (xBase.length() > 0) && (xExpression.length() > 0))
/* 1140:     */             {
/* 1141:2268 */               setXProperty(xProp);
/* 1142:2269 */               setXMin(Double.parseDouble(xMin));
/* 1143:2270 */               setXMax(Double.parseDouble(xMax));
/* 1144:2271 */               setXStep(Double.parseDouble(xStep));
/* 1145:2272 */               setXBase(Double.parseDouble(xBase));
/* 1146:2273 */               setXExpression(xExpression);
/* 1147:     */             }
/* 1148:     */             else
/* 1149:     */             {
/* 1150:2275 */               ok = false;
/* 1151:     */             }
/* 1152:2279 */             String gridExtend = GRID_SEARCH_PROPS.getProperty("gridIsExtendable", "false");
/* 1153:     */             
/* 1154:2281 */             setGridIsExtendable(Boolean.parseBoolean(gridExtend));
/* 1155:2282 */             String maxExtensions = GRID_SEARCH_PROPS.getProperty("maxGridExtensions", "3");
/* 1156:     */             
/* 1157:2284 */             setMaxGridExtensions(Integer.parseInt(maxExtensions));
/* 1158:2285 */             String sampleSizePerc = GRID_SEARCH_PROPS.getProperty("sampleSizePercent", "100");
/* 1159:     */             
/* 1160:2287 */             setSampleSizePercent(Integer.parseInt(sampleSizePerc));
/* 1161:2288 */             String traversal = GRID_SEARCH_PROPS.getProperty("traversal", "0");
/* 1162:2289 */             this.m_Traversal = Integer.parseInt(traversal);
/* 1163:2290 */             String eval = GRID_SEARCH_PROPS.getProperty("evaluation", "0");
/* 1164:2291 */             this.m_Evaluation = Integer.parseInt(eval);
/* 1165:2292 */             String numSlots = GRID_SEARCH_PROPS.getProperty("numSlots", "1");
/* 1166:2293 */             setNumExecutionSlots(Integer.parseInt(numSlots));
/* 1167:     */           }
/* 1168:     */           catch (Exception ex)
/* 1169:     */           {
/* 1170:2296 */             ok = false;
/* 1171:     */           }
/* 1172:2299 */           if (!ok)
/* 1173:     */           {
/* 1174:2301 */             setClassifier(new GaussianProcesses());
/* 1175:2302 */             setYProperty("kernel.exponent");
/* 1176:2303 */             setYMin(1.0D);
/* 1177:2304 */             setYMax(5.0D);
/* 1178:2305 */             setYStep(1.0D);
/* 1179:2306 */             setYBase(10.0D);
/* 1180:2307 */             setYExpression("I");
/* 1181:     */             
/* 1182:2309 */             setXProperty("noise");
/* 1183:2310 */             setXMin(0.2D);
/* 1184:2311 */             setXMax(2.0D);
/* 1185:2312 */             setXStep(0.2D);
/* 1186:2313 */             setXBase(10.0D);
/* 1187:2314 */             setXExpression("I");
/* 1188:     */           }
/* 1189:     */         }
/* 1190:     */       }
/* 1191:     */     }
/* 1192:     */     catch (Exception e)
/* 1193:     */     {
/* 1194:2319 */       e.printStackTrace();
/* 1195:     */     }
/* 1196:     */   }
/* 1197:     */   
/* 1198:     */   public String globalInfo()
/* 1199:     */   {
/* 1200:2330 */     return "Performs a grid search of parameter pairs for a classifier  and chooses the best pair found for the actual predicting.\n\nThe initial grid is worked on with 2-fold CV to determine the values of the parameter pairs for the selected type of evaluation (e.g., accuracy). The best point in the grid is then taken and a 10-fold CV is performed with the adjacent parameter pairs. If a better pair is found, then this will act as new center and another 10-fold CV will be performed (kind of hill-climbing). This process is repeated until no better pair is found or the best pair is on the border of the grid.\nIn case the best pair is on the border, one can let GridSearch automatically extend the grid and continue the search. Check out the properties 'gridIsExtendable' (option '-extend-grid') and 'maxGridExtensions' (option '-max-grid-extensions <num>').\n\nGridSearch can handle doubles, integers (values are just cast to int) and booleans (0 is false, otherwise true). float, char and long are supported as well.\n\nThe best classifier setup can be accessed after the buildClassifier call via the getBestClassifier methods.\nNote: with -num-slots/numExecutionSlots you can specify how many setups are evaluated in parallel, taking advantage of multi-cpu/core architectures.";
/* 1201:     */   }
/* 1202:     */   
/* 1203:     */   protected String getClassifierSpec()
/* 1204:     */   {
/* 1205:2362 */     Classifier c = getClassifier();
/* 1206:2363 */     if ((c instanceof OptionHandler)) {
/* 1207:2364 */       return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 1208:     */     }
/* 1209:2367 */     return c.getClass().getName();
/* 1210:     */   }
/* 1211:     */   
/* 1212:     */   protected String defaultClassifierString()
/* 1213:     */   {
/* 1214:     */     try
/* 1215:     */     {
/* 1216:2378 */       if (GRID_SEARCH_PROPS != null)
/* 1217:     */       {
/* 1218:2379 */         String classifierSpec = GRID_SEARCH_PROPS.getProperty("classifier");
/* 1219:2380 */         if ((classifierSpec != null) && (classifierSpec.length() > 0))
/* 1220:     */         {
/* 1221:2381 */           String[] parts = classifierSpec.split(" ");
/* 1222:2382 */           if (parts.length > 0) {
/* 1223:2383 */             return parts[0].trim();
/* 1224:     */           }
/* 1225:     */         }
/* 1226:     */       }
/* 1227:     */     }
/* 1228:     */     catch (Exception ex) {}
/* 1229:2391 */     return "weka.classifiers.functions.GaussianProcesses";
/* 1230:     */   }
/* 1231:     */   
/* 1232:     */   protected String[] defaultClassifierOptions()
/* 1233:     */   {
/* 1234:     */     try
/* 1235:     */     {
/* 1236:2402 */       if (GRID_SEARCH_PROPS != null)
/* 1237:     */       {
/* 1238:2403 */         String classifierSpec = GRID_SEARCH_PROPS.getProperty("classifier");
/* 1239:2404 */         if ((classifierSpec != null) && (classifierSpec.length() > 0))
/* 1240:     */         {
/* 1241:2406 */           String[] parts = Utils.splitOptions(classifierSpec);
/* 1242:2407 */           if (parts.length > 1)
/* 1243:     */           {
/* 1244:2409 */             parts[0] = "";
/* 1245:2410 */             return parts;
/* 1246:     */           }
/* 1247:     */         }
/* 1248:     */       }
/* 1249:     */     }
/* 1250:     */     catch (Exception ex) {}
/* 1251:2418 */     String[] opts = new String[0];
/* 1252:     */     
/* 1253:     */ 
/* 1254:2421 */     return opts;
/* 1255:     */   }
/* 1256:     */   
/* 1257:     */   public Enumeration<Option> listOptions()
/* 1258:     */   {
/* 1259:2432 */     Vector<Option> result = new Vector();
/* 1260:     */     
/* 1261:2434 */     String desc = "";
/* 1262:2435 */     for (Tag element : TAGS_EVALUATION)
/* 1263:     */     {
/* 1264:2436 */       SelectedTag tag = new SelectedTag(element.getID(), TAGS_EVALUATION);
/* 1265:2437 */       desc = desc + "\t" + tag.getSelectedTag().getIDStr() + " = " + tag.getSelectedTag().getReadable() + "\n";
/* 1266:     */     }
/* 1267:2440 */     result.addElement(new Option("\tDetermines the parameter used for evaluation:\n" + desc + "\t(default: " + new SelectedTag(0, TAGS_EVALUATION) + ")", "E", 1, "-E " + Tag.toOptionList(TAGS_EVALUATION)));
/* 1268:     */     
/* 1269:     */ 
/* 1270:     */ 
/* 1271:     */ 
/* 1272:2445 */     result.addElement(new Option("\tThe Y option to test (without leading dash).\n\t(default: kernel.gamma)", "y-property", 1, "-y-property <option>"));
/* 1273:     */     
/* 1274:     */ 
/* 1275:     */ 
/* 1276:     */ 
/* 1277:2450 */     result.addElement(new Option("\tThe minimum for Y.\n\t(default: -3)", "y-min", 1, "-y-min <num>"));
/* 1278:     */     
/* 1279:     */ 
/* 1280:2453 */     result.addElement(new Option("\tThe maximum for Y.\n\t(default: +3)", "y-max", 1, "-y-max <num>"));
/* 1281:     */     
/* 1282:     */ 
/* 1283:2456 */     result.addElement(new Option("\tThe step size for Y.\n\t(default: 1)", "y-step", 1, "-y-step <num>"));
/* 1284:     */     
/* 1285:     */ 
/* 1286:2459 */     result.addElement(new Option("\tThe base for Y.\n\t(default: 10)", "y-base", 1, "-y-base <num>"));
/* 1287:     */     
/* 1288:     */ 
/* 1289:2462 */     result.addElement(new Option("\tThe expression for Y.\n\tAvailable parameters:\n\t\tBASE\n\t\tFROM\n\t\tTO\n\t\tSTEP\n\t\tI - the current iteration value\n\t\t(from 'FROM' to 'TO' with stepsize 'STEP')\n\t(default: 'pow(BASE,I)')", "y-expression", 1, "-y-expression <expr>"));
/* 1290:     */     
/* 1291:     */ 
/* 1292:     */ 
/* 1293:     */ 
/* 1294:     */ 
/* 1295:     */ 
/* 1296:     */ 
/* 1297:2470 */     result.addElement(new Option("\tThe X option to test (without leading dash).\n\t(default: C)", "x-property", 1, "-x-property <option>"));
/* 1298:     */     
/* 1299:     */ 
/* 1300:     */ 
/* 1301:     */ 
/* 1302:2475 */     result.addElement(new Option("\tThe minimum for X.\n\t(default: -3)", "x-min", 1, "-x-min <num>"));
/* 1303:     */     
/* 1304:     */ 
/* 1305:2478 */     result.addElement(new Option("\tThe maximum for X.\n\t(default: 3)", "x-max", 1, "-x-max <num>"));
/* 1306:     */     
/* 1307:     */ 
/* 1308:2481 */     result.addElement(new Option("\tThe step size for X.\n\t(default: 1)", "x-step", 1, "-x-step <num>"));
/* 1309:     */     
/* 1310:     */ 
/* 1311:2484 */     result.addElement(new Option("\tThe base for X.\n\t(default: 10)", "x-base", 1, "-x-base <num>"));
/* 1312:     */     
/* 1313:     */ 
/* 1314:2487 */     result.addElement(new Option("\tThe expression for the X value.\n\tAvailable parameters:\n\t\tBASE\n\t\tMIN\n\t\tMAX\n\t\tSTEP\n\t\tI - the current iteration value\n\t\t(from 'FROM' to 'TO' with stepsize 'STEP')\n\t(default: 'pow(BASE,I)')", "x-expression", 1, "-x-expression <expr>"));
/* 1315:     */     
/* 1316:     */ 
/* 1317:     */ 
/* 1318:     */ 
/* 1319:     */ 
/* 1320:     */ 
/* 1321:     */ 
/* 1322:2495 */     result.addElement(new Option("\tWhether the grid can be extended.\n\t(default: no)", "extend-grid", 0, "-extend-grid"));
/* 1323:     */     
/* 1324:     */ 
/* 1325:2498 */     result.addElement(new Option("\tThe maximum number of grid extensions (-1 is unlimited).\n\t(default: 3)", "max-grid-extensions", 1, "-max-grid-extensions <num>"));
/* 1326:     */     
/* 1327:     */ 
/* 1328:     */ 
/* 1329:     */ 
/* 1330:2503 */     result.addElement(new Option("\tThe size (in percent) of the sample to search the inital grid with.\n\t(default: 100)", "sample-size", 1, "-sample-size <num>"));
/* 1331:     */     
/* 1332:     */ 
/* 1333:     */ 
/* 1334:2507 */     result.addElement(new Option("\tThe type of traversal for the grid.\n\t(default: " + new SelectedTag(1, TAGS_TRAVERSAL) + ")", "traversal", 1, "-traversal " + Tag.toOptionList(TAGS_TRAVERSAL)));
/* 1335:     */     
/* 1336:     */ 
/* 1337:     */ 
/* 1338:2511 */     result.addElement(new Option("\tThe log file to log the messages to.\n\t(default: none)", "log-file", 1, "-log-file <filename>"));
/* 1339:     */     
/* 1340:     */ 
/* 1341:2514 */     result.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)", "num-slots", 1, "-num-slots <num>"));
/* 1342:     */     
/* 1343:     */ 
/* 1344:     */ 
/* 1345:2518 */     result.addAll(Collections.list(super.listOptions()));
/* 1346:     */     
/* 1347:2520 */     return result.elements();
/* 1348:     */   }
/* 1349:     */   
/* 1350:     */   public String[] getOptions()
/* 1351:     */   {
/* 1352:2531 */     Vector<String> result = new Vector();
/* 1353:     */     
/* 1354:2533 */     result.add("-E");
/* 1355:2534 */     result.add("" + getEvaluation());
/* 1356:     */     
/* 1357:2536 */     result.add("-y-property");
/* 1358:2537 */     result.add("" + getYProperty());
/* 1359:     */     
/* 1360:2539 */     result.add("-y-min");
/* 1361:2540 */     result.add("" + getYMin());
/* 1362:     */     
/* 1363:2542 */     result.add("-y-max");
/* 1364:2543 */     result.add("" + getYMax());
/* 1365:     */     
/* 1366:2545 */     result.add("-y-step");
/* 1367:2546 */     result.add("" + getYStep());
/* 1368:     */     
/* 1369:2548 */     result.add("-y-base");
/* 1370:2549 */     result.add("" + getYBase());
/* 1371:     */     
/* 1372:2551 */     result.add("-y-expression");
/* 1373:2552 */     result.add("" + getYExpression());
/* 1374:     */     
/* 1375:2554 */     result.add("-x-property");
/* 1376:2555 */     result.add("" + getXProperty());
/* 1377:     */     
/* 1378:2557 */     result.add("-x-min");
/* 1379:2558 */     result.add("" + getXMin());
/* 1380:     */     
/* 1381:2560 */     result.add("-x-max");
/* 1382:2561 */     result.add("" + getXMax());
/* 1383:     */     
/* 1384:2563 */     result.add("-x-step");
/* 1385:2564 */     result.add("" + getXStep());
/* 1386:     */     
/* 1387:2566 */     result.add("-x-base");
/* 1388:2567 */     result.add("" + getXBase());
/* 1389:     */     
/* 1390:2569 */     result.add("-x-expression");
/* 1391:2570 */     result.add("" + getXExpression());
/* 1392:2572 */     if (getGridIsExtendable())
/* 1393:     */     {
/* 1394:2573 */       result.add("-extend-grid");
/* 1395:2574 */       result.add("-max-grid-extensions");
/* 1396:2575 */       result.add("" + getMaxGridExtensions());
/* 1397:     */     }
/* 1398:2578 */     result.add("-sample-size");
/* 1399:2579 */     result.add("" + getSampleSizePercent());
/* 1400:     */     
/* 1401:2581 */     result.add("-traversal");
/* 1402:2582 */     result.add("" + getTraversal());
/* 1403:     */     
/* 1404:2584 */     result.add("-log-file");
/* 1405:2585 */     result.add("" + getLogFile());
/* 1406:     */     
/* 1407:2587 */     result.add("-num-slots");
/* 1408:2588 */     result.add("" + getNumExecutionSlots());
/* 1409:     */     
/* 1410:2590 */     Collections.addAll(result, super.getOptions());
/* 1411:     */     
/* 1412:2592 */     return (String[])result.toArray(new String[result.size()]);
/* 1413:     */   }
/* 1414:     */   
/* 1415:     */   public void setOptions(String[] options)
/* 1416:     */     throws Exception
/* 1417:     */   {
/* 1418:2876 */     String tmpStr = Utils.getOption('E', options);
/* 1419:2877 */     if (tmpStr.length() != 0) {
/* 1420:2878 */       setEvaluation(new SelectedTag(tmpStr, TAGS_EVALUATION));
/* 1421:     */     } else {
/* 1422:2880 */       setEvaluation(new SelectedTag(0, TAGS_EVALUATION));
/* 1423:     */     }
/* 1424:2883 */     tmpStr = Utils.getOption("y-property", options);
/* 1425:2884 */     if (tmpStr.length() != 0) {
/* 1426:2885 */       setYProperty(tmpStr);
/* 1427:     */     } else {
/* 1428:2887 */       setYProperty("kernel.gamma");
/* 1429:     */     }
/* 1430:2890 */     tmpStr = Utils.getOption("y-min", options);
/* 1431:2891 */     if (tmpStr.length() != 0) {
/* 1432:2892 */       setYMin(Double.parseDouble(tmpStr));
/* 1433:     */     } else {
/* 1434:2894 */       setYMin(-3.0D);
/* 1435:     */     }
/* 1436:2897 */     tmpStr = Utils.getOption("y-max", options);
/* 1437:2898 */     if (tmpStr.length() != 0) {
/* 1438:2899 */       setYMax(Double.parseDouble(tmpStr));
/* 1439:     */     } else {
/* 1440:2901 */       setYMax(3.0D);
/* 1441:     */     }
/* 1442:2904 */     tmpStr = Utils.getOption("y-step", options);
/* 1443:2905 */     if (tmpStr.length() != 0) {
/* 1444:2906 */       setYStep(Double.parseDouble(tmpStr));
/* 1445:     */     } else {
/* 1446:2908 */       setYStep(1.0D);
/* 1447:     */     }
/* 1448:2911 */     tmpStr = Utils.getOption("y-base", options);
/* 1449:2912 */     if (tmpStr.length() != 0) {
/* 1450:2913 */       setYBase(Double.parseDouble(tmpStr));
/* 1451:     */     } else {
/* 1452:2915 */       setYBase(10.0D);
/* 1453:     */     }
/* 1454:2918 */     tmpStr = Utils.getOption("y-expression", options);
/* 1455:2919 */     if (tmpStr.length() != 0) {
/* 1456:2920 */       setYExpression(tmpStr);
/* 1457:     */     } else {
/* 1458:2922 */       setYExpression("pow(BASE,I)");
/* 1459:     */     }
/* 1460:2925 */     tmpStr = Utils.getOption("x-property", options);
/* 1461:2926 */     if (tmpStr.length() != 0) {
/* 1462:2927 */       setXProperty(tmpStr);
/* 1463:     */     } else {
/* 1464:2929 */       setXProperty("C");
/* 1465:     */     }
/* 1466:2932 */     tmpStr = Utils.getOption("x-min", options);
/* 1467:2933 */     if (tmpStr.length() != 0) {
/* 1468:2934 */       setXMin(Double.parseDouble(tmpStr));
/* 1469:     */     } else {
/* 1470:2936 */       setXMin(-3.0D);
/* 1471:     */     }
/* 1472:2939 */     tmpStr = Utils.getOption("x-max", options);
/* 1473:2940 */     if (tmpStr.length() != 0) {
/* 1474:2941 */       setXMax(Double.parseDouble(tmpStr));
/* 1475:     */     } else {
/* 1476:2943 */       setXMax(3.0D);
/* 1477:     */     }
/* 1478:2946 */     tmpStr = Utils.getOption("x-step", options);
/* 1479:2947 */     if (tmpStr.length() != 0) {
/* 1480:2948 */       setXStep(Double.parseDouble(tmpStr));
/* 1481:     */     } else {
/* 1482:2950 */       setXStep(1.0D);
/* 1483:     */     }
/* 1484:2953 */     tmpStr = Utils.getOption("x-base", options);
/* 1485:2954 */     if (tmpStr.length() != 0) {
/* 1486:2955 */       setXBase(Double.parseDouble(tmpStr));
/* 1487:     */     } else {
/* 1488:2957 */       setXBase(10.0D);
/* 1489:     */     }
/* 1490:2960 */     tmpStr = Utils.getOption("x-expression", options);
/* 1491:2961 */     if (tmpStr.length() != 0) {
/* 1492:2962 */       setXExpression(tmpStr);
/* 1493:     */     } else {
/* 1494:2964 */       setXExpression("pow(BASE,I)");
/* 1495:     */     }
/* 1496:2967 */     setGridIsExtendable(Utils.getFlag("extend-grid", options));
/* 1497:2968 */     if (getGridIsExtendable())
/* 1498:     */     {
/* 1499:2969 */       tmpStr = Utils.getOption("max-grid-extensions", options);
/* 1500:2970 */       if (tmpStr.length() != 0) {
/* 1501:2971 */         setMaxGridExtensions(Integer.parseInt(tmpStr));
/* 1502:     */       } else {
/* 1503:2973 */         setMaxGridExtensions(3);
/* 1504:     */       }
/* 1505:     */     }
/* 1506:2977 */     tmpStr = Utils.getOption("sample-size", options);
/* 1507:2978 */     if (tmpStr.length() != 0) {
/* 1508:2979 */       setSampleSizePercent(Double.parseDouble(tmpStr));
/* 1509:     */     } else {
/* 1510:2981 */       setSampleSizePercent(100.0D);
/* 1511:     */     }
/* 1512:2984 */     tmpStr = Utils.getOption("traversal", options);
/* 1513:2985 */     if (tmpStr.length() != 0) {
/* 1514:2986 */       setTraversal(new SelectedTag(tmpStr, TAGS_TRAVERSAL));
/* 1515:     */     } else {
/* 1516:2988 */       setTraversal(new SelectedTag(0, TAGS_TRAVERSAL));
/* 1517:     */     }
/* 1518:2991 */     tmpStr = Utils.getOption("log-file", options);
/* 1519:2992 */     if (tmpStr.length() != 0) {
/* 1520:2993 */       setLogFile(new File(tmpStr));
/* 1521:     */     } else {
/* 1522:2995 */       setLogFile(new File(System.getProperty("user.dir")));
/* 1523:     */     }
/* 1524:2998 */     tmpStr = Utils.getOption("num-slots", options);
/* 1525:2999 */     if (tmpStr.length() != 0) {
/* 1526:3000 */       setNumExecutionSlots(Integer.parseInt(tmpStr));
/* 1527:     */     } else {
/* 1528:3002 */       setNumExecutionSlots(1);
/* 1529:     */     }
/* 1530:3005 */     super.setOptions(options);
/* 1531:     */     
/* 1532:3007 */     Utils.checkForRemainingOptions(options);
/* 1533:     */   }
/* 1534:     */   
/* 1535:     */   public void setClassifier(Classifier newClassifier)
/* 1536:     */   {
/* 1537:3020 */     Capabilities cap = newClassifier.getCapabilities();
/* 1538:     */     
/* 1539:3022 */     boolean numeric = (cap.handles(Capabilities.Capability.NUMERIC_CLASS)) || (cap.hasDependency(Capabilities.Capability.NUMERIC_CLASS));
/* 1540:     */     
/* 1541:     */ 
/* 1542:3025 */     boolean nominal = (cap.handles(Capabilities.Capability.NOMINAL_CLASS)) || (cap.hasDependency(Capabilities.Capability.NOMINAL_CLASS)) || (cap.handles(Capabilities.Capability.BINARY_CLASS)) || (cap.hasDependency(Capabilities.Capability.BINARY_CLASS)) || (cap.handles(Capabilities.Capability.UNARY_CLASS)) || (cap.hasDependency(Capabilities.Capability.UNARY_CLASS));
/* 1543:3032 */     if ((this.m_Evaluation == 0) && (!numeric)) {
/* 1544:3033 */       throw new IllegalArgumentException("Classifier needs to handle numeric class for chosen type of evaluation!");
/* 1545:     */     }
/* 1546:3037 */     if (((this.m_Evaluation == 6) || (this.m_Evaluation == 7) || (this.m_Evaluation == 8)) && (!nominal)) {
/* 1547:3039 */       throw new IllegalArgumentException("Classifier needs to handle nominal class for chosen type of evaluation!");
/* 1548:     */     }
/* 1549:3043 */     super.setClassifier(newClassifier);
/* 1550:     */     try
/* 1551:     */     {
/* 1552:3046 */       this.m_BestClassifier = AbstractClassifier.makeCopy(this.m_Classifier);
/* 1553:     */     }
/* 1554:     */     catch (Exception e)
/* 1555:     */     {
/* 1556:3048 */       e.printStackTrace();
/* 1557:     */     }
/* 1558:     */   }
/* 1559:     */   
/* 1560:     */   public String evaluationTipText()
/* 1561:     */   {
/* 1562:3059 */     return "Sets the criterion for evaluating the classifier performance and choosing the best one.";
/* 1563:     */   }
/* 1564:     */   
/* 1565:     */   public void setEvaluation(SelectedTag value)
/* 1566:     */   {
/* 1567:3069 */     if (value.getTags() == TAGS_EVALUATION) {
/* 1568:3070 */       this.m_Evaluation = value.getSelectedTag().getID();
/* 1569:     */     }
/* 1570:     */   }
/* 1571:     */   
/* 1572:     */   public SelectedTag getEvaluation()
/* 1573:     */   {
/* 1574:3080 */     return new SelectedTag(this.m_Evaluation, TAGS_EVALUATION);
/* 1575:     */   }
/* 1576:     */   
/* 1577:     */   public String YPropertyTipText()
/* 1578:     */   {
/* 1579:3090 */     return "The Y property to test (normally the classifier).";
/* 1580:     */   }
/* 1581:     */   
/* 1582:     */   public String getYProperty()
/* 1583:     */   {
/* 1584:3099 */     return this.m_Y_Property;
/* 1585:     */   }
/* 1586:     */   
/* 1587:     */   public void setYProperty(String value)
/* 1588:     */   {
/* 1589:3108 */     this.m_Y_Property = value;
/* 1590:     */   }
/* 1591:     */   
/* 1592:     */   public String YMinTipText()
/* 1593:     */   {
/* 1594:3118 */     return "The minimum of Y (normally the classifier).";
/* 1595:     */   }
/* 1596:     */   
/* 1597:     */   public double getYMin()
/* 1598:     */   {
/* 1599:3127 */     return this.m_Y_Min;
/* 1600:     */   }
/* 1601:     */   
/* 1602:     */   public void setYMin(double value)
/* 1603:     */   {
/* 1604:3136 */     this.m_Y_Min = value;
/* 1605:     */   }
/* 1606:     */   
/* 1607:     */   public String YMaxTipText()
/* 1608:     */   {
/* 1609:3146 */     return "The maximum of Y.";
/* 1610:     */   }
/* 1611:     */   
/* 1612:     */   public double getYMax()
/* 1613:     */   {
/* 1614:3155 */     return this.m_Y_Max;
/* 1615:     */   }
/* 1616:     */   
/* 1617:     */   public void setYMax(double value)
/* 1618:     */   {
/* 1619:3164 */     this.m_Y_Max = value;
/* 1620:     */   }
/* 1621:     */   
/* 1622:     */   public String YStepTipText()
/* 1623:     */   {
/* 1624:3174 */     return "The step size of Y.";
/* 1625:     */   }
/* 1626:     */   
/* 1627:     */   public double getYStep()
/* 1628:     */   {
/* 1629:3183 */     return this.m_Y_Step;
/* 1630:     */   }
/* 1631:     */   
/* 1632:     */   public void setYStep(double value)
/* 1633:     */   {
/* 1634:3192 */     this.m_Y_Step = value;
/* 1635:     */   }
/* 1636:     */   
/* 1637:     */   public String YBaseTipText()
/* 1638:     */   {
/* 1639:3202 */     return "The base of Y.";
/* 1640:     */   }
/* 1641:     */   
/* 1642:     */   public double getYBase()
/* 1643:     */   {
/* 1644:3211 */     return this.m_Y_Base;
/* 1645:     */   }
/* 1646:     */   
/* 1647:     */   public void setYBase(double value)
/* 1648:     */   {
/* 1649:3220 */     this.m_Y_Base = value;
/* 1650:     */   }
/* 1651:     */   
/* 1652:     */   public String YExpressionTipText()
/* 1653:     */   {
/* 1654:3230 */     return "The expression for the Y value (parameters: BASE, FROM, TO, STEP, I).";
/* 1655:     */   }
/* 1656:     */   
/* 1657:     */   public String getYExpression()
/* 1658:     */   {
/* 1659:3239 */     return this.m_Y_Expression;
/* 1660:     */   }
/* 1661:     */   
/* 1662:     */   public void setYExpression(String value)
/* 1663:     */   {
/* 1664:3248 */     this.m_Y_Expression = value;
/* 1665:     */   }
/* 1666:     */   
/* 1667:     */   public String XPropertyTipText()
/* 1668:     */   {
/* 1669:3258 */     return "The X property to test.";
/* 1670:     */   }
/* 1671:     */   
/* 1672:     */   public String getXProperty()
/* 1673:     */   {
/* 1674:3267 */     return this.m_X_Property;
/* 1675:     */   }
/* 1676:     */   
/* 1677:     */   public void setXProperty(String value)
/* 1678:     */   {
/* 1679:3276 */     this.m_X_Property = value;
/* 1680:     */   }
/* 1681:     */   
/* 1682:     */   public String XMinTipText()
/* 1683:     */   {
/* 1684:3286 */     return "The minimum of X.";
/* 1685:     */   }
/* 1686:     */   
/* 1687:     */   public double getXMin()
/* 1688:     */   {
/* 1689:3295 */     return this.m_X_Min;
/* 1690:     */   }
/* 1691:     */   
/* 1692:     */   public void setXMin(double value)
/* 1693:     */   {
/* 1694:3304 */     this.m_X_Min = value;
/* 1695:     */   }
/* 1696:     */   
/* 1697:     */   public String XMaxTipText()
/* 1698:     */   {
/* 1699:3314 */     return "The maximum of X.";
/* 1700:     */   }
/* 1701:     */   
/* 1702:     */   public double getXMax()
/* 1703:     */   {
/* 1704:3323 */     return this.m_X_Max;
/* 1705:     */   }
/* 1706:     */   
/* 1707:     */   public void setXMax(double value)
/* 1708:     */   {
/* 1709:3332 */     this.m_X_Max = value;
/* 1710:     */   }
/* 1711:     */   
/* 1712:     */   public String XStepTipText()
/* 1713:     */   {
/* 1714:3342 */     return "The step size of X.";
/* 1715:     */   }
/* 1716:     */   
/* 1717:     */   public double getXStep()
/* 1718:     */   {
/* 1719:3351 */     return this.m_X_Step;
/* 1720:     */   }
/* 1721:     */   
/* 1722:     */   public void setXStep(double value)
/* 1723:     */   {
/* 1724:3360 */     this.m_X_Step = value;
/* 1725:     */   }
/* 1726:     */   
/* 1727:     */   public String XBaseTipText()
/* 1728:     */   {
/* 1729:3370 */     return "The base of X.";
/* 1730:     */   }
/* 1731:     */   
/* 1732:     */   public double getXBase()
/* 1733:     */   {
/* 1734:3379 */     return this.m_X_Base;
/* 1735:     */   }
/* 1736:     */   
/* 1737:     */   public void setXBase(double value)
/* 1738:     */   {
/* 1739:3388 */     this.m_X_Base = value;
/* 1740:     */   }
/* 1741:     */   
/* 1742:     */   public String XExpressionTipText()
/* 1743:     */   {
/* 1744:3398 */     return "The expression for the X value (parameters: BASE, FROM, TO, STEP, I).";
/* 1745:     */   }
/* 1746:     */   
/* 1747:     */   public String getXExpression()
/* 1748:     */   {
/* 1749:3407 */     return this.m_X_Expression;
/* 1750:     */   }
/* 1751:     */   
/* 1752:     */   public void setXExpression(String value)
/* 1753:     */   {
/* 1754:3416 */     this.m_X_Expression = value;
/* 1755:     */   }
/* 1756:     */   
/* 1757:     */   public String gridIsExtendableTipText()
/* 1758:     */   {
/* 1759:3426 */     return "Whether the grid can be extended.";
/* 1760:     */   }
/* 1761:     */   
/* 1762:     */   public boolean getGridIsExtendable()
/* 1763:     */   {
/* 1764:3435 */     return this.m_GridIsExtendable;
/* 1765:     */   }
/* 1766:     */   
/* 1767:     */   public void setGridIsExtendable(boolean value)
/* 1768:     */   {
/* 1769:3444 */     this.m_GridIsExtendable = value;
/* 1770:     */   }
/* 1771:     */   
/* 1772:     */   public String maxGridExtensionsTipText()
/* 1773:     */   {
/* 1774:3454 */     return "The maximum number of grid extensions, -1 for unlimited.";
/* 1775:     */   }
/* 1776:     */   
/* 1777:     */   public int getMaxGridExtensions()
/* 1778:     */   {
/* 1779:3463 */     return this.m_MaxGridExtensions;
/* 1780:     */   }
/* 1781:     */   
/* 1782:     */   public void setMaxGridExtensions(int value)
/* 1783:     */   {
/* 1784:3472 */     this.m_MaxGridExtensions = value;
/* 1785:     */   }
/* 1786:     */   
/* 1787:     */   public String sampleSizePercentTipText()
/* 1788:     */   {
/* 1789:3482 */     return "The sample size (in percent) to use in the initial grid search.";
/* 1790:     */   }
/* 1791:     */   
/* 1792:     */   public double getSampleSizePercent()
/* 1793:     */   {
/* 1794:3491 */     return this.m_SampleSize;
/* 1795:     */   }
/* 1796:     */   
/* 1797:     */   public void setSampleSizePercent(double value)
/* 1798:     */   {
/* 1799:3500 */     this.m_SampleSize = value;
/* 1800:     */   }
/* 1801:     */   
/* 1802:     */   public String traversalTipText()
/* 1803:     */   {
/* 1804:3510 */     return "Sets type of traversal of the grid, either by rows or columns.";
/* 1805:     */   }
/* 1806:     */   
/* 1807:     */   public void setTraversal(SelectedTag value)
/* 1808:     */   {
/* 1809:3519 */     if (value.getTags() == TAGS_TRAVERSAL) {
/* 1810:3520 */       this.m_Traversal = value.getSelectedTag().getID();
/* 1811:     */     }
/* 1812:     */   }
/* 1813:     */   
/* 1814:     */   public SelectedTag getTraversal()
/* 1815:     */   {
/* 1816:3530 */     return new SelectedTag(this.m_Traversal, TAGS_TRAVERSAL);
/* 1817:     */   }
/* 1818:     */   
/* 1819:     */   public String logFileTipText()
/* 1820:     */   {
/* 1821:3540 */     return "The log file to log the messages to.";
/* 1822:     */   }
/* 1823:     */   
/* 1824:     */   public File getLogFile()
/* 1825:     */   {
/* 1826:3549 */     return this.m_LogFile;
/* 1827:     */   }
/* 1828:     */   
/* 1829:     */   public void setLogFile(File value)
/* 1830:     */   {
/* 1831:3558 */     this.m_LogFile = value;
/* 1832:     */   }
/* 1833:     */   
/* 1834:     */   public String numExecutionSlotsTipText()
/* 1835:     */   {
/* 1836:3568 */     return "The number of execution slots (threads) to use for constructing the ensemble.";
/* 1837:     */   }
/* 1838:     */   
/* 1839:     */   public void setNumExecutionSlots(int value)
/* 1840:     */   {
/* 1841:3579 */     if (value >= 1) {
/* 1842:3580 */       this.m_NumExecutionSlots = value;
/* 1843:     */     }
/* 1844:     */   }
/* 1845:     */   
/* 1846:     */   public int getNumExecutionSlots()
/* 1847:     */   {
/* 1848:3591 */     return this.m_NumExecutionSlots;
/* 1849:     */   }
/* 1850:     */   
/* 1851:     */   protected void addPerformance(Performance performance, int folds)
/* 1852:     */   {
/* 1853:3603 */     if (this.m_Failed > 0) {
/* 1854:3604 */       return;
/* 1855:     */     }
/* 1856:3607 */     this.m_Performances.add(performance);
/* 1857:3608 */     this.m_Cache.add(folds, performance);
/* 1858:     */   }
/* 1859:     */   
/* 1860:     */   protected Instances getData()
/* 1861:     */   {
/* 1862:3617 */     return this.m_Data;
/* 1863:     */   }
/* 1864:     */   
/* 1865:     */   public Classifier getBestClassifier()
/* 1866:     */   {
/* 1867:3626 */     return this.m_BestClassifier;
/* 1868:     */   }
/* 1869:     */   
/* 1870:     */   public Enumeration<String> enumerateMeasures()
/* 1871:     */   {
/* 1872:3637 */     Vector<String> result = new Vector();
/* 1873:     */     
/* 1874:3639 */     result.add("measureX");
/* 1875:3640 */     result.add("measureY");
/* 1876:3641 */     result.add("measureGridExtensionsPerformed");
/* 1877:     */     
/* 1878:3643 */     return result.elements();
/* 1879:     */   }
/* 1880:     */   
/* 1881:     */   public double getMeasure(String measureName)
/* 1882:     */   {
/* 1883:3654 */     if (measureName.equalsIgnoreCase("measureX")) {
/* 1884:3655 */       return this.m_Generator.evaluate(getValues().getX(), true);
/* 1885:     */     }
/* 1886:3656 */     if (measureName.equalsIgnoreCase("measureY")) {
/* 1887:3657 */       return this.m_Generator.evaluate(getValues().getY(), false);
/* 1888:     */     }
/* 1889:3658 */     if (measureName.equalsIgnoreCase("measureGridExtensionsPerformed")) {
/* 1890:3659 */       return getGridExtensionsPerformed();
/* 1891:     */     }
/* 1892:3661 */     throw new IllegalArgumentException("Measure '" + measureName + "' not supported!");
/* 1893:     */   }
/* 1894:     */   
/* 1895:     */   public PointDouble getValues()
/* 1896:     */   {
/* 1897:3672 */     return this.m_Values;
/* 1898:     */   }
/* 1899:     */   
/* 1900:     */   public int getGridExtensionsPerformed()
/* 1901:     */   {
/* 1902:3683 */     return this.m_GridExtensionsPerformed;
/* 1903:     */   }
/* 1904:     */   
/* 1905:     */   public Capabilities getCapabilities()
/* 1906:     */   {
/* 1907:3698 */     Capabilities result = super.getCapabilities();
/* 1908:     */     
/* 1909:     */ 
/* 1910:3701 */     Capabilities classes = result.getClassCapabilities();
/* 1911:3702 */     Iterator<Capabilities.Capability> iter = classes.capabilities();
/* 1912:3703 */     while (iter.hasNext())
/* 1913:     */     {
/* 1914:3704 */       Capabilities.Capability capab = (Capabilities.Capability)iter.next();
/* 1915:3705 */       if ((capab != Capabilities.Capability.BINARY_CLASS) && (capab != Capabilities.Capability.UNARY_CLASS) && (capab != Capabilities.Capability.NOMINAL_CLASS) && (capab != Capabilities.Capability.NUMERIC_CLASS) && (capab != Capabilities.Capability.DATE_CLASS)) {
/* 1916:3710 */         result.disable(capab);
/* 1917:     */       }
/* 1918:     */     }
/* 1919:3714 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 1920:3717 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 1921:3718 */       result.enableDependency(cap);
/* 1922:     */     }
/* 1923:3721 */     if (result.getMinimumNumberInstances() < 1) {
/* 1924:3722 */       result.setMinimumNumberInstances(1);
/* 1925:     */     }
/* 1926:3725 */     result.setOwner(this);
/* 1927:     */     
/* 1928:3727 */     return result;
/* 1929:     */   }
/* 1930:     */   
/* 1931:     */   protected void log(String message)
/* 1932:     */   {
/* 1933:3737 */     log(message, false);
/* 1934:     */   }
/* 1935:     */   
/* 1936:     */   protected void log(String message, boolean onlyLog)
/* 1937:     */   {
/* 1938:3750 */     if ((getDebug()) && (!onlyLog)) {
/* 1939:3751 */       System.out.println(message);
/* 1940:     */     }
/* 1941:3755 */     if (!getLogFile().isDirectory()) {
/* 1942:3756 */       Debug.writeToFile(getLogFile().getAbsolutePath(), message, true);
/* 1943:     */     }
/* 1944:     */   }
/* 1945:     */   
/* 1946:     */   protected String logPerformances(Grid grid, Vector<Performance> performances, Tag type)
/* 1947:     */   {
/* 1948:3774 */     StringBuffer result = new StringBuffer(type.getReadable() + ":\n");
/* 1949:3775 */     PerformanceTable table = new PerformanceTable(this, grid, performances, type.getID());
/* 1950:     */     
/* 1951:3777 */     result.append(table.toString() + "\n");
/* 1952:3778 */     result.append("\n");
/* 1953:3779 */     result.append(table.toGnuplot() + "\n");
/* 1954:3780 */     result.append("\n");
/* 1955:     */     
/* 1956:3782 */     return result.toString();
/* 1957:     */   }
/* 1958:     */   
/* 1959:     */   protected void logPerformances(Grid grid, Vector<Performance> performances)
/* 1960:     */   {
/* 1961:3795 */     for (int i = 0; i < TAGS_EVALUATION.length; i++) {
/* 1962:3796 */       log("\n" + logPerformances(grid, performances, TAGS_EVALUATION[i]), true);
/* 1963:     */     }
/* 1964:     */   }
/* 1965:     */   
/* 1966:     */   protected void startExecutorPool()
/* 1967:     */   {
/* 1968:3804 */     stopExecutorPool();
/* 1969:     */     
/* 1970:3806 */     log("Starting thread pool with " + this.m_NumExecutionSlots + " slots...");
/* 1971:     */     
/* 1972:3808 */     this.m_ExecutorPool = new ThreadPoolExecutor(this.m_NumExecutionSlots, this.m_NumExecutionSlots, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/* 1973:     */   }
/* 1974:     */   
/* 1975:     */   protected void stopExecutorPool()
/* 1976:     */   {
/* 1977:3817 */     log("Shutting down thread pool...");
/* 1978:3819 */     if (this.m_ExecutorPool != null) {
/* 1979:3820 */       this.m_ExecutorPool.shutdownNow();
/* 1980:     */     }
/* 1981:3823 */     this.m_ExecutorPool = null;
/* 1982:     */   }
/* 1983:     */   
/* 1984:     */   protected synchronized void block(boolean doBlock)
/* 1985:     */   {
/* 1986:3832 */     if (doBlock) {
/* 1987:     */       try
/* 1988:     */       {
/* 1989:3834 */         if ((this.m_Completed + this.m_Failed < this.m_NumSetups) && (this.m_Failed == 0)) {
/* 1990:3835 */           wait();
/* 1991:     */         }
/* 1992:     */       }
/* 1993:     */       catch (InterruptedException ex) {}
/* 1994:     */     } else {
/* 1995:3841 */       notifyAll();
/* 1996:     */     }
/* 1997:     */   }
/* 1998:     */   
/* 1999:     */   protected synchronized void completedEvaluation(Object obj, Exception exception)
/* 2000:     */   {
/* 2001:3855 */     if (exception != null)
/* 2002:     */     {
/* 2003:3856 */       this.m_Failed += 1;
/* 2004:3857 */       if (this.m_Debug) {
/* 2005:3858 */         System.err.println("Training failed: " + Utils.toCommandLine(obj));
/* 2006:     */       }
/* 2007:     */     }
/* 2008:     */     else
/* 2009:     */     {
/* 2010:3861 */       this.m_Completed += 1;
/* 2011:     */     }
/* 2012:3864 */     if (this.m_Debug) {
/* 2013:3865 */       System.err.println("Progress: completed=" + this.m_Completed + ", failed=" + this.m_Failed + ", overall=" + this.m_NumSetups);
/* 2014:     */     }
/* 2015:3869 */     if ((this.m_Completed == this.m_NumSetups) || (this.m_Failed > 0))
/* 2016:     */     {
/* 2017:3870 */       if ((this.m_Failed > 0) && 
/* 2018:3871 */         (this.m_Debug)) {
/* 2019:3872 */         System.err.println("Problem building classifiers - some failed to be trained.");
/* 2020:     */       }
/* 2021:3876 */       stopExecutorPool();
/* 2022:3877 */       block(false);
/* 2023:     */     }
/* 2024:3880 */     this.m_Exception = exception;
/* 2025:     */   }
/* 2026:     */   
/* 2027:     */   protected PointDouble determineBestInGrid(Grid grid, Instances inst, int cv)
/* 2028:     */     throws Exception
/* 2029:     */   {
/* 2030:3905 */     startExecutorPool();
/* 2031:3906 */     this.m_Performances.clear();
/* 2032:     */     
/* 2033:3908 */     log("Determining best pair with " + cv + "-fold CV in Grid:\n" + grid + "\n");
/* 2034:     */     int size;
/* 2035:     */     int size;
/* 2036:3911 */     if (this.m_Traversal == 1) {
/* 2037:3912 */       size = grid.width();
/* 2038:     */     } else {
/* 2039:3914 */       size = grid.height();
/* 2040:     */     }
/* 2041:3917 */     boolean allCached = true;
/* 2042:3918 */     this.m_Failed = 0;
/* 2043:3919 */     this.m_Completed = 0;
/* 2044:3920 */     this.m_NumSetups = (grid.width() * grid.height());
/* 2045:3922 */     for (int i = 0; i < size; i++)
/* 2046:     */     {
/* 2047:     */       Enumeration<PointDouble> enm;
/* 2048:     */       Enumeration<PointDouble> enm;
/* 2049:3923 */       if (this.m_Traversal == 1) {
/* 2050:3924 */         enm = grid.column(i);
/* 2051:     */       } else {
/* 2052:3926 */         enm = grid.row(i);
/* 2053:     */       }
/* 2054:3929 */       while (enm.hasMoreElements())
/* 2055:     */       {
/* 2056:3930 */         PointDouble values = (PointDouble)enm.nextElement();
/* 2057:3933 */         if (this.m_Cache.isCached(cv, values))
/* 2058:     */         {
/* 2059:3934 */           this.m_Performances.add(this.m_Cache.get(cv, values));
/* 2060:3935 */           this.m_Completed += 1;
/* 2061:3936 */           log("" + this.m_Performances.get(this.m_Performances.size() - 1) + ": cached=true");
/* 2062:     */         }
/* 2063:     */         else
/* 2064:     */         {
/* 2065:3939 */           allCached = false;
/* 2066:3940 */           EvaluationTask newTask = new EvaluationTask(this, this.m_Generator, inst, values, cv, this.m_Evaluation);
/* 2067:3947 */           if (this.m_Failed == 0) {
/* 2068:3948 */             this.m_ExecutorPool.execute(newTask);
/* 2069:     */           }
/* 2070:     */         }
/* 2071:3953 */         if (this.m_Failed > 0) {
/* 2072:     */           break;
/* 2073:     */         }
/* 2074:     */       }
/* 2075:     */     }
/* 2076:3961 */     block(true);
/* 2077:3964 */     if (allCached)
/* 2078:     */     {
/* 2079:3965 */       log("All points were already cached - abnormal state!");
/* 2080:3966 */       throw new IllegalStateException("All points were already cached - abnormal state!");
/* 2081:     */     }
/* 2082:3970 */     if (this.m_Failed > 0)
/* 2083:     */     {
/* 2084:3971 */       if (this.m_Exception != null) {
/* 2085:3972 */         throw this.m_Exception;
/* 2086:     */       }
/* 2087:3974 */       throw new WekaException("Searched stopped due to failed setup!");
/* 2088:     */     }
/* 2089:3979 */     Collections.sort(this.m_Performances, new PerformanceComparator(this.m_Evaluation));
/* 2090:     */     
/* 2091:3981 */     PointDouble result = ((Performance)this.m_Performances.get(this.m_Performances.size() - 1)).getValues();
/* 2092:     */     
/* 2093:     */ 
/* 2094:3984 */     this.m_UniformPerformance = true;
/* 2095:3985 */     Performance p1 = (Performance)this.m_Performances.get(0);
/* 2096:3986 */     for (i = 1; i < this.m_Performances.size(); i++)
/* 2097:     */     {
/* 2098:3987 */       Performance p2 = (Performance)this.m_Performances.get(i);
/* 2099:3988 */       if (p2.getPerformance(this.m_Evaluation) != p1.getPerformance(this.m_Evaluation))
/* 2100:     */       {
/* 2101:3989 */         this.m_UniformPerformance = false;
/* 2102:3990 */         break;
/* 2103:     */       }
/* 2104:     */     }
/* 2105:3993 */     if (this.m_UniformPerformance) {
/* 2106:3994 */       log("All performances are the same!");
/* 2107:     */     }
/* 2108:3997 */     logPerformances(grid, this.m_Performances);
/* 2109:3998 */     log("\nBest performance:\n" + this.m_Performances.get(this.m_Performances.size() - 1));
/* 2110:     */     
/* 2111:4000 */     this.m_Performances.clear();
/* 2112:     */     
/* 2113:4002 */     return result;
/* 2114:     */   }
/* 2115:     */   
/* 2116:     */   protected PointDouble findBest()
/* 2117:     */     throws Exception
/* 2118:     */   {
/* 2119:4021 */     log("Step 1:\n");
/* 2120:     */     Instances sample;
/* 2121:     */     Instances sample;
/* 2122:4024 */     if (getSampleSizePercent() == 100.0D)
/* 2123:     */     {
/* 2124:4025 */       sample = this.m_Data;
/* 2125:     */     }
/* 2126:     */     else
/* 2127:     */     {
/* 2128:4027 */       log("Generating sample (" + getSampleSizePercent() + "%)");
/* 2129:4028 */       Resample resample = new Resample();
/* 2130:4029 */       resample.setRandomSeed(getSeed());
/* 2131:4030 */       resample.setSampleSizePercent(getSampleSizePercent());
/* 2132:4031 */       resample.setInputFormat(this.m_Data);
/* 2133:4032 */       sample = Filter.useFilter(this.m_Data, resample);
/* 2134:     */     }
/* 2135:4035 */     boolean finished = false;
/* 2136:4036 */     int iteration = 0;
/* 2137:4037 */     this.m_GridExtensionsPerformed = 0;
/* 2138:4038 */     this.m_UniformPerformance = false;
/* 2139:     */     
/* 2140:     */ 
/* 2141:4041 */     log("\n=== Initial grid - Start ===");
/* 2142:4042 */     PointDouble result = determineBestInGrid(this.m_Grid, sample, 2);
/* 2143:4043 */     log("\nResult of Step 1: " + result + "\n");
/* 2144:4044 */     log("=== Initial grid - End ===\n");
/* 2145:     */     
/* 2146:4046 */     finished = this.m_UniformPerformance;
/* 2147:4048 */     if (!finished) {
/* 2148:     */       do
/* 2149:     */       {
/* 2150:4050 */         iteration++;
/* 2151:4051 */         PointDouble resultOld = (PointDouble)result.clone();
/* 2152:4052 */         PointInt center = this.m_Grid.getLocation(result);
/* 2153:4054 */         if (this.m_Grid.isOnBorder(center))
/* 2154:     */         {
/* 2155:4055 */           log("Center is on border of grid.");
/* 2156:4058 */           if (getGridIsExtendable())
/* 2157:     */           {
/* 2158:4060 */             if (this.m_GridExtensionsPerformed == getMaxGridExtensions())
/* 2159:     */             {
/* 2160:4061 */               log("Maximum number of extensions reached!\n");
/* 2161:4062 */               finished = true;
/* 2162:     */             }
/* 2163:     */             else
/* 2164:     */             {
/* 2165:4064 */               this.m_GridExtensionsPerformed += 1;
/* 2166:4065 */               this.m_Grid = this.m_Grid.extend(result);
/* 2167:4066 */               center = this.m_Grid.getLocation(result);
/* 2168:4067 */               log("Extending grid (" + this.m_GridExtensionsPerformed + "/" + getMaxGridExtensions() + "):\n" + this.m_Grid + "\n");
/* 2169:     */             }
/* 2170:     */           }
/* 2171:     */           else {
/* 2172:4071 */             finished = true;
/* 2173:     */           }
/* 2174:     */         }
/* 2175:4077 */         if (!finished)
/* 2176:     */         {
/* 2177:4078 */           Grid neighborGrid = this.m_Grid.subgrid((int)center.getY() + 1, (int)center.getX() - 1, (int)center.getY() - 1, (int)center.getX() + 1);
/* 2178:     */           
/* 2179:     */ 
/* 2180:4081 */           result = determineBestInGrid(neighborGrid, sample, 10);
/* 2181:4082 */           log("\nResult of Step 2/Iteration " + iteration + ":\n" + result);
/* 2182:4083 */           finished = this.m_UniformPerformance;
/* 2183:4086 */           if (result.equals(resultOld))
/* 2184:     */           {
/* 2185:4087 */             finished = true;
/* 2186:4088 */             log("\nNo better point found.");
/* 2187:     */           }
/* 2188:     */         }
/* 2189:4091 */       } while (!finished);
/* 2190:     */     }
/* 2191:4094 */     log("\nFinal result: " + result);
/* 2192:     */     
/* 2193:4096 */     return result;
/* 2194:     */   }
/* 2195:     */   
/* 2196:     */   public void buildClassifier(Instances data)
/* 2197:     */     throws Exception
/* 2198:     */   {
/* 2199:4113 */     getCapabilities().testWithFail(data);
/* 2200:     */     
/* 2201:     */ 
/* 2202:4116 */     this.m_Data = new Instances(data);
/* 2203:4117 */     this.m_Data.deleteWithMissingClass();
/* 2204:     */     
/* 2205:4119 */     this.m_Cache = new PerformanceCache();
/* 2206:4120 */     this.m_Performances = new Vector();
/* 2207:4121 */     this.m_Generator = new SetupGenerator(this);
/* 2208:4122 */     this.m_Exception = null;
/* 2209:     */     
/* 2210:4124 */     String strX = this.m_Classifier.getClass().getName();
/* 2211:4125 */     String strY = this.m_Classifier.getClass().getName();
/* 2212:     */     
/* 2213:4127 */     this.m_Grid = new Grid(getXMin(), getXMax(), getXStep(), strX + ", property " + getXProperty() + ", expr. " + getXExpression() + ", base " + getXBase(), getYMin(), getYMax(), getYStep(), strY + ", property " + getYProperty() + ", expr. " + getYExpression() + ", base " + getYBase());
/* 2214:     */     
/* 2215:     */ 
/* 2216:     */ 
/* 2217:     */ 
/* 2218:     */ 
/* 2219:4133 */     log("\n" + getClass().getName() + "\n" + getClass().getName().replaceAll(".", "=") + "\n" + "Options: " + Utils.joinOptions(getOptions()) + "\n");
/* 2220:     */     
/* 2221:     */ 
/* 2222:     */ 
/* 2223:     */ 
/* 2224:4138 */     this.m_Values = findBest();
/* 2225:     */     
/* 2226:     */ 
/* 2227:4141 */     double x = this.m_Generator.evaluate(this.m_Values.getX(), true);
/* 2228:4142 */     double y = this.m_Generator.evaluate(this.m_Values.getY(), false);
/* 2229:4143 */     this.m_BestClassifier = ((Classifier)this.m_Generator.setup(getClassifier(), x, y));
/* 2230:     */     
/* 2231:     */ 
/* 2232:4146 */     this.m_Classifier = ((Classifier)this.m_Generator.setup(getClassifier(), x, y));
/* 2233:4147 */     this.m_Classifier.buildClassifier(this.m_Data);
/* 2234:     */     
/* 2235:     */ 
/* 2236:4150 */     this.m_Data = null;
/* 2237:     */   }
/* 2238:     */   
/* 2239:     */   public double[] distributionForInstance(Instance instance)
/* 2240:     */     throws Exception
/* 2241:     */   {
/* 2242:4163 */     return this.m_Classifier.distributionForInstance(instance);
/* 2243:     */   }
/* 2244:     */   
/* 2245:     */   public String toString()
/* 2246:     */   {
/* 2247:4175 */     String result = "";
/* 2248:4177 */     if (this.m_Values == null)
/* 2249:     */     {
/* 2250:4178 */       result = "No search performed yet.";
/* 2251:     */     }
/* 2252:     */     else
/* 2253:     */     {
/* 2254:4180 */       result = getClass().getName() + ":\n" + "Classifier: " + Utils.toCommandLine(getClassifier()) + "\n\n" + "X property: " + getXProperty() + "\n" + "Y property: " + getYProperty() + "\n\n" + "Evaluation: " + getEvaluation().getSelectedTag().getReadable() + "\n" + "Coordinates: " + getValues() + "\n";
/* 2255:4186 */       if (getGridIsExtendable()) {
/* 2256:4187 */         result = result + "Grid-Extensions: " + getGridExtensionsPerformed() + "\n";
/* 2257:     */       }
/* 2258:4190 */       result = result + "Values: " + this.m_Generator.evaluate(getValues().getX(), true) + " (X coordinate)" + ", " + this.m_Generator.evaluate(getValues().getY(), false) + " (Y coordinate)" + "\n\n" + this.m_Classifier.toString();
/* 2259:     */     }
/* 2260:4196 */     return result;
/* 2261:     */   }
/* 2262:     */   
/* 2263:     */   public String toSummaryString()
/* 2264:     */   {
/* 2265:4208 */     String result = "Best classifier: " + Utils.toCommandLine(getBestClassifier());
/* 2266:     */     
/* 2267:4210 */     return result;
/* 2268:     */   }
/* 2269:     */   
/* 2270:     */   public String getRevision()
/* 2271:     */   {
/* 2272:4220 */     return RevisionUtils.extract("$Revision: 11501 $");
/* 2273:     */   }
/* 2274:     */   
/* 2275:     */   public static void main(String[] args)
/* 2276:     */   {
/* 2277:4229 */     runClassifier(new GridSearch(), args);
/* 2278:     */   }
/* 2279:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.GridSearch
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.neighboursearch.PerformanceStats;
/*   8:    */ 
/*   9:    */ public class MinkowskiDistance
/*  10:    */   extends NormalizableDistance
/*  11:    */   implements Cloneable, TechnicalInformationHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -7446019339455453893L;
/*  14:101 */   protected double m_Order = 2.0D;
/*  15:    */   
/*  16:    */   public MinkowskiDistance() {}
/*  17:    */   
/*  18:    */   public MinkowskiDistance(Instances data)
/*  19:    */   {
/*  20:117 */     super(data);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public String globalInfo()
/*  24:    */   {
/*  25:128 */     return "Implementing Minkowski distance (or similarity) function.\n\nOne object defines not one distance but the data model in which the distances between objects of that data model can be computed.\n\nAttention: For efficiency reasons the use of consistency checks (like are the data models of the two instances exactly the same), is low.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  26:    */   }
/*  27:    */   
/*  28:    */   public TechnicalInformation getTechnicalInformation()
/*  29:    */   {
/*  30:148 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  31:149 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wikipedia");
/*  32:150 */     result.setValue(TechnicalInformation.Field.TITLE, "Minkowski distance");
/*  33:151 */     result.setValue(TechnicalInformation.Field.URL, "http://en.wikipedia.org/wiki/Minkowski_distance");
/*  34:    */     
/*  35:    */ 
/*  36:154 */     return result;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:164 */     Vector<Option> result = new Vector();
/*  42:    */     
/*  43:166 */     result.addElement(new Option("\tThe order 'p'. With '1' being the Manhattan distance and '2'\n\tthe Euclidean distance.\n\t(default: 2)", "P", 1, "-P <order>"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:171 */     result.addAll(Collections.list(super.listOptions()));
/*  49:    */     
/*  50:173 */     return result.elements();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setOptions(String[] options)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:186 */     String tmpStr = Utils.getOption('P', options);
/*  57:187 */     if (tmpStr.length() > 0) {
/*  58:188 */       setOrder(Double.parseDouble(tmpStr));
/*  59:    */     } else {
/*  60:190 */       setOrder(2.0D);
/*  61:    */     }
/*  62:193 */     super.setOptions(options);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String[] getOptions()
/*  66:    */   {
/*  67:205 */     Vector<String> result = new Vector();
/*  68:    */     
/*  69:207 */     result.add("-P");
/*  70:208 */     result.add("" + getOrder());
/*  71:    */     
/*  72:210 */     Collections.addAll(result, super.getOptions());
/*  73:    */     
/*  74:212 */     return (String[])result.toArray(new String[result.size()]);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String orderTipText()
/*  78:    */   {
/*  79:222 */     return "The order of the Minkowski distance ('1' is Manhattan distance and '2' the Euclidean distance).";
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOrder(double value)
/*  83:    */   {
/*  84:232 */     if (this.m_Order != 0.0D)
/*  85:    */     {
/*  86:233 */       this.m_Order = value;
/*  87:234 */       invalidate();
/*  88:    */     }
/*  89:    */     else
/*  90:    */     {
/*  91:236 */       System.err.println("Order cannot be zero!");
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public double getOrder()
/*  96:    */   {
/*  97:246 */     return this.m_Order;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public double distance(Instance first, Instance second)
/* 101:    */   {
/* 102:258 */     return Math.pow(distance(first, second, (1.0D / 0.0D)), 1.0D / this.m_Order);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double distance(Instance first, Instance second, PerformanceStats stats)
/* 106:    */   {
/* 107:284 */     return Math.pow(distance(first, second, (1.0D / 0.0D), stats), 1.0D / this.m_Order);
/* 108:    */   }
/* 109:    */   
/* 110:    */   protected double updateDistance(double currDist, double diff)
/* 111:    */   {
/* 112:302 */     double result = currDist;
/* 113:303 */     result += Math.pow(Math.abs(diff), this.m_Order);
/* 114:    */     
/* 115:305 */     return result;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void postProcessDistances(double[] distances)
/* 119:    */   {
/* 120:320 */     for (int i = 0; i < distances.length; i++) {
/* 121:321 */       distances[i] = Math.pow(distances[i], 1.0D / this.m_Order);
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public String getRevision()
/* 126:    */   {
/* 127:332 */     return RevisionUtils.extract("$Revision: 0$");
/* 128:    */   }
/* 129:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.MinkowskiDistance
 * JD-Core Version:    0.7.0.1
 */
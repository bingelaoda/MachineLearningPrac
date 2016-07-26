/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import weka.core.neighboursearch.PerformanceStats;
/*   4:    */ 
/*   5:    */ public class EuclideanDistance
/*   6:    */   extends NormalizableDistance
/*   7:    */   implements Cloneable, TechnicalInformationHandler
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 1068606253458807903L;
/*  10:    */   
/*  11:    */   public EuclideanDistance() {}
/*  12:    */   
/*  13:    */   public EuclideanDistance(Instances data)
/*  14:    */   {
/*  15: 97 */     super(data);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20:107 */     return "Implementing Euclidean distance (or similarity) function.\n\nOne object defines not one distance but the data model in which the distances between objects of that data model can be computed.\n\nAttention: For efficiency reasons the use of consistency checks (like are the data models of the two instances exactly the same), is low.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public TechnicalInformation getTechnicalInformation()
/*  24:    */   {
/*  25:128 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  26:129 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wikipedia");
/*  27:130 */     result.setValue(TechnicalInformation.Field.TITLE, "Euclidean distance");
/*  28:131 */     result.setValue(TechnicalInformation.Field.URL, "http://en.wikipedia.org/wiki/Euclidean_distance");
/*  29:    */     
/*  30:133 */     return result;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public double distance(Instance first, Instance second)
/*  34:    */   {
/*  35:144 */     return Math.sqrt(distance(first, second, (1.0D / 0.0D)));
/*  36:    */   }
/*  37:    */   
/*  38:    */   public double distance(Instance first, Instance second, PerformanceStats stats)
/*  39:    */   {
/*  40:164 */     return Math.sqrt(distance(first, second, (1.0D / 0.0D), stats));
/*  41:    */   }
/*  42:    */   
/*  43:    */   protected double updateDistance(double currDist, double diff)
/*  44:    */   {
/*  45:180 */     double result = currDist;
/*  46:181 */     result += diff * diff;
/*  47:    */     
/*  48:183 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void postProcessDistances(double[] distances)
/*  52:    */   {
/*  53:197 */     for (int i = 0; i < distances.length; i++) {
/*  54:198 */       distances[i] = Math.sqrt(distances[i]);
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public double sqDifference(int index, double val1, double val2)
/*  59:    */   {
/*  60:211 */     double val = difference(index, val1, val2);
/*  61:212 */     return val * val;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public double getMiddle(double[] ranges)
/*  65:    */   {
/*  66:223 */     double middle = ranges[0] + ranges[2] * 0.5D;
/*  67:224 */     return middle;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public int closestPoint(Instance instance, Instances allPoints, int[] pointList)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:239 */     double minDist = 2147483647.0D;
/*  74:240 */     int bestPoint = 0;
/*  75:241 */     for (int i = 0; i < pointList.length; i++)
/*  76:    */     {
/*  77:242 */       double dist = distance(instance, allPoints.instance(pointList[i]), (1.0D / 0.0D));
/*  78:243 */       if (dist < minDist)
/*  79:    */       {
/*  80:244 */         minDist = dist;
/*  81:245 */         bestPoint = i;
/*  82:    */       }
/*  83:    */     }
/*  84:248 */     return pointList[bestPoint];
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean valueIsSmallerEqual(Instance instance, int dim, double value)
/*  88:    */   {
/*  89:262 */     return instance.value(dim) <= value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String getRevision()
/*  93:    */   {
/*  94:271 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  95:    */   }
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.EuclideanDistance
 * JD-Core Version:    0.7.0.1
 */
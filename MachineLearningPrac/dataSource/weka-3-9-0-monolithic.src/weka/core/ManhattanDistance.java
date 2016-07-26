/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ public class ManhattanDistance
/*   4:    */   extends NormalizableDistance
/*   5:    */   implements TechnicalInformationHandler
/*   6:    */ {
/*   7:    */   private static final long serialVersionUID = 6783782554224000243L;
/*   8:    */   
/*   9:    */   public ManhattanDistance() {}
/*  10:    */   
/*  11:    */   public ManhattanDistance(Instances data)
/*  12:    */   {
/*  13: 90 */     super(data);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public String globalInfo()
/*  17:    */   {
/*  18:100 */     return "Implements the Manhattan distance (or Taxicab geometry). The distance between two points is the sum of the (absolute) differences of their coordinates.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  19:    */   }
/*  20:    */   
/*  21:    */   public TechnicalInformation getTechnicalInformation()
/*  22:    */   {
/*  23:118 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  24:119 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wikipedia");
/*  25:120 */     result.setValue(TechnicalInformation.Field.TITLE, "Taxicab geometry");
/*  26:121 */     result.setValue(TechnicalInformation.Field.URL, "http://en.wikipedia.org/wiki/Taxicab_geometry");
/*  27:    */     
/*  28:123 */     return result;
/*  29:    */   }
/*  30:    */   
/*  31:    */   protected double updateDistance(double currDist, double diff)
/*  32:    */   {
/*  33:139 */     double result = currDist;
/*  34:140 */     result += Math.abs(diff);
/*  35:    */     
/*  36:142 */     return result;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String getRevision()
/*  40:    */   {
/*  41:151 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  42:    */   }
/*  43:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ManhattanDistance
 * JD-Core Version:    0.7.0.1
 */
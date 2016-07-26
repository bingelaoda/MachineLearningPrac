/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ public class ChebyshevDistance
/*   4:    */   extends NormalizableDistance
/*   5:    */   implements TechnicalInformationHandler
/*   6:    */ {
/*   7:    */   private static final long serialVersionUID = -7739904999895461429L;
/*   8:    */   
/*   9:    */   public ChebyshevDistance() {}
/*  10:    */   
/*  11:    */   public ChebyshevDistance(Instances data)
/*  12:    */   {
/*  13: 90 */     super(data);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public String globalInfo()
/*  17:    */   {
/*  18:100 */     return "Implements the Chebyshev distance. The distance between two vectors is the greatest of their differences along any coordinate dimension.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  19:    */   }
/*  20:    */   
/*  21:    */   public TechnicalInformation getTechnicalInformation()
/*  22:    */   {
/*  23:117 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  24:118 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wikipedia");
/*  25:119 */     result.setValue(TechnicalInformation.Field.TITLE, "Chebyshev distance");
/*  26:120 */     result.setValue(TechnicalInformation.Field.URL, "http://en.wikipedia.org/wiki/Chebyshev_distance");
/*  27:    */     
/*  28:122 */     return result;
/*  29:    */   }
/*  30:    */   
/*  31:    */   protected double updateDistance(double currDist, double diff)
/*  32:    */   {
/*  33:138 */     double result = currDist;
/*  34:    */     
/*  35:140 */     diff = Math.abs(diff);
/*  36:141 */     if (diff > result) {
/*  37:142 */       result = diff;
/*  38:    */     }
/*  39:144 */     return result;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getRevision()
/*  43:    */   {
/*  44:153 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  45:    */   }
/*  46:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ChebyshevDistance
 * JD-Core Version:    0.7.0.1
 */
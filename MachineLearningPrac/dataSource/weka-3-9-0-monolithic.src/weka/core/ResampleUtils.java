/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ import java.util.Random;
/*  4:   */ 
/*  5:   */ public class ResampleUtils
/*  6:   */ {
/*  7:   */   public static boolean hasInstanceWeights(Instances insts)
/*  8:   */   {
/*  9:40 */     boolean result = false;
/* 10:41 */     for (int i = 0; i < insts.numInstances(); i++) {
/* 11:42 */       if (insts.instance(i).weight() != 1.0D)
/* 12:   */       {
/* 13:43 */         result = true;
/* 14:44 */         break;
/* 15:   */       }
/* 16:   */     }
/* 17:47 */     return result;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public static Instances resampleWithWeightIfNecessary(Instances insts, Random rand)
/* 21:   */   {
/* 22:60 */     if (hasInstanceWeights(insts)) {
/* 23:61 */       return insts.resampleWithWeights(rand);
/* 24:   */     }
/* 25:63 */     return insts;
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ResampleUtils
 * JD-Core Version:    0.7.0.1
 */
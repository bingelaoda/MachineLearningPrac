/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import weka.core.Instance;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public final class Values
/*   9:    */   implements RevisionHandler
/*  10:    */ {
/*  11:    */   int numInstances;
/*  12:    */   int missingInstances;
/*  13:    */   int first;
/*  14:    */   int last;
/*  15:    */   int attr;
/*  16:    */   double sum;
/*  17:    */   double sqrSum;
/*  18:    */   double va;
/*  19:    */   double sd;
/*  20:    */   
/*  21:    */   public Values(int low, int high, int attribute, Instances inst)
/*  22:    */   {
/*  23: 58 */     int count = 0;
/*  24:    */     
/*  25:    */ 
/*  26: 61 */     this.numInstances = (high - low + 1);
/*  27: 62 */     this.missingInstances = 0;
/*  28: 63 */     this.first = low;
/*  29: 64 */     this.last = high;
/*  30: 65 */     this.attr = attribute;
/*  31: 66 */     this.sum = 0.0D;
/*  32: 67 */     this.sqrSum = 0.0D;
/*  33: 68 */     for (int i = this.first; i <= this.last; i++)
/*  34:    */     {
/*  35: 69 */       if (!inst.instance(i).isMissing(this.attr))
/*  36:    */       {
/*  37: 70 */         count++;
/*  38: 71 */         double value = inst.instance(i).value(this.attr);
/*  39: 72 */         this.sum += value;
/*  40: 73 */         this.sqrSum += value * value;
/*  41:    */       }
/*  42: 76 */       if (count > 1)
/*  43:    */       {
/*  44: 77 */         this.va = ((this.sqrSum - this.sum * this.sum / count) / count);
/*  45: 78 */         this.va = Math.abs(this.va);
/*  46: 79 */         this.sd = Math.sqrt(this.va);
/*  47:    */       }
/*  48:    */       else
/*  49:    */       {
/*  50: 81 */         this.va = 0.0D;this.sd = 0.0D;
/*  51:    */       }
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   public final String toString()
/*  56:    */   {
/*  57: 91 */     StringBuffer text = new StringBuffer();
/*  58:    */     
/*  59: 93 */     text.append("Print statistic values of instances (" + this.first + "-" + this.last + "\n");
/*  60:    */     
/*  61: 95 */     text.append("    Number of instances:\t" + this.numInstances + "\n");
/*  62: 96 */     text.append("    NUmber of instances with unknowns:\t" + this.missingInstances + "\n");
/*  63:    */     
/*  64: 98 */     text.append("    Attribute:\t\t\t:" + this.attr + "\n");
/*  65: 99 */     text.append("    Sum:\t\t\t" + this.sum + "\n");
/*  66:100 */     text.append("    Squared sum:\t\t" + this.sqrSum + "\n");
/*  67:101 */     text.append("    Stanard Deviation:\t\t" + this.sd + "\n");
/*  68:    */     
/*  69:103 */     return text.toString();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getRevision()
/*  73:    */   {
/*  74:112 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.Values
 * JD-Core Version:    0.7.0.1
 */
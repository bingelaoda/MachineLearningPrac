/*   1:    */ package weka.classifiers.bayes.net;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class VaryNode
/*   9:    */   implements Serializable, RevisionHandler
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -6196294370675872424L;
/*  12:    */   public int m_iNode;
/*  13:    */   public int m_nMCV;
/*  14:    */   public ADNode[] m_ADNodes;
/*  15:    */   
/*  16:    */   public VaryNode(int iNode)
/*  17:    */   {
/*  18: 51 */     this.m_iNode = iNode;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void getCounts(int[] nCounts, int[] nNodes, int[] nOffsets, int iNode, int iOffset, ADNode parent, boolean bSubstract)
/*  22:    */   {
/*  23: 67 */     for (int iValue = 0; iValue < this.m_ADNodes.length; iValue++) {
/*  24: 68 */       if (iValue != this.m_nMCV)
/*  25:    */       {
/*  26: 69 */         if (this.m_ADNodes[iValue] != null) {
/*  27: 70 */           this.m_ADNodes[iValue].getCounts(nCounts, nNodes, nOffsets, iNode + 1, iOffset + nOffsets[iNode] * iValue, bSubstract);
/*  28:    */         }
/*  29:    */       }
/*  30:    */       else
/*  31:    */       {
/*  32: 74 */         parent.getCounts(nCounts, nNodes, nOffsets, iNode + 1, iOffset + nOffsets[iNode] * iValue, bSubstract);
/*  33: 76 */         for (int iValue2 = 0; iValue2 < this.m_ADNodes.length; iValue2++) {
/*  34: 77 */           if ((iValue2 != this.m_nMCV) && (this.m_ADNodes[iValue2] != null)) {
/*  35: 78 */             this.m_ADNodes[iValue2].getCounts(nCounts, nNodes, nOffsets, iNode + 1, iOffset + nOffsets[iNode] * iValue, !bSubstract);
/*  36:    */           }
/*  37:    */         }
/*  38:    */       }
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void print(String sTab)
/*  43:    */   {
/*  44: 92 */     for (int iValue = 0; iValue < this.m_ADNodes.length; iValue++)
/*  45:    */     {
/*  46: 93 */       System.out.print(sTab + iValue + ": ");
/*  47: 94 */       if (this.m_ADNodes[iValue] == null)
/*  48:    */       {
/*  49: 95 */         if (iValue == this.m_nMCV) {
/*  50: 96 */           System.out.println("MCV");
/*  51:    */         } else {
/*  52: 98 */           System.out.println("null");
/*  53:    */         }
/*  54:    */       }
/*  55:    */       else
/*  56:    */       {
/*  57:101 */         System.out.println();
/*  58:102 */         this.m_ADNodes[iValue].print();
/*  59:    */       }
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String getRevision()
/*  64:    */   {
/*  65:114 */     return RevisionUtils.extract("$Revision: 10153 $");
/*  66:    */   }
/*  67:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.VaryNode
 * JD-Core Version:    0.7.0.1
 */
/*  1:   */ package weka.gui.graphvisualizer;
/*  2:   */ 
/*  3:   */ public class GraphEdge
/*  4:   */ {
/*  5:   */   public int src;
/*  6:   */   public int dest;
/*  7:   */   public int type;
/*  8:   */   public String srcLbl;
/*  9:   */   public String destLbl;
/* 10:   */   
/* 11:   */   public GraphEdge(int s, int d, int t)
/* 12:   */   {
/* 13:44 */     this.src = s;this.dest = d;this.type = t;
/* 14:45 */     this.srcLbl = null;this.destLbl = null;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public GraphEdge(int s, int d, int t, String sLbl, String dLbl)
/* 18:   */   {
/* 19:49 */     this.src = s;this.dest = d;this.type = t;
/* 20:50 */     this.srcLbl = sLbl;this.destLbl = dLbl;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public String toString()
/* 24:   */   {
/* 25:54 */     return "(" + this.src + "," + this.dest + "," + this.type + ")";
/* 26:   */   }
/* 27:   */   
/* 28:   */   public boolean equals(Object e)
/* 29:   */   {
/* 30:58 */     if (((e instanceof GraphEdge)) && (((GraphEdge)e).src == this.src) && (((GraphEdge)e).dest == this.dest) && (((GraphEdge)e).type == this.type)) {
/* 31:62 */       return true;
/* 32:   */     }
/* 33:64 */     return false;
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.GraphEdge
 * JD-Core Version:    0.7.0.1
 */
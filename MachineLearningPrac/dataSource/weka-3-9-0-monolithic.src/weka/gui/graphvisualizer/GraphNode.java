/*  1:   */ package weka.gui.graphvisualizer;
/*  2:   */ 
/*  3:   */ public class GraphNode
/*  4:   */   implements GraphConstants
/*  5:   */ {
/*  6:39 */   public int y = 0;
/*  7:39 */   public int x = 0;
/*  8:46 */   public int nodeType = 3;
/*  9:   */   public String ID;
/* 10:   */   public String lbl;
/* 11:   */   public String[] outcomes;
/* 12:   */   public double[][] probs;
/* 13:   */   public int[] prnts;
/* 14:   */   public int[][] edges;
/* 15:   */   
/* 16:   */   public GraphNode(String id, String label)
/* 17:   */   {
/* 18:53 */     this.ID = id;this.lbl = label;this.nodeType = 3;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public GraphNode(String id, String label, int type)
/* 22:   */   {
/* 23:61 */     this.ID = id;this.lbl = label;this.nodeType = type;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public boolean equals(Object n)
/* 27:   */   {
/* 28:71 */     if (((n instanceof GraphNode)) && (((GraphNode)n).ID.equalsIgnoreCase(this.ID))) {
/* 29:74 */       return true;
/* 30:   */     }
/* 31:77 */     return false;
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.GraphNode
 * JD-Core Version:    0.7.0.1
 */
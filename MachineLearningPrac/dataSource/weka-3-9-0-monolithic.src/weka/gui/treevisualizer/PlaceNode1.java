/*   1:    */ package weka.gui.treevisualizer;
/*   2:    */ 
/*   3:    */ public class PlaceNode1
/*   4:    */   implements NodePlace
/*   5:    */ {
/*   6:    */   private double[] m_levels;
/*   7:    */   private int m_noLevels;
/*   8:    */   private int[] m_levelNode;
/*   9:    */   private double m_yRatio;
/*  10:    */   
/*  11:    */   public void place(Node r)
/*  12:    */   {
/*  13: 75 */     this.m_noLevels = (Node.getHeight(r, 0) + 1);
/*  14:    */     
/*  15: 77 */     this.m_yRatio = (1.0D / this.m_noLevels);
/*  16:    */     
/*  17: 79 */     this.m_levels = new double[this.m_noLevels];
/*  18: 80 */     this.m_levelNode = new int[this.m_noLevels];
/*  19: 81 */     for (int noa = 0; noa < this.m_noLevels; noa++)
/*  20:    */     {
/*  21: 82 */       this.m_levels[noa] = 1.0D;
/*  22: 83 */       this.m_levelNode[noa] = 0;
/*  23:    */     }
/*  24: 86 */     setNumOfNodes(r, 0);
/*  25: 88 */     for (int noa = 0; noa < this.m_noLevels; noa++) {
/*  26: 89 */       this.m_levels[noa] = (1.0D / this.m_levels[noa]);
/*  27:    */     }
/*  28: 92 */     placer(r, 0);
/*  29:    */   }
/*  30:    */   
/*  31:    */   private void setNumOfNodes(Node r, int l)
/*  32:    */   {
/*  33:103 */     l++;
/*  34:    */     
/*  35:105 */     this.m_levels[l] += 1.0D;
/*  36:    */     Edge e;
/*  37:106 */     for (int noa = 0; ((e = r.getChild(noa)) != null) && (r.getCVisible()); noa++) {
/*  38:107 */       setNumOfNodes(e.getTarget(), l);
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   private void placer(Node r, int l)
/*  43:    */   {
/*  44:119 */     l++;
/*  45:120 */     this.m_levelNode[l] += 1;
/*  46:121 */     r.setCenter(this.m_levelNode[l] * this.m_levels[l]);
/*  47:122 */     r.setTop(l * this.m_yRatio);
/*  48:    */     Edge e;
/*  49:123 */     for (int noa = 0; ((e = r.getChild(noa)) != null) && (r.getCVisible()); noa++) {
/*  50:124 */       placer(e.getTarget(), l);
/*  51:    */     }
/*  52:    */   }
/*  53:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.PlaceNode1
 * JD-Core Version:    0.7.0.1
 */
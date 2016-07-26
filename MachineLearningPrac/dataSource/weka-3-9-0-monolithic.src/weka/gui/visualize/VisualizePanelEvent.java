/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.core.Instances;
/*   5:    */ 
/*   6:    */ public class VisualizePanelEvent
/*   7:    */ {
/*   8: 40 */   public static int NONE = 0;
/*   9: 41 */   public static int RECTANGLE = 1;
/*  10: 42 */   public static int OVAL = 2;
/*  11: 43 */   public static int POLYGON = 3;
/*  12: 44 */   public static int LINE = 4;
/*  13: 45 */   public static int VLINE = 5;
/*  14: 46 */   public static int HLINE = 6;
/*  15:    */   private final ArrayList<ArrayList<Double>> m_values;
/*  16:    */   private final Instances m_inst;
/*  17:    */   private final Instances m_inst2;
/*  18:    */   private final int m_attrib1;
/*  19:    */   private final int m_attrib2;
/*  20:    */   
/*  21:    */   public VisualizePanelEvent(ArrayList<ArrayList<Double>> ar, Instances i, Instances i2, int at1, int at2)
/*  22:    */   {
/*  23: 70 */     this.m_values = ar;
/*  24: 71 */     this.m_inst = i;
/*  25: 72 */     this.m_inst2 = i2;
/*  26: 73 */     this.m_attrib1 = at1;
/*  27: 74 */     this.m_attrib2 = at2;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public ArrayList<ArrayList<Double>> getValues()
/*  31:    */   {
/*  32: 82 */     return this.m_values;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Instances getInstances1()
/*  36:    */   {
/*  37: 89 */     return this.m_inst;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Instances getInstances2()
/*  41:    */   {
/*  42: 96 */     return this.m_inst2;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int getAttribute1()
/*  46:    */   {
/*  47:103 */     return this.m_attrib1;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public int getAttribute2()
/*  51:    */   {
/*  52:110 */     return this.m_attrib2;
/*  53:    */   }
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.VisualizePanelEvent
 * JD-Core Version:    0.7.0.1
 */
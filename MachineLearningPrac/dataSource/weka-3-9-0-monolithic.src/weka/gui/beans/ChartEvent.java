/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import java.util.Vector;
/*   5:    */ 
/*   6:    */ public class ChartEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 7812460715499569390L;
/*  10:    */   private Vector<String> m_legendText;
/*  11:    */   private double m_max;
/*  12:    */   private double m_min;
/*  13:    */   private boolean m_reset;
/*  14:    */   private double[] m_dataPoint;
/*  15:    */   
/*  16:    */   public ChartEvent(Object source, Vector<String> legendText, double min, double max, double[] dataPoint, boolean reset)
/*  17:    */   {
/*  18: 61 */     super(source);
/*  19: 62 */     this.m_legendText = legendText;
/*  20: 63 */     this.m_max = max;
/*  21: 64 */     this.m_min = min;
/*  22: 65 */     this.m_dataPoint = dataPoint;
/*  23: 66 */     this.m_reset = reset;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public ChartEvent(Object source)
/*  27:    */   {
/*  28: 75 */     super(source);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Vector<String> getLegendText()
/*  32:    */   {
/*  33: 84 */     return this.m_legendText;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setLegendText(Vector<String> lt)
/*  37:    */   {
/*  38: 93 */     this.m_legendText = lt;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double getMin()
/*  42:    */   {
/*  43:102 */     return this.m_min;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setMin(double m)
/*  47:    */   {
/*  48:111 */     this.m_min = m;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public double getMax()
/*  52:    */   {
/*  53:120 */     return this.m_max;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setMax(double m)
/*  57:    */   {
/*  58:129 */     this.m_max = m;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public double[] getDataPoint()
/*  62:    */   {
/*  63:138 */     return this.m_dataPoint;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setDataPoint(double[] dp)
/*  67:    */   {
/*  68:147 */     this.m_dataPoint = dp;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setReset(boolean reset)
/*  72:    */   {
/*  73:156 */     this.m_reset = reset;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean getReset()
/*  77:    */   {
/*  78:165 */     return this.m_reset;
/*  79:    */   }
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ChartEvent
 * JD-Core Version:    0.7.0.1
 */
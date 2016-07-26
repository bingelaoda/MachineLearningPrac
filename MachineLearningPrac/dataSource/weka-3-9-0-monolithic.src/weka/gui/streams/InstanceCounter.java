/*   1:    */ package weka.gui.streams;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import javax.swing.JLabel;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ 
/*  10:    */ public class InstanceCounter
/*  11:    */   extends JPanel
/*  12:    */   implements InstanceListener
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -6084967152645935934L;
/*  15:    */   private final JLabel m_Count_Lab;
/*  16:    */   private int m_Count;
/*  17:    */   private boolean m_Debug;
/*  18:    */   
/*  19:    */   public void input(Instance instance)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 49 */     if (this.m_Debug) {
/*  23: 50 */       System.err.println("InstanceCounter::input(" + instance + ")");
/*  24:    */     }
/*  25: 52 */     this.m_Count += 1;
/*  26: 53 */     this.m_Count_Lab.setText("" + this.m_Count + " instances");
/*  27: 54 */     repaint();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void inputFormat(Instances instanceInfo)
/*  31:    */   {
/*  32: 59 */     if (this.m_Debug) {
/*  33: 60 */       System.err.println("InstanceCounter::inputFormat()");
/*  34:    */     }
/*  35: 63 */     this.m_Count = 0;
/*  36: 64 */     this.m_Count_Lab.setText("" + this.m_Count + " instances");
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setDebug(boolean debug)
/*  40:    */   {
/*  41: 69 */     this.m_Debug = debug;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean getDebug()
/*  45:    */   {
/*  46: 74 */     return this.m_Debug;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public InstanceCounter()
/*  50:    */   {
/*  51: 79 */     this.m_Count = 0;
/*  52: 80 */     this.m_Count_Lab = new JLabel("no instances");
/*  53: 81 */     add(this.m_Count_Lab);
/*  54:    */     
/*  55: 83 */     setBackground(Color.lightGray);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void instanceProduced(InstanceEvent e)
/*  59:    */   {
/*  60: 89 */     Object source = e.getSource();
/*  61: 90 */     if ((source instanceof InstanceProducer)) {
/*  62:    */       try
/*  63:    */       {
/*  64: 92 */         InstanceProducer a = (InstanceProducer)source;
/*  65: 93 */         switch (e.getID())
/*  66:    */         {
/*  67:    */         case 1: 
/*  68: 95 */           inputFormat(a.outputFormat());
/*  69: 96 */           break;
/*  70:    */         case 2: 
/*  71: 98 */           input(a.outputPeek());
/*  72: 99 */           break;
/*  73:    */         case 3: 
/*  74:101 */           if (this.m_Debug) {
/*  75:102 */             System.err.println("InstanceCounter::instanceProduced() - End of instance batch");
/*  76:    */           }
/*  77:    */           break;
/*  78:    */         default: 
/*  79:107 */           System.err.println("InstanceCounter::instanceProduced() - unknown event type");
/*  80:    */         }
/*  81:    */       }
/*  82:    */       catch (Exception ex)
/*  83:    */       {
/*  84:112 */         System.err.println(ex.getMessage());
/*  85:    */       }
/*  86:    */     } else {
/*  87:115 */       System.err.println("InstanceCounter::instanceProduced() - Unknown source object type");
/*  88:    */     }
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceCounter
 * JD-Core Version:    0.7.0.1
 */
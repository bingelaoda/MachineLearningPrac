/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ 
/*   7:    */ public class InstanceEvent
/*   8:    */   extends EventObject
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 6104920894559423946L;
/*  11:    */   public static final int FORMAT_AVAILABLE = 0;
/*  12:    */   public static final int INSTANCE_AVAILABLE = 1;
/*  13:    */   public static final int BATCH_FINISHED = 2;
/*  14:    */   private Instances m_structure;
/*  15:    */   private Instance m_instance;
/*  16:    */   private int m_status;
/*  17: 54 */   protected boolean m_formatNotificationOnly = false;
/*  18:    */   
/*  19:    */   public InstanceEvent(Object source, Instance instance, int status)
/*  20:    */   {
/*  21: 65 */     super(source);
/*  22: 66 */     this.m_instance = instance;
/*  23: 67 */     this.m_status = status;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public InstanceEvent(Object source, Instances structure)
/*  27:    */   {
/*  28: 78 */     super(source);
/*  29: 79 */     this.m_structure = structure;
/*  30: 80 */     this.m_status = 0;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public InstanceEvent(Object source)
/*  34:    */   {
/*  35: 84 */     super(source);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Instance getInstance()
/*  39:    */   {
/*  40: 93 */     return this.m_instance;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setInstance(Instance i)
/*  44:    */   {
/*  45:102 */     this.m_instance = i;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int getStatus()
/*  49:    */   {
/*  50:111 */     return this.m_status;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setStatus(int s)
/*  54:    */   {
/*  55:120 */     this.m_status = s;
/*  56:122 */     if (this.m_status != 0) {
/*  57:123 */       this.m_formatNotificationOnly = false;
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setStructure(Instances structure)
/*  62:    */   {
/*  63:133 */     this.m_structure = structure;
/*  64:134 */     this.m_instance = null;
/*  65:135 */     this.m_status = 0;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Instances getStructure()
/*  69:    */   {
/*  70:145 */     return this.m_structure;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.InstanceEvent
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package weka.gui.scripting.event;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.gui.scripting.Script;
/*   5:    */ 
/*   6:    */ public class ScriptExecutionEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -8357216611114356632L;
/*  10:    */   protected Type m_Type;
/*  11:    */   protected Object m_Additional;
/*  12:    */   
/*  13:    */   public static enum Type
/*  14:    */   {
/*  15: 47 */     STARTED,  FINISHED,  ERROR,  STOPPED;
/*  16:    */     
/*  17:    */     private Type() {}
/*  18:    */   }
/*  19:    */   
/*  20:    */   public ScriptExecutionEvent(Script source, Type type)
/*  21:    */   {
/*  22: 69 */     this(source, type, null);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ScriptExecutionEvent(Script source, Type type, Object additional)
/*  26:    */   {
/*  27: 80 */     super(source);
/*  28:    */     
/*  29: 82 */     this.m_Type = type;
/*  30: 83 */     this.m_Additional = additional;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Script getScript()
/*  34:    */   {
/*  35: 92 */     return (Script)getSource();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Type getType()
/*  39:    */   {
/*  40:101 */     return this.m_Type;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public boolean hasAdditional()
/*  44:    */   {
/*  45:111 */     return this.m_Additional != null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Object getAdditional()
/*  49:    */   {
/*  50:120 */     return this.m_Additional;
/*  51:    */   }
/*  52:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.event.ScriptExecutionEvent
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package weka.datagenerators;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import weka.core.Option;
/*   6:    */ import weka.core.OptionHandler;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class ClusterDefinition
/*  11:    */   implements Serializable, OptionHandler, RevisionHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -5950001207047429961L;
/*  14:    */   protected ClusterGenerator m_Parent;
/*  15:    */   
/*  16:    */   public ClusterDefinition()
/*  17:    */   {
/*  18: 54 */     this(null);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public ClusterDefinition(ClusterGenerator parent)
/*  22:    */   {
/*  23: 63 */     this.m_Parent = parent;
/*  24:    */     try
/*  25:    */     {
/*  26: 66 */       setDefaults();
/*  27:    */     }
/*  28:    */     catch (Exception e)
/*  29:    */     {
/*  30: 68 */       e.printStackTrace();
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   protected abstract void setDefaults()
/*  35:    */     throws Exception;
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39: 86 */     return "Contains informations about a certain cluster of a cluster generator.";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public abstract Enumeration<Option> listOptions();
/*  43:    */   
/*  44:    */   public abstract void setOptions(String[] paramArrayOfString)
/*  45:    */     throws Exception;
/*  46:    */   
/*  47:    */   public abstract String[] getOptions();
/*  48:    */   
/*  49:    */   public ClusterGenerator getParent()
/*  50:    */   {
/*  51:124 */     return this.m_Parent;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setParent(ClusterGenerator parent)
/*  55:    */   {
/*  56:133 */     this.m_Parent = parent;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String parentTipText()
/*  60:    */   {
/*  61:143 */     return "The cluster generator this object belongs to.";
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String toString()
/*  65:    */   {
/*  66:153 */     return getClass().getName() + ": " + Utils.joinOptions(getOptions());
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.ClusterDefinition
 * JD-Core Version:    0.7.0.1
 */
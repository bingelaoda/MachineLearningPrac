/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.LinkedHashMap;
/*   5:    */ import java.util.Map;
/*   6:    */ 
/*   7:    */ public class Defaults
/*   8:    */   implements Serializable
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 1061521489520308096L;
/*  11: 40 */   protected String m_defaultsID = "";
/*  12: 43 */   protected Map<Settings.SettingKey, Object> m_defaults = new LinkedHashMap();
/*  13:    */   
/*  14:    */   public Defaults(String ID)
/*  15:    */   {
/*  16: 52 */     setID(ID);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public Defaults(String ID, Map<Settings.SettingKey, Object> defaults)
/*  20:    */   {
/*  21: 62 */     this(ID);
/*  22: 63 */     this.m_defaults = defaults;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String getID()
/*  26:    */   {
/*  27: 72 */     return this.m_defaultsID;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setID(String ID)
/*  31:    */   {
/*  32: 81 */     this.m_defaultsID = ID;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Map<Settings.SettingKey, Object> getDefaults()
/*  36:    */   {
/*  37: 90 */     return this.m_defaults;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void add(Defaults toAdd)
/*  41:    */   {
/*  42:100 */     this.m_defaults.putAll(toAdd.getDefaults());
/*  43:    */   }
/*  44:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Defaults
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Hashtable;
/*   7:    */ import java.util.Properties;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.gui.LookAndFeel;
/*  10:    */ 
/*  11:    */ public class SystemInfo
/*  12:    */   implements RevisionHandler
/*  13:    */ {
/*  14: 41 */   private Hashtable<String, String> m_Info = null;
/*  15:    */   
/*  16:    */   public SystemInfo()
/*  17:    */   {
/*  18: 47 */     this.m_Info = new Hashtable();
/*  19: 48 */     readProperties();
/*  20:    */   }
/*  21:    */   
/*  22:    */   private void readProperties()
/*  23:    */   {
/*  24: 63 */     this.m_Info.clear();
/*  25:    */     
/*  26:    */ 
/*  27: 66 */     Properties props = System.getProperties();
/*  28: 67 */     Enumeration<?> enm = props.propertyNames();
/*  29: 68 */     while (enm.hasMoreElements())
/*  30:    */     {
/*  31: 69 */       String name = (String)enm.nextElement();
/*  32: 70 */       this.m_Info.put(name, (String)props.get(name));
/*  33:    */     }
/*  34: 74 */     this.m_Info.put("weka.version", Version.VERSION);
/*  35:    */     
/*  36:    */ 
/*  37: 77 */     String[] laf = LookAndFeel.getInstalledLookAndFeels();
/*  38: 78 */     String tmpStr = "";
/*  39: 79 */     for (int i = 0; i < laf.length; i++)
/*  40:    */     {
/*  41: 80 */       if (i > 0) {
/*  42: 81 */         tmpStr = tmpStr + ",";
/*  43:    */       }
/*  44: 83 */       tmpStr = tmpStr + laf[i];
/*  45:    */     }
/*  46: 85 */     this.m_Info.put("ui.installedLookAndFeels", tmpStr);
/*  47: 86 */     this.m_Info.put("ui.currentLookAndFeel", LookAndFeel.getSystemLookAndFeel());
/*  48:    */     
/*  49:    */ 
/*  50: 89 */     Memory mem = new Memory();
/*  51: 90 */     this.m_Info.put("memory.initial", "" + Utils.doubleToString(Memory.toMegaByte(mem.getInitial()), 1) + "MB" + " (" + mem.getInitial() + ")");
/*  52:    */     
/*  53:    */ 
/*  54: 93 */     this.m_Info.put("memory.max", "" + Utils.doubleToString(Memory.toMegaByte(mem.getMax()), 1) + "MB" + " (" + mem.getMax() + ")");
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Hashtable<String, String> getSystemInfo()
/*  58:    */   {
/*  59:103 */     return new Hashtable(this.m_Info);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String toString()
/*  63:    */   {
/*  64:118 */     String result = "";
/*  65:119 */     Vector<String> keys = new Vector();
/*  66:    */     
/*  67:    */ 
/*  68:122 */     Enumeration<String> enm = this.m_Info.keys();
/*  69:123 */     while (enm.hasMoreElements()) {
/*  70:124 */       keys.add(enm.nextElement());
/*  71:    */     }
/*  72:126 */     Collections.sort(keys);
/*  73:129 */     for (int i = 0; i < keys.size(); i++)
/*  74:    */     {
/*  75:130 */       String key = ((String)keys.get(i)).toString();
/*  76:131 */       String value = ((String)this.m_Info.get(key)).toString();
/*  77:132 */       if (key.equals("line.separator")) {
/*  78:133 */         value = Utils.backQuoteChars(value);
/*  79:    */       }
/*  80:135 */       result = result + key + ": " + value + "\n";
/*  81:    */     }
/*  82:138 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String getRevision()
/*  86:    */   {
/*  87:148 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static void main(String[] args)
/*  91:    */   {
/*  92:155 */     System.out.println(new SystemInfo());
/*  93:    */   }
/*  94:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SystemInfo
 * JD-Core Version:    0.7.0.1
 */
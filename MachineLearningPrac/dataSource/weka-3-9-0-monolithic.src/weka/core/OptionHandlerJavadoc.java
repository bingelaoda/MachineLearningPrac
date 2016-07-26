/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ public class OptionHandlerJavadoc
/*   8:    */   extends Javadoc
/*   9:    */ {
/*  10:    */   public static final String OPTIONS_STARTTAG = "<!-- options-start -->";
/*  11:    */   public static final String OPTIONS_ENDTAG = "<!-- options-end -->";
/*  12: 78 */   protected boolean m_Prolog = true;
/*  13:    */   
/*  14:    */   public OptionHandlerJavadoc()
/*  15:    */   {
/*  16: 86 */     this.m_StartTag = new String[1];
/*  17: 87 */     this.m_EndTag = new String[1];
/*  18: 88 */     this.m_StartTag[0] = "<!-- options-start -->";
/*  19: 89 */     this.m_EndTag[0] = "<!-- options-end -->";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public Enumeration<Option> listOptions()
/*  23:    */   {
/*  24: 99 */     Vector<Option> result = new Vector();
/*  25:    */     
/*  26:101 */     result.addAll(Collections.list(super.listOptions()));
/*  27:    */     
/*  28:103 */     result.addElement(new Option("\tSuppresses the 'Valid options are...' prolog in the Javadoc.", "noprolog", 0, "-noprolog"));
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:107 */     return result.elements();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setOptions(String[] options)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:118 */     super.setOptions(options);
/*  39:    */     
/*  40:120 */     setProlog(!Utils.getFlag("noprolog", options));
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45:130 */     Vector<String> result = new Vector();
/*  46:    */     
/*  47:132 */     Collections.addAll(result, super.getOptions());
/*  48:134 */     if (!getProlog()) {
/*  49:135 */       result.add("-noprolog");
/*  50:    */     }
/*  51:138 */     return (String[])result.toArray(new String[result.size()]);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setProlog(boolean value)
/*  55:    */   {
/*  56:147 */     this.m_Prolog = value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public boolean getProlog()
/*  60:    */   {
/*  61:156 */     return this.m_Prolog;
/*  62:    */   }
/*  63:    */   
/*  64:    */   protected String generateJavadoc(int index)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:172 */     String result = "";
/*  68:174 */     if (index == 0)
/*  69:    */     {
/*  70:175 */       if (!canInstantiateClass()) {
/*  71:176 */         return result;
/*  72:    */       }
/*  73:179 */       if (!ClassDiscovery.hasInterface(OptionHandler.class, getInstance().getClass())) {
/*  74:181 */         throw new Exception("Class '" + getClassname() + "' is not an OptionHandler!");
/*  75:    */       }
/*  76:186 */       OptionHandler handler = (OptionHandler)getInstance();
/*  77:187 */       Enumeration<Option> enm = handler.listOptions();
/*  78:188 */       if (!enm.hasMoreElements()) {
/*  79:189 */         return result;
/*  80:    */       }
/*  81:193 */       if (getProlog()) {
/*  82:194 */         result = "Valid options are: <p>\n\n";
/*  83:    */       }
/*  84:198 */       enm = handler.listOptions();
/*  85:199 */       while (enm.hasMoreElements())
/*  86:    */       {
/*  87:200 */         Option option = (Option)enm.nextElement();
/*  88:201 */         String optionStr = toHTML(option.synopsis()) + "\n" + toHTML(option.description().replaceAll("\\t", " "));
/*  89:    */         
/*  90:203 */         result = result + "<pre> " + optionStr.replaceAll("<br>", "") + "</pre>\n\n";
/*  91:    */       }
/*  92:207 */       if (getUseStars()) {
/*  93:208 */         result = indent(result, 1, "* ");
/*  94:    */       }
/*  95:    */     }
/*  96:212 */     return result;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String getRevision()
/* 100:    */   {
/* 101:222 */     return RevisionUtils.extract("$Revision: 11734 $");
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static void main(String[] args)
/* 105:    */   {
/* 106:231 */     runJavadoc(new OptionHandlerJavadoc(), args);
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.OptionHandlerJavadoc
 * JD-Core Version:    0.7.0.1
 */
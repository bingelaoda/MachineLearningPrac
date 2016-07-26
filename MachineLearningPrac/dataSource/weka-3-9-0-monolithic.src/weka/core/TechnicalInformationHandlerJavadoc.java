/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ public class TechnicalInformationHandlerJavadoc
/*   8:    */   extends Javadoc
/*   9:    */ {
/*  10:    */   public static final String PLAINTEXT_STARTTAG = "<!-- technical-plaintext-start -->";
/*  11:    */   public static final String PLAINTEXT_ENDTAG = "<!-- technical-plaintext-end -->";
/*  12:    */   public static final String BIBTEX_STARTTAG = "<!-- technical-bibtex-start -->";
/*  13:    */   public static final String BIBTEX_ENDTAG = "<!-- technical-bibtex-end -->";
/*  14: 84 */   protected boolean m_Prolog = true;
/*  15:    */   
/*  16:    */   public TechnicalInformationHandlerJavadoc()
/*  17:    */   {
/*  18: 92 */     this.m_StartTag = new String[2];
/*  19: 93 */     this.m_EndTag = new String[2];
/*  20: 94 */     this.m_StartTag[0] = "<!-- technical-plaintext-start -->";
/*  21: 95 */     this.m_EndTag[0] = "<!-- technical-plaintext-end -->";
/*  22: 96 */     this.m_StartTag[1] = "<!-- technical-bibtex-start -->";
/*  23: 97 */     this.m_EndTag[1] = "<!-- technical-bibtex-end -->";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Enumeration<Option> listOptions()
/*  27:    */   {
/*  28:107 */     Vector<Option> result = new Vector();
/*  29:    */     
/*  30:109 */     result.addAll(Collections.list(super.listOptions()));
/*  31:    */     
/*  32:111 */     result.addElement(new Option("\tSuppresses the 'BibTex:' prolog in the Javadoc.", "noprolog", 0, "-noprolog"));
/*  33:    */     
/*  34:    */ 
/*  35:    */ 
/*  36:115 */     return result.elements();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setOptions(String[] options)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:126 */     super.setOptions(options);
/*  43:    */     
/*  44:128 */     setProlog(!Utils.getFlag("noprolog", options));
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String[] getOptions()
/*  48:    */   {
/*  49:142 */     Vector<String> result = new Vector();
/*  50:    */     
/*  51:144 */     String[] options = super.getOptions();
/*  52:145 */     for (int i = 0; i < options.length; i++) {
/*  53:146 */       result.add(options[i]);
/*  54:    */     }
/*  55:149 */     if (!getProlog()) {
/*  56:150 */       result.add("-noprolog");
/*  57:    */     }
/*  58:153 */     return (String[])result.toArray(new String[result.size()]);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setProlog(boolean value)
/*  62:    */   {
/*  63:162 */     this.m_Prolog = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean getProlog()
/*  67:    */   {
/*  68:171 */     return this.m_Prolog;
/*  69:    */   }
/*  70:    */   
/*  71:    */   protected String generateJavadoc(int index)
/*  72:    */     throws Exception
/*  73:    */   {
/*  74:186 */     String result = "";
/*  75:188 */     if (!canInstantiateClass()) {
/*  76:189 */       return result;
/*  77:    */     }
/*  78:192 */     if (!ClassDiscovery.hasInterface(TechnicalInformationHandler.class, getInstance().getClass())) {
/*  79:194 */       throw new Exception("Class '" + getClassname() + "' is not a TechnicalInformationHandler!");
/*  80:    */     }
/*  81:198 */     TechnicalInformationHandler handler = (TechnicalInformationHandler)getInstance();
/*  82:200 */     switch (index)
/*  83:    */     {
/*  84:    */     case 0: 
/*  85:202 */       result = toHTML(handler.getTechnicalInformation().toString()) + "\n";
/*  86:203 */       break;
/*  87:    */     case 1: 
/*  88:207 */       if (getProlog()) {
/*  89:208 */         result = "BibTeX:\n";
/*  90:    */       }
/*  91:210 */       result = result + "<pre>\n";
/*  92:211 */       result = result + toHTML(handler.getTechnicalInformation().toBibTex()).replaceAll("<br>", "") + "\n";
/*  93:    */       
/*  94:213 */       result = result + "</pre>\n<br><br>\n";
/*  95:    */     }
/*  96:218 */     if (getUseStars()) {
/*  97:219 */       result = indent(result, 1, "* ");
/*  98:    */     }
/*  99:222 */     return result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getRevision()
/* 103:    */   {
/* 104:232 */     return RevisionUtils.extract("$Revision: 11736 $");
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static void main(String[] args)
/* 108:    */   {
/* 109:241 */     runJavadoc(new TechnicalInformationHandlerJavadoc(), args);
/* 110:    */   }
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.TechnicalInformationHandlerJavadoc
 * JD-Core Version:    0.7.0.1
 */
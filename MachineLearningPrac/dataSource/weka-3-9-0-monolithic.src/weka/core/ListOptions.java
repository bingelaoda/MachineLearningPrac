/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ public class ListOptions
/*   8:    */   implements OptionHandler, RevisionHandler
/*   9:    */ {
/*  10: 35 */   protected String m_Classname = ListOptions.class.getName();
/*  11:    */   
/*  12:    */   public Enumeration<Option> listOptions()
/*  13:    */   {
/*  14: 44 */     Vector<Option> result = new Vector();
/*  15:    */     
/*  16: 46 */     result.addElement(new Option("\tThe class to load.", "W", 1, "-W <classname>"));
/*  17:    */     
/*  18:    */ 
/*  19: 49 */     return result.elements();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void setOptions(String[] options)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 62 */     String tmpStr = Utils.getOption('W', options);
/*  26: 63 */     if (tmpStr.length() > 0) {
/*  27: 64 */       setClassname(tmpStr);
/*  28:    */     } else {
/*  29: 66 */       setClassname(getClass().getName());
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String[] getOptions()
/*  34:    */   {
/*  35: 79 */     Vector<String> result = new Vector();
/*  36:    */     
/*  37: 81 */     result.add("-W");
/*  38: 82 */     result.add(getClassname());
/*  39:    */     
/*  40: 84 */     return (String[])result.toArray(new String[result.size()]);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setClassname(String value)
/*  44:    */   {
/*  45: 93 */     this.m_Classname = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String getClassname()
/*  49:    */   {
/*  50:102 */     return this.m_Classname;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public String generateHelp()
/*  54:    */   {
/*  55:115 */     String result = getClass().getName().replaceAll(".*\\.", "") + " Options:\n\n";
/*  56:116 */     Enumeration<Option> enm = listOptions();
/*  57:117 */     while (enm.hasMoreElements())
/*  58:    */     {
/*  59:118 */       Option option = (Option)enm.nextElement();
/*  60:119 */       result = result + option.synopsis() + "\n" + option.description() + "\n";
/*  61:    */     }
/*  62:122 */     return result;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String generate()
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:137 */     StringBuffer result = new StringBuffer();
/*  69:    */     
/*  70:139 */     OptionHandler handler = (OptionHandler)Class.forName(getClassname()).newInstance();
/*  71:    */     
/*  72:141 */     Enumeration<Option> enm = handler.listOptions();
/*  73:142 */     while (enm.hasMoreElements())
/*  74:    */     {
/*  75:143 */       Option option = (Option)enm.nextElement();
/*  76:144 */       result.append(option.synopsis() + '\n');
/*  77:145 */       result.append(option.description() + "\n");
/*  78:    */     }
/*  79:148 */     return result.toString();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String getRevision()
/*  83:    */   {
/*  84:158 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static void main(String[] options)
/*  88:    */   {
/*  89:167 */     ListOptions list = new ListOptions();
/*  90:    */     try
/*  91:    */     {
/*  92:    */       try
/*  93:    */       {
/*  94:171 */         if (Utils.getFlag('h', options)) {
/*  95:172 */           throw new Exception("Help requested");
/*  96:    */         }
/*  97:175 */         list.setOptions(options);
/*  98:176 */         Utils.checkForRemainingOptions(options);
/*  99:    */       }
/* 100:    */       catch (Exception ex)
/* 101:    */       {
/* 102:178 */         String result = "\n" + ex.getMessage() + "\n\n" + list.generateHelp();
/* 103:179 */         throw new Exception(result);
/* 104:    */       }
/* 105:182 */       System.out.println("\n" + list.generate());
/* 106:    */     }
/* 107:    */     catch (Exception ex)
/* 108:    */     {
/* 109:184 */       System.err.println(ex.getMessage());
/* 110:    */     }
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ListOptions
 * JD-Core Version:    0.7.0.1
 */
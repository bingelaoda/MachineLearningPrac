/*   1:    */ package weka.core.stopwords;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Arrays;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public abstract class AbstractFileBasedStopwords
/*  15:    */   extends AbstractStopwords
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -8568762652879773063L;
/*  18: 48 */   protected File m_Stopwords = new File(System.getProperty("user.dir"));
/*  19:    */   
/*  20:    */   public Enumeration<Option> listOptions()
/*  21:    */   {
/*  22: 57 */     Vector<Option> result = new Vector();
/*  23:    */     
/*  24: 59 */     Enumeration<Option> enm = super.listOptions();
/*  25: 60 */     while (enm.hasMoreElements()) {
/*  26: 61 */       result.add(enm.nextElement());
/*  27:    */     }
/*  28: 63 */     result.addElement(new Option("\t" + stopwordsTipText() + "\n" + "\t(default: .)", "stopwords", 1, "-stopwords <file>"));
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33: 68 */     return result.elements();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setOptions(String[] options)
/*  37:    */     throws Exception
/*  38:    */   {
/*  39: 81 */     String tmpStr = Utils.getOption("stopwords", options);
/*  40: 82 */     if (tmpStr.isEmpty()) {
/*  41: 83 */       setStopwords(new File("."));
/*  42:    */     } else {
/*  43: 85 */       setStopwords(new File(tmpStr));
/*  44:    */     }
/*  45: 87 */     super.setOptions(options);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String[] getOptions()
/*  49:    */   {
/*  50: 97 */     List<String> options = new ArrayList(Arrays.asList(super.getOptions()));
/*  51:    */     
/*  52: 99 */     options.add("-stopwords");
/*  53:100 */     options.add(getStopwords().toString());
/*  54:    */     
/*  55:102 */     return (String[])options.toArray(new String[options.size()]);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setStopwords(File value)
/*  59:    */   {
/*  60:112 */     if (value == null) {
/*  61:113 */       value = new File(System.getProperty("user.dir"));
/*  62:    */     }
/*  63:116 */     this.m_Stopwords = value;
/*  64:117 */     reset();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public File getStopwords()
/*  68:    */   {
/*  69:127 */     return this.m_Stopwords;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public abstract String stopwordsTipText();
/*  73:    */   
/*  74:    */   protected List<String> read()
/*  75:    */   {
/*  76:149 */     result = new ArrayList();
/*  77:151 */     if ((this.m_Stopwords.exists()) && (!this.m_Stopwords.isDirectory()))
/*  78:    */     {
/*  79:152 */       BufferedReader reader = null;
/*  80:    */       try
/*  81:    */       {
/*  82:154 */         reader = new BufferedReader(new FileReader(this.m_Stopwords));
/*  83:    */         String line;
/*  84:155 */         while ((line = reader.readLine()) != null) {
/*  85:156 */           result.add(line.trim());
/*  86:    */         }
/*  87:174 */         return result;
/*  88:    */       }
/*  89:    */       catch (Exception e)
/*  90:    */       {
/*  91:159 */         error("Failed to read stopwords file '" + this.m_Stopwords + "'!");
/*  92:160 */         e.printStackTrace();
/*  93:    */       }
/*  94:    */       finally
/*  95:    */       {
/*  96:163 */         if (reader != null) {
/*  97:    */           try
/*  98:    */           {
/*  99:165 */             reader.close();
/* 100:    */           }
/* 101:    */           catch (Exception ex) {}
/* 102:    */         }
/* 103:    */       }
/* 104:    */     }
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stopwords.AbstractFileBasedStopwords
 * JD-Core Version:    0.7.0.1
 */
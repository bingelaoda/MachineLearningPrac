/*   1:    */ package weka.core.stemmers;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.FileOutputStream;
/*   7:    */ import java.io.InputStreamReader;
/*   8:    */ import java.io.OutputStreamWriter;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.io.Reader;
/*  11:    */ import java.io.Writer;
/*  12:    */ import java.util.Collections;
/*  13:    */ import java.util.Enumeration;
/*  14:    */ import java.util.Vector;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.Utils;
/*  20:    */ 
/*  21:    */ public class Stemming
/*  22:    */   implements RevisionHandler
/*  23:    */ {
/*  24:    */   protected static String makeOptionsString(Stemmer stemmer)
/*  25:    */   {
/*  26: 58 */     Vector<Option> options = new Vector();
/*  27:    */     
/*  28:    */ 
/*  29: 61 */     options.add(new Option("\tDisplays this help.", "h", 0, "-h"));
/*  30:    */     
/*  31: 63 */     options.add(new Option("\tThe file to process.", "i", 1, "-i <input-file>"));
/*  32:    */     
/*  33:    */ 
/*  34: 66 */     options.add(new Option("\tThe file to output the processed data to (default stdout).", "o", 1, "-o <output-file>"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38: 70 */     options.add(new Option("\tUses lowercase strings.", "l", 0, "-l"));
/*  39: 73 */     if ((stemmer instanceof OptionHandler)) {
/*  40: 74 */       options.addAll(Collections.list(((OptionHandler)stemmer).listOptions()));
/*  41:    */     }
/*  42: 79 */     StringBuffer result = new StringBuffer();
/*  43: 80 */     result.append("\nStemmer options:\n\n");
/*  44: 81 */     Enumeration<Option> enm = options.elements();
/*  45: 82 */     while (enm.hasMoreElements())
/*  46:    */     {
/*  47: 83 */       Option option = (Option)enm.nextElement();
/*  48: 84 */       result.append(option.synopsis() + "\n");
/*  49: 85 */       result.append(option.description() + "\n");
/*  50:    */     }
/*  51: 88 */     return result.toString();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static void useStemmer(Stemmer stemmer, String[] options)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:109 */     if (Utils.getFlag('h', options))
/*  58:    */     {
/*  59:110 */       System.out.println(makeOptionsString(stemmer));
/*  60:111 */       return;
/*  61:    */     }
/*  62:115 */     String tmpStr = Utils.getOption('i', options);
/*  63:116 */     if (tmpStr.length() == 0) {
/*  64:117 */       throw new IllegalArgumentException("No input file defined!" + makeOptionsString(stemmer));
/*  65:    */     }
/*  66:120 */     Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tmpStr)));
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:124 */     StringBuffer input = new StringBuffer();
/*  71:    */     
/*  72:    */ 
/*  73:127 */     tmpStr = Utils.getOption('o', options);
/*  74:    */     Writer output;
/*  75:    */     Writer output;
/*  76:128 */     if (tmpStr.length() == 0) {
/*  77:129 */       output = new BufferedWriter(new OutputStreamWriter(System.out));
/*  78:    */     } else {
/*  79:131 */       output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpStr)));
/*  80:    */     }
/*  81:136 */     boolean lowerCase = Utils.getFlag('l', options);
/*  82:139 */     if ((stemmer instanceof OptionHandler)) {
/*  83:140 */       ((OptionHandler)stemmer).setOptions(options);
/*  84:    */     }
/*  85:    */     try
/*  86:    */     {
/*  87:145 */       Utils.checkForRemainingOptions(options);
/*  88:    */     }
/*  89:    */     catch (Exception e)
/*  90:    */     {
/*  91:147 */       System.out.println(e.getMessage());
/*  92:148 */       System.out.println(makeOptionsString(stemmer));
/*  93:149 */       reader.close(); return;
/*  94:    */     }
/*  95:    */     int character;
/*  96:155 */     while ((character = reader.read()) != -1)
/*  97:    */     {
/*  98:156 */       char ch = (char)character;
/*  99:157 */       if (Character.isWhitespace(ch))
/* 100:    */       {
/* 101:158 */         if (input.length() > 0)
/* 102:    */         {
/* 103:159 */           output.write(stemmer.stem(input.toString()));
/* 104:160 */           input = new StringBuffer();
/* 105:    */         }
/* 106:162 */         output.write(ch);
/* 107:    */       }
/* 108:164 */       else if (lowerCase)
/* 109:    */       {
/* 110:165 */         input.append(Character.toLowerCase(ch));
/* 111:    */       }
/* 112:    */       else
/* 113:    */       {
/* 114:167 */         input.append(ch);
/* 115:    */       }
/* 116:    */     }
/* 117:171 */     output.flush();
/* 118:172 */     reader.close();
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String getRevision()
/* 122:    */   {
/* 123:182 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stemmers.Stemming
 * JD-Core Version:    0.7.0.1
 */
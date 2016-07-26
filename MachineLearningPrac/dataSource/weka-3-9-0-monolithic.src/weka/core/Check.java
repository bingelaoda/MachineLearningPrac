/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ public abstract class Check
/*   8:    */   implements OptionHandler, RevisionHandler
/*   9:    */ {
/*  10: 36 */   protected boolean m_Debug = false;
/*  11: 39 */   protected boolean m_Silent = false;
/*  12:    */   
/*  13:    */   public Enumeration<Option> listOptions()
/*  14:    */   {
/*  15: 48 */     Vector<Option> result = new Vector();
/*  16:    */     
/*  17: 50 */     result.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
/*  18:    */     
/*  19: 52 */     result.addElement(new Option("\tSilent mode - prints nothing to stdout.", "S", 0, "-S"));
/*  20:    */     
/*  21:    */ 
/*  22: 55 */     return result.elements();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void setOptions(String[] options)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 66 */     setDebug(Utils.getFlag('D', options));
/*  29:    */     
/*  30: 68 */     setSilent(Utils.getFlag('S', options));
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String[] getOptions()
/*  34:    */   {
/*  35: 80 */     Vector<String> result = new Vector();
/*  36: 82 */     if (getDebug()) {
/*  37: 83 */       result.add("-D");
/*  38:    */     }
/*  39: 86 */     if (getSilent()) {
/*  40: 87 */       result.add("-S");
/*  41:    */     }
/*  42: 90 */     return (String[])result.toArray(new String[result.size()]);
/*  43:    */   }
/*  44:    */   
/*  45:    */   protected Object forName(String prefix, Class<?> cls, String classname, String[] options)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:113 */     Object result = null;
/*  49:    */     try
/*  50:    */     {
/*  51:116 */       result = Utils.forName(cls, classname, options);
/*  52:    */     }
/*  53:    */     catch (Exception e)
/*  54:    */     {
/*  55:119 */       if (e.getMessage().toLowerCase().indexOf("can't find") > -1) {
/*  56:    */         try
/*  57:    */         {
/*  58:121 */           result = Utils.forName(cls, prefix + "." + classname, options);
/*  59:    */         }
/*  60:    */         catch (Exception ex)
/*  61:    */         {
/*  62:123 */           if (e.getMessage().toLowerCase().indexOf("can't find") > -1) {
/*  63:124 */             throw new Exception("Can't find class called '" + classname + "' or '" + prefix + "." + classname + "'!");
/*  64:    */           }
/*  65:127 */           throw new Exception(ex);
/*  66:    */         }
/*  67:    */       } else {
/*  68:131 */         throw new Exception(e);
/*  69:    */       }
/*  70:    */     }
/*  71:135 */     return result;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public abstract void doTests();
/*  75:    */   
/*  76:    */   public void setDebug(boolean debug)
/*  77:    */   {
/*  78:149 */     this.m_Debug = debug;
/*  79:151 */     if (getDebug()) {
/*  80:152 */       setSilent(false);
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public boolean getDebug()
/*  85:    */   {
/*  86:162 */     return this.m_Debug;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setSilent(boolean value)
/*  90:    */   {
/*  91:171 */     this.m_Silent = value;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean getSilent()
/*  95:    */   {
/*  96:180 */     return this.m_Silent;
/*  97:    */   }
/*  98:    */   
/*  99:    */   protected void print(Object msg)
/* 100:    */   {
/* 101:189 */     if (!getSilent()) {
/* 102:190 */       System.out.print(msg);
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   protected void println(Object msg)
/* 107:    */   {
/* 108:200 */     print(msg + "\n");
/* 109:    */   }
/* 110:    */   
/* 111:    */   protected void println()
/* 112:    */   {
/* 113:207 */     print("\n");
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected static void runCheck(Check check, String[] options)
/* 117:    */   {
/* 118:    */     try
/* 119:    */     {
/* 120:    */       try
/* 121:    */       {
/* 122:219 */         check.setOptions(options);
/* 123:220 */         Utils.checkForRemainingOptions(options);
/* 124:    */       }
/* 125:    */       catch (Exception ex)
/* 126:    */       {
/* 127:222 */         String result = ex.getMessage() + "\n\n" + check.getClass().getName().replaceAll(".*\\.", "") + " Options:\n\n";
/* 128:    */         
/* 129:    */ 
/* 130:225 */         Enumeration<Option> enm = check.listOptions();
/* 131:226 */         while (enm.hasMoreElements())
/* 132:    */         {
/* 133:227 */           Option option = (Option)enm.nextElement();
/* 134:228 */           result = result + option.synopsis() + "\n" + option.description() + "\n";
/* 135:    */         }
/* 136:230 */         throw new Exception(result);
/* 137:    */       }
/* 138:233 */       check.doTests();
/* 139:    */     }
/* 140:    */     catch (Exception ex)
/* 141:    */     {
/* 142:235 */       System.err.println(ex.getMessage());
/* 143:    */     }
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Check
 * JD-Core Version:    0.7.0.1
 */
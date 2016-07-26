/*   1:    */ package org.boon.messages;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import org.boon.validation.ValidationContext;
/*   7:    */ 
/*   8:    */ public class MessageSpecification
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = 1L;
/*  12: 54 */   private String detailMessage = "detailMessage";
/*  13: 58 */   private String summaryMessage = "summaryMessage";
/*  14:    */   private List<String> detailArgs;
/*  15:    */   private List<String> summaryArgs;
/*  16:    */   private String name;
/*  17:    */   private String parent;
/*  18: 83 */   private String subject = "";
/*  19:    */   private boolean noSummary;
/*  20:    */   private static final String SUMMARY_KEY = ".summary";
/*  21:    */   private static final String DETAIL_KEY = ".detail";
/*  22:    */   
/*  23:    */   public void init()
/*  24:    */   {
/*  25: 99 */     if ((this.name == null) && (this.parent == null))
/*  26:    */     {
/*  27:100 */       setDetailMessage("{" + getClass().getName() + ".detail" + "}");
/*  28:101 */       setSummaryMessage("{" + getClass().getName() + ".summary" + "}");
/*  29:    */     }
/*  30:105 */     else if ((this.name != null) && (this.parent == null))
/*  31:    */     {
/*  32:106 */       setDetailMessage("{message." + getName() + ".detail" + "}");
/*  33:107 */       setSummaryMessage("{message." + getName() + ".summary" + "}");
/*  34:    */     }
/*  35:111 */     else if (this.parent != null)
/*  36:    */     {
/*  37:112 */       setDetailMessage("{message." + this.parent + ".detail" + "}");
/*  38:113 */       setSummaryMessage("{message." + this.parent + ".summary" + "}");
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean isNoSummary()
/*  43:    */   {
/*  44:118 */     return this.noSummary;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setNoSummary(boolean noSummary)
/*  48:    */   {
/*  49:122 */     this.noSummary = noSummary;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String createSummaryMessage(Object... args)
/*  53:    */   {
/*  54:129 */     return createMessage(this.summaryMessage, this.summaryArgs, args);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String createDetailMessage(Object... args)
/*  58:    */   {
/*  59:136 */     return createMessage(this.detailMessage, this.detailArgs, args);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String createMessage(String key, List<String> argKeys, Object... args)
/*  63:    */   {
/*  64:148 */     String message = getMessage(key);
/*  65:    */     Object[] actualArgs;
/*  66:    */     Object[] actualArgs;
/*  67:155 */     if (args.length > 0)
/*  68:    */     {
/*  69:156 */       actualArgs = args;
/*  70:    */     }
/*  71:    */     else
/*  72:    */     {
/*  73:    */       Object[] actualArgs;
/*  74:158 */       if (argKeys != null) {
/*  75:160 */         actualArgs = keysToValues(argKeys);
/*  76:    */       } else {
/*  77:162 */         actualArgs = new Object[0];
/*  78:    */       }
/*  79:    */     }
/*  80:165 */     return doCreateMessage(message, actualArgs);
/*  81:    */   }
/*  82:    */   
/*  83:    */   private String doCreateMessage(String message, Object[] actualArgs)
/*  84:    */   {
/*  85:179 */     return ValidationContext.get().createMessage(message, getSubject(), actualArgs);
/*  86:    */   }
/*  87:    */   
/*  88:    */   private String getMessage(String key)
/*  89:    */   {
/*  90:184 */     return ValidationContext.get().getMessage(key);
/*  91:    */   }
/*  92:    */   
/*  93:    */   private Object[] keysToValues(List<String> argKeys)
/*  94:    */   {
/*  95:192 */     List<String> values = new ArrayList();
/*  96:193 */     for (String key : argKeys) {
/*  97:194 */       values.add(getMessage(key));
/*  98:    */     }
/*  99:196 */     return values.toArray();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setCurrentSubject(String subject)
/* 103:    */   {
/* 104:204 */     ValidationContext.get().setCurrentSubject(subject);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String getSubject()
/* 108:    */   {
/* 109:212 */     return ValidationContext.get().getCurrentSubject() == null ? this.subject : ValidationContext.get().getCurrentSubject();
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected String getDetailMessage()
/* 113:    */   {
/* 114:218 */     return this.detailMessage;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setDetailMessage(String detailKey)
/* 118:    */   {
/* 119:222 */     this.detailMessage = detailKey;
/* 120:    */   }
/* 121:    */   
/* 122:    */   protected String getSummaryMessage()
/* 123:    */   {
/* 124:226 */     return this.summaryMessage;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setSummaryMessage(String summaryKey)
/* 128:    */   {
/* 129:230 */     this.summaryMessage = summaryKey;
/* 130:    */   }
/* 131:    */   
/* 132:    */   protected List<String> getDetailArgs()
/* 133:    */   {
/* 134:234 */     return this.detailArgs;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setDetailArgs(List<String> argKeys)
/* 138:    */   {
/* 139:238 */     this.detailArgs = argKeys;
/* 140:    */   }
/* 141:    */   
/* 142:    */   protected List<String> getSummaryArgs()
/* 143:    */   {
/* 144:242 */     return this.summaryArgs;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setSummaryArgs(List<String> summaryArgKeys)
/* 148:    */   {
/* 149:246 */     this.summaryArgs = summaryArgKeys;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setName(String aName)
/* 153:    */   {
/* 154:250 */     this.name = aName;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String getName()
/* 158:    */   {
/* 159:254 */     return this.name;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setParent(String parent)
/* 163:    */   {
/* 164:259 */     this.parent = parent;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setSubject(String subject)
/* 168:    */   {
/* 169:264 */     this.subject = subject;
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.messages.MessageSpecification
 * JD-Core Version:    0.7.0.1
 */
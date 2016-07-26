/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.util.logging.Level;
/*   5:    */ import java.util.logging.Logger;
/*   6:    */ import java.util.regex.Matcher;
/*   7:    */ import java.util.regex.Pattern;
/*   8:    */ import java.util.regex.PatternSyntaxException;
/*   9:    */ import javax.swing.JOptionPane;
/*  10:    */ import javax.swing.SwingUtilities;
/*  11:    */ import javax.swing.text.BadLocationException;
/*  12:    */ import javax.swing.text.Document;
/*  13:    */ import javax.swing.text.JTextComponent;
/*  14:    */ import jsyntaxpane.SyntaxDocument;
/*  15:    */ import jsyntaxpane.actions.gui.QuickFindDialog;
/*  16:    */ import jsyntaxpane.actions.gui.ReplaceDialog;
/*  17:    */ 
/*  18:    */ public class DocumentSearchData
/*  19:    */ {
/*  20:    */   private static final String PROPERTY_KEY = "SearchData";
/*  21: 44 */   private Pattern pattern = null;
/*  22: 45 */   private boolean wrap = true;
/*  23:    */   private ReplaceDialog replaceDlg;
/*  24:    */   private QuickFindDialog quickFindDlg;
/*  25:    */   
/*  26:    */   public Pattern getPattern()
/*  27:    */   {
/*  28: 58 */     return this.pattern;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setPattern(Pattern pattern)
/*  32:    */   {
/*  33: 67 */     this.pattern = pattern;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setPattern(String pat, boolean regex, boolean ignoreCase)
/*  37:    */     throws PatternSyntaxException
/*  38:    */   {
/*  39: 79 */     if ((pat != null) && (pat.length() > 0))
/*  40:    */     {
/*  41: 80 */       int flag = regex ? 0 : 16;
/*  42: 81 */       flag |= (ignoreCase ? 2 : 0);
/*  43: 82 */       setPattern(Pattern.compile(pat, flag));
/*  44:    */     }
/*  45:    */     else
/*  46:    */     {
/*  47: 84 */       setPattern(null);
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public boolean isWrap()
/*  52:    */   {
/*  53: 89 */     return this.wrap;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setWrap(boolean wrap)
/*  57:    */   {
/*  58: 93 */     this.wrap = wrap;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static DocumentSearchData getFromEditor(JTextComponent target)
/*  62:    */   {
/*  63:103 */     if (target == null) {
/*  64:104 */       return null;
/*  65:    */     }
/*  66:106 */     Object o = target.getDocument().getProperty("SearchData");
/*  67:107 */     if ((o instanceof DocumentSearchData))
/*  68:    */     {
/*  69:108 */       DocumentSearchData documentSearchData = (DocumentSearchData)o;
/*  70:109 */       return documentSearchData;
/*  71:    */     }
/*  72:111 */     DocumentSearchData newDSD = new DocumentSearchData();
/*  73:112 */     target.getDocument().putProperty("SearchData", newDSD);
/*  74:113 */     return newDSD;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void doReplaceAll(JTextComponent target, String replacement)
/*  78:    */   {
/*  79:125 */     SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(target);
/*  80:126 */     if (sDoc == null) {
/*  81:127 */       return;
/*  82:    */     }
/*  83:129 */     if (getPattern() == null) {
/*  84:130 */       return;
/*  85:    */     }
/*  86:132 */     Matcher matcher = sDoc.getMatcher(getPattern());
/*  87:133 */     String newText = matcher.replaceAll(replacement);
/*  88:    */     try
/*  89:    */     {
/*  90:135 */       sDoc.replace(0, sDoc.getLength(), newText, null);
/*  91:    */     }
/*  92:    */     catch (BadLocationException ex)
/*  93:    */     {
/*  94:137 */       Logger.getLogger(DocumentSearchData.class.getName()).log(Level.SEVERE, null, ex);
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void doReplace(JTextComponent target, String replacement)
/*  99:    */   {
/* 100:147 */     if (target.getSelectedText() != null)
/* 101:    */     {
/* 102:148 */       target.replaceSelection(replacement);
/* 103:149 */       doFindNext(target);
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public boolean doFindPrev(JTextComponent target)
/* 108:    */   {
/* 109:159 */     if (getPattern() == null) {
/* 110:160 */       return false;
/* 111:    */     }
/* 112:162 */     SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(target);
/* 113:163 */     if (sDoc == null) {
/* 114:164 */       return false;
/* 115:    */     }
/* 116:166 */     int dot = target.getSelectionStart();
/* 117:167 */     Matcher matcher = sDoc.getMatcher(getPattern());
/* 118:168 */     if (matcher == null) {
/* 119:169 */       return false;
/* 120:    */     }
/* 121:173 */     int start = -1;
/* 122:174 */     int end = -1;
/* 123:175 */     while ((matcher.find()) && 
/* 124:176 */       (matcher.end() < dot))
/* 125:    */     {
/* 126:179 */       start = matcher.start();
/* 127:180 */       end = matcher.end();
/* 128:    */     }
/* 129:182 */     if (end > 0)
/* 130:    */     {
/* 131:183 */       target.select(start, end);
/* 132:184 */       return true;
/* 133:    */     }
/* 134:186 */     return false;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public boolean doFindNext(JTextComponent target)
/* 138:    */   {
/* 139:197 */     if (getPattern() == null) {
/* 140:198 */       return false;
/* 141:    */     }
/* 142:200 */     SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(target);
/* 143:201 */     if (sDoc == null) {
/* 144:202 */       return false;
/* 145:    */     }
/* 146:204 */     int start = target.getSelectionEnd();
/* 147:205 */     if (target.getSelectionEnd() == target.getSelectionStart()) {
/* 148:208 */       start++;
/* 149:    */     }
/* 150:210 */     if (start >= sDoc.getLength()) {
/* 151:211 */       start = sDoc.getLength();
/* 152:    */     }
/* 153:213 */     Matcher matcher = sDoc.getMatcher(getPattern(), start);
/* 154:214 */     if ((matcher != null) && (matcher.find()))
/* 155:    */     {
/* 156:217 */       target.select(matcher.start() + start, matcher.end() + start);
/* 157:218 */       return true;
/* 158:    */     }
/* 159:220 */     if (isWrap())
/* 160:    */     {
/* 161:221 */       matcher = sDoc.getMatcher(getPattern());
/* 162:222 */       if ((matcher != null) && (matcher.find()))
/* 163:    */       {
/* 164:223 */         target.select(matcher.start(), matcher.end());
/* 165:224 */         return true;
/* 166:    */       }
/* 167:226 */       return false;
/* 168:    */     }
/* 169:229 */     return false;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void msgNotFound(Component target)
/* 173:    */   {
/* 174:239 */     JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(target), "Search String " + getPattern() + " not found", "Find", 1);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void showReplaceDialog(JTextComponent target)
/* 178:    */   {
/* 179:249 */     if (this.replaceDlg == null) {
/* 180:250 */       this.replaceDlg = new ReplaceDialog(target, this);
/* 181:    */     }
/* 182:252 */     this.replaceDlg.setVisible(true);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void showQuickFindDialog(JTextComponent target)
/* 186:    */   {
/* 187:256 */     if (this.quickFindDlg == null) {
/* 188:257 */       this.quickFindDlg = new QuickFindDialog(target, this);
/* 189:    */     }
/* 190:259 */     this.quickFindDlg.showFor(target);
/* 191:    */   }
/* 192:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.DocumentSearchData
 * JD-Core Version:    0.7.0.1
 */
/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Graphics;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import java.util.Properties;
/*   9:    */ import java.util.logging.Logger;
/*  10:    */ import java.util.regex.Pattern;
/*  11:    */ import javax.swing.text.Segment;
/*  12:    */ import javax.swing.text.TabExpander;
/*  13:    */ import jsyntaxpane.util.Configuration;
/*  14:    */ import jsyntaxpane.util.Configuration.StringKeyMatcher;
/*  15:    */ import jsyntaxpane.util.JarServiceProvider;
/*  16:    */ 
/*  17:    */ public class SyntaxStyles
/*  18:    */ {
/*  19: 42 */   public static final Pattern STYLE_PATTERN = Pattern.compile("Style\\.(\\w+)");
/*  20:    */   Map<TokenType, SyntaxStyle> styles;
/*  21:    */   
/*  22:    */   public void mergeStyles(Properties styles)
/*  23:    */   {
/*  24: 51 */     for (Map.Entry e : styles.entrySet())
/*  25:    */     {
/*  26: 52 */       String tokenType = e.getKey().toString();
/*  27: 53 */       String style = e.getValue().toString();
/*  28:    */       try
/*  29:    */       {
/*  30: 55 */         TokenType tt = TokenType.valueOf(tokenType);
/*  31: 56 */         SyntaxStyle tokenStyle = new SyntaxStyle(style);
/*  32: 57 */         put(tt, tokenStyle);
/*  33:    */       }
/*  34:    */       catch (IllegalArgumentException ex)
/*  35:    */       {
/*  36: 59 */         LOG.warning("illegal token type or style for: " + tokenType);
/*  37:    */       }
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41: 64 */   private static SyntaxStyles instance = createInstance();
/*  42: 65 */   private static final Logger LOG = Logger.getLogger(SyntaxStyles.class.getName());
/*  43: 66 */   private static SyntaxStyle DEFAULT_STYLE = new SyntaxStyle(Color.BLACK, 0);
/*  44:    */   
/*  45:    */   private static SyntaxStyles createInstance()
/*  46:    */   {
/*  47: 76 */     SyntaxStyles syntaxstyles = new SyntaxStyles();
/*  48: 77 */     Properties styles = JarServiceProvider.readProperties(SyntaxStyles.class);
/*  49: 78 */     syntaxstyles.mergeStyles(styles);
/*  50: 79 */     return syntaxstyles;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static SyntaxStyles getInstance()
/*  54:    */   {
/*  55: 87 */     return instance;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static SyntaxStyles read(Configuration config)
/*  59:    */   {
/*  60: 91 */     SyntaxStyles ss = createInstance();
/*  61: 94 */     for (Configuration.StringKeyMatcher m : config.getKeys(STYLE_PATTERN))
/*  62:    */     {
/*  63: 95 */       String type = m.group1;
/*  64:    */       try
/*  65:    */       {
/*  66: 97 */         ss.put(TokenType.valueOf(type), new SyntaxStyle(m.value));
/*  67:    */       }
/*  68:    */       catch (IllegalArgumentException e)
/*  69:    */       {
/*  70: 99 */         Logger.getLogger(SyntaxStyles.class.getName()).warning(String.format("Invalid Token Type [%s] for Style of ", new Object[] { type }));
/*  71:    */       }
/*  72:    */     }
/*  73:103 */     return ss;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void put(TokenType type, SyntaxStyle style)
/*  77:    */   {
/*  78:107 */     if (this.styles == null) {
/*  79:108 */       this.styles = new HashMap();
/*  80:    */     }
/*  81:110 */     this.styles.put(type, style);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public SyntaxStyle getStyle(TokenType type)
/*  85:    */   {
/*  86:119 */     if ((this.styles != null) && (this.styles.containsKey(type))) {
/*  87:120 */       return (SyntaxStyle)this.styles.get(type);
/*  88:    */     }
/*  89:122 */     return DEFAULT_STYLE;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public int drawText(Segment segment, int x, int y, Graphics graphics, TabExpander e, Token token)
/*  93:    */   {
/*  94:140 */     SyntaxStyle s = getStyle(token.type);
/*  95:141 */     return s.drawText(segment, x, y, graphics, e, token.start);
/*  96:    */   }
/*  97:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.SyntaxStyles
 * JD-Core Version:    0.7.0.1
 */
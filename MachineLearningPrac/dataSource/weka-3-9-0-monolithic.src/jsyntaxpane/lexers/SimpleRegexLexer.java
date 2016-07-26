/*  1:   */ package jsyntaxpane.lexers;
/*  2:   */ 
/*  3:   */ import java.io.FileReader;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.util.HashMap;
/*  6:   */ import java.util.List;
/*  7:   */ import java.util.Map;
/*  8:   */ import java.util.Map.Entry;
/*  9:   */ import java.util.Properties;
/* 10:   */ import java.util.TreeSet;
/* 11:   */ import java.util.regex.Matcher;
/* 12:   */ import java.util.regex.Pattern;
/* 13:   */ import javax.swing.text.Segment;
/* 14:   */ import jsyntaxpane.Lexer;
/* 15:   */ import jsyntaxpane.Token;
/* 16:   */ import jsyntaxpane.TokenComparators;
/* 17:   */ import jsyntaxpane.TokenType;
/* 18:   */ 
/* 19:   */ public class SimpleRegexLexer
/* 20:   */   implements Lexer
/* 21:   */ {
/* 22:   */   public SimpleRegexLexer(Map props)
/* 23:   */   {
/* 24:57 */     putPatterns(props);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public SimpleRegexLexer(String propsLocation)
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:61 */     Properties props = new Properties();
/* 31:62 */     props.load(new FileReader(propsLocation));
/* 32:63 */     putPatterns(props);
/* 33:   */   }
/* 34:   */   
/* 35:   */   public void parse(Segment segment, int ofst, List<Token> tokens)
/* 36:   */   {
/* 37:68 */     TreeSet<Token> allMatches = new TreeSet(TokenComparators.LONGEST_FIRST);
/* 38:70 */     for (Map.Entry<TokenType, Pattern> e : this.patterns.entrySet())
/* 39:   */     {
/* 40:71 */       Matcher m = ((Pattern)e.getValue()).matcher(segment);
/* 41:72 */       while (m.find())
/* 42:   */       {
/* 43:73 */         Token t = new Token((TokenType)e.getKey(), m.start() + ofst, m.end() - m.start());
/* 44:74 */         allMatches.add(t);
/* 45:   */       }
/* 46:   */     }
/* 47:77 */     int end = -1;
/* 48:78 */     for (Token t : allMatches) {
/* 49:79 */       if (t.start > end)
/* 50:   */       {
/* 51:80 */         tokens.add(t);
/* 52:81 */         end = t.end();
/* 53:   */       }
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:85 */   Map<TokenType, Pattern> patterns = new HashMap();
/* 58:   */   
/* 59:   */   public SimpleRegexLexer putPattern(TokenType type, String regex)
/* 60:   */   {
/* 61:88 */     this.patterns.put(type, Pattern.compile(regex));
/* 62:89 */     return this;
/* 63:   */   }
/* 64:   */   
/* 65:   */   public SimpleRegexLexer putPatterns(Map props)
/* 66:   */   {
/* 67:93 */     for (Object key : props.keySet())
/* 68:   */     {
/* 69:94 */       TokenType t = TokenType.valueOf(key.toString());
/* 70:95 */       this.patterns.put(t, Pattern.compile(props.get(key).toString()));
/* 71:   */     }
/* 72:97 */     return this;
/* 73:   */   }
/* 74:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.SimpleRegexLexer
 * JD-Core Version:    0.7.0.1
 */
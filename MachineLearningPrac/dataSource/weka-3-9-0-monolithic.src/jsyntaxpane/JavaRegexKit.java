/*  1:   */ package jsyntaxpane;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import jsyntaxpane.lexers.SimpleRegexLexer;
/*  5:   */ 
/*  6:   */ public class JavaRegexKit
/*  7:   */   extends DefaultSyntaxKit
/*  8:   */ {
/*  9:   */   public JavaRegexKit()
/* 10:   */     throws IOException
/* 11:   */   {
/* 12:26 */     super(new SimpleRegexLexer("javaRegex.properties"));
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.JavaRegexKit
 * JD-Core Version:    0.7.0.1
 */
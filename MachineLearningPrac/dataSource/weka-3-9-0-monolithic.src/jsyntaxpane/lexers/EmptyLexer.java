package jsyntaxpane.lexers;

import java.util.List;
import javax.swing.text.Segment;
import jsyntaxpane.Lexer;
import jsyntaxpane.Token;

public class EmptyLexer
  implements Lexer
{
  public void parse(Segment segment, int ofst, List<Token> tokens) {}
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.EmptyLexer
 * JD-Core Version:    0.7.0.1
 */
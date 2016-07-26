package jsyntaxpane;

import java.util.List;
import javax.swing.text.Segment;

public abstract interface Lexer
{
  public abstract void parse(Segment paramSegment, int paramInt, List<Token> paramList);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.Lexer
 * JD-Core Version:    0.7.0.1
 */
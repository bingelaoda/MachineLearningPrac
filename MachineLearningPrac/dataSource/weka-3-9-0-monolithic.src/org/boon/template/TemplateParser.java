package org.boon.template;

import java.util.List;
import org.boon.template.support.Token;

public abstract interface TemplateParser
{
  public abstract void parse(String paramString);
  
  public abstract List<Token> getTokenList();
  
  public abstract void displayTokens(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.TemplateParser
 * JD-Core Version:    0.7.0.1
 */
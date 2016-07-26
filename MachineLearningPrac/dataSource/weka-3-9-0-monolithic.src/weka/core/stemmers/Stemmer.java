package weka.core.stemmers;

import java.io.Serializable;
import weka.core.RevisionHandler;

public abstract interface Stemmer
  extends Serializable, RevisionHandler
{
  public abstract String stem(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stemmers.Stemmer
 * JD-Core Version:    0.7.0.1
 */
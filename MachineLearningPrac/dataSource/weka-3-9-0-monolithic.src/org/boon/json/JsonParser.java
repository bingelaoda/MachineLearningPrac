package org.boon.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public abstract interface JsonParser
{
  public abstract Object parse(String paramString);
  
  public abstract Object parse(byte[] paramArrayOfByte);
  
  public abstract Object parse(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract Object parse(CharSequence paramCharSequence);
  
  public abstract Object parse(char[] paramArrayOfChar);
  
  public abstract Object parse(Reader paramReader);
  
  public abstract Object parse(InputStream paramInputStream);
  
  public abstract Object parse(InputStream paramInputStream, Charset paramCharset);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonParser
 * JD-Core Version:    0.7.0.1
 */
package org.boon.json;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;

public abstract interface ObjectMapper
{
  public abstract <T> T readValue(String paramString, Class<T> paramClass);
  
  public abstract <T> T readValue(File paramFile, Class<T> paramClass);
  
  public abstract <T> T readValue(byte[] paramArrayOfByte, Class<T> paramClass);
  
  public abstract <T> T readValue(char[] paramArrayOfChar, Class<T> paramClass);
  
  public abstract <T> T readValue(Reader paramReader, Class<T> paramClass);
  
  public abstract <T> T readValue(InputStream paramInputStream, Class<T> paramClass);
  
  public abstract <T extends Collection<C>, C> T readValue(String paramString, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(File paramFile, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(byte[] paramArrayOfByte, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(char[] paramArrayOfChar, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(Reader paramReader, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(InputStream paramInputStream, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(byte[] paramArrayOfByte, Charset paramCharset, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract <T extends Collection<C>, C> T readValue(InputStream paramInputStream, Charset paramCharset, Class<T> paramClass, Class<C> paramClass1);
  
  public abstract void writeValue(File paramFile, Object paramObject);
  
  public abstract void writeValue(OutputStream paramOutputStream, Object paramObject);
  
  public abstract void writeValue(Writer paramWriter, Object paramObject);
  
  public abstract String writeValueAsString(Object paramObject);
  
  public abstract char[] writeValueAsCharArray(Object paramObject);
  
  public abstract byte[] writeValueAsBytes(Object paramObject);
  
  public abstract byte[] writeValueAsBytes(Object paramObject, Charset paramCharset);
  
  public abstract JsonParserAndMapper parser();
  
  public abstract JsonSerializer serializer();
  
  public abstract String toJson(Object paramObject);
  
  public abstract void toJson(Object paramObject, Appendable paramAppendable);
  
  public abstract <T> T fromJson(String paramString, Class<T> paramClass);
  
  public abstract <T> T fromJson(byte[] paramArrayOfByte, Class<T> paramClass);
  
  public abstract <T> T fromJson(char[] paramArrayOfChar, Class<T> paramClass);
  
  public abstract <T> T fromJson(Reader paramReader, Class<T> paramClass);
  
  public abstract <T> T fromJson(InputStream paramInputStream, Class<T> paramClass);
  
  public abstract Object fromJson(String paramString);
  
  public abstract Object fromJson(Reader paramReader);
  
  public abstract Object fromJson(byte[] paramArrayOfByte);
  
  public abstract Object fromJson(char[] paramArrayOfChar);
  
  public abstract Object fromJson(InputStream paramInputStream);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.ObjectMapper
 * JD-Core Version:    0.7.0.1
 */
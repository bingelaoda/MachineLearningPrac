package org.boon.json;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract interface JsonParserAndMapper
  extends JsonParser
{
  public abstract Map<String, Object> parseMap(String paramString);
  
  public abstract Map<String, Object> parseMap(char[] paramArrayOfChar);
  
  public abstract Map<String, Object> parseMap(byte[] paramArrayOfByte);
  
  public abstract Map<String, Object> parseMap(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract Map<String, Object> parseMap(InputStream paramInputStream, Charset paramCharset);
  
  public abstract Map<String, Object> parseMap(CharSequence paramCharSequence);
  
  public abstract Map<String, Object> parseMap(InputStream paramInputStream);
  
  public abstract Map<String, Object> parseMap(Reader paramReader);
  
  public abstract Map<String, Object> parseMapFromFile(String paramString);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, String paramString);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, InputStream paramInputStream);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, Reader paramReader);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, InputStream paramInputStream, Charset paramCharset);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, byte[] paramArrayOfByte);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, char[] paramArrayOfChar);
  
  public abstract <T> List<T> parseList(Class<T> paramClass, CharSequence paramCharSequence);
  
  public abstract <T> List<T> parseListFromFile(Class<T> paramClass, String paramString);
  
  public abstract <T> T parse(Class<T> paramClass, String paramString);
  
  public abstract <T> T parse(Class<T> paramClass, byte[] paramArrayOfByte);
  
  public abstract <T> T parse(Class<T> paramClass, byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract <T> T parse(Class<T> paramClass, CharSequence paramCharSequence);
  
  public abstract <T> T parse(Class<T> paramClass, char[] paramArrayOfChar);
  
  public abstract <T> T parse(Class<T> paramClass, Reader paramReader);
  
  public abstract <T> T parse(Class<T> paramClass, InputStream paramInputStream);
  
  public abstract <T> T parse(Class<T> paramClass, InputStream paramInputStream, Charset paramCharset);
  
  public abstract <T> T parseDirect(Class<T> paramClass, byte[] paramArrayOfByte);
  
  public abstract <T> T parseAsStream(Class<T> paramClass, byte[] paramArrayOfByte);
  
  public abstract <T> T parseFile(Class<T> paramClass, String paramString);
  
  public abstract int parseInt(String paramString);
  
  public abstract int parseInt(InputStream paramInputStream);
  
  public abstract int parseInt(InputStream paramInputStream, Charset paramCharset);
  
  public abstract int parseInt(byte[] paramArrayOfByte);
  
  public abstract int parseInt(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract int parseInt(char[] paramArrayOfChar);
  
  public abstract int parseInt(CharSequence paramCharSequence);
  
  public abstract int parseIntFromFile(String paramString);
  
  public abstract long parseLong(String paramString);
  
  public abstract long parseLong(InputStream paramInputStream);
  
  public abstract long parseLong(InputStream paramInputStream, Charset paramCharset);
  
  public abstract long parseLong(byte[] paramArrayOfByte);
  
  public abstract long parseLong(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract long parseLong(char[] paramArrayOfChar);
  
  public abstract long parseLong(CharSequence paramCharSequence);
  
  public abstract long parseLongFromFile(String paramString);
  
  public abstract String parseString(String paramString);
  
  public abstract String parseString(InputStream paramInputStream);
  
  public abstract String parseString(InputStream paramInputStream, Charset paramCharset);
  
  public abstract String parseString(byte[] paramArrayOfByte);
  
  public abstract String parseString(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract String parseString(char[] paramArrayOfChar);
  
  public abstract String parseString(CharSequence paramCharSequence);
  
  public abstract String parseStringFromFile(String paramString);
  
  public abstract double parseDouble(String paramString);
  
  public abstract double parseDouble(InputStream paramInputStream);
  
  public abstract double parseDouble(byte[] paramArrayOfByte);
  
  public abstract double parseDouble(char[] paramArrayOfChar);
  
  public abstract double parseDouble(CharSequence paramCharSequence);
  
  public abstract double parseDouble(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract double parseDouble(InputStream paramInputStream, Charset paramCharset);
  
  public abstract double parseDoubleFromFile(String paramString);
  
  public abstract float parseFloat(String paramString);
  
  public abstract float parseFloat(InputStream paramInputStream);
  
  public abstract float parseFloat(byte[] paramArrayOfByte);
  
  public abstract float parseFloat(char[] paramArrayOfChar);
  
  public abstract float parseFloat(CharSequence paramCharSequence);
  
  public abstract float parseFloat(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract float parseFloat(InputStream paramInputStream, Charset paramCharset);
  
  public abstract float parseFloatFromFile(String paramString);
  
  public abstract BigDecimal parseBigDecimal(String paramString);
  
  public abstract BigDecimal parseBigDecimal(InputStream paramInputStream);
  
  public abstract BigDecimal parseBigDecimal(byte[] paramArrayOfByte);
  
  public abstract BigDecimal parseBigDecimal(char[] paramArrayOfChar);
  
  public abstract BigDecimal parseBigDecimal(CharSequence paramCharSequence);
  
  public abstract BigDecimal parseBigDecimal(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract BigDecimal parseBigDecimal(InputStream paramInputStream, Charset paramCharset);
  
  public abstract BigDecimal parseBigDecimalFromFile(String paramString);
  
  public abstract BigInteger parseBigInteger(String paramString);
  
  public abstract BigInteger parseBigInteger(InputStream paramInputStream);
  
  public abstract BigInteger parseBigInteger(byte[] paramArrayOfByte);
  
  public abstract BigInteger parseBigInteger(char[] paramArrayOfChar);
  
  public abstract BigInteger parseBigInteger(CharSequence paramCharSequence);
  
  public abstract BigInteger parseBigInteger(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract BigInteger parseBigInteger(InputStream paramInputStream, Charset paramCharset);
  
  public abstract BigInteger parseBigIntegerFile(String paramString);
  
  public abstract Date parseDate(String paramString);
  
  public abstract Date parseDate(InputStream paramInputStream);
  
  public abstract Date parseDate(InputStream paramInputStream, Charset paramCharset);
  
  public abstract Date parseDate(byte[] paramArrayOfByte);
  
  public abstract Date parseDate(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract Date parseDate(char[] paramArrayOfChar);
  
  public abstract Date parseDate(CharSequence paramCharSequence);
  
  public abstract Date parseDateFromFile(String paramString);
  
  public abstract short parseShort(String paramString);
  
  public abstract byte parseByte(String paramString);
  
  public abstract char parseChar(String paramString);
  
  public abstract <T extends Enum> T parseEnum(Class<T> paramClass, String paramString);
  
  public abstract char[] parseCharArray(String paramString);
  
  public abstract byte[] parseByteArray(String paramString);
  
  public abstract short[] parseShortArray(String paramString);
  
  public abstract int[] parseIntArray(String paramString);
  
  public abstract float[] parseFloatArray(String paramString);
  
  public abstract double[] parseDoubleArray(String paramString);
  
  public abstract long[] parseLongArray(String paramString);
  
  public abstract Object parse(String paramString);
  
  public abstract Object parse(byte[] paramArrayOfByte);
  
  public abstract Object parse(byte[] paramArrayOfByte, Charset paramCharset);
  
  public abstract Object parse(CharSequence paramCharSequence);
  
  public abstract Object parse(char[] paramArrayOfChar);
  
  public abstract Object parse(Reader paramReader);
  
  public abstract Object parse(InputStream paramInputStream);
  
  public abstract Object parse(InputStream paramInputStream, Charset paramCharset);
  
  public abstract Object parseDirect(byte[] paramArrayOfByte);
  
  public abstract Object parseAsStream(byte[] paramArrayOfByte);
  
  public abstract Object parseFile(String paramString);
  
  public abstract void close();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonParserAndMapper
 * JD-Core Version:    0.7.0.1
 */
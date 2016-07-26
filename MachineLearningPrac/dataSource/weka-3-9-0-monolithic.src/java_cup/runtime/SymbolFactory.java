package java_cup.runtime;

public abstract interface SymbolFactory
{
  public abstract Symbol newSymbol(String paramString, int paramInt, Symbol paramSymbol1, Symbol paramSymbol2, Object paramObject);
  
  public abstract Symbol newSymbol(String paramString, int paramInt, Symbol paramSymbol1, Symbol paramSymbol2);
  
  public abstract Symbol newSymbol(String paramString, int paramInt, Symbol paramSymbol, Object paramObject);
  
  public abstract Symbol newSymbol(String paramString, int paramInt, Object paramObject);
  
  public abstract Symbol newSymbol(String paramString, int paramInt);
  
  public abstract Symbol startSymbol(String paramString, int paramInt1, int paramInt2);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     java_cup.runtime.SymbolFactory
 * JD-Core Version:    0.7.0.1
 */
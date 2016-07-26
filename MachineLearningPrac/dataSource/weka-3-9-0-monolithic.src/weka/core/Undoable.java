package weka.core;

public abstract interface Undoable
{
  public abstract boolean isUndoEnabled();
  
  public abstract void setUndoEnabled(boolean paramBoolean);
  
  public abstract void clearUndo();
  
  public abstract boolean canUndo();
  
  public abstract void undo();
  
  public abstract void addUndoPoint();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Undoable
 * JD-Core Version:    0.7.0.1
 */
package weka.gui.graphvisualizer;

import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public abstract interface LayoutEngine
{
  public abstract void layoutGraph();
  
  public abstract void setNodesEdges(ArrayList<GraphNode> paramArrayList, ArrayList<GraphEdge> paramArrayList1);
  
  public abstract void setNodeSize(int paramInt1, int paramInt2);
  
  public abstract ArrayList<GraphNode> getNodes();
  
  public abstract JPanel getControlPanel();
  
  public abstract JProgressBar getProgressBar();
  
  public abstract void addLayoutCompleteEventListener(LayoutCompleteEventListener paramLayoutCompleteEventListener);
  
  public abstract void removeLayoutCompleteEventListener(LayoutCompleteEventListener paramLayoutCompleteEventListener);
  
  public abstract void fireLayoutCompleteEvent(LayoutCompleteEvent paramLayoutCompleteEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.LayoutEngine
 * JD-Core Version:    0.7.0.1
 */
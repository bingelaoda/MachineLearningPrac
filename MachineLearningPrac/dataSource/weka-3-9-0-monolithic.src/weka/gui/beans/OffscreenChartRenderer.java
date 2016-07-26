package weka.gui.beans;

import java.awt.image.BufferedImage;
import java.util.List;
import weka.core.Instances;

public abstract interface OffscreenChartRenderer
{
  public abstract String rendererName();
  
  public abstract String optionsTipTextHTML();
  
  public abstract BufferedImage renderXYLineChart(int paramInt1, int paramInt2, List<Instances> paramList, String paramString1, String paramString2, List<String> paramList1)
    throws Exception;
  
  public abstract BufferedImage renderXYScatterPlot(int paramInt1, int paramInt2, List<Instances> paramList, String paramString1, String paramString2, List<String> paramList1)
    throws Exception;
  
  public abstract BufferedImage renderHistogram(int paramInt1, int paramInt2, List<Instances> paramList, String paramString, List<String> paramList1)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.OffscreenChartRenderer
 * JD-Core Version:    0.7.0.1
 */
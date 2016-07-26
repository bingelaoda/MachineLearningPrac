package weka.experiment;

import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface Compute
  extends Remote
{
  public abstract Object executeTask(Task paramTask)
    throws RemoteException;
  
  public abstract Object checkStatus(Object paramObject)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.Compute
 * JD-Core Version:    0.7.0.1
 */
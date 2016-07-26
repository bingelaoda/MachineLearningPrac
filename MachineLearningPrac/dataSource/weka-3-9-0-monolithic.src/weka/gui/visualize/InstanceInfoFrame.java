/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.util.Vector;
/*   7:    */ import javax.swing.JFrame;
/*   8:    */ import javax.swing.JScrollPane;
/*   9:    */ import javax.swing.JTextArea;
/*  10:    */ import weka.core.Instances;
/*  11:    */ 
/*  12:    */ public class InstanceInfoFrame
/*  13:    */   extends JFrame
/*  14:    */   implements InstanceInfo
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 4684184733677263009L;
/*  17:    */   protected Vector<Instances> m_Data;
/*  18:    */   protected JTextArea m_TextInfo;
/*  19:    */   
/*  20:    */   public InstanceInfoFrame()
/*  21:    */   {
/*  22: 56 */     super("Weka: Instance info");
/*  23:    */     
/*  24: 58 */     initialize();
/*  25: 59 */     initGUI();
/*  26: 60 */     initFinished();
/*  27:    */   }
/*  28:    */   
/*  29:    */   protected void initialize()
/*  30:    */   {
/*  31: 67 */     this.m_Data = new Vector();
/*  32:    */   }
/*  33:    */   
/*  34:    */   protected void initGUI()
/*  35:    */   {
/*  36: 74 */     getContentPane().setLayout(new BorderLayout());
/*  37:    */     
/*  38: 76 */     this.m_TextInfo = new JTextArea();
/*  39: 77 */     this.m_TextInfo.setEditable(false);
/*  40: 78 */     this.m_TextInfo.setFont(new Font("Monospaced", 0, 12));
/*  41: 79 */     getContentPane().add(new JScrollPane(this.m_TextInfo), "Center");
/*  42:    */     
/*  43: 81 */     pack();
/*  44: 82 */     setSize(320, 400);
/*  45:    */   }
/*  46:    */   
/*  47:    */   protected void initFinished() {}
/*  48:    */   
/*  49:    */   public void setInfoText(String text)
/*  50:    */   {
/*  51: 97 */     this.m_TextInfo.setText(text);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getInfoText()
/*  55:    */   {
/*  56:106 */     return this.m_TextInfo.getText();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setInfoData(Vector<Instances> data)
/*  60:    */   {
/*  61:115 */     this.m_Data = new Vector();
/*  62:116 */     if (data != null) {
/*  63:117 */       this.m_Data.addAll(data);
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Vector<Instances> getInfoData()
/*  68:    */   {
/*  69:126 */     return this.m_Data;
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.InstanceInfoFrame
 * JD-Core Version:    0.7.0.1
 */
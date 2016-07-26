/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.ObjectOutputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class SerializedInstancesSaver
/*  11:    */   extends AbstractFileSaver
/*  12:    */   implements BatchConverter
/*  13:    */ {
/*  14:    */   static final long serialVersionUID = -7717010648500658872L;
/*  15:    */   protected ObjectOutputStream m_objectstream;
/*  16:    */   
/*  17:    */   public SerializedInstancesSaver()
/*  18:    */   {
/*  19: 65 */     resetOptions();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public String globalInfo()
/*  23:    */   {
/*  24: 75 */     return "Serializes the instances to a file with extension bsi.";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String getFileDescription()
/*  28:    */   {
/*  29: 84 */     return "Binary serialized instances";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void resetOptions()
/*  33:    */   {
/*  34: 92 */     super.resetOptions();
/*  35: 93 */     setFileExtension(".bsi");
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Capabilities getCapabilities()
/*  39:    */   {
/*  40:103 */     Capabilities result = super.getCapabilities();
/*  41:    */     
/*  42:    */ 
/*  43:106 */     result.enableAllAttributes();
/*  44:107 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  45:    */     
/*  46:    */ 
/*  47:110 */     result.enableAllClasses();
/*  48:111 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  49:112 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  50:    */     
/*  51:114 */     return result;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void resetWriter()
/*  55:    */   {
/*  56:121 */     super.resetWriter();
/*  57:    */     
/*  58:123 */     this.m_objectstream = null;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setDestination(OutputStream output)
/*  62:    */     throws IOException
/*  63:    */   {
/*  64:133 */     super.setDestination(output);
/*  65:    */     
/*  66:135 */     this.m_objectstream = new ObjectOutputStream(output);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void writeBatch()
/*  70:    */     throws IOException
/*  71:    */   {
/*  72:144 */     if (getRetrieval() == 2) {
/*  73:145 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/*  74:    */     }
/*  75:147 */     if (getInstances() == null) {
/*  76:148 */       throw new IOException("No instances to save");
/*  77:    */     }
/*  78:150 */     setRetrieval(1);
/*  79:152 */     if (this.m_objectstream == null) {
/*  80:153 */       throw new IOException("No output for serialization.");
/*  81:    */     }
/*  82:155 */     setWriteMode(0);
/*  83:156 */     this.m_objectstream.writeObject(getInstances());
/*  84:157 */     this.m_objectstream.flush();
/*  85:158 */     this.m_objectstream.close();
/*  86:159 */     setWriteMode(1);
/*  87:160 */     resetWriter();
/*  88:161 */     setWriteMode(2);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getRevision()
/*  92:    */   {
/*  93:170 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static void main(String[] args)
/*  97:    */   {
/*  98:179 */     runFileSaver(new SerializedInstancesSaver(), args);
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.SerializedInstancesSaver
 * JD-Core Version:    0.7.0.1
 */
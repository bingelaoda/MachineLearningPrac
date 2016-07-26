/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.ByteArrayInputStream;
/*   6:    */ import java.io.ByteArrayOutputStream;
/*   7:    */ import java.io.ObjectInputStream;
/*   8:    */ import java.io.ObjectOutputStream;
/*   9:    */ import java.io.OutputStream;
/*  10:    */ import java.io.Serializable;
/*  11:    */ import java.util.zip.GZIPInputStream;
/*  12:    */ import java.util.zip.GZIPOutputStream;
/*  13:    */ import weka.core.scripting.Jython;
/*  14:    */ import weka.core.scripting.JythonSerializableObject;
/*  15:    */ 
/*  16:    */ public class SerializedObject
/*  17:    */   implements Serializable, RevisionHandler
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 6635502953928860434L;
/*  20:    */   private byte[] m_storedObjectArray;
/*  21:    */   private boolean m_isCompressed;
/*  22:    */   private boolean m_isJython;
/*  23:    */   
/*  24:    */   public SerializedObject(Object toStore)
/*  25:    */     throws Exception
/*  26:    */   {
/*  27: 69 */     this(toStore, false);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public SerializedObject(Object toStore, boolean compress)
/*  31:    */     throws Exception
/*  32:    */   {
/*  33: 81 */     ByteArrayOutputStream ostream = new ByteArrayOutputStream();
/*  34: 82 */     OutputStream os = ostream;
/*  35:    */     ObjectOutputStream p;
/*  36:    */     ObjectOutputStream p;
/*  37: 84 */     if (!compress) {
/*  38: 85 */       p = new ObjectOutputStream(new BufferedOutputStream(os));
/*  39:    */     } else {
/*  40: 87 */       p = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(os)));
/*  41:    */     }
/*  42: 88 */     p.writeObject(toStore);
/*  43: 89 */     p.flush();
/*  44: 90 */     p.close();
/*  45: 91 */     this.m_storedObjectArray = ostream.toByteArray();
/*  46:    */     
/*  47: 93 */     this.m_isCompressed = compress;
/*  48: 94 */     this.m_isJython = (toStore instanceof JythonSerializableObject);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public final boolean equals(Object compareTo)
/*  52:    */   {
/*  53:105 */     if (compareTo == null) {
/*  54:105 */       return false;
/*  55:    */     }
/*  56:106 */     if (!compareTo.getClass().equals(getClass())) {
/*  57:106 */       return false;
/*  58:    */     }
/*  59:107 */     byte[] compareArray = ((SerializedObject)compareTo).m_storedObjectArray;
/*  60:108 */     if (compareArray.length != this.m_storedObjectArray.length) {
/*  61:108 */       return false;
/*  62:    */     }
/*  63:109 */     for (int i = 0; i < compareArray.length; i++) {
/*  64:110 */       if (compareArray[i] != this.m_storedObjectArray[i]) {
/*  65:110 */         return false;
/*  66:    */       }
/*  67:    */     }
/*  68:112 */     return true;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public int hashCode()
/*  72:    */   {
/*  73:122 */     return this.m_storedObjectArray.length;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Object getObject()
/*  77:    */   {
/*  78:    */     try
/*  79:    */     {
/*  80:137 */       ByteArrayInputStream istream = new ByteArrayInputStream(this.m_storedObjectArray);
/*  81:    */       
/*  82:139 */       Object toReturn = null;
/*  83:140 */       if (this.m_isJython)
/*  84:    */       {
/*  85:141 */         if (!this.m_isCompressed) {
/*  86:142 */           toReturn = Jython.deserialize(new BufferedInputStream(istream));
/*  87:    */         } else {
/*  88:144 */           toReturn = Jython.deserialize(new BufferedInputStream(new GZIPInputStream(istream)));
/*  89:    */         }
/*  90:    */       }
/*  91:    */       else
/*  92:    */       {
/*  93:    */         ObjectInputStream p;
/*  94:    */         ObjectInputStream p;
/*  95:147 */         if (!this.m_isCompressed) {
/*  96:148 */           p = new ObjectInputStream(new BufferedInputStream(istream));
/*  97:    */         } else {
/*  98:150 */           p = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(istream)));
/*  99:    */         }
/* 100:151 */         toReturn = p.readObject();
/* 101:    */       }
/* 102:153 */       istream.close();
/* 103:154 */       return toReturn;
/* 104:    */     }
/* 105:    */     catch (Exception e)
/* 106:    */     {
/* 107:156 */       e.printStackTrace();
/* 108:    */     }
/* 109:157 */     return null;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String getRevision()
/* 113:    */   {
/* 114:167 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SerializedObject
 * JD-Core Version:    0.7.0.1
 */
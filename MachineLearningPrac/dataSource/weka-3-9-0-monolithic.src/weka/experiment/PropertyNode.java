/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.beans.IntrospectionException;
/*   4:    */ import java.beans.PropertyDescriptor;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import java.io.ObjectOutputStream;
/*   8:    */ import java.io.Serializable;
/*   9:    */ import java.lang.reflect.Method;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ 
/*  13:    */ public class PropertyNode
/*  14:    */   implements Serializable, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -8718165742572631384L;
/*  17:    */   public Object value;
/*  18:    */   public Class<?> parentClass;
/*  19:    */   public PropertyDescriptor property;
/*  20:    */   
/*  21:    */   public PropertyNode(Object pValue)
/*  22:    */   {
/*  23: 60 */     this(pValue, null, null);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public PropertyNode(Object pValue, PropertyDescriptor prop, Class<?> pClass)
/*  27:    */   {
/*  28: 72 */     this.value = pValue;
/*  29: 73 */     this.property = prop;
/*  30: 74 */     this.parentClass = pClass;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String toString()
/*  34:    */   {
/*  35: 85 */     if (this.property == null) {
/*  36: 86 */       return "Available properties";
/*  37:    */     }
/*  38: 88 */     return this.property.getDisplayName();
/*  39:    */   }
/*  40:    */   
/*  41:    */   private void writeObject(ObjectOutputStream out)
/*  42:    */     throws IOException
/*  43:    */   {
/*  44:    */     try
/*  45:    */     {
/*  46: 97 */       out.writeObject(this.value);
/*  47:    */     }
/*  48:    */     catch (Exception ex)
/*  49:    */     {
/*  50: 99 */       throw new IOException("Can't serialize object: " + ex.getMessage());
/*  51:    */     }
/*  52:101 */     out.writeObject(this.parentClass);
/*  53:102 */     out.writeObject(this.property.getDisplayName());
/*  54:103 */     out.writeObject(this.property.getReadMethod().getName());
/*  55:104 */     out.writeObject(this.property.getWriteMethod().getName());
/*  56:    */   }
/*  57:    */   
/*  58:    */   private void readObject(ObjectInputStream in)
/*  59:    */     throws IOException, ClassNotFoundException
/*  60:    */   {
/*  61:110 */     this.value = in.readObject();
/*  62:111 */     this.parentClass = ((Class)in.readObject());
/*  63:112 */     String name = (String)in.readObject();
/*  64:113 */     String getter = (String)in.readObject();
/*  65:114 */     String setter = (String)in.readObject();
/*  66:    */     try
/*  67:    */     {
/*  68:121 */       this.property = new PropertyDescriptor(name, this.parentClass, getter, setter);
/*  69:    */     }
/*  70:    */     catch (IntrospectionException ex)
/*  71:    */     {
/*  72:123 */       throw new ClassNotFoundException("Couldn't create property descriptor: " + this.parentClass.getName() + "::" + name);
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getRevision()
/*  77:    */   {
/*  78:135 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  79:    */   }
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.PropertyNode
 * JD-Core Version:    0.7.0.1
 */
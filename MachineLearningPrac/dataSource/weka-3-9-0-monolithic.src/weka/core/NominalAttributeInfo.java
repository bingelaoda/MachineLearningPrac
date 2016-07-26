/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Hashtable;
/*  6:   */ import java.util.List;
/*  7:   */ 
/*  8:   */ public class NominalAttributeInfo
/*  9:   */   implements AttributeInfo
/* 10:   */ {
/* 11:   */   protected ArrayList<Object> m_Values;
/* 12:   */   protected Hashtable<Object, Integer> m_Hashtable;
/* 13:   */   
/* 14:   */   public NominalAttributeInfo(List<String> attributeValues, String attributeName)
/* 15:   */   {
/* 16:43 */     if (attributeValues == null)
/* 17:   */     {
/* 18:44 */       this.m_Values = new ArrayList();
/* 19:45 */       this.m_Hashtable = new Hashtable();
/* 20:   */     }
/* 21:   */     else
/* 22:   */     {
/* 23:47 */       this.m_Values = new ArrayList(attributeValues.size());
/* 24:48 */       this.m_Hashtable = new Hashtable(attributeValues.size());
/* 25:49 */       for (int i = 0; i < attributeValues.size(); i++)
/* 26:   */       {
/* 27:50 */         Object store = attributeValues.get(i);
/* 28:51 */         if (((String)store).length() > 200) {
/* 29:   */           try
/* 30:   */           {
/* 31:53 */             store = new SerializedObject(attributeValues.get(i), true);
/* 32:   */           }
/* 33:   */           catch (Exception ex)
/* 34:   */           {
/* 35:55 */             System.err.println("Couldn't compress nominal attribute value - storing uncompressed.");
/* 36:   */           }
/* 37:   */         }
/* 38:59 */         if (this.m_Hashtable.containsKey(store)) {
/* 39:60 */           throw new IllegalArgumentException("A nominal attribute (" + attributeName + ") cannot" + " have duplicate labels (" + store + ").");
/* 40:   */         }
/* 41:64 */         this.m_Values.add(store);
/* 42:65 */         this.m_Hashtable.put(store, new Integer(i));
/* 43:   */       }
/* 44:   */     }
/* 45:   */   }
/* 46:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.NominalAttributeInfo
 * JD-Core Version:    0.7.0.1
 */
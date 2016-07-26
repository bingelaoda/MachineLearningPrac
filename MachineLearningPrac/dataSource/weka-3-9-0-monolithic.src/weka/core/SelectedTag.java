/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.HashSet;
/*   5:    */ 
/*   6:    */ public class SelectedTag
/*   7:    */   implements RevisionHandler, Serializable
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 6947341624626504975L;
/*  10:    */   protected int m_Selected;
/*  11:    */   protected Tag[] m_Tags;
/*  12:    */   
/*  13:    */   public SelectedTag(int tagID, Tag[] tags)
/*  14:    */   {
/*  15: 57 */     HashSet<Integer> ID = new HashSet();
/*  16: 58 */     HashSet<String> IDStr = new HashSet();
/*  17: 59 */     for (int i = 0; i < tags.length; i++)
/*  18:    */     {
/*  19: 60 */       Integer newID = new Integer(tags[i].getID());
/*  20: 61 */       if (!ID.contains(newID)) {
/*  21: 62 */         ID.add(newID);
/*  22:    */       } else {
/*  23: 64 */         throw new IllegalArgumentException("The IDs are not unique: " + newID + "!");
/*  24:    */       }
/*  25: 66 */       String IDstring = tags[i].getIDStr();
/*  26: 67 */       if (!IDStr.contains(IDstring)) {
/*  27: 68 */         IDStr.add(IDstring);
/*  28:    */       } else {
/*  29: 70 */         throw new IllegalArgumentException("The ID strings are not unique: " + IDstring + "!");
/*  30:    */       }
/*  31:    */     }
/*  32: 74 */     for (int i = 0; i < tags.length; i++) {
/*  33: 75 */       if (tags[i].getID() == tagID)
/*  34:    */       {
/*  35: 76 */         this.m_Selected = i;
/*  36: 77 */         this.m_Tags = tags;
/*  37: 78 */         return;
/*  38:    */       }
/*  39:    */     }
/*  40: 82 */     throw new IllegalArgumentException("Selected tag is not valid");
/*  41:    */   }
/*  42:    */   
/*  43:    */   public SelectedTag(String tagText, Tag[] tags)
/*  44:    */   {
/*  45: 94 */     for (int i = 0; i < tags.length; i++) {
/*  46: 95 */       if ((tags[i].getReadable().equalsIgnoreCase(tagText)) || (tags[i].getIDStr().equalsIgnoreCase(tagText)))
/*  47:    */       {
/*  48: 97 */         this.m_Selected = i;
/*  49: 98 */         this.m_Tags = tags;
/*  50: 99 */         return;
/*  51:    */       }
/*  52:    */     }
/*  53:102 */     throw new IllegalArgumentException("Selected tag is not valid");
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean equals(Object o)
/*  57:    */   {
/*  58:112 */     if ((o == null) || (!o.getClass().equals(getClass()))) {
/*  59:113 */       return false;
/*  60:    */     }
/*  61:115 */     SelectedTag s = (SelectedTag)o;
/*  62:116 */     if ((s.getTags() == this.m_Tags) && (s.getSelectedTag() == this.m_Tags[this.m_Selected])) {
/*  63:118 */       return true;
/*  64:    */     }
/*  65:120 */     return false;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Tag getSelectedTag()
/*  69:    */   {
/*  70:131 */     return this.m_Tags[this.m_Selected];
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Tag[] getTags()
/*  74:    */   {
/*  75:140 */     return this.m_Tags;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String toString()
/*  79:    */   {
/*  80:149 */     return getSelectedTag().toString();
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getRevision()
/*  84:    */   {
/*  85:158 */     return RevisionUtils.extract("$Revision: 11718 $");
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SelectedTag
 * JD-Core Version:    0.7.0.1
 */
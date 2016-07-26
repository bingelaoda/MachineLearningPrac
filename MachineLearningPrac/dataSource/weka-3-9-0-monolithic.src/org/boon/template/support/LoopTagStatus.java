/*  1:   */ package org.boon.template.support;
/*  2:   */ 
/*  3:   */ public class LoopTagStatus
/*  4:   */ {
/*  5:   */   int count;
/*  6:   */   Object current;
/*  7:   */   Integer begin;
/*  8:   */   Integer end;
/*  9:   */   Integer step;
/* 10:   */   int index;
/* 11:   */   
/* 12:   */   public int getCount()
/* 13:   */   {
/* 14:15 */     return this.count;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void setCount(int count)
/* 18:   */   {
/* 19:19 */     this.count = count;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public Object getCurrent()
/* 23:   */   {
/* 24:23 */     return this.current;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void setCurrent(Object current)
/* 28:   */   {
/* 29:27 */     this.current = current;
/* 30:   */   }
/* 31:   */   
/* 32:   */   public Integer getEnd()
/* 33:   */   {
/* 34:31 */     return this.end;
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void setEnd(Integer end)
/* 38:   */   {
/* 39:35 */     this.end = end;
/* 40:   */   }
/* 41:   */   
/* 42:   */   public Integer getStep()
/* 43:   */   {
/* 44:39 */     return this.step;
/* 45:   */   }
/* 46:   */   
/* 47:   */   public void setStep(Integer step)
/* 48:   */   {
/* 49:43 */     this.step = step;
/* 50:   */   }
/* 51:   */   
/* 52:   */   public boolean isFirst()
/* 53:   */   {
/* 54:47 */     boolean first = false;
/* 55:49 */     if ((this.begin == null) && (this.index == 0)) {
/* 56:50 */       first = true;
/* 57:51 */     } else if ((this.begin != null) && (this.index == this.begin.intValue())) {
/* 58:52 */       first = true;
/* 59:   */     }
/* 60:54 */     return first;
/* 61:   */   }
/* 62:   */   
/* 63:   */   public int getIndex()
/* 64:   */   {
/* 65:60 */     return this.index;
/* 66:   */   }
/* 67:   */   
/* 68:   */   public void setIndex(int index)
/* 69:   */   {
/* 70:64 */     this.index = index;
/* 71:   */   }
/* 72:   */   
/* 73:   */   public boolean isLast()
/* 74:   */   {
/* 75:70 */     boolean last = false;
/* 76:72 */     if ((this.end == null) && (this.index + 1 == this.count)) {
/* 77:73 */       last = true;
/* 78:74 */     } else if ((this.end != null) && (this.index + 1 == this.end.intValue())) {
/* 79:75 */       last = true;
/* 80:   */     }
/* 81:77 */     return last;
/* 82:   */   }
/* 83:   */   
/* 84:   */   public void setBegin(Integer begin)
/* 85:   */   {
/* 86:81 */     this.begin = begin;
/* 87:   */   }
/* 88:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.support.LoopTagStatus
 * JD-Core Version:    0.7.0.1
 */
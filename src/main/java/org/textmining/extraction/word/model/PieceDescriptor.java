/*
 * Textmining.org text extractors
 * 
 * Copyright (C) 2008 Benryan Software Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */      


package org.textmining.extraction.word.model;

import org.apache.poi.util.LittleEndian;

public class PieceDescriptor
{
  short descriptor;
  int fc;
  short prm;
  boolean unicode;


  public PieceDescriptor(byte[] buf, int offset)
  {
    descriptor = LittleEndian.getShort(buf, offset);
    offset += LittleEndian.SHORT_SIZE;
    fc = LittleEndian.getInt(buf, offset);
    offset += LittleEndian.INT_SIZE;
    prm = LittleEndian.getShort(buf, offset);

    // see if this piece uses unicode.
    if ((fc & 0x40000000) == 0)
    {
        unicode = true;
    }
    else
    {
        unicode = false;
        fc &= ~(0x40000000);//gives me FC in doc stream
        fc /= 2;
    }

  }
  public PieceDescriptor()
  {
    
  }
  public int getFilePosition()
  {
    return fc;
  }

  public void setFilePosition(int pos)
  {
    fc = pos;
  }

  public boolean isUnicode()
  {
    return unicode;
  }

  protected byte[] toByteArray()
  {
    // set up the fc
    int tempFc = fc;
    if (!unicode)
    {
      tempFc *= 2;
      tempFc |= (0x40000000);
    }

    int offset = 0;
    byte[] buf = new byte[8];
    LittleEndian.putShort(buf, offset, descriptor);
    offset += LittleEndian.SHORT_SIZE;
    LittleEndian.putInt(buf, offset, tempFc);
    offset += LittleEndian.INT_SIZE;
    LittleEndian.putShort(buf, offset, prm);

    return buf;

  }

  public static int getSizeInBytes()
  {
    return 8;
  }

  public boolean equals(Object o)
  {
    PieceDescriptor pd = (PieceDescriptor)o;

    return descriptor == pd.descriptor && prm == pd.prm && unicode == pd.unicode;
  }
  public void setUnicode(boolean b)
  {
    unicode = b;
    
  }
}

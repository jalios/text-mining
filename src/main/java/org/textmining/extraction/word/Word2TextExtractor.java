package org.textmining.extraction.word;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.LittleEndian;

public class Word2TextExtractor
  extends Word6TextExtractor
{
  public Word2TextExtractor(InputStream in) throws IOException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int read = -1;
    byte[] buf = new byte[4096];
    
    read = in.read(buf);
    while (read != -1)
    {
      out.write(buf, 0, read);
      read = in.read(buf);
    }
    _header = out.toByteArray();
    int info = LittleEndian.getShort(_header, 0xa);
    _fastSave = (info & 0x4) != 0;
  }
  
  protected int getChpTableSize()
  {
    return LittleEndian.getShort(_header, 0xa4);
  }
  protected int getChpTableOffset()
  {
    return LittleEndian.getInt(_header, 0xa0);
  }
  

  protected int getComplexOffset()
  {
    return LittleEndian.getInt(_header, 0x11e);
  }
  
}

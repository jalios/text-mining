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
package org.textmining.extraction.excel;

public class Record
{
  public final static short BOF_BIFF2 = 0x9;
  public final static short BOF_BIFF3 = 0x209;
  public final static short BOF_BIFF4 = 0x409;
  public final static short BOF_BIFF5678 = 0x809;
  //public final static short 
  public final static short SST_RECORD = 0xfc;
  
  public final static short EOF = 0xa;
  public final static short BOUNDSHEET = 0x85;
  public final static short DEFAULTROWHEIGHT = 0x255;
  public final static short DEFAULTCOLWIDTH = 0x55;
  public final static short DIMENSIONS = 0x200;
  public final static short INDEX = 0x20b;
  public final static short DBCELL = 0xd7;
  public final static short ROW = 0x208;
  
  
  
  int _type;
  int _size;
  int _offset;
  
  public Record (int type, int size, int offset)
  {
    _type = type;
    _size = size;
    _offset = offset;
  }
  
  public int getType()
  {
    return _type;
  }
  
  public int getSize()
  {
    return _size;
  }

  public int getOffset()
  {
    return _offset;
  }
}

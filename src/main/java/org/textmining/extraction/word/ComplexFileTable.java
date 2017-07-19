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
package org.textmining.extraction.word;

import java.io.IOException;

import org.apache.poi.util.LittleEndian;
import org.textmining.extraction.word.model.TextPieceTable;


public class ComplexFileTable
{
  private static final byte GRPPRL_TYPE = 1;
  private static final byte TEXT_PIECE_TABLE_TYPE = 2;

  protected TextPieceTable _tpt;


  public ComplexFileTable(byte[] documentStream, byte[] tableStream, int offset, int fcMin) throws IOException
  {
    //skips through the prms before we reach the piece table. These contain data
    //for actual fast saved files
    while (tableStream[offset] == GRPPRL_TYPE)
    {
      offset++;
      int skip = LittleEndian.getShort(tableStream, offset);
      offset += LittleEndian.SHORT_SIZE + skip;
    }
    if(tableStream[offset] != TEXT_PIECE_TABLE_TYPE)
    {
      throw new IOException("The text piece table is corrupted");
    }
    else
    {
      int pieceTableSize = LittleEndian.getInt(tableStream, ++offset);
      offset += LittleEndian.INT_SIZE;
      _tpt = new TextPieceTable(documentStream, tableStream, offset, pieceTableSize, fcMin);
    }
  }

  public TextPieceTable getTextPieceTable()
  {
    return _tpt;
  }
}

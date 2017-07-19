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

package org.textmining.extraction.word.sprm;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

public class SprmOperation
{
  final static private BitField OP_BITFIELD = new BitField(0x1ff);
  final static private BitField TYPE_BITFIELD = new BitField(0x1c00);
  final static private BitField SIZECODE_BITFIELD = new BitField(0xe000);

  private int _type;
  //private boolean _variableLen;
  private int _operation;
  private int _operand;
  private byte[] _varOperand;
  private int _sizeNeeded;

  public SprmOperation(byte[] grpprl, int offset)
  {
    short sprmStart = LittleEndian.getShort(grpprl, offset);
    offset += 2;

    _operation = OP_BITFIELD.getValue(sprmStart);
    _type = TYPE_BITFIELD.getValue(sprmStart);
    int sizeCode = SIZECODE_BITFIELD.getValue(sprmStart);

    switch (sizeCode)
    {
      case 0:
      case 1:
        _operand = LittleEndian.getUByte(grpprl, offset);
        _sizeNeeded = 3;
        break;
      case 2:
      case 4:
      case 5:
        _operand = LittleEndian.getShort(grpprl, offset);
        _sizeNeeded = 4;
        break;
      case 3:
        _operand = LittleEndian.getInt(grpprl, offset);
        _sizeNeeded = 6;
        break;
      case 6:
        _varOperand = new byte[grpprl[offset++]];
        System.arraycopy(grpprl, offset, _varOperand, 0, _varOperand.length);
        _sizeNeeded = _varOperand.length + 3;
        break;
      case 7:
        byte threeByteInt[] = new byte[4];
        threeByteInt[0] = grpprl[offset];
        threeByteInt[1] = grpprl[offset + 1];
        threeByteInt[2] = grpprl[offset + 2];
        threeByteInt[3] = (byte)0;
        _operand = LittleEndian.getInt(threeByteInt, 0);
        _sizeNeeded = 5;
        break;

    }
  }

  public int getType()
  {
    return _type;
  }

  public int getOperation()
  {
    return _operation;
  }

  public int getOperand()
  {
    return _operand;
  }

  public int size()
  {
    return _sizeNeeded;
  }
}
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

import java.util.ArrayList;

import org.apache.poi.util.LittleEndian;



/**
 * common data structure in a Word file. Contains an array of 4 byte ints in
 * the front that relate to an array of abitrary data structures in the back.
 *
 *
 * @author Ryan Ackley
 */
public class PlexOfCps
{
  private int _count;
  private int _offset;
  private int _sizeOfStruct;
  private ArrayList<GenericPropertyNode> _props;


  public PlexOfCps(int sizeOfStruct)
  {
    _props = new ArrayList<>();
    _sizeOfStruct = sizeOfStruct;
  }

  /**
   * Constructor
   *
   * @param size The size in bytes of this PlexOfCps
   * @param sizeOfStruct The size of the data structure type stored in
   *        this PlexOfCps.
   */
  public PlexOfCps(byte[] buf, int start, int size, int sizeOfStruct)
  {
    _count = (size - 4)/(4 + sizeOfStruct);
    _sizeOfStruct = sizeOfStruct;
    _props = new ArrayList<>(_count);

    for (int x = 0; x < _count; x++)
    {
      _props.add(getProperty(x, buf, start));
    }
  }

  public GenericPropertyNode getProperty(int index)
  {
    return (GenericPropertyNode)_props.get(index);
  }

  public void addProperty(GenericPropertyNode node)
  {
    _props.add(node);
  }

  public byte[] toByteArray()
  {
    int size = _props.size();
    int cpBufSize = ((size + 1) * LittleEndian.INT_SIZE);
    int structBufSize =  + (_sizeOfStruct * size);
    int bufSize = cpBufSize + structBufSize;

    byte[] buf = new byte[bufSize];

    GenericPropertyNode node = null;
    for (int x = 0; x < size; x++)
    {
      node = (GenericPropertyNode)_props.get(x);

      // put the starting offset of the property into the plcf.
      LittleEndian.putInt(buf, (LittleEndian.INT_SIZE * x), node.getStart());

      // put the struct into the plcf
      System.arraycopy(node.getBytes(), 0, buf, cpBufSize + (x * _sizeOfStruct),
                       _sizeOfStruct);
    }
    // put the ending offset of the last property into the plcf.
    LittleEndian.putInt(buf, LittleEndian.INT_SIZE * size, node.getEnd());

    return buf;

  }

  private GenericPropertyNode getProperty(int index, byte[] buf, int offset)
  {
    int start = LittleEndian.getInt(buf, offset + getIntOffset(index));
    int end = LittleEndian.getInt(buf, offset + getIntOffset(index+1));

    byte[] struct = new byte[_sizeOfStruct];
    System.arraycopy(buf, offset + getStructOffset(index), struct, 0, _sizeOfStruct);

    return new GenericPropertyNode(start, end, struct);
  }

  private int getIntOffset(int index)
  {
    return index * 4;
  }

  /**
   * returns the number of data structures in this PlexOfCps.
   *
   * @return The number of data structures in this PlexOfCps
   */
  public int length()
  {
    return _props.size();
  }

  /**
   * Returns the offset, in bytes, from the beginning if this PlexOfCps to
   * the data structure at index.
   *
   * @param index The index of the data structure.
   *
   * @return The offset, in bytes, from the beginning if this PlexOfCps to
   *         the data structure at index.
   */
  private int getStructOffset(int index)
  {
    return (4 * (_count + 1)) + (_sizeOfStruct * index);
  }
}

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

import java.util.Arrays;

/**
 * Represents a lightweight node in the Trees used to store content
 * properties.
 *
 * @author Ryan Ackley
 */
public abstract class PropertyNode implements Comparable, Cloneable
{
  protected Object _buf;
  private int _cpStart;
  private int _cpEnd;


  /**
   * @param fcStart The start of the text for this property.
   * @param fcEnd The end of the text for this property.
   * @param buf FIXME: Old documentation is: "grpprl The property description in compressed form."
   */
  protected PropertyNode(int fcStart, int fcEnd, Object buf)
  {
      _cpStart = fcStart;
      _cpEnd = fcEnd;
      _buf = buf;

  }

  /**
   * @return The offset of this property's text.
   */
  public int getStart()
  {
      return _cpStart;
  }

  public void setStart(int start)
  {
    _cpStart = start;
  }

  /**
   * @return The offset of the end of this property's text.
   */
  public int getEnd()
  {
    return _cpEnd;
  }

  public void setEnd(int end)
  {
    _cpEnd = end;
  }

  /**
   * Adjust for a deletion that can span multiple PropertyNodes.
   * @param start
   * @param length
   */
  public void adjustForDelete(int start, int length)
  {
    int end = start + length;

    if (_cpEnd > start)
    {
      if (_cpStart < end)
      {
        _cpEnd = end >= _cpEnd ? start : _cpEnd - length;
        _cpStart = Math.min(start, _cpStart);
      }
      else
      {
        _cpEnd -= length;
        _cpStart -= length;
      }
    }
  }

  protected boolean limitsAreEqual(Object o)
  {
    return ((PropertyNode)o).getStart() == _cpStart &&
           ((PropertyNode)o).getEnd() == _cpEnd;

  }

  public boolean equals(Object o)
  {
    if (limitsAreEqual(o))
    {
      Object testBuf = ((PropertyNode)o)._buf;
      if (testBuf instanceof byte[] && _buf instanceof byte[])
      {
        return Arrays.equals((byte[])testBuf, (byte[])_buf);
      }
      return _buf.equals(testBuf);
    }
    return false;
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Used for sorting in collections.
   */
  public int compareTo(Object o)
  {
      int cpEnd = ((PropertyNode)o)._cpEnd;
      if(_cpEnd == cpEnd)
      {
        return 0;
      }
      else if(_cpEnd < cpEnd)
      {
        return -1;
      }
      else
      {
        return 1;
      }
  }





}

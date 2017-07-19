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

/**
 * This class stores info about the data structure describing a chunk of text
 * in a Word document. Specifically, whether or not a Range of text uses
 * unicode or Cp1252 encoding.
 *
 * @author Ryan Ackley
 */

class WordTextPiece
{
  private int _fcStart;
  private boolean _usesUnicode;
  private int _length;

  public WordTextPiece(int start, int length, boolean unicode)
  {
    _usesUnicode = unicode;
    _length = length;
    _fcStart = start;
  }
   public boolean usesUnicode()
  {
      return _usesUnicode;
  }

  public int getStart()
  {
      return _fcStart;
  }
  public int getLength()
  {
    return _length;
  }



}
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

/**
 * This class is used to iterate through a list of sprms from a Word 97/2000/XP
 * document.
 * @author Ryan Ackley
 * @version 1.0
 */

public class SprmIterator
{
  private byte[] _grpprl;
  int _offset;

  public SprmIterator(byte[] grpprl)
  {
    _grpprl = grpprl;
    _offset = 0;
  }

  public boolean hasNext()
  {
    return _offset < _grpprl.length;
  }

  public SprmOperation next()
  {
    SprmOperation op = new SprmOperation(_grpprl, _offset);
    _offset += op.size();
    return op;
  }


}

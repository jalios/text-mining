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
import java.io.Writer;

/**
 * This class acts as a StringBuffer for text from a word document. It allows
 * processing of character before they
 * @author Ryan Ackley
 * @version 1.0
 */
public class WordTextScrubber
{
  boolean _hold;

  public WordTextScrubber()
  {
    _hold = false;
  }

  public void append(Writer writer, String text)
    throws IOException
  {
    char[] letters = text.toCharArray();
    for (int x = 0; x < letters.length; x++)
    {
      switch(letters[x])
      {
        case '\r':
          writer.write("\r\n");
          break;
        case 0x13:
          _hold = true;
          break;
        case 0x14:
          _hold = false;
          break;
        case 0x15:
          _hold = false;
          break;
        default:
          if (!_hold)
          {
            writer.write(letters[x]);
          }
          break;
      }
    }
  }

}
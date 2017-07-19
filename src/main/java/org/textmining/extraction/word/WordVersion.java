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

public class WordVersion
{
  public static int Word6 = 0;
  public static int Word97 = 1;
  public static int getVersion(int nFib)
  {
    switch (nFib)
    {
      case 101:
      case 102:
      case 103:
      case 104:
      case 105:// could be wrong about 105. Spec says Word 97 starts at 106 but it's been wrong before.
        return Word6;
      default:
        return Word97;
    }
  }
}

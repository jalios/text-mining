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

import java.io.UnsupportedEncodingException;

import org.textmining.extraction.word.model.PieceDescriptor;
import org.textmining.extraction.word.model.PropertyNode;


public class TextPiece extends PropertyNode implements Comparable
{  
  //PieceDescriptor _pd;
  public static String UNICODE_ENC = "UTF-16LE";
  public static String ASCII_ENC = "Cp1252";
  /**
   * @param start Offset in main document stream.
   */
  public TextPiece(int start, int end, PieceDescriptor pd)
    throws UnsupportedEncodingException
  {
     /** start - end is length on file. This is double the expected when its
     * unicode.*/
    super(start, end, pd);
    //_pd = pd;
    
  }
  /**
   * @return If this text piece uses unicode
   */
   public boolean usesUnicode()
   {
      return getPieceDescriptor().isUnicode();
   }

   public PieceDescriptor getPieceDescriptor()
   {
     return (PieceDescriptor)_buf;
   }
   
   public boolean unicode()
   {
     return getPieceDescriptor().isUnicode();
   }
   
   public int getFcStart()
   {
     return getPieceDescriptor().getFilePosition();     
   }
   
   public int getFcEnd()
   {
     return getFcStart() + ((super.getEnd() - super.getStart()) * (getPieceDescriptor().isUnicode() ? 2 : 1));
   }
   
   public String getText(byte[] txtSource)
   {
     try
     {
       return new String(txtSource, getFcStart(), getFcEnd() - getFcStart(), unicode() ? UNICODE_ENC : ASCII_ENC);
     }
     catch (UnsupportedEncodingException e)
     {
       throw new RuntimeException(e.getMessage());
     }
   }
}

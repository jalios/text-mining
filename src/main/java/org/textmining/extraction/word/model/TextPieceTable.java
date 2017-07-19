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
import java.util.ArrayList;
import java.util.List;

import org.textmining.extraction.word.model.GenericPropertyNode;
import org.textmining.extraction.word.model.PieceDescriptor;
import org.textmining.extraction.word.model.PlexOfCps;


public class TextPieceTable
{
  protected ArrayList<TextPiece> _textPieces = new ArrayList<TextPiece>();
  
  
  
  public TextPieceTable()
  {
    
  }
  
  public TextPieceTable(byte[] documentStream, byte[] tableStream, int offset,
      int size, int fcMin) throws UnsupportedEncodingException
  {
    // get our plex of PieceDescriptors
    PlexOfCps pieceTable = new PlexOfCps(tableStream, offset, size,
        PieceDescriptor.getSizeInBytes());

    // _multiple = 2;
    int length = pieceTable.length();
    // PieceDescriptor[] pieces = new PieceDescriptor[length];

    // iterate through piece descriptors raw bytes and create
    // PieceDescriptor objects
    for (int x = 0; x < length; x++)
    {
      GenericPropertyNode node = pieceTable.getProperty(x);
      PieceDescriptor piece = new PieceDescriptor(node.getBytes(), 0);
      
      int fcStart = piece.getFilePosition();
      int fcEnd = fcStart + ((node.getEnd() - node.getStart()) * (piece.isUnicode() ? 2 : 1));
      
      //String txt = new String(documentStream, fcStart, fcEnd - fcStart, piece.isUnicode() ? UNICODE_ENC : ASCII_ENC);
      
      _textPieces.add(new TextPiece(node.getStart(), node.getEnd(), piece));
    }    
  }
  
  public List<TextPiece> getTextPieces()
  {
    return _textPieces;
  }
}

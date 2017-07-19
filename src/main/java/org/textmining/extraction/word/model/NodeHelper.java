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

import java.util.List;

public class NodeHelper
{
  List<TextPiece> _textPieces;
  int _lastTxtPiece;
  int _fcMin;
  
  public NodeHelper (TextPieceTable tpt)
  {
    _textPieces = tpt.getTextPieces();
    _fcMin = ((TextPiece)_textPieces.get(0)).getPieceDescriptor().getFilePosition();
    _lastTxtPiece = 0;
  }
  
  private boolean setCpRange(PropertyNode node)
  {
    int fcStart = Math.max(node.getStart(), _fcMin);
    int fcEnd = node.getEnd();
    
    for (; _lastTxtPiece < _textPieces.size(); _lastTxtPiece++)
    {
      TextPiece tp = (TextPiece)_textPieces.get(_lastTxtPiece);
      
      int pieceFcStart = tp.getFcStart();
      int pieceFcEnd = tp.getFcEnd();
      
      if (fcStart >= pieceFcStart && fcStart < pieceFcEnd)
      {
        int divisor = (tp.unicode() ? 2 : 1);
        int cpStart = tp.getStart() + (fcStart - pieceFcStart)/divisor;
        int cpEnd = cpStart + (fcEnd - fcStart)/divisor ;
        
        node.setStart(cpStart);
        node.setEnd(cpEnd);
        return true;
      }      
    }
    return false;
  }
  
  public void addChpNodes(List<CHPX> nodes, byte[] grpprl, int fcStart, int fcEnd)
  {
    CHPX chpx = new CHPX(fcStart, fcEnd, grpprl);
    if (setCpRange(chpx))
    {
      nodes.add(chpx);
    }
  }  
  
  public void sortNodes(List<? extends PropertyNode> nodes, boolean paragraphs)
  {
    // do nothing they should already be sorted for the simple case
  }
}

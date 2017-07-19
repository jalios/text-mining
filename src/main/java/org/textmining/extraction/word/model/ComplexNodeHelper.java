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

import java.util.Collections;
import java.util.List;

public class ComplexNodeHelper extends NodeHelper
{

  public ComplexNodeHelper(TextPieceTable tpt)
  {
    super(tpt);
  }

  public void addChpNodes(List<CHPX> nodes, byte[] grpprl, int fcStart, int fcEnd)
  {    
    for (int x = 0; x < _textPieces.size(); x++)
    {
      TextPiece tp = _textPieces.get(x);
      
      int pieceFcStart = tp.getFcStart();
      int pieceFcEnd = tp.getFcEnd();
      
      
      if (!(fcStart < pieceFcStart && fcEnd <= pieceFcStart) && 
          !(fcStart >= pieceFcEnd && fcEnd > pieceFcEnd))
      {
        int textStart = Math.max(fcStart, pieceFcStart);
        int textEnd = Math.min(fcEnd, pieceFcEnd);
        
        int divisor = (tp.unicode() ? 2 : 1);
        int cpStart = tp.getStart() + (textStart - pieceFcStart)/divisor;
        int cpEnd = cpStart + (textEnd - textStart)/divisor ;
        
        CHPX chpx = new CHPX(cpStart, cpEnd, grpprl);    
        nodes.add(chpx);
      }      
    }   
  }
  
  
  
  public void sortNodes(List<? extends PropertyNode> nodes, boolean paragraphs)
  {
    Collections.sort(nodes);
    
    if (paragraphs)
    {
      int len = nodes.size();
      int lastEnd = 0;
      for (int x = 0; x < len; x++)
      {
        PropertyNode node = nodes.get(x);
        node.setStart(lastEnd);
        lastEnd = node.getEnd();
      }
    }
    
  }  
}

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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;
import org.textmining.extraction.TextExtractor;
import org.textmining.extraction.word.model.CHPX;
import org.textmining.extraction.word.model.PieceDescriptor;
import org.textmining.extraction.word.model.TextPiece;


public abstract class WordTextExtractor
  implements TextExtractor
{
  protected byte[] _header;
  protected boolean _fastSave;
  protected POIFSFileSystem _fsys;
  
  protected void doFastSaveExtraction(Writer stringWriter, int fcMin, List textPieces, List textRuns, WordTextScrubber scrubber) throws UnsupportedEncodingException, IOException
  {
    // probably a faster way to do this but I'm tired of working on this.
    for (int x = 0; x < textPieces.size(); x++)
    {
      TextPiece currentPiece = (TextPiece)textPieces.get(x);
      PieceDescriptor pd = currentPiece.getPieceDescriptor();
      
      int fcStart = pd.getFilePosition();
      int fcEnd = fcStart + ((currentPiece.getEnd() - currentPiece.getStart()) * (pd.isUnicode() && supportsUnicode() ? 2 : 1));
      
      for (int y = 0; y < textRuns.size(); y++)
      {
        CHPX chpx = (CHPX)textRuns.get(y);
        
        int chpxStart = chpx.getStart() + fcMin;
        int chpxEnd = chpx.getEnd() + fcMin;         
        
        if (!(chpxStart < fcStart && chpxEnd <= fcStart) && 
            !(chpxStart >= fcEnd && chpxEnd > fcEnd))
        {
          if (isDeleted(chpx.getGrpprl()))
          {
            continue;
          }
          int textStart = Math.max(chpxStart, fcStart);
          int textEnd = Math.min(chpxEnd, fcEnd);
          String str = new String(_header, textStart, textEnd - textStart, pd.isUnicode() && supportsUnicode() ? "UTF-16LE" : "Cp1252");
          scrubber.append(stringWriter, str);
        }
      }
    }
  }
  
  protected abstract boolean isDeleted(byte[] grpprl);
  protected boolean supportsUnicode()
  {
    return false;
  }  
  
  
  protected void initWordHeader(InputStream in) throws IOException, PasswordProtectedException
  {
    _fsys = new POIFSFileSystem(in);
    // load our POIFS document streams.
    DocumentEntry headerProps =
        (DocumentEntry)_fsys.getRoot().getEntry("WordDocument");
    DocumentInputStream din = _fsys.createDocumentInputStream("WordDocument");
    _header = new byte[headerProps.getSize()];
  
  
    din.read(_header);
    din.close(); 
    
    initOptions();
  
  }

  protected void initOptions() throws PasswordProtectedException
  {
    int info = LittleEndian.getShort(_header, 0xa);
    _fastSave = (info & 0x4) != 0;
    if ((info & 0x100) != 0)
    {
      throw new PasswordProtectedException("This document is password protected");
    }
  }
  
}

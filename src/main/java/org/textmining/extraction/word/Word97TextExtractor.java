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

import java.io.*;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.util.LittleEndian;
//import org.apache.poi.hwpf.model.*;
import org.textmining.extraction.*;
import org.textmining.extraction.word.model.CHPBinTable;
import org.textmining.extraction.word.model.CHPX;
import org.textmining.extraction.word.model.ComplexNodeHelper;
import org.textmining.extraction.word.model.NodeHelper;
import org.textmining.extraction.word.model.PieceDescriptor;
import org.textmining.extraction.word.model.TextPiece;
import org.textmining.extraction.word.model.TextPieceTable;
import org.textmining.extraction.word.sprm.SprmIterator;
import org.textmining.extraction.word.sprm.SprmOperation;


public class Word97TextExtractor
  extends WordTextExtractor
{  
  
  
  Word97TextExtractor(byte[] header, POIFSFileSystem fsys, boolean fastSaved)
  {
    _header = header;
    _fsys = fsys;
    _fastSave = fastSaved;
  }
  
  public Word97TextExtractor(InputStream in) throws IOException, PasswordProtectedException
  {
    super.initWordHeader(in);
  }
  
  public String getText()
    throws IOException
  {
    StringWriter stringWriter = new StringWriter();    
    getText(stringWriter);
    return stringWriter.toString();
  }

  public void getText(Writer stringWriter) throws IOException
  {
    int info = LittleEndian.getShort(_header, 0xa);
//  Get the information we need from the _header
    boolean useTable1 = (info & 0x200) != 0;

    //get the location of the piece table
    int complexOffset = LittleEndian.getInt(_header, 0x1a2);

    // determine which table stream we must use.
    String tableName = null;
    if (useTable1)
    {
      tableName = "1Table";
    }
    else
    {
      tableName = "0Table";
    }

    DocumentEntry table = (DocumentEntry)_fsys.getRoot().getEntry(tableName);
    byte[] tableStream = new byte[table.getSize()];

    DocumentInputStream din = _fsys.createDocumentInputStream(tableName);

    din.read(tableStream);
    din.close();
    
    int fcMin = LittleEndian.getInt(_header, 0x18);
//  load our text pieces and our character runs
    ComplexFileTable cft = new ComplexFileTable(_header, tableStream, complexOffset, fcMin);
    TextPieceTable tpt = cft.getTextPieceTable();
    List textPieces = tpt.getTextPieces();
    
    StringBuffer allTxt = new StringBuffer();
    for (int x = 0; x < textPieces.size(); x++)
    {
      TextPiece tp = (TextPiece)textPieces.get(x);
      String txt = tp.getText(_header);
      allTxt.append(txt);
    }
    
    NodeHelper fc2Cp = null;
    if (_fastSave)
    {
      fc2Cp = new ComplexNodeHelper(tpt);
    }
    else
    {
      fc2Cp = new NodeHelper(tpt);
    }    
    
    int chpOffset = LittleEndian.getInt(_header, 0xfa);
    int chpSize = LittleEndian.getInt(_header, 0xfe);
    
    CHPBinTable cbt = new CHPBinTable(_header, tableStream, chpOffset, chpSize, fcMin, fc2Cp);
    
    // make the POIFS objects available for garbage collection
    din = null;
    _fsys = null;
    table = null;    

    List textRuns = cbt.getTextRuns();
    
    WordTextScrubber scrubber = new WordTextScrubber();
    
    for (int x = 0; x < textRuns.size(); x++)
    {
      CHPX chpx = (CHPX)textRuns.get(x);
      if (!isDeleted(chpx.getGrpprl()))
      {
        String str = allTxt.substring(chpx.getStart(), chpx.getEnd());
        scrubber.append(stringWriter, str);
      }
    }
    
    // iterate through all text runs extract the text only if they haven't been
    // deleted
    
//      int y = 0;
//      for (int x = 0; x < textPieces.size(); x++)
//      {
//        TextPiece currentPiece = (TextPiece)textPieces.get(x);
//        PieceDescriptor pd = currentPiece.getPieceDescriptor();
//        
//        int fcStart = pd.getFilePosition();
//        int fcEnd = fcStart + ((currentPiece.getEnd() - currentPiece.getStart()) * (pd.isUnicode() ? 2 : 1));
//                
//        CHPX chpx = (CHPX)textRuns.get(y);
//        
//        int chpxStart = chpx.getStart() + fcMin;
//        int chpxEnd = chpx.getEnd() + fcMin;
//        
//        while (chpxStart < fcEnd)
//        {
//          int textStart = Math.max(chpxStart, fcStart);
//          int textEnd = Math.min(chpxEnd, fcEnd);      
//          
//          if (!isDeleted(chpx.getGrpprl()))
//          {
//            String str = new String(_header, textStart, textEnd - textStart, pd.isUnicode() ? "UTF-16LE" : "Cp1252");
//            scrubber.append(stringWriter, str);
//          }          
//          if (chpxEnd > fcEnd)
//          {
//            break;
//          }
//          else if (y < textRuns.size() - 1)
//          {
//            y++;
//            chpx = (CHPX)textRuns.get(y);            
//            chpxStart = chpx.getStart() + fcMin;
//            chpxEnd = chpx.getEnd() + fcMin;
//          }
//          else
//          {
//            break;
//          }
//        }        
//      }
    //}
      
  }

  /**
   * Used to determine if a run of text has been deleted.
   *
   * @param grpprl The list of sprms for a particular run of text.
   * @return true if this run of text has been deleted.
   */
  protected boolean isDeleted(byte[] grpprl)
  {
    SprmIterator iterator = new SprmIterator(grpprl);
    while (iterator.hasNext())
    {
      SprmOperation op = iterator.next();
      // 0 is the operation that signals a FDelRMark operation
      if (op.getOperation() == 0 && op.getOperand() != 0)
      {
        return true;
      }
    }
    return false;
  }

  protected boolean supportsUnicode()
  {
    return true;
  }
  
  
}

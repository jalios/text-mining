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
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.poi.util.LittleEndian;
import org.textmining.extraction.word.chp.Word6CHPBinTable;
import org.textmining.extraction.word.model.CHPX;
import org.textmining.extraction.word.model.ComplexNodeHelper;
import org.textmining.extraction.word.model.NodeHelper;
import org.textmining.extraction.word.model.PieceDescriptor;
import org.textmining.extraction.word.model.TextPiece;
import org.textmining.extraction.word.model.TextPieceTable;

/**
 * This class is used to extract text from Word 6 documents only. It should
 * only be called from the org.textmining.text.extraction.WordExtractor because
 * it will automatically determine the version.
 *
 * @author Ryan Ackley
 */
public class Word6TextExtractor
  extends WordTextExtractor
{  
  
  Word6TextExtractor(byte[] header, boolean fastSave)
  {
    _header = header;
    _fastSave = fastSave;
  }
  
  protected Word6TextExtractor()
  {
    
  }
  
  public Word6TextExtractor(InputStream in) throws IOException, PasswordProtectedException
  {
    super.initWordHeader(in);
  }
  
 

  /**
   * Used to determine if a run of text has been deleted.
   * @param grpprl The list of sprms for this run of text.
   * @return
   */
  protected boolean isDeleted(byte[] grpprl)
  {
    int offset = 0;
    boolean deleted = false;
    while (offset < grpprl.length)
    {
      switch (LittleEndian.getUByte(grpprl, offset++))
      {
        case 65:
          deleted = grpprl[offset++] != 0;
          break;
        case 66:
          offset++;
          break;
        case 67:
          offset++;
          break;
        case 68:
          offset += grpprl[offset];
          break;
        case 69:
          offset += 2;
          break;
        case 70:
          offset += 4;
          break;
        case 71:
          offset++;
          break;
        case 72:
          offset += 2;
          break;
        case 73:
          offset += 3;
          break;
        case 74:
          offset += grpprl[offset];
          break;
        case 75:
          offset++;
          break;
        case 80:
          offset += 2;
          break;
        case 81:
          offset += grpprl[offset];
          break;
        case 82:
          offset += grpprl[offset];
          break;
        case 83:
          break;
        case 85:
          offset++;
          break;
        case 86:
          offset++;
          break;
        case 87:
          offset++;
          break;
        case 88:
          offset++;
          break;
        case 89:
          offset++;
          break;
        case 90:
          offset++;
          break;
        case 91:
          offset++;
          break;
        case 92:
          offset++;
          break;
        case 93:
          offset += 2;
          break;
        case 94:
          offset++;
          break;
        case 95:
          offset += 3;
          break;
        case 96:
          offset += 2;
          break;
        case 97:
          offset += 2;
          break;
        case 98:
          offset++;
          break;
        case 99:
          offset++;
          break;
        case 100:
          offset++;
          break;
        case 101:
          offset++;
          break;
        case 102:
          offset++;
          break;
        case 103:
          offset += grpprl[offset];
          break;
        case 104:
          offset++;
          break;
        case 105:
          offset += grpprl[offset];
          break;
        case 106:
          offset += grpprl[offset];
          break;
        case 107:
          offset += 2;
          break;
        case 108:
          offset += grpprl[offset];
          break;
        case 109:
          offset += 2;
          break;
        case 110:
          offset += 2;
          break;
        case 117:
          offset++;
          break;
        case 118:
          offset++;
          break;

      }
    }
    return deleted;
  }

  public String getText() throws IOException
  {
    StringWriter writer = new StringWriter();
    getText(writer);
    return writer.toString();
  }

  public void getText(Writer writer) throws IOException
  {
    int fcMin = LittleEndian.getInt(_header, 0x18);
    int fcMax = LittleEndian.getInt(_header, 0x1C);

    int chpTableOffset = getChpTableOffset();
    int chpTableSize = getChpTableSize();

    
    NodeHelper fc2Cp = null;
    StringBuffer allTxt = new StringBuffer();
    
    if (_fastSave)
    {
      int complexOffset = getComplexOffset();
      ComplexFileTable cft = new ComplexFileTable(_header, _header, complexOffset, fcMin);
      TextPieceTable tpt = cft.getTextPieceTable();
      fc2Cp = new ComplexNodeHelper(tpt);
      
      List<TextPiece> textPieces = tpt.getTextPieces();
      for (int x = 0; x < textPieces.size(); x++)
      {
        TextPiece tp = textPieces.get(x);
        tp.getPieceDescriptor().setUnicode(false);
        String txt = tp.getText(_header);
        allTxt.append(txt);
      }
    }
    else
    {      
      PieceDescriptor pd = new PieceDescriptor();
      pd.setFilePosition(fcMin);
      TextPieceTable tpt = new TextPieceTable();
      TextPiece tp = new TextPiece(0, fcMax - fcMin, pd);     
      tpt.getTextPieces().add(tp);
      fc2Cp = new NodeHelper(tpt);
      allTxt.append(tp.getText(_header));
    }   
    
    // get a list of character properties
    Word6CHPBinTable chpTable = new Word6CHPBinTable(_header, chpTableOffset,
      chpTableSize, fcMin, fc2Cp);
    
    List<CHPX> textRuns = chpTable.getTextRuns();
    
    WordTextScrubber scrubber = new WordTextScrubber();
    
    // iterate through the text runs
    
    for (int x = 0; x < textRuns.size(); x++)
    {
      CHPX chpx = (CHPX)textRuns.get(x);      
      
      if (!isDeleted(chpx.getGrpprl()))
      {
        String str = allTxt.substring(chpx.getStart(), chpx.getEnd());
        scrubber.append(writer, str);
      }
    }      
  }
  protected int getChpTableSize()
  {
    return LittleEndian.getInt(_header, 0xbc);
  }
  protected int getChpTableOffset()
  {
    return LittleEndian.getInt(_header, 0xb8);
  }
  protected int getComplexOffset()
  {
    return LittleEndian.getInt(_header, 0x160);
  }
}
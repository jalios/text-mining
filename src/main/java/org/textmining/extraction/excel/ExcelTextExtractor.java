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
package org.textmining.extraction.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.util.*;

import org.textmining.extraction.TextExtractor;

public class ExcelTextExtractor
  implements TextExtractor
{
  byte[] _recordStream;
  int _offset;
  
  public ExcelTextExtractor(InputStream in) throws IOException
  {
    POIFSFileSystem poifs = new POIFSFileSystem(in);
    DocumentEntry headerProps =
        (DocumentEntry)poifs.getRoot().getEntry("Workbook");
    DocumentInputStream din = poifs.createDocumentInputStream("Workbook");
    _recordStream = new byte[headerProps.getSize()];


    din.read(_recordStream);
    din.close();   
  }
  public String getText() throws IOException
  {
    StringWriter writer = new StringWriter();
    getText(writer);
    return writer.toString();
  }

  public void getText(Writer writer) throws IOException
  {
    while (_offset < _recordStream.length)
    {
      int type = LittleEndian.getShort(_recordStream, _offset);
      _offset += LittleEndian.SHORT_SIZE;
      if (type == 0xa)
      {
        //if (_offset == _recordStream.length)
          break;
//        else
//        {
//          continue;
//        }
      }
      int size = LittleEndian.getShort(_recordStream, _offset);
      _offset += LittleEndian.SHORT_SIZE;
      if (type == Record.SST_RECORD)
      {
        int totalStrings = LittleEndian.getInt(_recordStream, _offset);
        _offset += LittleEndian.INT_SIZE;
        int sharedStrings = LittleEndian.getInt(_recordStream, _offset);
        _offset += LittleEndian.INT_SIZE;
        for (int x = 0; x < sharedStrings; x++)
        {
          int strLength = LittleEndian.getShort(_recordStream, _offset);
          _offset += LittleEndian.SHORT_SIZE;
          
          int flags = _recordStream[_offset++];
          boolean compression = (flags & 0x1) == 0;
          boolean asian = (flags & 0x4) != 0;
          boolean richText = (flags & 8) != 0;
          int numRuns = 0;
          int sizeofAsian = 0;
          
          if (richText)
          {
            numRuns = LittleEndian.getShort(_recordStream, _offset);
            _offset += LittleEndian.SHORT_SIZE;
          }
          if (asian)
          {
            sizeofAsian = LittleEndian.getInt(_recordStream, _offset);
            _offset += LittleEndian.SHORT_SIZE;
          }
          int byteLength = !compression ? strLength * 2 : strLength;
          String string = new String(_recordStream, _offset, byteLength, 
              compression ? "Cp1252":"UTF-16LE");
          //System.out.println(string);
          writer.write(string + ' ');         
          
          _offset += byteLength;
          if (richText)
          {
            _offset += (numRuns * 4);
          }
          
        }
      }
      else 
      {
        _offset += size;
      }
    }
  }
  
  
  
  
}

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

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.util.LittleEndian;
import org.textmining.extraction.word.model.*;


public class WordExtractorFactory
{
  
  byte[] _header;
  POIFSFileSystem _fsys;
  protected boolean _fastSaved;
  
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
  
  }

  protected int getVersion() throws PasswordProtectedException
  {
    int info = LittleEndian.getShort(_header, 0xa);
    _fastSaved = (info & 0x4) != 0;
    if ((info & 0x100) != 0)
    {
      throw new PasswordProtectedException("This document is password protected");
    }
    // determine the version of Word this document came from.
    int nFib = LittleEndian.getShort(_header, 0x2);
    return WordVersion.getVersion(nFib);
    
  }

}

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
package org.textmining.extraction.word.chp;

import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.LittleEndian;
import org.textmining.extraction.word.model.*;



/**
 * This class holds all of the character formatting properties from a Word
 * 6.0/95 document.
 *
 * @author Ryan Ackley
 */
public class Word6CHPBinTable
{
  /** List of character properties.*/
  ArrayList<CHPX> _textRuns = new ArrayList<>();

  /**
   * Constructor used to read a binTable in from a Word document.
   *
   * @param documentStream The POIFS "WordDocument" stream from a Word document
   * @param offset The offset of the Chp bin table in the main stream.
   * @param size The size of the Chp bin table in the main stream.
   * @param fcMin The start of text in the main stream.
   */
  public Word6CHPBinTable(byte[] documentStream, int offset,
                     int size, int fcMin, NodeHelper helper)
  {
    PlexOfCps binTable = new PlexOfCps(documentStream, offset, size, 2);

    int length = binTable.length();
    for (int x = 0; x < length; x++)
    {
      GenericPropertyNode node = binTable.getProperty(x);

      int pageNum = LittleEndian.getShort((byte[])node.getBytes());
      int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE * pageNum;
      
      //TODO fix this
      CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream,
        pageOffset, fcMin, helper);

      int fkpSize = cfkp.size();

      for (int y = 0; y < fkpSize; y++)
      {
        _textRuns.add(cfkp.getCHPX(y));
      }
    }
    helper.sortNodes(_textRuns, false);
  }

  public List<CHPX> getTextRuns()
  {
    return _textRuns;
  }

}
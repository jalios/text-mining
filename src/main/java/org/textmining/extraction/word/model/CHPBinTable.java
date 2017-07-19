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

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.LittleEndian;


/**
 * This class holds all of the character formatting properties.
 *
 * @author Ryan Ackley
 */
public class CHPBinTable
{
  /** List of character properties.*/
  protected ArrayList<CHPX> _textRuns = new ArrayList<>();


  public CHPBinTable()
  {
  }

  /**
   * Constructor used to read a binTable in from a Word document.
   * @param documentStream
   * @param tableStream
   * @param offset
   * @param size
   * @param fcMin
   * @param fc2Cp TODO
   */
  public CHPBinTable(byte[] documentStream, byte[] tableStream,
                     int offset, int size, int fcMin, NodeHelper fc2Cp)
  {
    PlexOfCps binTable = new PlexOfCps(tableStream, offset, size, 4);

    int length = binTable.length();
    for (int x = 0; x < length; x++)
    {
      GenericPropertyNode node = binTable.getProperty(x);

      int pageNum = LittleEndian.getInt(node.getBytes());
      int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE * pageNum;

      CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream,
        pageOffset, fcMin, fc2Cp);

      int fkpSize = cfkp.size();

      for (int y = 0; y < fkpSize; y++)
      {
        _textRuns.add(cfkp.getCHPX(y));
      }
    }
    fc2Cp.sortNodes(_textRuns, false);
  }

 

 

  

  public List getTextRuns()
  {
    return _textRuns;
  }

  




}

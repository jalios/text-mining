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
import java.util.ArrayList;

import org.apache.poi.util.LittleEndian;

/**
 * Represents a CHP fkp. The style properties for paragraph and character runs
 * are stored in fkps. There are PAP fkps for paragraph properties and CHP fkps
 * for character run properties. The first part of the fkp for both CHP and PAP
 * fkps consists of an array of 4 byte int offsets that represent a
 * Paragraph's or Character run's text offset in the main stream. The ending
 * offset is the next value in the array. For example, if an fkp has X number of
 * Paragraph's stored in it then there are (x + 1) 4 byte ints in the beginning
 * array. The number X is determined by the last byte in a 512 byte fkp.
 *
 * CHP and PAP fkps also store the compressed styles(grpprl) that correspond to
 * the offsets on the front of the fkp. The offset of the grpprls is determined
 * differently for CHP fkps and PAP fkps.
 *
 * @author Ryan Ackley
 */
public class CHPFormattedDiskPage extends FormattedDiskPage
{
    private static final int FC_SIZE = 4;

    private ArrayList<CHPX> _chpxList = new ArrayList<>();
    private ArrayList _overFlow;


    public CHPFormattedDiskPage()
    {
    }

    /**
     * This constructs a CHPFormattedDiskPage from a raw fkp (512 byte array
     * read from a Word file).
     */
    public CHPFormattedDiskPage(byte[] documentStream, int offset, int fcMin, NodeHelper fc2Cp)
    {
      super(documentStream, offset, fc2Cp);

      for (int x = 0; x < _crun; x++)
      {
        fc2Cp.addChpNodes(_chpxList, getGrpprl(x), getStart(x), getEnd(x));
//        CHPX chpx = new CHPX(getStart(x), getEnd(x), getGrpprl(x));
//        if (fc2Cp.setCpRangeForCHP(chpx))
//        {
//          _chpxList.add(chpx);
//        }
      }
    }

    public CHPX getCHPX(int index)
    {
      return (CHPX)_chpxList.get(index);
    }

    public void fill(List<CHPX> filler)
    {
      _chpxList.addAll(filler);
    }
    
    public void fill(ArrayList<CHPX> filler, int start)
    {
      _chpxList = filler;
      _currentIndex = start;
    }
    
    public int getEndIndex()
    {
      return _currentIndex;
    }
    
    public ArrayList getOverflow()
    {
      return _overFlow;
    }

    /**
     * Gets the chpx for the character run at index in this fkp.
     *
     * @param index The index of the chpx to get.
     * @return a chpx grpprl.
     */
    protected byte[] getGrpprl(int index)
    {
        int chpxOffset = 2 * LittleEndian.getUByte(_fkp, _offset + (((_crun + 1) * 4) + index));

        //optimization if offset == 0 use "Normal" style
        if(chpxOffset == 0)
        {
            return new byte[0];
        }

        int size = LittleEndian.getUByte(_fkp, _offset + chpxOffset);

        byte[] chpx = new byte[size];

        System.arraycopy(_fkp, _offset + ++chpxOffset, chpx, 0, size);
        return chpx;
    }

    public byte[] toByteArray(int fcMin)
    {
      byte[] buf = new byte[512];
      int size = _chpxList.size();
      int grpprlOffset = 511;
      int offsetOffset = 0;
      int fcOffset = 0;

      // total size is currently the size of one FC
      int totalSize = FC_SIZE + 2;

      int index = _currentIndex;
      for (; index < size; index++)
      {
        int grpprlLength = _chpxList.get(index).getGrpprl().length;

        // check to see if we have enough room for an FC, the grpprl offset,
        // the grpprl size byte and the grpprl.
        totalSize += (FC_SIZE + 2 + grpprlLength);
        // if size is uneven we will have to add one so the first grpprl falls
        // on a word boundary
        if (totalSize > 511 + (index % 2))
        {
          totalSize -= (FC_SIZE + 2 + grpprlLength);
          break;
        }

        // grpprls must fall on word boundaries
        if ((1 + grpprlLength) % 2 > 0)
        {
          totalSize += 1;
        }
      }

      _currentIndex = index;

      // index should equal number of CHPXs that will be in this fkp now.
      buf[511] = (byte)index;

      offsetOffset = (FC_SIZE * index) + FC_SIZE;
      //grpprlOffset =  offsetOffset + index + (grpprlOffset % 2);

      CHPX chpx = null;
      for (int x = 0; x < index; x++)
      {
        chpx = _chpxList.get(x);
        byte[] grpprl = chpx.getGrpprl();

        LittleEndian.putInt(buf, fcOffset, chpx.getStart() + fcMin);
        grpprlOffset -= (1 + grpprl.length);
        grpprlOffset -= (grpprlOffset % 2);
        buf[offsetOffset] = (byte)(grpprlOffset/2);
        buf[grpprlOffset] = (byte)grpprl.length;
        System.arraycopy(grpprl, 0, buf, grpprlOffset + 1, grpprl.length);

        offsetOffset += 1;
        fcOffset += FC_SIZE;
      }
      // put the last chpx's end in
      LittleEndian.putInt(buf, fcOffset, chpx.getEnd() + fcMin);
      return buf;
    }

    public int size()
    {
      return _chpxList.size();
    }

}

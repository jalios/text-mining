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

import org.apache.poi.util.LittleEndian;

/**
 * Represents an FKP data structure. This data structure is used to store the
 * grpprls of the paragraph and character properties of the document. A grpprl
 * is a list of sprms(decompression operations) to perform on a parent style.
 *
 * The style properties for paragraph and character runs
 * are stored in fkps. There are PAP fkps for paragraph properties and CHP fkps
 * for character run properties. The first part of the fkp for both CHP and PAP
 * fkps consists of an array of 4 byte int offsets in the main stream for that
 * Paragraph's or Character run's text. The ending offset is the next
 * value in the array. For example, if an fkp has X number of Paragraph's
 * stored in it then there are (x + 1) 4 byte ints in the beginning array. The
 * number X is determined by the last byte in a 512 byte fkp.
 *
 * CHP and PAP fkps also store the compressed styles(grpprl) that correspond to
 * the offsets on the front of the fkp. The offset of the grpprls is determined
 * differently for CHP fkps and PAP fkps.
 *
 * @author Ryan Ackley
 */
public abstract class FormattedDiskPage
{
    protected byte[] _fkp;
    protected int _crun;
    protected int _offset;
    protected NodeHelper _fc2Cp;
    protected int _currentIndex;


    public FormattedDiskPage()
    {

    }
    
    /**
     * Uses a 512-byte array to create a FKP
     */
    public FormattedDiskPage(byte[] documentStream, int offset, NodeHelper fc2Cp)
    {
        _crun = LittleEndian.getUByte(documentStream, offset + 511);
        _fkp = documentStream;
        _offset = offset;
        _fc2Cp = fc2Cp;
    }
    /**
     * Used to get a text offset corresponding to a grpprl in this fkp.
     * @param index The index of the property in this FKP
     * @return an int representing an offset in the "WordDocument" stream
     */
    protected int getStart(int index)
    {
        return LittleEndian.getInt(_fkp, _offset + (index * 4));
    }
    /**
     * Used to get the end of the text corresponding to a grpprl in this fkp.
     * @param index The index of the property in this fkp.
     * @return an int representing an offset in the "WordDocument" stream
     */
    protected int getEnd(int index)
    {
        return LittleEndian.getInt(_fkp, _offset + ((index + 1) * 4));
    }
    /**
     * Used to get the total number of grrprl's stored int this FKP
     * @return The number of grpprls in this FKP
     */
    public abstract int size();
    
    protected abstract byte[] getGrpprl(int index);
}

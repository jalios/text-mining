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

import org.apache.poi.poifs.filesystem.*;
import org.textmining.extraction.TextExtractor;
import org.textmining.extraction.word.model.*;
import org.textmining.extraction.word.sprm.*;


import java.util.*;
import java.io.*;

/**
 * This class extracts the text from a Word 6.0/95/97/2000/XP word doc
 *
 * @author Ryan Ackley
 */
public class WordTextExtractorFactory
  extends WordExtractorFactory
{
	
  /**
   * Constructor
   */
  public WordTextExtractorFactory()
  {
  }
   
  /**
   * Gets the text from a Word document.
   *
   * @param in The InputStream representing the Word file.
   */
  public TextExtractor textExtractor(InputStream in) throws Exception
  {
    try
    {
      initWordHeader(in);
    }
    catch (Exception e)
    {
      return new Word2TextExtractor(in);
    }
    int version = getVersion();
    if (version == WordVersion.Word6)
    {
      Word6TextExtractor oldExtractor = new Word6TextExtractor(_header, _fastSaved);
      return oldExtractor;
    }
    else
    {
      return new Word97TextExtractor(_header, _fsys, _fastSaved);
    }
  }
}
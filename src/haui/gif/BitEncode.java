package haui.gif;

/*
   Copyright (C) 2000 Bernhard Haumacher <haui@haumacher.de>

   This file is part of the haui.gif package.

   haui.gif is a Java package that allows manipulations of GIF images
   that do not need a LZW decoding or encoding algorithm. 

   You can redistribute it and/or modify it under the terms of the GNU
   General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option)
   any later version.

   haui.gif is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with haui.gif; see the file GPL.  If not, write to the
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
   MA 02111-1307, USA.
*/

import java.io.*;

public abstract class BitEncode {
  final int initialCodeSize;
  int codeSize;

  final int bufferLen;
  int bufferCnt;
  final byte[] buffer;

  int bitCnt;
  int bits;

  public BitEncode(int bufferLen, int codeSize) {
    this.bufferLen = bufferLen;
    this.initialCodeSize = codeSize;

    this.bufferCnt = 0;
    this.buffer = new byte[bufferLen];

    this.bitCnt = 0;
    this.bits = 0;
  }

  public void close() throws IOException {
    if (bitCnt > 0) {
      store((byte) bits);
      bits   = 0;
      bitCnt = 0;
    }

    if (bufferCnt > 0) {
      deliver(buffer, bufferCnt);
      bufferCnt = 0;
    }
  }

  public final void output(int code) throws IOException {
    //System.err.print("[" + code + "]");

    bits   |= (code << bitCnt);
    bitCnt += codeSize;

    while (bitCnt >= 8) {
      store((byte) bits);
      bits >>>= 8;
      bitCnt -= 8;
    }

    //System.err.print("(cnt=" + bitCnt + ")");
  }

  private void store(byte value) throws IOException {
    //System.err.print("(" + (((int) value) & 0xFF) + ")");

    buffer[bufferCnt++] = value;
    if (bufferCnt == bufferLen) {
      deliver(buffer, bufferCnt);
      bufferCnt = 0;
    }
  }

  protected abstract void deliver(byte[] buffer, int bufferCnt) throws IOException;

}

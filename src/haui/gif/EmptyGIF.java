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

public class EmptyGIF extends GIF {

  static class GIFEncoder extends BitEncode {
    Data imageData;

    public GIFEncoder(int dataSize, Data data) {
      super(255, dataSize);
      this.imageData = data;
    };

    protected void deliver(byte[] buffer, int bufferCnt) {
      imageData.data = new byte[bufferCnt];
      System.arraycopy(buffer, 0, imageData.data, 0, bufferCnt);
      imageData.next = new Data();

      imageData = imageData.next;
    }
  }

  public EmptyGIF(int width, int height, int depth) {
    header = new Header();
    header.version = VERSION_89a;

    screen = new ScreenDescriptor();
    screen.width = width;
    screen.height = height;
    screen.colorTableFlag = true;
    screen.colorResolution = depth - 1;
    screen.sortFlag = false;
    screen.colorTableSize = depth - 1;
    screen.backgroundColorIndex = 0;
    screen.pixelAspectReatio = 0;

    screen.colorTable = new ColorTable();
    screen.colorTable.size = 3 * (1 << depth);
    screen.colorTable.data = new byte[screen.colorTable.size];
    screen.colorTable.data[0] = (byte) 255;
    screen.colorTable.data[1] = (byte) 255;
    screen.colorTable.data[2] = (byte) 255;

    image = new ImageDescriptor();
    image.leftPosition = 0;
    image.topPosition = 0;
    image.width = 1;
    image.height = 1;
    image.colorTableFlag = false;
    image.interlacedFlag = false;
    image.sortFlag = false;

    image.data = new ImageData();
    image.data.minimumCodeSize = depth;

    int resetCode = 1 << depth;
    int eofCode = resetCode + 1;

    BitEncode encoder = new GIFEncoder(depth + 1, image.data);
    try {
      encoder.output(resetCode);
      encoder.output(0);
      encoder.output(eofCode);
      encoder.close();
    } catch (IOException ex) {
      // can not occurr
      throw new InternalError(ex.toString());
    }

    ControlExtension controlExtension = new ControlExtension();
    controlExtension.disposalMethod = GIF.ControlExtension.DISPOSAL_NO;
    controlExtension.userInputFlag = GIF.ControlExtension.USERINPUT_NO;
    controlExtension.transparentColorFlag = true;
    controlExtension.delayTime = 0;
    controlExtension.transparentColorIndex = 0;

    image.extension = controlExtension;
  }
}

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

import haui.gif.GIF.*;

import java.io.*;

public class AnimationFilter {
  public static final int MODE_WIPE_OUT       = 1;
  public static final int MODE_SHOW_FIRST     = 2;
  public static final int MODE_SHOW_LAST      = 3;
  public static final int MODE_INTERACTIVE    = 4;
  public static final int MODE_ANIMATION      = 5;

  /** Filter mode, set in the constructor */
  private int mode;

  /** Only relevant for <code>MODE_ANIMATION</code>. Controls the
      number of animation cycles performed (0=infinite).
  */
  public int loopCnt = 1;

  public AnimationFilter(int mode) {
    this.mode = mode;
  }

  public static GIF.ImageDescriptor getLastImage(GIF gif) {
    GIF.ImageDescriptor image = gif.image;
    while (image.next != null) image = image.next;
    return image;
  }

  class AbortException extends RuntimeException {

  /**
   * Serializable class should declare this:
   */
  private static final long serialVersionUID = 1L;}

  public void filter(InputStream in, OutputStream out) throws IOException {
    GIFInputStream gin = new GIFInputStream(in);
    GIFOutputStream gout = new GIFOutputStream(out);

    GIF.Filter f = createFilter(gin, gout);
    //try {
    f.run();
    //} catch (AbortException ex) {
    //  gin.close();
    //}

    if (! f.success()) {
      throw f.getIOException();
    }

    gout.flush();
  }

  /** Override this method in a subclass to change the GIF filter
      created.
  */
  public GIF.Filter createFilter(GIFInputStream gin, GIFOutputStream gout) {
    return new Filter(gin, gout);
  }

  public class Filter extends GIF.Filter {
    /** The the image that is filtered. This is constructed during the
        filter process by <code>creator</code>.
    */
    GIF gif;

    /** All parser events are delegated to <code>creator</code> to
        build the GIF data structure storde in <code>gif</code>.
    */
    GIF.Creator creator;

    public Filter(GIFInputStream in, GIFOutputStream out) {
      super(in, out);

      gif = new GIF();
      creator = new GIF.Creator(gif);
    }

    /** The number of single images seen within the GIF data stream so
        far. 
    */
    int imageCnt = 0;

    /** Preliminary: when set to <code>true</code>, the tail of the
        GIF stream is ignored. Future versions of muffin should allow
        to stop the download of uninteresting data. 
    */
    boolean aborted = false;

    public void notifyHeader(Header header) throws IOException {
      // delay delivery
      creator.notifyHeader(header);
    }

    public void notifyScreen(ScreenDescriptor screen) throws IOException {
      // delay delivery
      creator.notifyScreen(screen);
    }

    public void notifyExtension(Extension extension) throws IOException {
      if (aborted) return;

      if (extension instanceof GIF.ApplicationExtension) {
	// ignore all application extensions, new ones are introduced
	// if necessary.
	return;
      }

      switch (mode) {
      case MODE_WIPE_OUT:
	if (extension instanceof GIF.ControlExtension) {
	  ControlExtension control = (ControlExtension) extension;

	  if (imageCnt > 0) {
	    // oops, we did not detect that animation. Simply stop it.
	    super.notifyTrailer();
	    out.flush();

	    //throw new AbortException();
	    aborted = true;
	  }

	  if (
	    (control.delayTime > 0) || 
	    (control.disposalMethod != GIF.ControlExtension.DISPOSAL_NONE)
	  ) {
	    // stop download, replace with empty gif
	    new EmptyGIF(gif.screen.width, gif.screen.height, 2).write(out);
	    out.flush();

	    //throw new AbortException();
	    aborted = true;
	  }
	}
	break;

      case MODE_SHOW_FIRST:
      case MODE_SHOW_LAST:
	if (extension instanceof GIF.ControlExtension) {
	  GIF.ControlExtension control = (GIF.ControlExtension) extension;

	  control.disposalMethod = GIF.ControlExtension.DISPOSAL_NONE;
	  control.userInputFlag  = GIF.ControlExtension.USERINPUT_NO;
	  control.delayTime = 0;
	}
	break;

      case MODE_INTERACTIVE:
	if (extension instanceof GIF.ControlExtension) {
	  GIF.ControlExtension control = (GIF.ControlExtension) extension;
	  control.userInputFlag = GIF.ControlExtension.USERINPUT_YES;
	  control.delayTime = 0;
	}
	break;

      case MODE_ANIMATION:
	// break
	break;

      default:
	// do nothing
	break;
      }

      creator.notifyExtension(extension);
    }

    private boolean forwardData;

    public void notifyImage(ImageDescriptor image) throws IOException {
      if (aborted) return;

      if (imageCnt == 0) {
	// forward header data
	super.notifyHeader(gif.header);
	super.notifyScreen(gif.screen);
      }

      switch (mode) {
      case MODE_SHOW_FIRST:
	if (imageCnt > 0) {
	  // stop download, finish gif stream immediately
	  super.notifyTrailer();
	  out.flush();

	  //throw new AbortException();
	  aborted = true;
	  forwardData = false;
	} else {
	  forwardData = true;
	}
	break;

      case MODE_SHOW_LAST:
	// delete all images seen so far
	gif.image = null;

	// don't know wether this is the last image, only buffer image
	// data
	forwardData = false;
	break;

      case MODE_ANIMATION:
	if (imageCnt == 0) {
	  // add application extension with loop=1
	  GIF.NetscapeExtension ext = new GIF.NetscapeExtension(loopCnt);
	  ext.next = creator.firstExtension;

	  creator.firstExtension = ext;
	}
	forwardData = true;
	break;

      default:
	forwardData = true;
	break;
      }

      if (forwardData) {
	// forward extensions seen so far
	if (creator.firstExtension != null) {
	  GIF.writeData(out, creator.firstExtension);
	}

	// forward image descriptor
	super.notifyImage(image);
      }

      imageCnt++;

      creator.notifyImage(image);
    }

    public void notifyImageData(ImageData data) throws IOException {
      if (aborted) return;
      if (forwardData) super.notifyImageData(data);

      creator.notifyImageData(data);
    }

    public void notifyData(Data data) throws IOException {
      if (aborted) return;
      if (forwardData) super.notifyData(data);

      creator.notifyData(data);
    }

    public void notifyTrailer() throws IOException {
      if (aborted) return;

      switch (mode) {
      case MODE_SHOW_LAST:
	// write out the image cached last
	GIF.writeData(out, gif.image.extension);
	super.notifyImage(gif.image);
	GIF.writeData(out, gif.image.data);
	break;
      }

      super.notifyTrailer();

      creator.notifyTrailer();
    }

  }
}

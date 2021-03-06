/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.sikuli.basics.proxies;

public class OCRParagraphs {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected OCRParagraphs(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(OCRParagraphs obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        VisionProxyJNI.delete_OCRParagraphs(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public OCRParagraphs() {
    this(VisionProxyJNI.new_OCRParagraphs__SWIG_0(), true);
  }

  public OCRParagraphs(long n) {
    this(VisionProxyJNI.new_OCRParagraphs__SWIG_1(n), true);
  }

  public long size() {
    return VisionProxyJNI.OCRParagraphs_size(swigCPtr, this);
  }

  public long capacity() {
    return VisionProxyJNI.OCRParagraphs_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    VisionProxyJNI.OCRParagraphs_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return VisionProxyJNI.OCRParagraphs_isEmpty(swigCPtr, this);
  }

  public void clear() {
    VisionProxyJNI.OCRParagraphs_clear(swigCPtr, this);
  }

  public void add(OCRParagraph x) {
    VisionProxyJNI.OCRParagraphs_add(swigCPtr, this, OCRParagraph.getCPtr(x), x);
  }

  public OCRParagraph get(int i) {
    return new OCRParagraph(VisionProxyJNI.OCRParagraphs_get(swigCPtr, this, i), false);
  }

  public void set(int i, OCRParagraph val) {
    VisionProxyJNI.OCRParagraphs_set(swigCPtr, this, i, OCRParagraph.getCPtr(val), val);
  }

}

package com.linkedin.pinot.query.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.linkedin.pinot.index.segment.ColumnarReader;


public class SimpleColumnarReader implements ColumnarReader {

  private int[] _intArray = null;

  public SimpleColumnarReader(int[] intArray) {
    _intArray = intArray;
  }

  public static ColumnarReader readFromFile(int numRecords, File file) throws IOException {
    int[] intArray = new int[numRecords];
    FileInputStream fis = new FileInputStream(file);
    byte[] byteArray = new byte[numRecords * 4];
    fis.read((byte[]) byteArray);
    fis.close();
    ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
    for (int i = 0; i < numRecords; ++i) {
      intArray[i] = byteBuffer.getInt();
    }
    SimpleColumnarReader simpleColumnReader = new SimpleColumnarReader(intArray);
    return simpleColumnReader;
  }

  @Override
  public int getIntegerValue(int docId) {
    return _intArray[docId];
  }

  @Override
  public long getLongValue(int docId) {
    return (long) _intArray[docId];
  }

  @Override
  public float getFloatValue(int docId) {
    return (float) _intArray[docId];
  }

  @Override
  public double getDoubleValue(int docId) {
    return (double) _intArray[docId];
  }

  @Override
  public String getStringValue(int docId) {
    return Integer.toString(_intArray[docId]);
  }

  @Override
  public Object getRawValue(int docId) {
    return _intArray[docId];
  }

}
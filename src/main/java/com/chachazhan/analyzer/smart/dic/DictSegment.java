package com.chachazhan.analyzer.smart.dic;

import lombok.Getter;
import lombok.experimental.var;
import lombok.val;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jack
 * @date 27/12/2017
 * @time 18:45
 */
public class DictSegment implements Comparable<DictSegment> {

  private static final Map<Character, Character> CHAR_MAP = new ConcurrentHashMap<>(16, 0.95f);
  private static final int ARRAY_LENGTH_LIMIT = 3;

  private Map<Character, DictSegment> childrenMap;
  private DictSegment[] childrenArray;

  @Getter
  private Character nodeChar;

  private int storeSize = 0;
  private int nodeState = 0;

  public DictSegment(Character nodeChar) {
    if (nodeChar == null) {
      throw new NullPointerException("节点字符不能为null");
    }
    this.nodeChar = nodeChar;
  }

  public boolean hasNextNode() {
    return this.storeSize > 0;
  }

  public Hit match(char[] charArray) {
    return this.match(charArray, 0, charArray.length, null);
  }

  public Hit match(char[] charArray, int begin, int length) {
    return this.match(charArray, begin, length, null);
  }

  public Hit match(char[] charArray, int begin, int length, Hit searchHit) {
    if (searchHit == null) {
      searchHit = new Hit();
      searchHit.setBegin(begin);
    } else {
      searchHit.setUnmatch();
    }
    searchHit.setEnd(begin);

    DictSegment ds = null;
    DictSegment[] segmentArray = this.childrenArray;
    Map<Character, DictSegment> segmentMap = this.childrenMap;

    var keyChar = charArray[begin];
    if (segmentArray != null) {
      val keySegment = new DictSegment(keyChar);
      var position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
      if (position >= 0) {
        ds = segmentArray[position];
      }
    } else if (segmentMap != null) {
      ds = segmentMap.get(keyChar);
    }

    if (ds != null) {
      if (length > 1) {
        return ds.match(charArray, begin + 1, length - 1, searchHit);
      } else if (length == 1) {
        if (ds.nodeState == 1) {
          searchHit.setMatch();
        }
        if (ds.hasNextNode()) {
          searchHit.setPrefix();
          searchHit.setMatchedDictSegment(ds);
        }
        return searchHit;
      }
    }
    return searchHit;
  }

  public void fillSegment(char[] charArray) {
    this.fillSegment(charArray, 0, charArray.length, 1);
  }

  public void disableSegment(char[] charArray) {
    this.fillSegment(charArray, 0, charArray.length, 0);
  }

  private synchronized void fillSegment(char[] charArray, int begin, int length, int enabled) {
    Character beginChar = charArray[begin];
    Character keyChar = CHAR_MAP.get(beginChar);
    if (keyChar == null) {
      CHAR_MAP.put(beginChar, beginChar);
      keyChar = beginChar;
    }

    val ds = lookForSegment(keyChar, enabled);
    if (ds != null) {
      if (length > 1) {
        ds.fillSegment(charArray, begin + 1, length - 1, enabled);
      } else if (length == 1) {
        ds.nodeState = enabled;
      }
    }
  }

  private DictSegment lookForSegment(char keyChar, int create) {
    DictSegment ds = null;
    if (this.storeSize <= ARRAY_LENGTH_LIMIT) {
      val segmentArray = getChildrenArray();
      val keySegment = new DictSegment(keyChar);
      int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
      if (position >= 0) {
        ds = segmentArray[position];
      }

      if (ds == null && create == 1) {
        ds = keySegment;
        if (this.storeSize < ARRAY_LENGTH_LIMIT) {
          segmentArray[this.storeSize] = ds;
          this.storeSize++;
          Arrays.sort(segmentArray, 0, this.storeSize);
        } else {
          val segmentMap = getChildrenMap();
          migrate(segmentArray, segmentMap);
          segmentMap.put(keyChar, ds);
          this.storeSize++;
          this.childrenArray = null;
        }
      }
    } else {
      val segmentMap = getChildrenMap();
      ds = segmentMap.get(keyChar);
      if (ds == null && create == 1) {
        ds = new DictSegment(keyChar);
        segmentMap.put(keyChar, ds);
        this.storeSize++;
      }
    }
    return ds;
  }

  private DictSegment[] getChildrenArray() {
    synchronized (this) {
      if (this.childrenArray == null) {
        this.childrenArray = new DictSegment[ARRAY_LENGTH_LIMIT];
      }
    }
    return this.childrenArray;
  }

  private Map<Character, DictSegment> getChildrenMap() {
    synchronized (this) {
      if (this.childrenMap == null) {
        this.childrenMap = new ConcurrentHashMap<>(ARRAY_LENGTH_LIMIT * 2, 0.8f);
      }
    }
    return this.childrenMap;
  }

  private void migrate(DictSegment[] segmentArray, Map<Character, DictSegment> segmentMap) {
    for (DictSegment dictSegment : segmentArray) {
      if (dictSegment != null) {
        segmentMap.put(dictSegment.nodeChar, dictSegment);
      }
    }
  }

  @Override
  public int compareTo(DictSegment other) {
    return this.nodeChar.compareTo(other.nodeChar);
  }
}

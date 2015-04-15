package im.tox.tox4j.core

import im.tox.tox4j.ToxCoreImplTestBase
import im.tox.tox4j.ToxCoreTestBase
import im.tox.tox4j.core.enums.ToxStatus
import im.tox.tox4j.exceptions.ToxException
import org.junit.Test

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import org.junit.Assert._

final class LoadSaveTest extends ToxCoreImplTestBase {

  private trait Check {
    @throws(classOf[ToxException])
    def change(tox: ToxCore) : Boolean
    def check(tox: ToxCore)
  }
  @throws(classOf[Exception])
  private def testLoadSave(check: Check) {
    var moreTests = true
    while (moreTests) {
      var tox = newTox()
      moreTests = check.change(tox)
      var data = tox.save()
      tox = newTox(data)
      check.check(tox)
    }
  }

  @throws(classOf[Exception])
  @Test def testName() {
    testLoadSave(new Check() {
      private var expected: Array[Byte] = null

      @throws(classOf[ToxException])
      override def change(tox: ToxCore): Boolean = {
        if (expected == null) {
          expected = Array[Byte]()
        } else {
          expected = ToxCoreTestBase.randomBytes(expected.length + 1)
        }
        tox.setName(expected)
        return expected.length < ToxConstants.MAX_NAME_LENGTH
      }

      
      override def check(tox: ToxCore) {
        assertArrayEquals(expected, tox.getName())
      }
    })
  }

  @throws(classOf[Exception])
  @Test def testStatusMessage() {
    testLoadSave(new Check() {
      private var expected: Array[Byte] = null

      @throws(classOf[ToxException])
      override def change(tox: ToxCore): Boolean = {
        if (expected == null) {
          expected = Array[Byte]()
        } else {
          expected = ToxCoreTestBase.randomBytes(expected.length + 1)
        }
        tox.setStatusMessage(expected)
        return expected.length < ToxConstants.MAX_NAME_LENGTH
      }

      
      override def check(tox: ToxCore) {
        assertArrayEquals(expected, tox.getStatusMessage())
      }
    })
  }

  @throws(classOf[Exception])
  @Test def testStatus() {
    testLoadSave(new Check() {
      
      private val expected = new ArrayList(Arrays.asList(ToxStatus.values:_*))

      @throws(classOf[ToxException])
      override def change(tox: ToxCore): Boolean = {
        tox.setStatus(expected.get(expected.size() - 1))
        return expected.size() > 1
      }

      
      override def check(tox: ToxCore) {
        assertEquals(expected.remove(expected.size() - 1), tox.getStatus())
      }
    })
  }
 
  @throws(classOf[Exception])
  @Test def testNoSpam() {
    testLoadSave(new Check() {
      private var expected = -1

      @throws(classOf[ToxException])
      override def change(tox: ToxCore): Boolean = {
        expected += 1
        tox.setNospam(expected)
        return expected < 100
      }

      
      override def check(tox: ToxCore) {
        assertEquals(expected, tox.getNospam())
      }
    })
  }
 
  @throws(classOf[Exception])
  @Test def testFriend() {
    testLoadSave(new Check() {
      private var expected: Int = 1

      @throws(classOf[ToxException])
      override def change(tox: ToxCore): Boolean = {
        var toxFriend = newTox()
        expected = tox.addFriend(toxFriend.getAddress(), "hello".getBytes)
        return false
      }
 
    
      override def check(tox: ToxCore) {
        assertEquals(1, tox.getFriendList().length)
        assertEquals(expected, tox.getFriendList()(0))
      }
    })
  }
  
  @throws(classOf[Exception])
  @Test def testSaveNotEmpty() {
    var tox = newTox()
    var data = tox.save()
    assertNotNull(data)
    assertNotEquals(0, data.length)
  }

  @throws(classOf[Exception])
  @Test def testSaveRepeatable() {
    var tox = newTox()
    assertArrayEquals(tox.save(), tox.save())
  }

  @throws(classOf[Exception])
  @Test def testLoadSave1() {
    var data = newTox().save()
    assertArrayEquals(newTox(data).save(), newTox(data).save())
  }

  @throws(classOf[Exception])
  @Test def testLoadSave2() {
    var data = newTox().save()
    assertEquals(data.length, newTox(data).save().length)
  }

  @throws(classOf[Exception])
  @Test def testLoadSave3() {
    var data = newTox().save()
    assertArrayEquals(data, newTox(data).save())
  }

}

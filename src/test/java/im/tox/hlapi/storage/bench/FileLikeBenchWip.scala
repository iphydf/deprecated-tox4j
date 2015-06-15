package im.tox.hlapi.storage.bench

import im.tox.tox4j.bench.{ Confidence, TimingReport }
import org.scalameter.api._
import org.scalameter.Log

import java.io.{ File, RandomAccessFile }
import java.nio.channels.FileChannel.MapMode
import im.tox.hlapi.storage.{ MappedFile, TempMappedFile }

final class FileLikeBench extends TimingReport {
  //  protected override def confidence = Confidence.high

  private val fileLike =
    Gen.range("size (KB)")(100, 10500, 400).map(TempMappedFile.apply).cached

  timing of "FileLike" in {

    measure method "get" in {
      using(fileLike) in { x =>
        val slice = x(0.toLong, x.size.toInt).orNull
        for (i <- 0 to x.size.toInt - 1) {
          x.get(slice)(i)
        }
      }
    }

    measure method "set" in {
      using(fileLike) in { x =>
        val slice = x(0.toLong, x.size.toInt).orNull
        for (i <- 0 to x.size.toInt - 1) {
          x.set(slice)(i, i.toByte)
        }
      }
    }

    measure method "traverse" in {
      using(fileLike) in { x =>
        val slice = x(0.toLong, x.size.toInt).orNull
        x.traverse(slice).foreach(c => ())
      }
    }

  }
}

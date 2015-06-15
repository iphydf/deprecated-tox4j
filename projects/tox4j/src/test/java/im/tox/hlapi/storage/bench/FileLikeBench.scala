package im.tox.hlapi.storage.bench

import im.tox.hlapi.storage.{ MappedFile, TempMappedFile }

import im.tox.tox4j.bench.TimingReport
import org.scalameter.api._
import org.scalameter.Log

import scala.collection.immutable.Stream

final class FileLikeBench extends TimingReport {
  private val fileLike = {
    Gen
      .range("size (KB)")(100, 10500, 400)
      .map { x => TempMappedFile(x.toLong * 1024L) }
      .cached
  }

  timing of "FileLike" in {

    measure method "readByte" in {
      using(fileLike) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        for (i <- 0 to file.size.toInt - 1) {
          file.readByte(slice)(i)
        }
      }
    }

    measure method "writeByte" in {
      using(fileLike) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        for (i <- 0 to file.size.toInt - 1) {
          file.writeByte(slice)(i, i.toByte)
        }
      }
    }

    measure method "readSeq" in {
      using(fileLike) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        file.readSeq(slice).foreach(c => ())
      }
    }

    measure method "writeSeq" in {
      using(fileLike) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        // TODO(nbraud) This is waaaay too slow.
        val data = Stream.fill(file.size.toInt)(42.toByte)
        file.writeSeq(slice)(0, data)
      }
    }

  }
}

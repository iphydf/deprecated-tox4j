package im.tox.hlapi.state

import scalaz.Lens

object FileState {

  final case class FileList(files: Map[Int, File] = Map[Int, File]())

  final case class File(
    fileName: String,
    fileData: Array[Byte],
    fileKind: FileKind,
    fileSendStatus: FileStatus = RequestInitiated()
  )
  sealed abstract class FileKind
  final case class Data() extends FileKind
  final case class Avatar() extends FileKind

  sealed abstract class FileStatus
  final case class RequestInitiated() extends FileStatus
  final case class RequestSent() extends FileStatus
  final case class RequestAccepted() extends FileStatus
  final case class InTransmission() extends FileStatus
  final case class Paused() extends FileStatus
  final case class FileReceived() extends FileStatus
  final case class FileSent() extends FileStatus

  val fileFileStatusL = Lens.lensu[File, FileStatus](
    (a, value) => a.copy(fileSendStatus = value),
    _.fileSendStatus
  )

  val fileListFilesL = Lens.lensu[FileList, Map[Int, File]](
    (a, value) => a.copy(files = value),
    _.files
  )
}

package im.tox.client

import scala.language.postfixOps

case object TestClient extends App {

  // From ArmagetronAd's config/aiplayers.cfg.in
  private val names = Seq(
    "Outlook 3" -> "Anyone want to send me a postcard?",
    "Notepad 9" -> "Keeping track of important business.",
    "Word" -> "Writer's block :(",
    "Excel" -> "Spreadsheets everywhere!",
    "Emacs" -> "I'm better than Vi!",
    "Vi" -> "I'm better than Emacs!",
    "Pine" -> "Mutt. Nice mutt.",
    "Elm" -> "Professor, please. Have you seen Oak?",
    "LaTeX" -> "I'm the most beautiful thing you've ever seen.",
    "TeX" -> "You kids with your fancy macros all need Jesus.",
    "Gcc" -> "Internal compiler error; compilation completed with severe errors",
    "Gdb" -> "I've seen things you people wouldn't believe.",
    "MSVC++ 6" -> "I am something you people wouldn't believe",
    "Photoshop 2" -> "Let's just draw a happy little tree here",
    "Gimp" -> "Guile guile guile.",
    "Windows 7" -> "I'm a serious businessman.. oh look, a butterfly!",
    "Linux" -> "It's a unix system, I know this!",
    "Unreal 10" -> "Please don't kill me :/",
    "Quake" -> "Bring it on, noobs! *dies by rocket*"
  )

}

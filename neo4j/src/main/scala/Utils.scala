import java.io.File

object FileUtils {
  def removeAll(path: String) =
    listRecursively(new File(path)) foreach { f ⇒
      if (!f.delete) throw new RuntimeException("Failed to delete " + f.getAbsolutePath)
    }

  def listRecursively(f: File): Seq[File] =
    if (f.exists)
      f.listFiles.filter(_.isDirectory).flatMap(listRecursively) ++ f.listFiles
    else Nil
}

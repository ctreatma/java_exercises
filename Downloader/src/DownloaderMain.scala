import scala.actors.Actor
import scala.actors.Actor._
import scala.io.Source
import java.util.Queue
import java.util.concurrent.LinkedBlockingQueue

object DownloaderMain {
	def main(args: Array[String]) {
		args.toList match {
		case url :: ignored => {
			chooseDirectory() match {
			case Some(directory) => {
				val manager = new DownloadManager(url, directory);
				manager.start
				waitForQuit(manager);
			}
			case None => return
			}
		}
		case _ => return
		}
	}

	def waitForQuit(manager: DownloadManager) {
		print("Type q or Q to quit at any time: ")
		val input = readLine
		if (input.trim.equalsIgnoreCase("q")) manager ! "quit"
		else waitForQuit(manager);
	}

	def chooseDirectory() = {
		import scala.swing.FileChooser
		val chooser = new FileChooser()
		chooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
		val theFileResult = chooser.showOpenDialog(null)
		theFileResult match {
		case FileChooser.Result.Approve => Some(chooser.selectedFile)
		case _ => None
		}
	}
}

class DownloadManager(url:String, directory:java.io.File) extends Actor {
	val urlQueue = new LinkedBlockingQueue[String]()
	val parser = new Parser(this)

	def act() {
		var seenUrls = List[String]()
		parser.start
		val downloaders = (1 to 10) map (_ => new Downloader(urlQueue, directory, parser))
		for (downloader <- downloaders) downloader.start

		urlQueue add url

		loop {
			for (downloader <- downloaders) downloader ! "continue"

			receiveWithin(2000) {
			case (url:String, newUrls:List[String]) => {
				seenUrls = url :: seenUrls
				newUrls.filter(s => !(seenUrls contains s) &&
						!(s matches "\\w+://.*") && !(s startsWith "/") &&
						!(s contains "#") && !(s contains "?") && !(s contains "..")).
						map(s => urlQueue.add(url.substring(0, url.lastIndexOf("/")+1) + s))
			}
			case "quit" => {			
				for (downloader <- downloaders) downloader ! "quit"
				parser ! "quit"
				exit()
			}
			}
		}
	}
}

class Downloader(urlQueue:Queue[String], directory:java.io.File, parser:Parser) extends Actor {
	def act() {
		loop {
			react {
			case "continue" => {
				val url = urlQueue.poll
				if (url != null) {
					try {
						val inStream = new java.net.URL(url).openStream
						val contents = Stream.continually(inStream.read).takeWhile(_ >= 0).map(_.toByte)
						val lines = Source.fromBytes(contents.toArray).getLines().toList
						parser ! (url, lines)

						val path = url.split("/").filter(s => !(s matches "\\w+://.*") && !(s startsWith "http:") && !(s contains ".")).mkString("/");
						val fileName = url.split("/").last

						println("Creating directory: " + path);
						val newDirectory = new java.io.File(directory + "/" + path)
						val success = newDirectory.mkdirs()

						println("Creating file: " + fileName);
						val file = new java.io.File(newDirectory + "/" + fileName)
						val outStream = new java.io.FileOutputStream(file)
						outStream.write(contents.toArray)
					}
					catch {
					case _ => println("Had trouble getting url: " + url + ".\nSkipping that url.")
					}
				}
			}
			case "quit" => {
				exit()
			}
			}
		}
	}
}

class Parser(manager:DownloadManager) extends Actor {
	val HtmlPattern = """(?i).*<\s*(a)\s+.*href\s*=\s*['"]?([^'" ]+\.html?)['" >].*""".r;
	val ImgPattern = """(?i).*<\s*(img)\s+.*src\s*=\s*['"]?([^'" ]+\.(gif|jpg|png))['" >].*""".r;

	def act() {
		loop {
			react {
			case (url:String, page:List[String]) => {
				val newUrls = for (line <- page) yield
				line match {
				case HtmlPattern(tag, url) => url
				case ImgPattern(tag, url, ext) => url
				case _ => ""
				}
				manager ! (url, newUrls.filter(_.length > 0))
			}
			case "quit" => {
				exit()
			}
			}
		}
	}
}
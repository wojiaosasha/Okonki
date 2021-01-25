import javafx.application.Application
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.media.MediaPlayer
import javafx.scene.media.Track
import javafx.stage.Stage
import java.io.File
import javafx.event.EventHandler

open class PlayerApp : Application() {

    private val filefxml = "player.fxml"

    @Throws(Exception::class)
    override fun start(primaryStage: Stage?) {

        //primaryStage?.scene = Scene(load<Parent?>(Main.javaClass.getResource(filefxml)))

        var root = Scene(load<Parent?>(Main.javaClass.getResource(filefxml)))
        var control : Controller = FXMLLoader( PlayerApp::class.java.getResource("player.fxml")).getController()

        primaryStage?.onShown = EventHandler { e ->
            control.initialize()
        }

        control.title_song.text = "eee"

        primaryStage?.show()

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}
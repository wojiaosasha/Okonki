import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class Main : Application() {

    //val layout = "/resources/Test.fxml"
    //val layout = "C:/Users/Sasha/IdeaProjects/Player/src/main/resources/Test.fxml"
    val layout = "player.fxml"

    override fun start(primaryStage: Stage?) {

        primaryStage?.scene = Scene(load<Parent?>(Main.javaClass.getResource(layout)))

        primaryStage?.show()

    }

    companion object {
        var a = 42
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}
import javafx.beans.Observable
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.Track
import javafx.stage.FileChooser
import java.io.File
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Stage

open class View {

    @FXML
    internal var slider1 = Slider()

    @FXML
    internal var slider2 = Slider()

    @FXML
    internal var previous = Button()

    @FXML
    internal var play = Button()

    @FXML
    internal var stop = Button()

    @FXML
    internal var next = Button()

    @FXML
    internal var title_song = Label()

    @FXML
    internal var adding = ToggleButton()

    @FXML
    internal var time = Label()

    @FXML
    internal var table : TableView<TracksForTable> = TableView()



    @FXML
    fun funny() {
        title_song.text = "rab"
        adding.setOnAction { e->
            title_song.text = "knopka"
        }
        slider1.min = 0.0
        slider1.max = 20.0


        Thread(Runnable {
        }).start()
    }


}

public class TracksForTable(c : Int, file : File) {
    //internal var n : IntegerProperty = IntegerProperty.integerProperty()
    internal var url = file.toURI().toString()
    //internal var name : StringProperty = file.name
    internal var duration : Double = 0.0
    init {
        //n.set(c)

    }
}

public class Controller : View() {
    //internal var tracks : ArrayList<File> = ArrayList()
    internal var number : Int = 0
    internal var mplayer: MediaPlayer? = null
    internal  var tracks : ObservableList<TracksForTable> =  FXCollections.observableArrayList()

    fun startPlaying(num : Int) {
        mplayer?.stop()
        mplayer = MediaPlayer(Media(tracks[number].url))
        mplayer?.play()
        mplayer?.setOnPlaying {
            tracks[number].duration = mplayer?.media?.duration?.toSeconds()!!
        }

    }

    @FXML
    fun initialize() {
        val primaryStage = Stage()
        val fileChooser = FileChooser()
        fileChooser.title = "Open File"
        fileChooser.extensionFilters.addAll(
            ExtensionFilter("Audio Files", "*.wav", "*.mp3")
        )

        adding.setOnAction { _->
            var song = fileChooser.showOpenDialog(primaryStage)
            if (song != null) {
                tracks.add(TracksForTable(tracks.size, song))
                //tracks.add(TracksForTable())

                number = tracks.size - 1
                startPlaying(number)
            }
            adding.arm()
        }

        val numberColumn = TableColumn<TracksForTable, Int>("№")
        numberColumn.cellValueFactory = PropertyValueFactory<TracksForTable, Int>("n")
        val nameColumn = TableColumn<TracksForTable, String>("name")
        nameColumn.cellValueFactory = PropertyValueFactory<TracksForTable, String>("name")
        val durColumn = TableColumn<TracksForTable, Double>("duration")
        durColumn.cellValueFactory = PropertyValueFactory<TracksForTable, Double>("duration")
        table.columns.addAll(numberColumn, nameColumn, durColumn)//numberColumn, nameColumn, durColumn

        table.setRowFactory { _ ->
            val row: TableRow<TracksForTable> = TableRow()
            row.setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    val rowData: TracksForTable = row.item
                    /*(playButton.text = "Pause"
                    if (tracks.size > 1) {
                        number = rowData.num - 1
                        createMP(tracks[number])
                    }*/
                }
            }
            row
        }
        table.items = tracks

    }


    //protected var obsList : ObservableList<Track> = FXCollections.observableArrayList()
    //protected val table : TableView<Track> = TableView()



}
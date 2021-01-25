import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Stage
import javafx.util.Duration
import java.lang.Math.abs
//import kotlinx.coroutines.Runnable
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader

import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.layout.GridPane
import javafx.collections.FXCollections
import javafx.scene.control.cell.PropertyValueFactory


import java.io.File
import java.util.regex.Pattern
import javax.swing.event.ChangeListener
import kotlin.math.absoluteValue
import kotlin.time.toDuration
//import sun.security.x509.OIDMap.getClass

open class MPlayer : Application() {
    internal var tracks : ArrayList<File> = ArrayList()
    internal var number : Int = 0
    //internal var selectedFile: File? = null
    internal var mplayer: MediaPlayer? = null
    internal var musicSlider: Slider = Slider()
    private var sliderChange: Boolean = true
    private val filenameLabel = Label("")
    protected var obsList : ObservableList<Track> = FXCollections.observableArrayList()
    protected val table : TableView<Track> = TableView()


    class  Track(file : File, number : Int) : MPlayer() {
        var url : String = file.toURI().toString()
        var dur : Double = 0.0
        var duration = "Unknown"
        var name : String? = file.name
        var num : Int = number
        init {
            val media = Media(url)
            val mp = MediaPlayer(media)
            //dr.addListener { _,_,_ ->
            //    //table.items = obsList
            //    println("Changed: ${dr.value}")
            //}
            mp.setOnReady {
                dur = mp.media?.duration?.toSeconds()!!.toDouble()
                duration = (dur/60).toInt().toString() + ":" + (dur%60).toInt().toString()
                table.items = obsList
            }
        }
    }
    //private val layout = "/resources/player.fxml"

    //var loader : FXMLLoader = FXMLLoader()

    internal var volumeSlider: Slider = Slider(0.0, 1.0, 0.5)

    private fun createMP(selected : File) {
        mplayer?.stop()
        var media: Media? = null
        val url = selected.toURI()
        println(url.toString())
        media = Media(url.toString())

        mplayer = MediaPlayer(media)

        //volumeSlider.value = volume
        mplayer?.volume = volumeSlider.value

        filenameLabel.text = selected.name

        musicSlider.min = 0.0
        //musicSlider.max = mplayer?.media?.duration!!.toSeconds()
        musicSlider.max = 100.0
        mplayer?.isAutoPlay = true
        if (tracks.size > 1) {
            mplayer?.setOnEndOfMedia {
                if (number == tracks.size-1)
                    number=0
                else
                    number++
                createMP(tracks[number])
            }
        }
        else {
            mplayer?.cycleCount = 100
            mplayer?.setOnEndOfMedia {
                mplayer?.pause()
                mplayer?.startTime = Duration.seconds(0.0)
                mplayer?.play()
            }

        }
    }

    fun safeSliderChange(Value : Double) {
        sliderChange = false
        musicSlider.value = Value
        sliderChange = true
    }

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {

        //var loader : FXMLLoader = FXMLLoader()
        //loader.location (getClass().getResource("Player.fxml"))

        val playButton = Button("Pause")

        var root = object : BorderPane() {      //val
            init {
                //val filenameLabel = Label("")
                val fileChooser = FileChooser()
                fileChooser.title = "Open File"

                fileChooser.extensionFilters.addAll(
                    ExtensionFilter("Audio Files", "*.wav", "*.mp3")
                )

                val vbox = object : VBox() {
                    init {
                        children.add(filenameLabel)
                        val hbox = object : HBox() {
                            init {

                                playButton.setOnAction {
                                    e->
                                    if (mplayer != null) {
                                        if (mplayer?.status.toString() != "PLAYING") {
                                            //(playButton.text == "Play") - корректно
                                            //(mplayer?.status.toString() == "PAUSED") не включает STOPPED
                                            mplayer?.play()
                                            playButton.text = "Pause"
                                            //filenameLabel.text = mplayer!!.media.duration.toString()
                                        } else {
                                            mplayer?.pause()
                                            playButton.text = "Play"
                                        }
                                    }
                                }

                                val stopButton = object : Button("Stop") {
                                    init {
                                        setOnAction {
                                                e -> mplayer?.stop()
                                            playButton.text = "Play"         //playButton.fire()
                                            mplayer?.startTime = Duration.seconds(0.0)
                                            safeSliderChange(0.0)
                                        }
                                    }
                                }

                                val prevButton = object : Button("Prev") {
                                    init {
                                        setOnAction {
                                                e ->
                                            playButton.text = "Pause"
                                            if (tracks.size > 1) {
                                                if (number == 0)
                                                    number = tracks.size - 1
                                                else
                                                    number--
                                                createMP(tracks[number])
                                            }
                                            else {
                                                mplayer?.stop()
                                                //mplayer?.startTime = Duration.seconds(5.0)
                                                mplayer?.play()
                                            }
                                        }
                                    }
                                }

                                val nextButton = object : Button("Next") {
                                    init {
                                        setOnAction {
                                                e ->
                                            playButton.text = "Pause"
                                            if (tracks.size > 1) {
                                                if (number == tracks.size-1)
                                                    number = 0
                                                else
                                                    number++
                                                createMP(tracks[number])
                                            }
                                            else {
                                                mplayer?.stop()
                                                mplayer?.play()
                                            }
                                        }
                                    }
                                }
                                /*var sl = object : BorderPane() {
                                    init {
                                        children.add(volumeSlider)
                                    }
                                }

                                 */


                                children.addAll(prevButton, playButton, stopButton, nextButton)
                            }
                        }
                        children.add(hbox)
                        //var table: TableView<String> = TableView()
                        //children.add(table)

                        var gridpane = object : GridPane() {
                            init {
                                hgap = 50.0
                                children.add(volumeSlider)
                                spacing = 3.0
                            }
                        }
                        //volumeSlider.width = 50.0
                        children.add(gridpane)

                    }
                }
                center = vbox

                val menubar = object : MenuBar() {
                    init {
                        val menu = object : Menu("File") {
                            init {
                                val selectMenuItem = object : MenuItem("Select") {
                                    init {
                                        setOnAction { e ->

                                            //selectedFile = fileChooser.showOpenDialog(primaryStage)

                                            tracks.add(fileChooser.showOpenDialog(primaryStage)!!)
                                            obsList.add(Track(tracks[tracks.size-1], tracks.size))

                                            if (tracks.size == 1)
                                                createMP(tracks[number])
                                            //obsList.add(Track(tracks[0], 0))
                                            //obsList.removeAt(obsList.size-1)
                                        }
                                    }
                                }


                                //val pauseMenuItem = MenuItem("Pause")
                                //val playMenuItem = MenuItem("Play")
                                //val stopMenuItem = MenuItem("Stop")

                                //items.addAll(selectMenuItem, playMenuItem, pauseMenuItem, stopMenuItem)

                                items.add(selectMenuItem)

                            }
                        }
                        menus.add(menu)
                        /*var table : TableView<String> = TableView()
                        menus.add(table)*/
                    }

                }
                top = menubar

                val bttm = object : VBox() {
                    init {
                        val numberColumn = TableColumn<Track, Int>("№")
                        numberColumn.cellValueFactory = PropertyValueFactory<Track, Int>("num")
                        val nameColumn = TableColumn<Track, String>("Name")
                        nameColumn.cellValueFactory = PropertyValueFactory<Track, String>("name")
                        val durColumn = TableColumn<Track, String>("Duration")
                        durColumn.cellValueFactory = PropertyValueFactory<Track, String>("duration")
                        table.columns.addAll(numberColumn, nameColumn, durColumn)

                        table.setRowFactory { _ ->
                            val row: TableRow<Track> = TableRow()
                            row.setOnMouseClicked { event ->
                                if (event.clickCount == 2) {
                                    val rowData: Track = row.item
                                    playButton.text = "Pause"
                                    if (tracks.size > 1) {
                                        number = rowData.num - 1
                                        createMP(tracks[number])
                                    }

                                    //obsList[0].name = "AAAAAAAA" ///test
                                }
                            }
                            row
                        }



                        table.items = obsList

                        children.addAll(musicSlider, table)
                    }
                }
                //bottom = musicSlider
                bottom = bttm

                    musicSlider.valueProperty().addListener { _, _, new ->
                    //настроить сброс startTime

                    if (sliderChange) {
                        var t: Double? = mplayer?.media?.duration?.toSeconds()
                        if (t != null) {
                            t = t!! / 100 * new.toDouble() //musicSlider.value
                            mplayer?.startTime = Duration.seconds(t)
                            if (mplayer?.status.toString() == "PLAYING") {
                                mplayer?.stop()
                                mplayer?.play()
                                //mplayer?.startTime = Duration.seconds(0.0)
                            }
                        }
                    }
                }

                volumeSlider.valueProperty().addListener { _, _, new ->
                    mplayer?.volume = new.toDouble()
                }
            }
        }

        //root = FXMLLoader.load(getClass().getResource("resources/player.fxml"))

        Thread(Runnable {
            while (true) {
                if (mplayer != null) {
                    val currentTime = mplayer?.currentTime!!.toSeconds()
                    val allTime =  mplayer?.stopTime!!.toSeconds()

                    safeSliderChange(currentTime * 100.0 / allTime)
                    //musicSlider.value = currentTime * 100.0 / allTime

                    println("Cur time " + currentTime * 100.0 / allTime)
                }
                try {
                    Thread.sleep(900)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()


        val scene = Scene(root, 400.0, 100.0)

        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MPlayer::class.java)
        }
    }
}

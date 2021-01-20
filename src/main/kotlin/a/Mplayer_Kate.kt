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
import javafx.collections.ObservableList
import javafx.scene.layout.GridPane
import javafx.collections.FXCollections
import javafx.scene.control.cell.PropertyValueFactory
import java.io.File


open class Mplayerk : Application() {
    internal var tracks : ArrayList<File> = ArrayList()
    internal var number : Int = 0
    internal var mplayer: MediaPlayer? = null
    internal var musicSlider: Slider = Slider()
    private var sliderChange: Boolean = true
    private val filenameLabel = Label("")
    protected var obsList : ObservableList<Track> = FXCollections.observableArrayList()
    protected val table : TableView<Track> = TableView()


    class  Track(file : File, number : Int) : Mplayerk() {
        var url : String = file.toURI().toString()
        var dur : Double = 0.0
        var name : String? = file.name
        var num : Int = number
        init {
            val media = Media(url)
            val mp = MediaPlayer(media)
            mp.setOnReady {
                dur = mp.media?.duration?.toSeconds()!!.toDouble()
                table.items = obsList
            }
        }
    }

    internal var volumeSlider: Slider = Slider(0.0, 1.0, 0.5)

    private fun create(selected : File) {
        mplayer?.stop()
        var media: Media? = null
        val url = selected.toURI()
        println(url.toString())
        media = Media(url.toString())

        mplayer = MediaPlayer(media)

        mplayer?.volume = volumeSlider.value

        filenameLabel.text = selected.name

        musicSlider.min = 0.0
        musicSlider.max = 100.0
        mplayer?.isAutoPlay = true
        if (tracks.size > 1) {
            mplayer?.setOnEndOfMedia {
                if (number == tracks.size-1)
                    number=0
                else
                    number++
                create(tracks[number])
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

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {

        var root = object : BorderPane() {
            init {
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

                                val playButton =  object : Button("Play") {
                                    init {
                                        setOnAction { e->
                                            mplayer?.play()
                                        }
                                    }
                                }

                                val pauseButton = object : Button("Pause") {
                                    init {
                                        setOnAction { e->
                                            mplayer?.pause()
                                        }
                                    }
                                }

                                val stopButton = object : Button("Stop") {
                                    init {
                                        setOnAction {
                                                e -> mplayer?.stop()
                                            mplayer?.startTime = Duration.seconds(0.0)
                                            sliderChange = false
                                            musicSlider.value = 0.0
                                            sliderChange = true
                                        }
                                    }
                                }

                                val prevButton = object : Button("Prev") {
                                    init {
                                        setOnAction {
                                                e ->
                                            if (tracks.size > 1) {
                                                if (number == 0)
                                                    number = tracks.size - 1
                                                else
                                                    number--
                                                create(tracks[number])
                                            }
                                        }
                                    }
                                }

                                val nextButton = object : Button("Next") {
                                    init {
                                        setOnAction {
                                                e ->
                                            if (tracks.size > 1) {
                                                if (number == tracks.size-1)
                                                    number = 0
                                                else
                                                    number++
                                                create(tracks[number])
                                            }
                                        }
                                    }
                                }

                                children.addAll(pauseButton, playButton, stopButton, prevButton, nextButton)

                                val gridpane = object : GridPane() {
                                    init {
                                        hgap = 50.0
                                        children.add(volumeSlider)
                                        spacing = 3.0
                                    }
                                }
                                children.add(gridpane)
                            }
                        }
                        children.add(hbox)
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

                                            tracks.add(fileChooser.showOpenDialog(primaryStage)!!)
                                            obsList.add(Track(tracks[tracks.size-1], tracks.size))

                                            if (tracks.size == 1)
                                                create(tracks[number])
                                        }
                                    }
                                }


                                val pauseMenuItem = object : MenuItem("Pause") {
                                    init {
                                        setOnAction {
                                            mplayer?.pause()
                                        }
                                    }
                                }
                                val playMenuItem = object : MenuItem("Play") {
                                    init {
                                        setOnAction {
                                            mplayer?.play()
                                        }
                                    }
                                }
                                val stopMenuItem = object : MenuItem("Stop") {
                                    init {
                                        setOnAction {
                                            mplayer?.stop()
                                        }
                                    }
                                }

                                items.addAll(selectMenuItem, playMenuItem, pauseMenuItem, stopMenuItem)

                            }
                        }
                        menus.add(menu)

                    }

                }
                top = menubar

                val bm = object : VBox() {
                    init {
                        val numberColumn = TableColumn<Track, Int>("№")
                        numberColumn.cellValueFactory = PropertyValueFactory<Track, Int>("num")
                        val nameColumn = TableColumn<Track, String>("Name")
                        nameColumn.cellValueFactory = PropertyValueFactory<Track, String>("name")
                        val durColumn = TableColumn<Track, Double>("Duration")
                        durColumn.cellValueFactory = PropertyValueFactory<Track, Double>("dur")
                        table.columns.addAll(numberColumn, nameColumn, durColumn)


                        table.setRowFactory { _ ->
                            val row: TableRow<Track> = TableRow()
                            row.setOnMouseClicked { event ->
                                if (event.clickCount == 2) {
                                    val rowData: Track = row.item
                                    if (tracks.size > 1) {
                                        number = rowData.num - 1
                                        create(tracks[number])
                                    }

                                }
                            }
                            row
                        }



                        table.items = obsList

                        children.addAll(musicSlider, table)
                    }
                }

                bottom = bm

                musicSlider.valueProperty().addListener { ob, old, new ->

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

                volumeSlider.valueProperty().addListener { ob, old, new ->
                    mplayer?.volume = new.toDouble()
                }
            }
        }

        Thread(Runnable {
            while (true) {
                if (mplayer != null) {
                    val currentTime = mplayer?.currentTime!!.toSeconds()
                    val allTime =  mplayer?.stopTime!!.toSeconds()

                    sliderChange = false
                    musicSlider.value = currentTime * 100.0 / allTime
                    sliderChange = true

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
            launch(Mplayerk::class.java)
        }
    }
}
